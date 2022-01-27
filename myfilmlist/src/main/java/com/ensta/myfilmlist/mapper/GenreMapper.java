package com.ensta.myfilmlist.mapper;

import com.ensta.myfilmlist.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.form.GenreForm;

/**
 * Effectue les conversions des Genres entre les couches de l'application.
 */
public class GenreMapper {
	
	/**
	 * Convertit une liste de genres en liste de DTO.
	 * 
	 * @param genres la liste des genres
	 * @return Une liste non nulle de dtos construite à partir de la liste des genres.
	 */
	public static List<GenreDTO> convertGenreToGenreDTOs(List<Genre> genres){
		return genres.stream()
				.map(GenreMapper::convertGenreToGenreDTO)
				.collect(Collectors.toList());
	}
	
	/**
	 * Convertit un genre en DTO.
	 * 
	 * @param genre le genre à convertir
	 * @return Un DTO construit à partir des données du genre.
	 */
	public static GenreDTO convertGenreToGenreDTO(Genre genre) {
		GenreDTO genreDTO = new GenreDTO();
		genreDTO.setId(genre.getId());
		genreDTO.setGenre(genre.getGenre());
		return genreDTO;
	}
	
	/**
	 * Convertit un DTO en Genre.
	 * 
	 * @param genreDTO le DTO à convertir
	 * @return Un Genre construit à partir des données du DTO.
	 */
	public static Genre convertGenreDTOToGenre(GenreDTO genreDTO) {
		Genre genre = new Genre();
		genre.setId(genreDTO.getId());
		genre.setGenre(genreDTO.getGenre());
		return genre;
	}
	
	/**
	 * Convertit un Form en genre
	 * 
	 * @param genreForm le form à convertir
	 * @return Un Genre construit à partir des données du Form
	 */
	public static Genre convertGenreFormToGenre(GenreForm genreForm) {
		Genre genre = new Genre();
		genre.setGenre(genreForm.getGenre());
		return genre;
	}
}
