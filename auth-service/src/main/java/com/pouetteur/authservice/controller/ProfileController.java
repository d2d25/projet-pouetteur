package com.pouetteur.authservice.controller;

import com.pouetteur.authservice.dto.ProfileDTO;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.service.IProfileService;

import com.pouetteur.authservice.service.exception.ClassTypeExpectException;
import com.pouetteur.authservice.service.exception.NotAutorizeToUpdateException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import jakarta.validation.Validator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final IProfileService profileService;

    private final Validator validator;

    public ProfileController(IProfileService profileService, Validator validator) {
        this.profileService = profileService;
        this.validator = validator;
    }

    /**
     * Méthode qui permet de recuperer tous les profils
     */
    @GetMapping("")
    public ResponseEntity getAllProfiles() {
        return ResponseEntity.ok(profileService.getAll());
    }

    /**
     * Méthode qui permet de recuperer un profil par son id
     */
    @GetMapping("/{id}")
    public ResponseEntity getProfileById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(profileService.getById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Méthode qui permet de recuperer un profil par son username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity getProfileByUsername(@PathVariable String username) {
        try {
            return ResponseEntity.ok(profileService.getByUsername(username));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Méthode qui permet de supprimer un profil
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == #user.id")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteProfile(@AuthenticationPrincipal User user, @PathVariable String id) {
        try {
            profileService.delete(id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Méthode qui permet de modifier un profil
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == #user.id")
    @PutMapping("/{id}")
    public ResponseEntity updateProfile(@AuthenticationPrincipal User user, @PathVariable String id, @RequestBody ProfileDTO profile) {
        try {
            profileService.update(id, profile);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NotAutorizeToUpdateException e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Méthode qui permet de creer une Communitée
     */
    @PostMapping("")
    public ResponseEntity createCommunity(@RequestBody ProfileDTO profile) {
        try {
            profile.setCreationDate(new Date());
            profileService.createCommunity(profile);
            return ResponseEntity.ok().build();
        } catch (ClassTypeExpectException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    /**
     * Méthode qui permet de creer un Membre
     */
    public ResponseEntity createMember(@RequestBody ProfileDTO profile) {
        try {
            profile.setCreationDate(new Date());
            profileService.createMember(profile);
            return ResponseEntity.ok().build();
        } catch (ClassTypeExpectException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    /**
     * Méthode qui permet de recuperer tous les Communitées
     */
    @GetMapping("/communities")
    public ResponseEntity getAllCommunities() {
        return ResponseEntity.ok(profileService.getAllCommunities());
    }

    /**
     * Méthode qui permet de recuperer tous les Membres
     */
    @GetMapping("/members")
    public ResponseEntity getAllMembers() {
        return ResponseEntity.ok(profileService.getAllMembers());
    }

    /**
     * Méthode qui permet s'abonner à une Communitée ou un Membre
     */
    @PostMapping("/subscribe/{id}")
    public ResponseEntity subscribe(@AuthenticationPrincipal User user, @PathVariable String id) {
        try {
            profileService.subscribe(user, id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Méthode qui permet de se desabonner d'une Communitée ou d'un Membre
     */
    @PostMapping("/unsubscribe/{id}")
    public ResponseEntity unsubscribe(@AuthenticationPrincipal User user, @PathVariable String id) {
        try {
            profileService.unsubscribe(user, id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
