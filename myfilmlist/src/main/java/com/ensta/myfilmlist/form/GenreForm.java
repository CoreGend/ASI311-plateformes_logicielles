package com.ensta.myfilmlist.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * Contient les données pour requêter un genre
 */
public class GenreForm {
	@NotNull(message="Le genre ne doit pas être null")
	@NotBlank(message="Le genre ne doit pas être vide")
	@ApiModelProperty(example="Drame")
	private String genre;

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
}
