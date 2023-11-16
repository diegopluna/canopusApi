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

import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController()
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDAO userDAO;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) throws MessagingException, UnsupportedEncodingException {

            if (!Validator.isValidFullName(request.getFullName())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Full name must be between 5 and 100 characters, contain only letters, and have a valid first and last name format."));
            }

            if (!Validator.isValidEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: The provided email address does not match the expected format."));
            }

            if (!Validator.isValidPhoneNumber(request.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid phone number format"));
            }

            if (!Validator.isValidPassword(request.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: The password must be between 6 and 40 characters."));
            }

            if (!Validator.isValidInterests(request.getInterests())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: The provided interests collection contains invalid values."));
            }

            if (!Validator.isValidCep(request.getCep())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: The provided CEP is invalid."));
            }

            if (!Validator.isValidStreetNumber(request.getStreetNumber())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: The provided street number is an invalid value."));
            }

            Address address = AddressRetriever.retrieveAddressByCep(request.getCep());

            if (address == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Error connecting to the CEP retrieval API. Try again later."));
            }
            address.setStreetNumber(request.getStreetNumber());
            if (request.getComplement() != null) {
                address.setComplement(request.getComplement());
            }

            User user = new User(request.getFullName(), request.getEmail(), request.getPhoneNumber(), request.getPassword(), request.getInterests(), address);

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
