package com.ensta.myfilmlist.dao;

import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.model.Genre;

public interface GenreDAO {
	/**
	 * Renvoie la liste de tous les genres dans la base de données
	 * 
	 * @param /
	 * @return la liste des genres présents dans la base de données
	 */
	List<Genre> findAll();
	
	/**
	 * Trouve un genre d'après son identifiant
	 * 
	 * @param id l'identifiant du genre recherché
	 * @return Un optional contenant le genre correspondant, ou empty s'il n'existe pas dans la base
	 */	
	Optional<Genre> findById(long id);
	
	/**
	 * Ajoute un genre dans la base de données
	 * 
	 * @param g le genre à ajouter
	 * @return le genre ainsi créé
	 */
	Genre save(Genre g);
	
	/**
	 * Met à jour un genre dans la base de données
	 * 
	 * @param g genre à mettre à jour
	 * @return le genre mis à jour dans la base de données
	 */
	Genre updateGenre(Genre g);

	/**
	 * Supprime un genre de la base de données
	 * 
	 * @param genre le genre à supprimer
	 * @return () (supprime le genre de la base de données)
	 */
	void delete(Genre genre);
}