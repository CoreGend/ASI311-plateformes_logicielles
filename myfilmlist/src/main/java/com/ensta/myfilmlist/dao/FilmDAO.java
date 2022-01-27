package com.ensta.myfilmlist.dao;

import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.model.Film;

public interface FilmDAO {
	/**
	 * Renvoie la liste de tous les films dans la base de données
	 * 
	 * @param /
	 * @return la liste des films présents dans la base de données
	 */
	List<Film> findAll();
	
    /**
	 * Trouve un film d'après son identifiant
	 * 
	 * @param id l'identifiant du film recherché
	 * @return Un optional contenant le film correspondant, ou empty si le film n'existe pas dans la base
	 */
	Optional<Film> findById(long id);
	
	/**
	 * Trouve la liste des films réalisés par un réalisateur donné
	 * 
	 * @param id l'identifiant du réalisateur
	 * @return la liste des films réalisés par ce réalisateur
	 */
	List<Film> findByRealisateurId(long realisateurId);
	
    /**
	 * Trouve la liste des films d'un même genre donné
	 * 
	 * @param id l'identifiant du genre
	 * @return la liste des films ayant ce genre
	 */
	List<Film> findByGenreId(long genreId);
	
	/**
	 * Rajoute un film dans la base de données
	 * 
	 * @param f un film à ajouter
	 * @return le film ainsi créé
	 */
	Film save(Film f);
	
	/**
	* Met à jour un film dans la base de données
	* 
	* @param f le film à mettre à jour
	* @return le film mis à jour dans la base de données
	*/
	Film updateFilm(Film f);
	
    /**
	 * Supprime un film de la base de données 
	 *  
	 * @param film le film à supprimer
	 * @return () (supprime le film de la base de données)
	 */
	void delete(Film film);
}
