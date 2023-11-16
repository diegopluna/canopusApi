package com.jade.canopusapi.controller.util;

import com.jade.canopusapi.models.utils.Goal;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@NoArgsConstructor
public class Validator {

    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.isEmpty() || fullName.isBlank()) {
            return false;
        }

        if (fullName.length() < 5 || fullName.length() > 100) {
            return false;
        }

        String[] nameParts = fullName.trim().split("\\s+");

        if (nameParts.length < 2) {
            return false;
        }

        for (String part : nameParts) {
            if (!part.matches("[A-Za-z\\-]+")) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty() || email.isBlank()) {
            return false;
        }

        if (email.length() > 255) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.isBlank()) {
            return false;
        }
        String phoneRegex = "^\\d{2}9\\d{8}$";

        return Pattern.compile(phoneRegex).matcher(phoneNumber).matches();
    }

    public static boolean isValidInterests(Collection<Goal> interests) {
        if (interests == null || interests.isEmpty()) {
            return false;
        }

        Set<Goal> validGoals = new HashSet<>();
        Collections.addAll(validGoals, Goal.values());

        for (Goal interest : interests) {
            if (!validGoals.contains(interest)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty() || password.isBlank()) {
            return false;
        }

        return password.length() >= 6 && password.length() <= 40;
    }

    public static boolean isValidCep(String cep) {
        if (cep == null || cep.isEmpty() || cep.isBlank()) {
            return false;
        }

        String cepRegex = "\\d{5}-\\d{3}";

        if (!Pattern.compile(cepRegex).matcher(cep).matches()) {
            return false;
        }

        return true;
    }

    public static boolean isValidStreetNumber(Short streetNumber) {
        return streetNumber != null && streetNumber >= 1;
    }


}
