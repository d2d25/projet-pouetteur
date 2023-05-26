package com.pouetteur.pouetservice.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pouets")
public class Pouet {

    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @Id
    private long idPouet;

    private Member author;
    private String title;

    private String body;

    private List<String> medias;
    private List<Profile> postedOn;
    private String publishDate;

    private List<String> tags;

    private List<Reaction> reactions;

    private long nbPartages;

    private long idPouetParent;

}
