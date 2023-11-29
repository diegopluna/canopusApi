package com.jade.canopusapi.controller;


import com.jade.canopusapi.controller.util.Validator;
import com.jade.canopusapi.dao.UserDAO;
import com.jade.canopusapi.models.User;
import com.jade.canopusapi.models.utils.UserRole;
import com.jade.canopusapi.payload.request.CreateAdminRequest;
import com.jade.canopusapi.payload.response.MessageResponse;
import com.jade.canopusapi.security.services.UserDetailsImpl;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController()
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDAO userDAO;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createAdminOrMod(@Valid @RequestBody CreateAdminRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getRole() != UserRole.ADMINISTRADOR) {
            return ResponseEntity.status(401).body(new MessageResponse("Usuário não autorizado"));
        }

        if (!Validator.isValidFullName(request.getFullName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Nome completo deve ter entre 5 e 100 caracteres, conter apenas letras e ter um primeiro nome e sobrenome."));
        }
        if (!Validator.isValidEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("E-mail inválido"));
        }
        if (!Validator.isValidPhoneNumber(request.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Telefone inválido"));
        }
        if (!Validator.isValidInterests(request.getInterests())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Os interesses possuem valores inválidos."));
        }

        User user = new User(request.getFullName(), request.getEmail(), request.getPhoneNumber(), request.getUserRole(), request.getInterests());

        return userDAO.registerMod(user);
    }

    @PreAuthorize("hasAnyAuthority()")
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body(new MessageResponse("teste"));
    }
}
