package com.jade.canopusapi.payload.request;


import com.jade.canopusapi.models.utils.Goal;
import com.jade.canopusapi.models.utils.UserRole;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class CreateAdminRequest {

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

    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
