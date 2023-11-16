package com.jade.canopusapi.models.utils;


import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @NotBlank
    private String cep;

    @NotBlank
    private String state;

    @NotBlank
    private String municipality;

    @NotBlank
    private String district;

    @NotBlank
    private String street;

    @Min(1)
    private short streetNumber;

    private String complement;

}