package com.jade.canopusapi.controller;


import com.jade.canopusapi.controller.util.AddressRetriever;
import com.jade.canopusapi.controller.util.Validator;
import com.jade.canopusapi.models.User;
import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.payload.request.SignUpRequest;
import com.jade.canopusapi.payload.response.MessageResponse;
import com.jade.canopusapi.dao.UserDAO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController()
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDAO userDAO;

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
            return ResponseEntity.ok().body(new MessageResponse("Account verified successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Failed to verify account."));
        }
    }

    @PostMapping("/resend_code")
    public ResponseEntity<?> resendCode(@Param("email") String email) throws MessagingException, UnsupportedEncodingException {
        if (userDAO.resendVerificationEmail(email)) {
            return ResponseEntity.ok().body(new MessageResponse("Verification email sent successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found or already verified."));
        }
    }

}
