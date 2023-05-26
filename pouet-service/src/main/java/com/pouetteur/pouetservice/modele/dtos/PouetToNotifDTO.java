package com.pouetteur.pouetservice.modele.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PouetToNotifDTO {
    private long idPouet;
    private MemberDTO author;
    private String title;
    private String body;
    private String publishDate;
    private List<String> medias;

    public PouetToNotifDTO() {
    }

    public PouetToNotifDTO(long idPouet, MemberDTO author, String title, String body, String publishDate, List<String> medias) {
        this.idPouet = idPouet;
        this.author = author;
        this.title = title;
        this.body = body;
        this.publishDate = publishDate;
        this.medias = medias;
    }
}