package com.ensta.myfilmlist.model;

public class Utilisateur {
    private long id;
    private String nom;
    private String prenom;

    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }

    public String getNom() { return this.nom; }
    public void setNom(String nom) {this.nom = nom;}
    
    public String getPrenom() {return this.prenom;}
    public void setPrenom(String prenom) {this.prenom = prenom;}

}
