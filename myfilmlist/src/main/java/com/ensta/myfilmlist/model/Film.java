package com.ensta.myfilmlist.model;

/**
 * Represente un Film.
 */
public class Film {

	private long id;

	private String titre;

	private int duree;

	private Realisateur realisateur;

	private Genre genre;

	public Genre getGenre() { return genre; }
	public void setGenre(Genre genre) { this.genre = genre; }
	
	public Realisateur getRealisateur() { return this.realisateur; }
	public void setRealisateur(Realisateur realisateur) { this.realisateur = realisateur; }
	
	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	
	public String getTitre() { return titre; }
	public void setTitre(String titre) { this.titre = titre; }

	public int getDuree() {	return duree; }
	public void setDuree(int duree) { this.duree = duree; }

	@Override
	public String toString() {
		return "Film [id=" + id + ", titre=" + titre + ", duree=" + duree + ", realisateur=" + realisateur
//				+ ", genre=" + genre + "]";
		+ "]";
	}
}
