package com.jade.canopusapi.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishAdminCreationRequest {
    @NotBlank
    @Size(min = 9, max = 9)
    private String cep;

    @Min(1)
    private short streetNumber;

    private String complement;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String avatar;
}
