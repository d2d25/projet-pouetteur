package com.pouetteur.pouetservice.service;

import com.pouetteur.pouetservice.modele.*;
import com.pouetteur.pouetservice.modele.dtos.PouetDTO;
import com.pouetteur.pouetservice.modele.dtos.ProfileDTO;
import com.pouetteur.pouetservice.service.exceptions.PouetDejaExistantException;
import com.pouetteur.pouetservice.service.exceptions.PouetSansReponsesException;
import com.pouetteur.pouetservice.service.exceptions.ReactionNullException;

import java.util.List;
import java.util.Optional;

public interface IPublicationService {

    /**
     * Creer un pouet
     * @param pouet
     * @return
     */
    PouetDTO createPouet(PouetDTO pouet) throws PouetDejaExistantException;

    /**
     * Récupérer le pouet par son id
     * @param pouetId
     * @return
     */
    PouetDTO getPouetById(Long pouetId);

    /**
     * Récupérer tous les pouets
     * @return
     */
    List<PouetDTO> getAllPouets();

    /**
     * Récuperer les pouets par un tag
     * @param tagName
     * @return
     */
    List<PouetDTO> getPouetsByTag(String tagName);

    /**
     * Récupérer les pouets d'un membre
     * @param author
     * @return
     */
    List<PouetDTO> getPouetsByMember(String author);

    /**
     * Ajouter un media a un pouet
     * @param pouetId
     * @param media
     */
    void addMediaToPouet(Long pouetId, String media);

    /**
     * Ajouter un tag a un pouet
     * @param pouetId
     * @param tag
     */
    void addTagToPouet(Long pouetId, String tag);

    /**
     * Répondre a un pouet
     * @param pouetDTO
     * @param pouetId
     * @return
     */
    PouetDTO answerPouet(long pouetId, PouetDTO pouetDTO) throws PouetDejaExistantException ;

    /**
     * Réagir a un pouet
     * @param pouetId
     */
    PouetDTO reactToAPouet(long pouetId, String reaction) throws ReactionNullException;

    /**
     * Avoir le nombre de reaction sur un pouet
     * @return
     */
    long getNbReactionByPouet( long pouetId);

    /**
     * Avoir le nombre de reaction Like sur un pouet
     * @return
     */
    long getNbeactionLikeByPouet(long pouetId);

    /**
     * Avoir le nombre de reaction Love sur un pouet
     * @return
     */
    long getNbReactionLoveByPouet(long pouetId);

    /**
     * Avoir le nombre de reaction HaHa sur un pouet
     * @return
     */
    long getNbReactionHahaByPouet(long pouetId);

    /**
     * Avoir le nombre de reaction Wow sur un pouet
     * @return
     */
    long getNbReactionWowByPouet(long pouetId);

    /**
     * Avoir le nombre de reaction Sad sur un pouet
     * @return
     */
    long getNbReactionSadByPouet(long pouetId);

    /**
     * Avoir le nombre de reaction Angry sur un pouet
     * @return
     */
    long getNbReactionAngryByPouet(long pouetId);


    /**
     * Partager un pouet sur le profil ou une communauté de l'utilisateur
     * @param pouetId
     * @param destination
     * @return le pouet partagé
     */
    PouetDTO sharePouet(Long pouetId, ProfileDTO destination);

    /**
     * Récupérer les réponses du pouet
     * @param pouetId
     * @return
     */
    List<PouetDTO> getAnswersPouet(Long pouetId) throws PouetDejaExistantException, PouetSansReponsesException;


    /**
     * Récupérer le nombre de partage d'un pouet
     * @param pouetId
     * @return
     */
    long getNbPartagePouet(long pouetId);

}
