package com.pouetteur.authservice.dto;

import com.pouetteur.authservice.model.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO {
    @NotEmpty
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
    @Size(min = 4, max = 20)
    private String username;
    @NotEmpty
    @NotNull
    @Size(min = 8, max = 20)
    private String password;
    @NotEmpty
    @NotNull
    @Email
    @Size(max = 50)
    private String email;

    private List<Role> roles;

    public SignupDTO(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
