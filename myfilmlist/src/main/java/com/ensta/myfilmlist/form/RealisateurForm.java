package com.ensta.myfilmlist.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * Contient les donnees pour requeter un realisateur.
 */
public class RealisateurForm {
	@NotNull(message="Le nom doit être renseigné")
	@NotBlank(message="Le nom doit contenir des lettres")
	@ApiModelProperty(example="Tarantino")
	private String nom;
	
	@NotNull(message="Le nom doit être renseigné")
	@NotBlank(message="Le nom doit contenir des lettres")
	@ApiModelProperty(example="Quentin")
	private String prenom;
	
	@NotNull(message="La date de naissance doit être renseignée (format YYYY/MM/DD)")
	@NotBlank(message="La date de naissance doit être renseignée (format YYYY/MM/DD)")
	@ApiModelProperty(example="1966-08-22")
	private String dateNaissance;

	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	
	public String getPrenom() { return prenom; }
	public void setPrenom(String prenom) { this.prenom = prenom; }
	
	public String getDateNaissance() { return dateNaissance; }
	public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
}
