package com.pouetteur.messagingservice.controller;

import com.pouetteur.messagingservice.modele.DTO.MemberDTO;
import com.pouetteur.messagingservice.modele.exceptions.AtLeast2MembersException;
import com.pouetteur.messagingservice.service.IConversationService;
import com.pouetteur.messagingservice.service.exceptions.*;
import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/messaging",produces = {MediaType.APPLICATION_JSON_VALUE})
public class ConversationController {

    private final IConversationService IConversationService;

    public ConversationController(IConversationService IConversationService) {
        this.IConversationService = IConversationService;
    }

    //    // Récupérer la clé publique du service d'authentification
//    private final SecretKey publicKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
//
//    @GetMapping("/secured")
//    public ResponseEntity<String> securedMethod(@RequestHeader("Authorization") String token) {
//
//        try {
//            // Vérifier la signature du token avec la clé publique
//            Jws<Claims> claims = Jwts.parserBuilder()
//                    .setSigningKey(publicKey)
//                    .build()
//                    .parseClaimsJws(token.replace("Bearer ", ""));
//
//            // Vérifier la date d'expiration du token
//            Date expirationDate = claims.getBody().getExpiration();
//            Instant now = Instant.now();
//            if (expirationDate.toInstant().isBefore(now)) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
//            }
//
//            // Vérifier que le token est émis pour l'application ou l'utilisateur en question
//            // Vérifier les claims comme "aud" (audience) ou "sub" (subject) selon le cas.
//
//            // Si tout est OK, renvoyer une réponse avec un message de succès
//            return ResponseEntity.ok("Method secured with token");
//        } catch (Exception e) {
//            // En cas d'erreur de vérification, renvoyer une réponse avec un code 401 (Unauthorized)
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }
    @GetMapping(value = "/hello")
    public ResponseEntity<Object> hello() {

        return ResponseEntity.status(201).body("salut");

    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/conversation")
    public ResponseEntity<Object> createConversation(@RequestBody ConversationDTO conversationDTO, Authentication authentication) {
        //TODO verifier que la personne qui envoie la requete(authenticated) se trouve dans la liste des membres de la conversation
        try {
            ConversationDTO conversationDTOa = IConversationService.createConversation(conversationDTO, authentication);
            return ResponseEntity.status(201).body(conversationDTOa);
        } catch (AtLeast2MembersException | MissingParametersException | UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/{idConversation}")
    public ResponseEntity<Object> getConversation(@PathVariable String idConversation, Authentication authentication) {
        try {
            ConversationDTO conversationDTO = IConversationService.getConversation(idConversation, authentication);
            return ResponseEntity.status(200).body(conversationDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/conversations")
    public ResponseEntity<Object> getConversations(Authentication authentication) {
        try {
            List<ConversationDTO> conversationDTOs = IConversationService.getConversations(authentication);
            return ResponseEntity.status(200).body(conversationDTOs);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{idConversation}/addMembers")
    public ResponseEntity<Object> addMembers(@PathVariable String idConversation, @RequestBody List<MemberDTO> membersDTO, Authentication authentication) {
        try {
            ConversationDTO conversationDTO = IConversationService.addMembers(idConversation, membersDTO, authentication);
            return ResponseEntity.status(200).body(conversationDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (MissingParametersException | AlreadyThereException | UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{idConversation}/removeMember")
    public ResponseEntity<Object> removeMember(@PathVariable String idConversation, @RequestParam String idMember, Authentication authentication) {
        try {
            ConversationDTO conversationDTO = IConversationService.removeMember(idConversation, idMember);
            return ResponseEntity.status(200).body(conversationDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (MissingParametersException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{idConversation}/leaveConversation")
    public ResponseEntity<Object> leaveConversation(@PathVariable String idConversation, Authentication authentication) {
        try {
            IConversationService.leaveConversation(idConversation, authentication);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (MissingParametersException | UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (SuccessfullyLeavedConversationException e) {
            return ResponseEntity.status(200).body(e.getMessage());
        }
        return ResponseEntity.status(400).body("Something went wrong");
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{idConversation}/editPhoto")
    public ResponseEntity<Object> editPhoto(@PathVariable String idConversation, @RequestParam String photo, Authentication authentication) {
        try {
            ConversationDTO conversationDTO = IConversationService.editPhoto(idConversation, photo, authentication);
            return ResponseEntity.status(200).body(conversationDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (MissingParametersException | UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{idConversation}/editName")
    public ResponseEntity<Object> editName(@PathVariable String idConversation, @RequestParam String name, Authentication authentication) {
        try {
            ConversationDTO conversationDTO = IConversationService.editName(idConversation, name, authentication);
            return ResponseEntity.status(200).body(conversationDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (MissingParametersException | UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{idConversation}")
    public ResponseEntity<Object> deleteConversation(@PathVariable String idConversation, Authentication authentication) {
        try {
            IConversationService.deleteConversation(idConversation);
            return ResponseEntity.status(200).body("Conversation deleted");
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
