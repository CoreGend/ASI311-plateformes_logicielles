package com.ensta.myfilmlist.model;

import java.time.LocalDate;
import java.util.List;

public class Realisateur{
    private static final int NB_FILM_CELEBRE = 3;
	private long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private List<Film> filmRealises;
    private boolean celebre;

    /**
     * Getters
     */
    public long getId(){ return id; }
    public String getNom(){ return nom; }
    public String getPrenom(){ return prenom; }
    public LocalDate getDateNaissance(){ return dateNaissance; }
    public List<Film> getFilmRealises(){ return filmRealises; }
    public boolean getCelebre() { return celebre; }
    public void isCelebre(){ 
    	if (filmRealises == null) {
    		this.celebre = false;
    	}
    	this.celebre = (filmRealises.size() >= NB_FILM_CELEBRE);
    }

    /**
     * Setters
     */
    public void setId(long id){ this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom){this.prenom=prenom;}
    public void setDateNaissance(LocalDate dateNaissance){this.dateNaissance=dateNaissance;}
    public void setFilmRealises(List<Film> filmRealises){this.filmRealises = filmRealises;}
    public void setCelebre(boolean celebre){this.celebre = celebre;}
	@Override
	public String toString() {
		return "Realisateur [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", dateNaissance=" + dateNaissance
				+ ", celebre=" + celebre + "]";
//		", filmRealises=" + filmRealises + 
	}
    
    
}