package com.jade.canopusapi.payload.request;

import com.jade.canopusapi.models.utils.Goal;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank
    @NotNull
    @Size(min=5, max = 100)
    private String fullName;

    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @NotBlank
    @Size(min = 11, max = 11)
    @Digits(integer = 11, fraction = 0)
    private String phoneNumber;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Collection<Goal> interests;

    @NotBlank
    @Size(min = 9, max = 9)
    private String cep;

    @Min(1)
    private short streetNumber;

    private String complement;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}
