package com.ensta.myfilmlist.service;

import java.util.List;

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Realisateur;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.form.GenreForm;
import com.ensta.myfilmlist.form.RealisateurForm;

public interface MyFilmsService {

	/**
	 * Met à jour le statut célèbre d'un réalisateur
	 * 
	 * @param realisateur le réalisateur qu'on veut mettre à jour - potentiellement null
	 * @return le réalisateur avec son statut célèbre mis à jour
	 * @throws ServiceException si realisateur null
	 */
    Realisateur updateRealisateurCelebre(Realisateur realisateur) 
        throws ServiceException;

	/**
	 * Met à jour le statut célèbre d'une liste de réalisateurs
	 * 
	 * @param l une liste de réalisateurs
	 * @return la liste des réalisateurs avec leur statut de célébrité mis à jour
	 */
    List<Realisateur> updateRealisateurCelebres(List<Realisateur> l);
    
    /**
	 * Calcule la durée totale d'une liste de films
	 * 
	 * @param l une liste de films supposés non null
	 * @return la somme de leur durée
	 */
    int calculerDureeTotale(List<Film> l);

	/**
	 * Calcule la moyenne d'une liste de de notes
	 * 
	 * @param d une liste de double
	 * @return la moyenne de cette liste
	 */
    double calculerNoteMoyenne(double[] d);

/* CRUD FILM */

	/**
	 * Renvoie la liste de tous les films dans la base de données
	 * 
	 * @param /
	 * @return la liste des films présents dans la base de données
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    List<FilmDTO> findAllFilms() throws ServiceException;
    
    /**
	 * Trouve un film d'après son identifiant
	 * 
	 * @param id l'identifiant du film recherché
	 * @return le film correspondant, ou null si le film n'existe pas dans la base
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    FilmDTO findFilmById(long id) throws ServiceException;
    
	/**
	 * Trouve la liste des films réalisés par un réalisateur donné
	 * 
	 * @param id l'identifiant du réalisateur
	 * @return la liste des films réalisés par ce réalisateur
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    List<FilmDTO> findFilmByRealisateurId(long id) throws ServiceException;
    
    /**
	 * Trouve la liste des films d'un même genre donné
	 * 
	 * @param id l'identifiant du genre
	 * @return la liste des films ayant ce genre
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
	List<FilmDTO> findFilmByGenreId(long genreId) throws ServiceException;
	
	/**
	 * Crée un film et le rajoute dans la base de données
	 * 
	 * @param f un FilmForm pour la création du film
	 * @return le film ainsi créé
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    FilmDTO createFilm(FilmForm f) throws ServiceException;
    
    /**
	 * Met à jour un film dans la base de données
	 * 
	 * @param f le formulaire de mise à jour du film et id son identifiant
	 * @return le film mis à jour dans la base de données
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    FilmDTO updateFilm(FilmForm f, long id) throws ServiceException;
    
    /**
	 * Supprime un film de la base de données et met à jour le statut
	 * célèbre du réalisateur correspondant
	 * 
	 * @param id l'identifiant du film à supprimer
	 * @return () (supprime le film de la base de données)
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    void deleteFilm(long id) throws ServiceException;
    
/* CRUD REALISATEUR */
    
	/**
	 * Renvoie la liste de tous les réalisateurs dans la base de données
	 * 
	 * @param /
	 * @return la liste des réalisateurs présents dans la base de données
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    List<RealisateurDTO> findAllRealisateurs() throws ServiceException;
    
    /**
	 * Trouve un réalisateur à partir de son nom et de son prénom
	 * 
	 * @param nom et prénom deux chaînes de caractères représentant le nom et le prénom du réalisateur recherché
	 * @return le premier réalisateur trouvé répondant aux exigences
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    RealisateurDTO findRealisateurByNomAndPrenom(String nom, String prenom) throws ServiceException;

	/**
	 * Trouve un réalisateur d'après son identifiant
	 * 
	 * @param id l'identifiant du réalisateur recherché
	 * @return le réalisateur correspondant, ou null si le réalisateur n'existe pas dans la base
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    RealisateurDTO findRealById(long id) throws ServiceException;

	/**
	 * Crée un réalisateur et le rajoute dans la base de données
	 * 
	 * @param r un RealisateurForm pour la création du réalisateur
	 * @return le réalisateur ainsi créé
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    RealisateurDTO createRealisateur(RealisateurForm r) throws ServiceException;

	/**
	 * Met à jour le statut célèbre d'un réalisateur dans la base de données
	 * 
	 * @param id l'identifiant du réalisateur à mettre à jour
	 * @return le réalisateur mis à jour
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    RealisateurDTO updateRealisateur(long id) throws ServiceException;
    
	/**
	 * Met à jour un réalisateur dans la base de données
	 * 
	 * @param f le formulaire de mise à jour du réalisateur et id son identifiant
	 * @return le réalisateur mis à jour dans la base de données
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    RealisateurDTO updateReal(RealisateurForm f, long id) throws ServiceException ;
    
	/**
	 * Supprime un réalisateur de la base de données
	 * 
	 * @param id l'identifiant du réalisateur à supprimer
	 * @return () (supprime le réalisateur de la base de données)
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    void deleteReal(long id) throws ServiceException;

/* CRUD GENRE */
    
	/**
	 * Renvoie la liste de tous les genres dans la base de données
	 * 
	 * @param /
	 * @return la liste des genres présents dans la base de données
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
    List<GenreDTO> findAllGenres() throws ServiceException;

	/**
	 * Trouve un genre d'après son identifiant
	 * 
	 * @param id l'identifiant du genre recherché
	 * @return le genre correspondant, ou null si le genre n'existe pas dans la base
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */	
	GenreDTO findGenreById(long id) throws ServiceException;
	
	/**
	 * Crée un genre et le rajoute dans la base de données
	 * 
	 * @param g un GenreForm pour la création du genre
	 * @return le genre ainsi créé
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
	GenreDTO createGenre(GenreForm g) throws ServiceException;
	
	/**
	 * Met à jour un genre dans la base de données
	 * 
	 * @param g le formulaire de mise à jour du genre et id son identifiant
	 * @return le genre mis à jour dans la base de données
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
	GenreDTO updateGenre(GenreForm g, long id) throws ServiceException;
	
	/**
	 * Supprime un genre de la base de données
	 * 
	 * @param id l'identifiant du genre à supprimer
	 * @return () (supprime le genre de la base de données)
	 * @throws ServiceException si une erreur d'accès à la BDD survient
	 */
	void deleteGenre(long id) throws ServiceException;
}
