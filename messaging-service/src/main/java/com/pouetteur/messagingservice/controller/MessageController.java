package com.pouetteur.messagingservice.controller;
import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import com.pouetteur.messagingservice.modele.DTO.MessageDTO;
import com.pouetteur.messagingservice.service.IMessageService;
import com.pouetteur.messagingservice.service.exceptions.NotFoundException;
import com.pouetteur.messagingservice.service.exceptions.UnAuthorizedException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/messaging",produces = {MediaType.APPLICATION_JSON_VALUE})
public class MessageController {
    private final IMessageService IMessageService;

    public MessageController(IMessageService IMessageService) {
        this.IMessageService = IMessageService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/{idConversation}/sendMessage")
    public ResponseEntity<Object> sendMessage(@PathVariable String idConversation, @RequestParam String text, Authentication authentication) {
        //TODO verifier que la personne qui envoie la requete(authenticated) est la meme que celle qui se trouve en temps que author dans messageDTO
        try {
            ConversationDTO conversationDTO = IMessageService.sendMessage(idConversation, text, authentication);
            return ResponseEntity.status(200).body(conversationDTO);
            //MessageDTO resultMessageDTO = messageService.sendMessage(idConversation, messageDTO);
            //return ResponseEntity.created(URI.create("/messaging/" + idConversation + "/" + resultMessageDTO.getId())).body(resultMessageDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/{idConversation}/{idMessage}")
    public ResponseEntity<Object> getMessage(@PathVariable String idConversation, @PathVariable String idMessage, Authentication authentication){
        //TODO verifier que la personne qui envoie la requete(authenticated) est la meme que celle qui se trouve en temps que author dans messageDTO
        try {
            MessageDTO messageDTO = IMessageService.getMessage(idConversation, idMessage, authentication);
            return ResponseEntity.status(200).body(messageDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(value = "/{idConversation}/{idMessage}/deleteMessage")
    public ResponseEntity<Object> deleteMessage(@PathVariable String idConversation, @PathVariable String idMessage, Authentication authentication) {
        //TODO verifier que la personne qui envoie la requete(authenticated) est la meme que celle qui se trouve en temps que author dans messageDTO
        try {
            ConversationDTO conversationDTO = IMessageService.deleteMessage(idConversation, idMessage, authentication);
            return ResponseEntity.status(200).body(conversationDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnAuthorizedException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
