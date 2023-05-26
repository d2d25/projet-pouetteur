package com.pouetteur.pouetservice.controleur;

import com.pouetteur.pouetservice.modele.*;
import com.pouetteur.pouetservice.modele.dtos.MemberDTO;
import com.pouetteur.pouetservice.modele.dtos.PouetDTO;
import com.pouetteur.pouetservice.modele.dtos.ProfileDTO;
import com.pouetteur.pouetservice.service.PublicationService;
import com.pouetteur.pouetservice.service.exceptions.PouetDejaExistantException;
import com.pouetteur.pouetservice.service.exceptions.PouetSansReponsesException;
import com.pouetteur.pouetservice.service.exceptions.ReactionNullException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;



@RestController
@RequestMapping("/api")
public class PouetController {

    @Autowired
    private PublicationService publicationService;
    private final ModelMapper modelMapper;

    public PouetController(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @PostMapping("/pouets")
    public ResponseEntity<Object> createPouet(@RequestBody PouetDTO pouet, Authentication authentication) {

        try{
            Member member = (Member)authentication.getPrincipal();
            pouet.setAuthor(this.modelMapper.map(member, MemberDTO.class));

            PouetDTO createdPouet = publicationService.createPouet(pouet);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPouet);
        }catch (PouetDejaExistantException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{id}")
    public ResponseEntity<Object> getPouetById(@PathVariable Long id) {
        try{
            PouetDTO pouet = publicationService.getPouetById(id);
            return ResponseEntity.ok(pouet);
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping(value = "/pouets")
    public ResponseEntity<Object> getAllPouets() {
        try{
            List<PouetDTO> pouets = this.publicationService.getAllPouets();
            return ResponseEntity.status(HttpStatus.OK).body(pouets);

        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }

    }
    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("pouets/tags/{tagName}")
    public ResponseEntity<Object> getPouetsByTag(@PathVariable String tagName) {
        try{
            List<PouetDTO> pouets = publicationService.getPouetsByTag(tagName);
            return ResponseEntity.ok(pouets);
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("pouets/members/{author}")
    public ResponseEntity<Object> getPouetsByMember(@PathVariable String author) {
        try{
            List<PouetDTO> pouets = publicationService.getPouetsByMember(author);
            return ResponseEntity.ok(pouets);
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @PatchMapping("/pouets/{pouetId}/medias")
    public ResponseEntity<Object> addMediaToPouet(@PathVariable Long pouetId, @RequestBody String media) {
        try{
            publicationService.addMediaToPouet(pouetId, media);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @PatchMapping("/pouets/{pouetId}/tags")
    public ResponseEntity<Void> addTagToPouet(@PathVariable Long pouetId, @RequestBody String tag) {

        try{
            publicationService.addTagToPouet(pouetId, tag);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @PostMapping("/pouets/{pouetId}/answers")
    public ResponseEntity<Object> answerPouet(@PathVariable Long pouetId, @RequestBody PouetDTO answer, Authentication authentication) {
        try{
            Member member = (Member)authentication.getPrincipal();
            answer.setAuthor(this.modelMapper.map(member, MemberDTO.class));
            PouetDTO answeredPouet = this.publicationService.answerPouet(pouetId,answer);
            return ResponseEntity.ok(answeredPouet);
        }catch(NoSuchElementException  e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch( PouetDejaExistantException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @PatchMapping("/pouets/{pouetId}/reactions")
    public ResponseEntity<Object> reactToAPouet(@PathVariable Long pouetId, @RequestBody ReactionRequest reactionRequest) {
        try{
            PouetDTO reactedPouet = publicationService.reactToAPouet(pouetId, reactionRequest.getReaction());
            return ResponseEntity.ok(reactedPouet);
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ReactionNullException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{pouetId}/reactions/count")
    public ResponseEntity<Object> getNbReactionsByPouet(@PathVariable Long pouetId) {
        try {
            return ResponseEntity.ok(publicationService.getNbReactionByPouet(pouetId));
        }catch (NoSuchElementException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{pouetId}/reactions/like")
    public ResponseEntity<Object> getNbReactionsLikeByPouet(@PathVariable Long pouetId) {
        try{
            return ResponseEntity.ok(this.publicationService.getNbeactionLikeByPouet(pouetId));


        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{id}/reactions/love")
    public ResponseEntity<Object> getNbReactionsLoveByPouet(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(this.publicationService.getNbReactionLoveByPouet(id));


        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{id}/reactions/haha")
    public ResponseEntity<Object> getNbReactionsHahaByPouet(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(this.publicationService.getNbReactionHahaByPouet(id));
        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{id}/reactions/wow")
    public ResponseEntity<Object> getNbReactionsWowByPouet(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(this.publicationService.getNbReactionWowByPouet(id));


        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{id}/reactions/sad")
    public ResponseEntity<Object> getNbReactionsSadByPouet(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(this.publicationService.getNbReactionSadByPouet(id));


        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }
    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("/pouets/{id}/reactions/angry")
    public ResponseEntity<Object> getNbReactionsAngryByPouet(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(this.publicationService.getNbReactionAngryByPouet(id));


        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @GetMapping("pouets/{pouetId}/answers")
    public ResponseEntity<Object> getAllAnswersPouet(@PathVariable long pouetId){
        try {
            return ResponseEntity.ok(this.publicationService.getAnswersPouet(pouetId));
        }catch(PouetDejaExistantException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }catch (PouetSansReponsesException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")
    @PatchMapping("/pouets/{pouetId}/share")
    public ResponseEntity<Object> sharePouet(@PathVariable Long pouetId,
                                            @RequestBody ProfileDTO destination) {

        try{
            PouetDTO repost = publicationService.sharePouet(pouetId, destination);
            return ResponseEntity.ok(repost);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or #id == #user.id")

    @GetMapping("/pouets/{pouetId}/partages")
    public ResponseEntity<Object> getNbPartages(@PathVariable long pouetId){
        try {
            Long nbPartages = this.publicationService.getNbPartagePouet(pouetId);
            return ResponseEntity.ok(nbPartages);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }

    }


}
