package com.pouetteur.pouetservice.modele.dtos;

import com.pouetteur.pouetservice.modele.Pouet;
import com.pouetteur.pouetservice.modele.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PouetDTO {
    private long idPouet;
    private MemberDTO author;
    private String title;
    private String body;
    private List<String> tags;
    private List<ProfileDTO> postedOn;
    private String publishDate;
    private List<Reaction> reactions;
    private List<String> medias;
    private long nbPartages;
    private long idPouetParent;

    public PouetDTO(Pouet pouet) {
        this.idPouet = pouet.getIdPouet();
        this.author = new MemberDTO(pouet.getAuthor());
        this.title = pouet.getTitle();
        this.body = pouet.getBody();
        this.tags = pouet.getTags();
        this.postedOn = pouet.getPostedOn().stream()
                .map(ProfileDTO::new)
                .collect(Collectors.toList());
        this.publishDate = pouet.getPublishDate();
        this.reactions = pouet.getReactions();
        this.medias = pouet.getMedias();
        this.nbPartages = pouet.getNbPartages();
        this.idPouetParent = pouet.getIdPouetParent();
    }
}
