package com.pouetteur.pouetservice.repository;

import com.pouetteur.pouetservice.modele.Pouet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PouetRepository extends MongoRepository<Pouet, Long> {
    List<Pouet> findByAuthor(String author);
    List<Pouet> findByTags(String tagName);
    List<Pouet> findPouetsByIdPouetParent(long idPouetParent);
    Pouet findByIdPouet(long idPouet);

}
