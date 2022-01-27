package com.ensta.myfilmlist.dao;

import java.util.List;
import java.util.Optional;

import com.ensta.myfilmlist.model.Realisateur;

public interface RealisateurDAO {
	/**
	 * Renvoie la liste de tous les réalisateurs dans la base de données
	 * 
	 * @param /
	 * @return la liste des réalisateurs présents dans la base de données
	 */
	List<Realisateur> findAll();
	
	/**
	 * Trouve un réalisateur à partir de son nom et de son prénom
	 * 
	 * @param nom et prénom deux chaînes de caractères représentant le nom et le prénom du réalisateur recherché
	 * @return le premier réalisateur trouvé répondant aux exigences
	 */
	Realisateur findByNomAndPrenom(String nom, String prenom);
	
	/**
	 * Trouve un réalisateur d'après son identifiant
	 * 
	 * @param id l'identifiant du réalisateur recherché
	 * @return Un optional contenant le réalisateur correspondant, ou empty s'il n'existe pas dans la base
	 */
	Optional<Realisateur> findById(long id);
	
	/**
	 * Rajoute un réalisateur dans la base de données
	 * 
	 * @param r le réalisateur à rajouter
	 * @return le réalisateur ainsi créé
	 */
	Realisateur save(Realisateur r);
	
	/**
	 * Met à jour un réalisateur dans la base de données
	 * 
	 * @param realisateur le réalisateur à mettre à jour
	 * @return le réalisateur mis à jour dans la base de données
	 */
	Realisateur update(Realisateur realisateur);
		
	/**
	 * Supprime un réalisateur de la base de données
	 * 
	 * @param r le réalisateur à supprimer
	 * @return () (supprime le réalisateur de la base de données)
	 */
	void deleteReal(Realisateur r);
}
