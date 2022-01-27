package com.ensta.myfilmlist.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * Contient les donnees pour requeter un film.
 */
public class FilmForm {

	@NotNull(message = "Le titre ne doit pas être null")
	@NotBlank(message = "Le titre ne doit pas être vide")
	@ApiModelProperty(example="Il était une fois la vie")
	private String titre;

	@NotNull(message = "La durée doit être renseignée")
	@Min(value = 1, message = "La durée doit être strictement positive")
	@ApiModelProperty(example="132")
	private int duree;

	@NotNull(message = "L'id du réalisateur doit être renseigné")
	@Min(value=1,message = "L'id du réalisateur doit être strictement positif")
	@ApiModelProperty(example="1")
	private long realisateurId;
	
	@NotNull(message= "L'id du genre doit être renseigné")
	@Min(value=1, message="L'id du genre doit être strictement positif")
	@ApiModelProperty(example="1")
	private long genreId;

	public long getGenreId() { return genreId; }
	public void setGenreId(long genreId) { this.genreId = genreId; }

	
	public String getTitre() { return titre; }
	public void setTitre(String titre) { this.titre = titre; }

	public int getDuree() { return duree; }
	public void setDuree(int duree) { this.duree = duree; }
	
	public long getRealisateurId() { return realisateurId; }
	public void setRealisateurId(long realisateurId) { this.realisateurId = realisateurId; }

}
