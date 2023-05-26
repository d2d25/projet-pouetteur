package com.pouetteur.authservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    @NotEmpty
    @NotNull
    @Size(min = 8, max = 20)
    private String oldPassword;
    @NotEmpty
    @NotNull
    @Size(min = 8, max = 20)
    private String newPassword;
}
