package com.pouetteur.authservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotNull
    @NotEmpty
    private String login;

    @NotEmpty
    @NotNull
    private String password;

}
