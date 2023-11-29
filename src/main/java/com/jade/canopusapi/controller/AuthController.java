package com.jade.canopusapi.controller;


import com.jade.canopusapi.controller.util.AddressRetriever;
import com.jade.canopusapi.controller.util.Validator;
import com.jade.canopusapi.exception.TokenRefreshException;
import com.jade.canopusapi.models.RefreshToken;
import com.jade.canopusapi.models.User;
import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.payload.request.FinishAdminCreationRequest;
import com.jade.canopusapi.payload.request.SignInRequest;
import com.jade.canopusapi.payload.request.SignUpRequest;
import com.jade.canopusapi.payload.request.TokenRefreshRequest;
import com.jade.canopusapi.payload.response.JwtResponse;
import com.jade.canopusapi.payload.response.MessageResponse;
import com.jade.canopusapi.dao.UserDAO;
import com.jade.canopusapi.payload.response.TokenRefreshResponse;
import com.jade.canopusapi.security.jwt.JwtUtils;
import com.jade.canopusapi.security.services.RefreshTokenService;
import com.jade.canopusapi.security.services.UserDetailsImpl;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController()
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) throws MessagingException, UnsupportedEncodingException {

            if (!Validator.isValidFullName(request.getFullName())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Nome completo deve ter entre 5 e 100 caracteres, conter apenas letras e ter um primeiro nome e sobrenome."));
            }

            if (!Validator.isValidEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(new MessageResponse("E-mail inválido"));
            }

            if (!Validator.isValidPhoneNumber(request.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Telefone inválido"));
            }

            if (!Validator.isValidPassword(request.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("A senha deve ter entre 6 e 40 caracteres."));
            }

            if (!Validator.isValidInterests(request.getInterests())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Os interesses possuem valores inválidos."));
            }

            if (!Validator.isValidCep(request.getCep())) {
                return ResponseEntity.badRequest().body(new MessageResponse("O CEP fornecido é inválido."));
            }

            if (!Validator.isValidStreetNumber(request.getStreetNumber())) {
                return ResponseEntity.badRequest().body(new MessageResponse("O número de rua está em um formato inválido."));
            }

            String avatarBase64 = request.getAvatar();
            String imageURL = null;
            if (avatarBase64 != null) {
                try {
                    imageURL = userDAO.saveImage(avatarBase64);
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Não foi possível salvar a imagem"));
                }
            }


            Address address = AddressRetriever.retrieveAddressByCep(request.getCep());

            if (address == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Erro ao conectar ao serviço de CEP. Favor tentar novamente mais tarde"));
            }
            address.setStreetNumber(request.getStreetNumber());
            if (request.getComplement() != null) {
                address.setComplement(request.getComplement());
            }

            User user = new User(request.getFullName(), request.getEmail(), request.getPhoneNumber(), request.getPassword(), request.getInterests(), address);

            if (imageURL != null) {
                user.setAvatar(imageURL);
            }

            return userDAO.register(user);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Param("code") String code) {
        if (userDAO.verify(code)) {
            return ResponseEntity.ok().body(new MessageResponse("Conta verificada com sucesso!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Falha em verificar a conta."));
        }
    }

    @PostMapping("/verify_create")
    public ResponseEntity<?> finishAdminCreation(@Param("code") String code, @Valid @RequestBody FinishAdminCreationRequest request) {
        if (!Validator.isValidPassword(request.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("A senha deve ter entre 6 e 40 caracteres."));
        }
        if (!Validator.isValidCep(request.getCep())) {
            return ResponseEntity.badRequest().body(new MessageResponse("O CEP fornecido é inválido."));
        }

        if (!Validator.isValidStreetNumber(request.getStreetNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("O número de rua está em um formato inválido."));
        }

        User user = userDAO.findUser(code);
        if (user == null ||user.getVerified()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Usuário já existe ou se econtra verificado"));
        }

        String avatarBase64 = request.getAvatar();
        String imageURL = null;
        if (avatarBase64 != null) {
            try {
                imageURL = userDAO.saveImage(avatarBase64);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Não foi possível salvar a imagem"));
            }
        }

        Address address = AddressRetriever.retrieveAddressByCep(request.getCep());

        if (address == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erro ao conectar ao serviço de CEP. Favor tentar novamente mais tarde"));
        }
        address.setStreetNumber(request.getStreetNumber());
        if (request.getComplement() != null) {
            address.setComplement(request.getComplement());
        }

        user.setPassword(request.getPassword());
        user.setAddress(address);
        if (imageURL != null) {
            user.setAvatar(imageURL);
        }
        userDAO.verifyAdmin(user);
        return ResponseEntity.ok().body(new MessageResponse("Usuário verificado com sucesso"));

    }

    @PostMapping("/resend_code")
    public ResponseEntity<?> resendCode(@Param("email") String email) throws MessagingException, UnsupportedEncodingException {
        if (userDAO.resendVerificationEmail(email)) {
            return ResponseEntity.ok().body(new MessageResponse("Verification email sent successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found or already verified."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefresh = request.getRefresh();


        return refreshTokenService.findByToken(requestRefresh).map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUser(UserDetailsImpl.build(user));
                    String newRefresh = refreshTokenService.createRefreshToken(user.getId()).getToken();
                    refreshTokenService.deleteByToken(requestRefresh);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, newRefresh));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefresh,"Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

}
