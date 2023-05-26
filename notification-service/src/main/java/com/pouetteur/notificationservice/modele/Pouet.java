package com.pouetteur.notificationservice.modele;

public class Pouet {

    private long idPouet;

    private Member author;
    private String title;
    private String body;

    // Constructeur vide pour les opérations de MongoDB
    public Pouet() {
    }

    // Constructeur avec tous les champs
    public Pouet(long idPouet, Member author, String title, String body) {
        this.idPouet = idPouet;
        this.author = author;
        this.title = title;
        this.body = body;
    }

    public long getIdPouet() {
        return idPouet;
    }

    public void setIdPouet(long idPouet) {
        this.idPouet = idPouet;
    }

    public Member getAuthor() {
        return author;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Pouet{" +
                "idPouet=" + idPouet +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
