package com.ensta.myfilmlist.mapper;

import java.util.List;

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Genre;
import com.ensta.myfilmlist.model.Realisateur;

import java.util.stream.Collectors;

/**
 * Effectue les conversions des Films entre les couches de l'application.
 */
public class FilmMapper {

	/**
	 * Convertit une liste de films en liste de DTO.
	 * 
	 * @param films la liste des films
	 * @return Une liste non nulle de dtos construite a partir de la liste des films.
	 */
	public static List<FilmDTO> convertFilmToFilmDTOs(List<Film> films) {
		return films.stream()
				.map(FilmMapper::convertFilmToFilmDTO)
				.collect(Collectors.toList());

	}

	/**
	 * Convertit un film en DTO.
	 * 
	 * @param film le film a convertir
	 * @return Un DTO construit a partir des donnees du film.
	 */
	public static FilmDTO convertFilmToFilmDTO(Film film) {
		FilmDTO filmDTO = new FilmDTO();
		filmDTO.setId(film.getId());
		filmDTO.setTitre(film.getTitre());
		filmDTO.setDuree(film.getDuree());
		
		RealisateurDTO real;
		try {
			real = RealisateurMapper.convertRealisateurToRealisateurDTO(film.getRealisateur());
		}catch(Exception e) {
			real = null;
		}
		filmDTO.setRealisateur(real);
		
		GenreDTO genre;
		try {
			genre = GenreMapper.convertGenreToGenreDTO(film.getGenre());
		}catch(Exception e) {
			genre = null;
		}
		filmDTO.setGenre(genre);
		
		return filmDTO;
	}

	/**
	 * Convertit un DTO en film.
	 * 
	 * @param filmDTO le DTO a convertir
	 * @return Un Film construit a partir des donnes du DTO.
	 */
	public static Film convertFilmDTOToFilm(FilmDTO filmDTO) {
		Film film = new Film();
		film.setId(filmDTO.getId());
		film.setTitre(filmDTO.getTitre());
		film.setDuree(filmDTO.getDuree());
		
		Realisateur real;
		try {
			real = RealisateurMapper.convertRealisateurDTOToRealisateur(filmDTO.getRealisateur());
		}catch(Exception e) {
			real = null;
		}
		film.setRealisateur(real);
		
		Genre genre;
		try {
			genre = GenreMapper.convertGenreDTOToGenre(filmDTO.getGenre());
		}catch(Exception e) {
			genre = null;
		}
		film.setGenre(genre);
		
		return film;
	}

	/**
	 * Convertit un Form en film.
	 * --
	 * @param filmForm le Form a convertir
	 * @return Un Film construit a partir des donnes du Form.
	 */
	public static Film convertFilmFormToFilm(FilmForm filmForm) {
		Film film = new Film();
		film.setTitre(filmForm.getTitre());
		film.setDuree(filmForm.getDuree());

		return film;
	}
}
