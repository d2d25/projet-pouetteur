package com.pouetteur.pouetservice.service;

import com.pouetteur.pouetservice.modele.*;
import com.pouetteur.pouetservice.modele.dtos.PouetDTO;
import com.pouetteur.pouetservice.modele.dtos.ProfileDTO;
import com.pouetteur.pouetservice.rabbitmq.RabbitMQProducer;
import com.pouetteur.pouetservice.repository.PouetRepository;
import com.pouetteur.pouetservice.service.exceptions.PouetDejaExistantException;
import com.pouetteur.pouetservice.service.exceptions.PouetSansReponsesException;
import com.pouetteur.pouetservice.service.exceptions.ReactionNullException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class PublicationService implements IPublicationService {

    @Autowired
    private PouetRepository pouetRepository;

    private final ModelMapper modelMapper;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    public PublicationService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public PouetDTO createPouet(PouetDTO pouetDTO) throws PouetDejaExistantException {

        Pouet newPouet = this.modelMapper.map(pouetDTO, Pouet.class);
        newPouet.setIdPouet(sequenceGenerator.generateSequence(Pouet.SEQUENCE_NAME));
        pouetDTO.setIdPouet(newPouet.getIdPouet());
        // obtenir la date et l'heure actuelles
        LocalDateTime maintenant = LocalDateTime.now();

        // formatter la date et l'heure
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateFormatee = maintenant.format(formatter);
        newPouet.setPublishDate(dateFormatee);
        pouetDTO.setPostedOn(new ArrayList<>());
        if(this.pouetRepository.existsById(newPouet.getIdPouet())){
            throw new PouetDejaExistantException();
        }
        this.pouetRepository.save(newPouet);
        return pouetDTO;
    }
    @Override
    public PouetDTO getPouetById(Long pouetId) {

            Pouet pouet = pouetRepository.findById(pouetId)
                    .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
            return this.modelMapper.map(pouet,PouetDTO.class);

    }

    @Override
    public List<PouetDTO> getAllPouets() {

        try{
            List<Pouet> pouets = pouetRepository.findAll();
            List<PouetDTO> pouetDTOS = new ArrayList<>();
            pouets.forEach(p -> pouetDTOS.add(this.modelMapper.map(p,PouetDTO.class)));
            return pouetDTOS;
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Aucun pouet trouvé");

        }

    }

    @Override
    public List<PouetDTO> getPouetsByMember(String author) {

        try{
            List<Pouet> pouets = pouetRepository.findAll().stream()
                    .filter(pouet -> author.equals(pouet.getAuthor().getUsername()))
                    .toList();
            List<PouetDTO> pouetDTOS = new ArrayList<>();
            pouets.forEach(p -> pouetDTOS.add(this.modelMapper.map(p,PouetDTO.class)));
            return pouetDTOS;
        } catch (NoSuchElementException e){
            throw new NoSuchElementException("Aucun pouet trouvé");
        }
    }

    @Override
    public List<PouetDTO> getPouetsByTag(String tagName) {
        try{
            List<Pouet> pouets = pouetRepository.findByTags(tagName);
            List<PouetDTO> pouetDTOS = new ArrayList<>();
            pouets.forEach(p -> pouetDTOS.add(this.modelMapper.map(p,PouetDTO.class)));
            return pouetDTOS;
        } catch (NoSuchElementException e){
            throw new NoSuchElementException("Aucun pouet trouvé");

        }
    }
    @Override
    public void addMediaToPouet(Long pouetId, String media) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
        List<String> mediaSet;
        if(Objects.nonNull(pouet.getMedias())){
            mediaSet = pouet.getMedias();
        }else{
            mediaSet = new ArrayList<>();
        }
        mediaSet.add(media);
        pouet.setMedias(mediaSet);
        pouetRepository.save(pouet);
    }

    @Override
    public void addTagToPouet(Long pouetId, String tag) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
        List<String> tagSet;
        if(Objects.nonNull(pouet.getTags())) {
            tagSet = pouet.getTags();
        }else{
            tagSet = new ArrayList<>();
        }
        tagSet.add(tag);
        pouet.setTags(tagSet);
        pouetRepository.save(pouet);

    }

    @Override
    public PouetDTO answerPouet(long pouetParentId, PouetDTO pouetDTO) throws PouetDejaExistantException {
        if (this.pouetRepository.existsById(pouetParentId)) {
            // obtenir la date et l'heure actuelles
            LocalDateTime maintenant = LocalDateTime.now();

            // formatter la date et l'heure
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dateFormatee = maintenant.format(formatter);

            pouetDTO.setPublishDate(dateFormatee);
            pouetDTO.setIdPouetParent(pouetParentId);
            Pouet newPouet = this.modelMapper.map(pouetDTO, Pouet.class);
            newPouet.setIdPouet(sequenceGenerator.generateSequence(Pouet.SEQUENCE_NAME));
            newPouet.setPostedOn(new ArrayList<>());

            if(this.pouetRepository.existsById(newPouet.getIdPouet())){
                throw new PouetDejaExistantException();
            }
            this.pouetRepository.save(newPouet);
            rabbitMQProducer.sendNotif(newPouet);
            return pouetDTO;
        }

        throw new NoSuchElementException("Pouet avec id " + pouetParentId + " pas trouvé");

    }

    @Override
    public PouetDTO reactToAPouet(long pouetParentId, String reaction) throws ReactionNullException {
        Pouet pouet = pouetRepository.findById(pouetParentId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetParentId)));
        Reaction react;
        if(Objects.nonNull(reaction)) {
            react = Reaction.valueOf(reaction);
        } else {
            throw new ReactionNullException();
        }

        List<Reaction> reactions;
            if (Objects.nonNull(pouet.getReactions())) {
                reactions = pouet.getReactions();
            } else {
                reactions = new ArrayList<>();
            }
            reactions.add(react);
            pouet.setReactions(reactions);
            pouetRepository.save(pouet);
            rabbitMQProducer.sendNotif(pouet);
            return this.modelMapper.map(pouet,PouetDTO.class);

    }

    @Override
    public long getNbReactionByPouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
        if(Objects.isNull(pouet.getReactions())){
            return 0;
        }
        return pouet.getReactions()
                   .size();

    }

    @Override
    public long getNbeactionLikeByPouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
            if(Objects.isNull(pouet.getReactions())){
                return 0;
            }
            return pouet.getReactions()
                    .stream()
                    .filter(reaction -> reaction.name().equals("Like"))
                    .count();
        }

    @Override
    public long getNbReactionLoveByPouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
            if(Objects.isNull(pouet.getReactions())){
                return 0;
            }
            return pouet.getReactions()
                    .stream().filter(reaction -> reaction.name().equals("Love"))
                    .count();
            }

    @Override
    public long getNbReactionHahaByPouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
            if(Objects.isNull(pouet.getReactions())){
                return 0;
            }
            return pouet.getReactions()
                    .stream().filter(reaction -> reaction.name().equals("HaHa"))
                    .count();
            }

    @Override
    public long getNbReactionWowByPouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));            if(Objects.isNull(pouet.getReactions())){
                return 0;
            }
            return pouet.getReactions()
                    .stream().filter(reaction -> reaction.name().equals("Wow"))
                    .count();
    }

    @Override
    public long getNbReactionSadByPouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));


        if(Objects.isNull(pouet.getReactions())){
            return 0;
        }

        return pouet.getReactions()
                .stream().filter(reaction -> reaction.name().equals("Sad"))
                .count();
           }

    @Override
    public long getNbReactionAngryByPouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));
            if(Objects.isNull(pouet.getReactions())){
                return 0;
            }
            return pouet.getReactions()
                    .stream().filter(reaction -> reaction.name().equals("Angry"))
                    .count();
    }

    @Override
    public PouetDTO sharePouet(Long pouetId, ProfileDTO destination) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));

        if(Objects.isNull(pouet.getPostedOn())){
            pouet.setPostedOn(new ArrayList<>());
        }
        pouet.getPostedOn().add(this.modelMapper.map(destination,Profile.class));
        pouet.setNbPartages(pouet.getNbPartages()+1);
        pouetRepository.save(pouet);
        return this.modelMapper.map(pouet, PouetDTO.class);
    }

    @Override
    public List<PouetDTO> getAnswersPouet(Long pouetId) throws PouetDejaExistantException, PouetSansReponsesException {
        if(this.pouetRepository.existsById(pouetId)){
            if(this.pouetRepository.findAll()
                    .stream()
                    .anyMatch(pouet -> pouet.getIdPouetParent() == pouetId)) {

                List<PouetDTO> pouetDTOS = new ArrayList<>();
                this.pouetRepository.findPouetsByIdPouetParent(pouetId).forEach(p -> pouetDTOS.add(this.modelMapper.map(p,PouetDTO.class)));
                return pouetDTOS;

            }else{
                throw new PouetSansReponsesException();
            }
        }
        throw new PouetDejaExistantException();
    }

    @Override
    public long getNbPartagePouet(long pouetId) {
        Pouet pouet = pouetRepository.findById(pouetId)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Pouet not found ", pouetId)));

        return pouet.getNbPartages();
    }



}

