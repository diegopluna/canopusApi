package com.jade.canopusapi.controller;


import com.jade.canopusapi.model.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/signup")
    private int Signup( @RequestBody User user) {

        if (user.getEmail() != null) {
            return 200;
        }
        return 400;
    }
}
