package com.pouetteur.authservice.controller;

import com.pouetteur.authservice.dto.ErrorMessageResponseDTO;
import com.pouetteur.authservice.dto.PasswordDTO;
import com.pouetteur.authservice.dto.ProfileDTO;
import com.pouetteur.authservice.dto.UserDTO;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.service.IUserService;
import com.pouetteur.authservice.service.exception.EmailAlreadyExistsException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import com.pouetteur.authservice.service.exception.UsernameAlreadyExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/auth/users")
public class UserController {

    private final IUserService userService;
    private final Validator validator;
    private final ProfileController profileController;

    public UserController(IUserService userService, Validator validator, ProfileController profileController) {
        this.userService = userService;
        this.validator = validator;
        this.profileController = profileController;
    }

    /**
     * Méthode qui permet de recuperer tous les utilisateurs
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    /**
     * Méthode qui permet de recuperer un utilisateur par son id
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == #user.id")
    @GetMapping("/{id}")
    public ResponseEntity getUserById(@AuthenticationPrincipal User user, @PathVariable String id) {
        try {
            return ResponseEntity.ok(userService.getById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Méthode qui permet de supprimer un utilisateur
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == #user.id")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@AuthenticationPrincipal User user, @PathVariable String id) {
        try {
            userService.delete(id);
            profileController.deleteProfile(user, id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Méthode qui permet de modifier un utilisateur
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == #user.id")
    @PatchMapping("/{id}")
    public ResponseEntity updateUser(@AuthenticationPrincipal User user, @PathVariable String id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO update = userService.update(id, userDTO);
            ProfileDTO profile = new ProfileDTO();
            profile.setId(id);
            profile.setUsername(update.getUsername());
            profile.setEmail(update.getEmail());
            profileController.updateProfile(user, id, profile);
            return ResponseEntity.ok(update);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("Username already exists");
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
    }

    /**
     * Méthode qui permet de modifier le mot de passe d'un utilisateur
     */
    @PreAuthorize("#id == #user.id")
    @PatchMapping("/{id}/password")
    public ResponseEntity updatePassword(@AuthenticationPrincipal User user, @PathVariable String id, @RequestBody PasswordDTO password) {
        Set<ConstraintViolation<PasswordDTO>> violations = validator.validate(password);

        if (!violations.isEmpty()) {
            //recupère une liste du champ qui ne respecte pas la contrainte suivie du message d'erreur
            List<String> errors = violations.stream().map(v -> "{ Property : " + v.getPropertyPath() + ", Value : " + v.getInvalidValue() + ", Error Message : " + v.getMessage() + "}").toList();
            return ResponseEntity.badRequest().body(new ErrorMessageResponseDTO(errors, HttpStatus.BAD_REQUEST));
        }
        
        try {
            return ResponseEntity.ok(userService.updatePassword(id, password.getOldPassword(), password.getNewPassword()));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
