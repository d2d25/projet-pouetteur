package com.pouetteur.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "communities")
public class Community implements Profile{
    @Id
    @GeneratedValue(generator = "IdGenerator")
    @GenericGenerator(name = "IdGenerator", strategy = "com.pouetteur.authservice.config.IdGenerator")
    private String id;
    private String username;
    private String name;
    private Date creationDate;
    private String profilePhoto;
    private String profileBanner;
    private String bio;
    @ManyToMany
    private ArrayList<Member> members;
    @ManyToOne
    private Member owner;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
