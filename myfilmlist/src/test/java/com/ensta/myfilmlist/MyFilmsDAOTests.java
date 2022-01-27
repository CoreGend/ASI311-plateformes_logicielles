package com.ensta.myfilmlist;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.dao.GenreDAO;
import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Genre;
import com.ensta.myfilmlist.model.Realisateur;

/**
 * Tests de la persistance de l'application MyFilms.
 */
@SpringBootTest
@Sql(scripts = { "/clean_data.sql", "/data_test.sql" })
@Transactional
class MyFilmsDAOTests {

	@Autowired
	private FilmDAO filmDAO;

	@Autowired
	private RealisateurDAO realisateurDAO;
	
	@Autowired
	private GenreDAO genreDAO;

	/** Teste la recuperation des films */
	@Test
	public void findAllFilmsTest() {
		List<Film> films = filmDAO.findAll();

		Assertions.assertEquals(4, films.size());
	}

	/** Teste la creation d'un film */
	@Test
	public void saveFilmTest() {
		Film filmToSave = getTerminatorFilm();

		Film film = filmDAO.save(filmToSave);
		long filmId = film.getId();
		Assertions.assertNotNull(film);
		Assertions.assertTrue(filmId > 0);

		Assertions.assertNotNull(filmDAO.findById(filmId));
	}

	/** Teste la mise à jour d'un film */
	@Test
	public void updateFilmTest() {
		Film filmToUpdate = getTerminatorFilm();
		filmToUpdate.setId(1);
		
		Film f = filmDAO.updateFilm(filmToUpdate);
		
		Assertions.assertNotNull(f);
		Assertions.assertEquals(f.getTitre(), filmToUpdate.getTitre());
		Assertions.assertEquals(f.getDuree(), filmToUpdate.getDuree());
		Assertions.assertEquals(f.getRealisateur().getId(), filmToUpdate.getRealisateur().getId());
		Assertions.assertEquals(f.getGenre().getId(), filmToUpdate.getGenre().getId());
	}
	
	/** Teste la recuperation d'un film par son id */
	@Test
	public void findByIdFilmTest() {
		long filmId = 1;
		Optional<Film> filmOptional = filmDAO.findById(filmId);

		Assertions.assertNotNull(filmOptional);
		Assertions.assertFalse(filmOptional.isEmpty());

		Film film = filmOptional.get();

		Assertions.assertEquals(filmId, film.getId());
		Assertions.assertTrue(film.getDuree() > 0);
		Assertions.assertNotNull(film.getRealisateur());
		Assertions.assertTrue(film.getRealisateur().getId() > 0);
		Assertions.assertNotNull(film.getRealisateur().getNom());
	}

	/** Teste la recuperation d'un film par son id qui n'existe pas */
	@Test
	public void findByIdFilmNotExisitingTest() {
		long filmId = 404;
		Optional<Film> filmOptional = filmDAO.findById(filmId);

		Assertions.assertNotNull(filmOptional);
		Assertions.assertTrue(filmOptional.isEmpty());
	}

	/** Teste la suppression d'un film */
	@Test
	public void deleteFilmTest() {
		long filmId = 1;
		Optional<Film> filmOptional = filmDAO.findById(filmId);
		
		Assertions.assertNotNull(filmOptional);
		Assertions.assertFalse(filmOptional.isEmpty());
		
		filmDAO.delete(filmOptional.get());
		
		Assertions.assertTrue(filmDAO.findById(filmId).isEmpty());
		
		// Verifier que le film existe bien avant de la supprimer
	}

	/** Teste la suppression d'un film qui n'existe pas */
	@Test
	public void deleteFilmNotExistingTest() {
		long filmId = 404;
		Film filmToDelete = new Film();
		filmToDelete.setId(filmId);
		Optional<Film> filmOptional = filmDAO.findById(filmId);

		Assertions.assertNotNull(filmOptional);
		Assertions.assertTrue(filmOptional.isEmpty());

		filmDAO.delete(filmToDelete);

		Assertions.assertTrue(filmDAO.findById(filmId).isEmpty());
	}

	/** Teste la recuperation des films par realisateurId */
	@Test
	public void findByRealisateurIdTest() {
		long realisateurId = 2;

		List<Film> films = filmDAO.findByRealisateurId(realisateurId);

		Assertions.assertNotNull(films);
		Assertions.assertEquals(3, films.size());
		for (Film film : films) {
			Assertions.assertTrue(film.getId() > 0);
			Assertions.assertNotNull(film.getRealisateur());
			Assertions.assertEquals(realisateurId, film.getRealisateur().getId());
		}
	}

	/** Teste la recuperation des film par un realisateurId qui n'existe pas */
	@Test
	public void findByRealisateurIdNotExisitingTest() {
		long realisateurId = 404;

		List<Film> films = filmDAO.findByRealisateurId(realisateurId);

		Assertions.assertNotNull(films);
		Assertions.assertTrue(films.isEmpty());
	}

	/** Teste la recuperation des films par genreId */
	@Test
	public void findByGenreIdTest() {
		long genreId = 2;

		List<Film> films = filmDAO.findByGenreId(genreId);

		Assertions.assertNotNull(films);
		Assertions.assertEquals(3, films.size());
		for (Film film : films) {
			Assertions.assertTrue(film.getId() > 0);
			Assertions.assertNotNull(film.getRealisateur());
			Assertions.assertNotNull(film.getGenre());
			Assertions.assertEquals(genreId, film.getGenre().getId());
		}
	}
	
	/** Teste la recuperation des film par un genreId qui n'existe pas */
	@Test
	public void findByGenreIdNotExisitingTest() {
		long genreId = 404;

		List<Film> films = filmDAO.findByGenreId(genreId);

		Assertions.assertNotNull(films);
		Assertions.assertTrue(films.isEmpty());
	}
	
	/** Teste la recuperation des realisateurs */
	@Test
	public void findAllRealisateursTest() {
		List<Realisateur> reals = realisateurDAO.findAll();

		Assertions.assertEquals(2, reals.size());
	}

	/** Teste la recuperation d'un realisateur par son id */
	@Test
	public void findByIdRealisateurTest() {
		long realisateurId = 1;
		Optional<Realisateur> realisateurOptional = realisateurDAO.findById(realisateurId);

		Assertions.assertNotNull(realisateurOptional);
		Assertions.assertFalse(realisateurOptional.isEmpty());

		Realisateur realisateur = realisateurOptional.get();

		Assertions.assertEquals(realisateurId, realisateur.getId());
		Assertions.assertNotNull(realisateur.getNom());
		Assertions.assertNotNull(realisateur.getPrenom());
		Assertions.assertNotNull(realisateur.getDateNaissance());
	}

	/** Teste la recuperation d'un realisateur par son id qui n'existe pas */
	@Test
	public void findByIdRealisateurNotExisitingTest() {
		long realisateurId = 404;
		Optional<Realisateur> realisateurOptional = realisateurDAO.findById(realisateurId);

		Assertions.assertNotNull(realisateurOptional);
		Assertions.assertTrue(realisateurOptional.isEmpty());
	}

	/** Teste la mise a jour d'un realisateur */
	@Test
	public void updateRealisateurTest() {
		long realisateurId = 1;
		Realisateur realisateur = realisateurDAO.findById(realisateurId).get();
		
		Assertions.assertEquals(realisateurId, realisateur.getId());
		Assertions.assertNotNull(realisateur.getNom());
		Assertions.assertNotNull(realisateur.getPrenom());
		Assertions.assertNotNull(realisateur.getDateNaissance());
		Assertions.assertFalse(realisateur.getCelebre());
		
		String nouveauNom = "Eboue";
		String nouveauPrenom = "Fabrice";
		LocalDate nouvelleDateNaissance = Date.valueOf("1973-10-20").toLocalDate();
		boolean cel = true;
		
		realisateur.setNom(nouveauNom);
		realisateur.setPrenom(nouveauPrenom);
		realisateur.setDateNaissance(nouvelleDateNaissance);
		realisateur.setCelebre(cel);
		realisateur = realisateurDAO.update(realisateur);
		Assertions.assertEquals(realisateurId, realisateur.getId());
		Assertions.assertEquals(nouveauNom, realisateur.getNom());
		Assertions.assertEquals(nouveauPrenom, realisateur.getPrenom());
		Assertions.assertEquals(nouvelleDateNaissance, realisateur.getDateNaissance());
		Assertions.assertTrue(realisateur.getCelebre());
		
		Realisateur realisateur2 = realisateurDAO.findById(realisateurId).get();
		Assertions.assertEquals(realisateurId, realisateur2.getId());
		Assertions.assertEquals(nouveauNom, realisateur2.getNom());
		Assertions.assertEquals(nouveauPrenom, realisateur2.getPrenom());
		Assertions.assertEquals(nouvelleDateNaissance, realisateur2.getDateNaissance());
		Assertions.assertTrue(realisateur2.getCelebre());
	}
	
	/** Teste la récupération d'un réalisateur par son nom et prénom */
	@Test
	public void findByNomAndPrenomRealisateurTest() {
		Realisateur realisateur = getJamesCameronRealisateur();
		Realisateur realisateurFound = realisateurDAO.findByNomAndPrenom(realisateur.getNom(), realisateur.getPrenom());
		
		Assertions.assertNotNull(realisateurFound);
		Assertions.assertEquals(realisateur.getId(), realisateurFound.getId());
		Assertions.assertEquals(realisateur.getNom(), realisateurFound.getNom());
		Assertions.assertEquals(realisateur.getPrenom(), realisateurFound.getPrenom());
		Assertions.assertEquals(realisateur.getDateNaissance(), realisateurFound.getDateNaissance());
		Assertions.assertEquals(realisateur.getCelebre(), realisateurFound.getCelebre());
	}
	
	/** Teste la récupération d'un réalisateur par son nom et prénom inexistants */
	@Test
	public void findByNomAndPrenomRealisateurNotExistingTest() {
		Realisateur realisateurFound = realisateurDAO.findByNomAndPrenom("Robert", "Pattinson");
		
		Assertions.assertNull(realisateurFound);
	}
	
	/** Teste la création d'un réalisateur */
	@Test
	public void saveRealisateurTest() {
		Realisateur realisateurToSave = getQuentinTarantinoRealisateur();
		
		Realisateur realisateur = realisateurDAO.save(realisateurToSave);
		long realisateurId = realisateur.getId();
		
		Assertions.assertNotNull(realisateur);
		Assertions.assertTrue(realisateurId>0);
		Assertions.assertTrue(realisateurDAO.findById(realisateurId).isPresent());
	}
	
	/** Teste la suppression d'un réalisateur */
	@Test
	public void deleteRealisateurTest() {
		long realId = 1;
		Optional<Realisateur> realOptional = realisateurDAO.findById(realId);
	
		Assertions.assertNotNull(realOptional);
		Assertions.assertFalse(realOptional.isEmpty());
		
		realisateurDAO.deleteReal(realOptional.get());
		
		Assertions.assertTrue(realisateurDAO.findById(realId).isEmpty());
	}
	
	/** Teste la suppression d'un réalisateur qui n'existe pas */
	@Test
	public void deleteRealNotExistingTest() {
		long realId = 404;
		Realisateur realToDelete = new Realisateur();
		realToDelete.setId(realId);
		Optional<Realisateur> realOptional = realisateurDAO.findById(realId);

		Assertions.assertNotNull(realOptional);
		Assertions.assertTrue(realOptional.isEmpty());

		realisateurDAO.deleteReal(realToDelete);

		Assertions.assertTrue(realisateurDAO.findById(realId).isEmpty());
	}
	
	/** Teste la recuperation des genres */
	@Test
	public void findAllGenresTest() {
		List<Genre> genres = genreDAO.findAll();

		Assertions.assertEquals(2, genres.size());
	}
	
	/** Teste la recuperation d'un genre par son id */
	@Test
	public void findByIdGenreTest() {
		long genreId = 1;
		Optional<Genre> genreOptional = genreDAO.findById(genreId);

		Assertions.assertNotNull(genreOptional);
		Assertions.assertFalse(genreOptional.isEmpty());

		Genre genre = genreOptional.get();

		Assertions.assertEquals(genreId, genre.getId());
		Assertions.assertNotNull(genre.getGenre());
	}

	/** Teste la recuperation d'un genre par son id qui n'existe pas */
	@Test
	public void findByIdGenreNotExisitingTest() {
		long genreId = 404;
		Optional<Genre> genreOptional = genreDAO.findById(genreId);

		Assertions.assertNotNull(genreOptional);
		Assertions.assertTrue(genreOptional.isEmpty());
	}

	/** Teste la creation d'un genre */
	@Test
	public void saveGenreTest() {
		Genre genreToSave = getActionGenre();

		Genre genre = genreDAO.save(genreToSave);
		long genreId = genre.getId();
		Assertions.assertNotNull(genre);
		Assertions.assertTrue(genreId > 0);

		Assertions.assertTrue(genreDAO.findById(genreId).isPresent());
	}

	/** Teste la mise à jour d'un genre */
	@Test
	public void updateGenreTest() {
		Genre genreToUpdate = getActionGenre();
		genreToUpdate.setId(1);
		
		Genre f = genreDAO.updateGenre(genreToUpdate);
		
		Assertions.assertNotNull(f);
		Assertions.assertEquals(f.getGenre(), genreToUpdate.getGenre());
	}
	
	
	/** Teste la suppression d'un genre */
	@Test
	public void deleteGenreTest() {
		long genreId = 1;
		Optional<Genre> genreOptional = genreDAO.findById(genreId);
		
		Assertions.assertNotNull(genreOptional);
		Assertions.assertFalse(genreOptional.isEmpty());
		
		genreDAO.delete(genreOptional.get());
		
		Assertions.assertTrue(genreDAO.findById(genreId).isEmpty());
		
		// Verifier que le film existe bien avant de la supprimer
	}

	/** Teste la suppression d'un genre qui n'existe pas */
	@Test
	public void deleteGenreNotExistingTest() {
		long genreId = 404;
		Genre genreToDelete = new Genre();
		genreToDelete.setId(genreId);
		Optional<Genre> genreOptional = genreDAO.findById(genreId);

		Assertions.assertNotNull(genreOptional);
		Assertions.assertTrue(genreOptional.isEmpty());

		genreDAO.delete(genreToDelete);

		Assertions.assertTrue(genreDAO.findById(genreId).isEmpty());
	}
	
	private Realisateur getJamesCameronRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(1);
		realisateur.setNom("Cameron");
		realisateur.setPrenom("James");
		realisateur.setDateNaissance(LocalDate.of(1954, 8, 16));
		return realisateur;
	}

	private Film getTerminatorFilm() {
		Film film = new Film();
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateur(getJamesCameronRealisateur());
		film.setGenre(getActionGenre());
		return film;
	}
	
	private Realisateur getQuentinTarantinoRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(3);
		realisateur.setNom("Tarantino");
		realisateur.setPrenom("Quentin");
		realisateur.setDateNaissance(LocalDate.of(1963, 3, 27));
		return realisateur;
	}

	private Genre getActionGenre() {
		Genre genre = new Genre();
		genre.setId(3);
		genre.setGenre("action");
		return genre;
	}
}
