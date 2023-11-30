package com.jade.canopusapi.payload.request;

import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.models.utils.SchoolType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class CreateSchoolRequest {
    @NotBlank
    @NotNull
    @Size(min=5, max = 100)
    private String name;

    @NotBlank
    @Size(min = 9, max = 9)
    private String cep;

    @Min(1)
    private short streetNumber;

    @Enumerated(EnumType.STRING)
    private SchoolType type;

    @ElementCollection
    private Collection<String> emails;
}
