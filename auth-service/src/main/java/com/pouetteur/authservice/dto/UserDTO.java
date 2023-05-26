package com.pouetteur.authservice.dto;

import com.pouetteur.authservice.model.Role;
import com.pouetteur.authservice.model.User;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private List<Role> roles;
}
