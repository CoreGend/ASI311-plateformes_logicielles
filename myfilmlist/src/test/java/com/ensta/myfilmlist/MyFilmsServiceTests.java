package com.ensta.myfilmlist;

import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.ensta.myfilmlist.dao.FilmDAO;
import com.ensta.myfilmlist.dao.GenreDAO;
import com.ensta.myfilmlist.dao.RealisateurDAO;
import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.form.GenreForm;
import com.ensta.myfilmlist.form.RealisateurForm;
import com.ensta.myfilmlist.model.Film;
import com.ensta.myfilmlist.model.Genre;
import com.ensta.myfilmlist.model.Realisateur;
import com.ensta.myfilmlist.service.MyFilmsService;
import com.ensta.myfilmlist.service.impl.MyFilmsServiceImpl;

/**
 * Tests des services de l'application MyFilms.
 */
@ExtendWith(MockitoExtension.class)
class MyFilmsServiceTests {

	/** Service MyFilms contenant des Mocks de FilmDAO et RealisateurDAO */
	@InjectMocks
	private MyFilmsService myFilmsServiceMock = new MyFilmsServiceImpl();

	@Mock
	private FilmDAO filmDAOMock;

	@Mock
	private RealisateurDAO realisateurDAOMock;
	
	@Mock
	private GenreDAO genreDAOMock;

	/** Teste le calcul de la duree totale des films */
	@Test
	public void calculerDureeTotaleTest() {
		List<Film> films = List.of(getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(), getLeRetourDuRoiFilm());
		
		int duree = myFilmsServiceMock.calculerDureeTotale(films);
		
		Assertions.assertEquals(558, duree);
	}

	/** Teste le calcul de la duree totale d'une liste vide de films */
	@Test
	public void calculerDureeTotaleEmptyListTest() {
		List<Film> films = Collections.emptyList();
		
		int duree = myFilmsServiceMock.calculerDureeTotale(films);
		
		Assertions.assertEquals(0, duree);	
	}

	/** Teste le calcul de la note moyenne */
	@Test
	public void calculerNoteMoyenneTest() {
		double[] notes = { 18, 15.5, 12 };

		double noteMoyenne = myFilmsServiceMock.calculerNoteMoyenne(notes);

		Assertions.assertEquals(15.17, noteMoyenne);
	}

	/** Teste le calcul de la note moyenne d'une liste vide */
	@Test
	public void calculerNoteMoyenneNoNoteTest() {
		double[] notes = {};

		double noteMoyenne = myFilmsServiceMock.calculerNoteMoyenne(notes);

		Assertions.assertEquals(0, noteMoyenne);
	}

	/** Teste la recuperation des films */
	@Test
	public void findAllFilmsTest() throws ServiceException {
		List<Film> mockFilms = List.of(getAvatarFilm(), getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(), getLeRetourDuRoiFilm());
		Mockito.when(filmDAOMock.findAll()).thenReturn(mockFilms);

		List<FilmDTO> films = myFilmsServiceMock.findAllFilms();
		Mockito.verify(filmDAOMock).findAll();

		Assertions.assertNotNull(films);
		Assertions.assertEquals(4, films.size());

		for (FilmDTO film : films) {
			Assertions.assertNotNull(film);
			Assertions.assertTrue(film.getId() > 0);
			Assertions.assertTrue(film.getDuree() > 0);
			Assertions.assertNotNull(film.getRealisateur());
			Assertions.assertTrue(film.getRealisateur().getId() > 0);
			Assertions.assertNotNull(film.getRealisateur().getNom());
		}
	}

	/** Teste le cas ou le DAO remonte une liste vide */
	@Test
	public void findAllFilmsEmptyTest() throws ServiceException {
		Mockito.when(filmDAOMock.findAll()).thenReturn(new ArrayList<Film>());

		List<FilmDTO> films = myFilmsServiceMock.findAllFilms();
		Mockito.verify(filmDAOMock).findAll();

		Assertions.assertNotNull(films);
		Assertions.assertEquals(0, films.size());
	}

	/** Teste le cas ou le DAO remonte une exception */
	@Test
	public void findAllFilmsExceptionTest() throws ServiceException {
		Mockito.when(filmDAOMock.findAll()).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findAllFilms();
		});
		Mockito.verify(filmDAOMock).findAll();
	}

	/** Teste la creation d'un film */
	@Test
	public void createFilmTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Film expectedFilmMock = getTerminatorFilm();
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.of(expectedFilmMock.getRealisateur()));
		Mockito.when(genreDAOMock.findById(filmFormMock.getGenreId())).thenReturn(Optional.of(getScienceFictionGenre()));
		Mockito.when(filmDAOMock.save(Mockito.any(Film.class))).thenReturn(expectedFilmMock);
		Mockito.when(filmDAOMock.findByRealisateurId(filmFormMock.getRealisateurId())).thenReturn(List.of(getAvatarFilm()));
		// On renvoie le meme realisateur que celui passe en argument : son statut celebre a du etre mis a jour
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

		FilmDTO filmDTO = myFilmsServiceMock.createFilm(filmFormMock);
		Mockito.verify(realisateurDAOMock, Mockito.atLeastOnce()).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock).save(Mockito.any(Film.class));

		Assertions.assertNotNull(filmDTO);
		Assertions.assertEquals(expectedFilmMock.getId(), filmDTO.getId());
		Assertions.assertEquals(expectedFilmMock.getDuree(), filmDTO.getDuree());
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getNom(), filmDTO.getRealisateur().getNom());
		Assertions.assertFalse(filmDTO.getRealisateur().isCelebre());
	}

	/** Teste la creation d'un film sans realisateur */
	@Test
	public void createFilmNoRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		filmFormMock.setRealisateurId(0);

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.createFilm(filmFormMock);
		});
		Mockito.verify(filmDAOMock, never()).save(Mockito.any(Film.class));
	}

	/** Teste la creation d'un film avec un realisateur qui n'existe pas */
	@Test
	public void createFilmNotExistingRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		filmFormMock.setRealisateurId(404);
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.empty());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.createFilm(filmFormMock);
		});
		Mockito.verify(realisateurDAOMock).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock, never()).save(Mockito.any(Film.class));
	}

	/** Teste la creation d'un film qui met a jour le statut celebre d'un realisateur */
	@Test
	public void createFilmStatutCelebreRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Film expectedFilmMock = getTerminatorFilm();
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.of(expectedFilmMock.getRealisateur()));
		Mockito.when(genreDAOMock.findById(filmFormMock.getGenreId())).thenReturn(Optional.of(getScienceFictionGenre()));
		Mockito.when(filmDAOMock.save(Mockito.any(Film.class))).thenReturn(expectedFilmMock);
		Mockito.when(filmDAOMock.findByRealisateurId(filmFormMock.getRealisateurId())).thenReturn(List.of(getAvatarFilm(), getTitanicFilm(), getTerminatorFilm()));
		// On renvoie le meme realisateur que celui passe en argument : son statut celebre a du etre mis a jour
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

		FilmDTO filmDTO = myFilmsServiceMock.createFilm(filmFormMock);
		Mockito.verify(realisateurDAOMock, Mockito.atLeastOnce()).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock).save(Mockito.any(Film.class));
		Mockito.verify(realisateurDAOMock).update(Mockito.any(Realisateur.class));

		Assertions.assertNotNull(filmDTO);
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertTrue(filmDTO.getRealisateur().isCelebre());
		
//		Assertions.fail("Corriger le contenu de ce test : ");
	}

	/** Teste la creation d'un film qui renvoie une exception */
	@Test
	public void createFilmExceptionTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.of(getJamesCameronRealisateur()));
		Mockito.when(genreDAOMock.findById(filmFormMock.getGenreId())).thenReturn(Optional.of(getScienceFictionGenre()));
		Mockito.when(filmDAOMock.save(Mockito.any(Film.class))).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.createFilm(filmFormMock);
		});
		Mockito.verify(filmDAOMock).save(Mockito.any(Film.class));
//		Assertions.fail("Corriger le contenu de ce test");
	}

	/** Teste la mise à jour d'un film */
	@Test
	public void updateFilmTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Film expectedFilmMock = getTerminatorFilm();
		expectedFilmMock.setId(1);
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.of(expectedFilmMock.getRealisateur()));
		Mockito.when(genreDAOMock.findById(filmFormMock.getGenreId())).thenReturn(Optional.of(getScienceFictionGenre()));
		Mockito.when(filmDAOMock.findByRealisateurId(filmFormMock.getRealisateurId())).thenReturn(List.of(getAvatarFilm()));
		Mockito.when(filmDAOMock.updateFilm(Mockito.any(Film.class))).thenReturn(expectedFilmMock);
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
				
		FilmDTO filmDTO = myFilmsServiceMock.updateFilm(filmFormMock, 1);
		Mockito.verify(realisateurDAOMock, Mockito.atLeastOnce()).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock).updateFilm(Mockito.any(Film.class));
		
		Assertions.assertNotNull(filmDTO);
		Assertions.assertEquals(expectedFilmMock.getId(), filmDTO.getId());
		Assertions.assertEquals(expectedFilmMock.getDuree(), filmDTO.getDuree());
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getNom(), filmDTO.getRealisateur().getNom());
		Assertions.assertFalse(filmDTO.getRealisateur().isCelebre());
	}
	
	/** Teste la mise à jour d'un film sans realisateur */
	@Test
	public void updateFilmNoRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		filmFormMock.setRealisateurId(0);

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.updateFilm(filmFormMock, 1);
		});
		Mockito.verify(filmDAOMock, never()).updateFilm(Mockito.any(Film.class));
	}
	
	/** Teste la mise à jour d'un film avec un realisateur qui n'existe pas */
	@Test
	public void updateFilmNotExistingRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		filmFormMock.setRealisateurId(404);
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.empty());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.updateFilm(filmFormMock, 1);
		});
		Mockito.verify(realisateurDAOMock).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock, never()).updateFilm(Mockito.any(Film.class));
	}

	/** Teste la mise à jour d'un film qui met a jour le statut celebre d'un realisateur */
	@Test
	public void updateFilmStatutCelebreRealisateurTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Film expectedFilmMock = getTerminatorFilm();
		expectedFilmMock.setId(2);
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.of(expectedFilmMock.getRealisateur()));
		Mockito.when(genreDAOMock.findById(filmFormMock.getGenreId())).thenReturn(Optional.of(getScienceFictionGenre()));
		Mockito.when(filmDAOMock.findByRealisateurId(filmFormMock.getRealisateurId())).thenReturn(List.of(getAvatarFilm(), getTitanicFilm(), getTerminatorFilm()));
		Mockito.when(filmDAOMock.updateFilm(Mockito.any(Film.class))).thenReturn(expectedFilmMock);

		// On renvoie le meme realisateur que celui passe en argument : son statut celebre a du etre mis a jour
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

		FilmDTO filmDTO = myFilmsServiceMock.updateFilm(filmFormMock, 2);
		Mockito.verify(realisateurDAOMock, Mockito.atLeastOnce()).findById(filmFormMock.getRealisateurId());
		Mockito.verify(filmDAOMock).updateFilm(Mockito.any(Film.class));
		Mockito.verify(realisateurDAOMock).update(Mockito.any(Realisateur.class));
		
		Assertions.assertNotNull(filmDTO);
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertTrue(filmDTO.getRealisateur().isCelebre());
	}
	
	/** Teste la mise à jour d'un film qui renvoie une exception */
	@Test
	public void updateFilmExceptionTest() throws ServiceException {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Mockito.when(realisateurDAOMock.findById(filmFormMock.getRealisateurId())).thenReturn(Optional.of(getJamesCameronRealisateur()));
		Mockito.when(genreDAOMock.findById(filmFormMock.getGenreId())).thenReturn(Optional.of(getScienceFictionGenre()));
		Mockito.doThrow(getDataAccessException()).when(filmDAOMock).updateFilm(Mockito.any(Film.class));

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.updateFilm(filmFormMock, 1);
		});
		Mockito.verify(filmDAOMock).updateFilm(Mockito.any(Film.class));
	}
	
	/** Teste la recuperation d'un film a partir de son id */
	@Test
	public void findFilmByIdTest() throws ServiceException {
		long filmId = 1;
		Film expectedFilmMock = getAvatarFilm();
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.of(expectedFilmMock));

		FilmDTO filmDTO = myFilmsServiceMock.findFilmById(filmId);
		Mockito.verify(filmDAOMock).findById(filmId);

		Assertions.assertNotNull(filmDTO);
		Assertions.assertEquals(expectedFilmMock.getId(), filmDTO.getId());
		Assertions.assertEquals(expectedFilmMock.getDuree(), filmDTO.getDuree());
		Assertions.assertNotNull(filmDTO.getRealisateur());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getId(), filmDTO.getRealisateur().getId());
		Assertions.assertEquals(expectedFilmMock.getRealisateur().getNom(), filmDTO.getRealisateur().getNom());
	}

	/** Teste la recuperation d'un film qui n'existe pas */
	@Test
	public void findFilmByIdNotExisitingTest() throws ServiceException {
		long filmId = 404;
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.empty());
		
		FilmDTO filmDTO = myFilmsServiceMock.findFilmById(filmId);
		Mockito.verify(filmDAOMock).findById(filmId);
		
		Assertions.assertNull(filmDTO);		
	}

	/** Teste la recuperation d'un film qui renvoie une exception */
	@Test
	public void findFilmByIdExceptionTest() throws ServiceException {
		long filmId = 1;
		Mockito.when(filmDAOMock.findById(filmId)).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findFilmById(filmId);
		});
		Mockito.verify(filmDAOMock).findById(filmId);
	}

	/** Teste la suppression d'un film */
	@Test
	public void deleteFilmTest() throws ServiceException {
		long filmId = 1;
		Film filmMock = getAvatarFilm();
		Realisateur realisateur = filmMock.getRealisateur();
		realisateur.setCelebre(true);
		
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.of(filmMock));
		Mockito.when(filmDAOMock.findByRealisateurId(filmMock.getRealisateur().getId())).thenReturn(List.of(getTitanicFilm(), getTerminatorFilm()));
		// On renvoie le meme realisateur que celui passe en argument : son statut celebre a du etre mis a jour
		Mockito.when(realisateurDAOMock.update(Mockito.any(Realisateur.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

		myFilmsServiceMock.deleteFilm(filmId);
		Mockito.verify(filmDAOMock).delete(filmMock);
		Mockito.verify(realisateurDAOMock).update(Mockito.any(Realisateur.class));

		Assertions.assertFalse(realisateur.getCelebre());
	}

	/** Teste la suppression d'un film qui n'existe pas */
	@Test
	public void deleteFilmNotExisitingTest() throws ServiceException {
		long filmId = 404;
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.empty());

		myFilmsServiceMock.deleteFilm(filmId);

		Mockito.verify(filmDAOMock, never()).delete(Mockito.any(Film.class));
	}

	/** Teste la suppression d'un film qui renvoie une exception */
	@Test
	public void deleteFilmExceptionTest() throws ServiceException {
		long filmId = 1;
		Film filmMock = getAvatarFilm();
		Mockito.when(filmDAOMock.findById(filmId)).thenReturn(Optional.of(filmMock));
		// La methode delete() renvoie void, on simule le fait qu'elle leve une exception
		Mockito.doThrow(getDataAccessException()).when(filmDAOMock).delete(filmMock);

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.deleteFilm(filmId);
		});
//		Mockito.verify(filmDAOMock).delete(filmMock);
//		Assertions.fail("Corriger le contenu de ce test");
	}

	/** Teste la recuperation des realisateurs */
	@Test
	public void findAllRealisateurTest() throws ServiceException {
		// liste des réalisateurs que devrait renvoyer findAll
		List<Realisateur> realisateurMocks = List.of(getJamesCameronRealisateur(), getPeterJacksonRealisateur());
		
		// quand on appelle le DAO pour findAll, renvoie la liste fournie
		Mockito.when(realisateurDAOMock.findAll()).thenReturn(realisateurMocks);
		
		// on appelle le service mock comme on le ferait
		List<RealisateurDTO> realisateurs = myFilmsServiceMock.findAllRealisateurs();
		
		// on vérifie que la méthode findAll a été appelée sur realisateurDAOMock
		Mockito.verify(realisateurDAOMock).findAll();
		
		// on vérifie que realisateurs n'est pas null
		Assertions.assertNotNull(realisateurs);
		Assertions.assertEquals(2, realisateurs.size());
		
		for (RealisateurDTO real: realisateurs) {
			Assertions.assertNotNull(real);
			Assertions.assertNotNull(real.getNom());
			Assertions.assertNotNull(real.getPrenom());
			Assertions.assertTrue(real.getId() > 0);
			Assertions.assertNotNull(real.getDateNaissance());
			Assertions.assertNotNull(real.isCelebre());
		}
	}

	/** Teste le cas ou le DAO remonte une liste vide */
	@Test
	public void findAllRealisateurEmptyTest() throws ServiceException {
		Mockito.when(realisateurDAOMock.findAll()).thenReturn(new ArrayList<Realisateur>());

		List<RealisateurDTO> realisateurs = myFilmsServiceMock.findAllRealisateurs();
		Mockito.verify(realisateurDAOMock).findAll();

		Assertions.assertNotNull(realisateurs);
		Assertions.assertEquals(0, realisateurs.size());
	}

	/** Teste le cas ou le DAO remonte une exception */
	@Test
	public void findAllRealisateurExceptionTest() throws ServiceException {
		Mockito.when(realisateurDAOMock.findAll()).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findAllRealisateurs();
		});
		Mockito.verify(realisateurDAOMock).findAll();
	}
	
	/** Teste la récupération d'un réalisateur à partir de son nom et prénom */
	@Test
	public void findRealisateurByNomAndPrenomTest() 
			throws ServiceException {
		Realisateur expectedRealMock = getJamesCameronRealisateur();
		String expectedNom = expectedRealMock.getNom();
		String expectedPrenom = expectedRealMock.getPrenom() ;
		Mockito	.when(realisateurDAOMock
					.findByNomAndPrenom(expectedNom, expectedPrenom))
				.thenReturn(expectedRealMock);
		
		RealisateurDTO realDTO = myFilmsServiceMock.findRealisateurByNomAndPrenom(expectedNom, expectedPrenom);
		Mockito.verify(realisateurDAOMock).findByNomAndPrenom(expectedNom, expectedPrenom);
		
		Assertions.assertNotNull(realDTO);
		Assertions.assertEquals(expectedRealMock.getId(), realDTO.getId());
		Assertions.assertEquals(expectedPrenom, realDTO.getPrenom());
		Assertions.assertEquals(expectedNom, realDTO.getNom());
		Assertions.assertEquals(expectedRealMock.getDateNaissance(), realDTO.getDateNaissance());
		Assertions.assertEquals(expectedRealMock.getCelebre(), realDTO.isCelebre());
	}

	/** Teste la récupération d'un réalisateur sans nom */
	@Test
	public void findRealisateurByNomAndPrenomSansNomTest() throws ServiceException {
		String expectedPrenom = "James" ;
		Mockito.when(realisateurDAOMock.findByNomAndPrenom("", expectedPrenom)).thenReturn(null);
		
		RealisateurDTO realDTO = myFilmsServiceMock.findRealisateurByNomAndPrenom("", expectedPrenom);
		Mockito.verify(realisateurDAOMock).findByNomAndPrenom("", expectedPrenom);
		
		Assertions.assertNull(realDTO);
	}
	
	/** Teste la récupération d'un réalisateur sans prenom */
	@Test
	public void findRealisateurByNomAndPrenomSansPrenomTest() throws ServiceException {
		String expectedNom = "Cameron" ;
		Mockito.when(realisateurDAOMock.findByNomAndPrenom(expectedNom, "")).thenReturn(null);
		
		RealisateurDTO realDTO = myFilmsServiceMock.findRealisateurByNomAndPrenom(expectedNom,"");
		Mockito.verify(realisateurDAOMock).findByNomAndPrenom(expectedNom,"");
		
		Assertions.assertNull(realDTO);
	}
	
	/** Teste la récupération d'un réalisateur avec un mauvais nom et prenom */
	@Test
	public void findRealisateurByNomAndPrenomNotExistingTest() throws ServiceException {
		String expectedPrenom = "Quentin";
		String expectedNom = "Tarantino" ;
		Mockito.when(realisateurDAOMock.findByNomAndPrenom(expectedNom, expectedPrenom)).thenReturn(null);
		
		RealisateurDTO realDTO = myFilmsServiceMock.findRealisateurByNomAndPrenom(expectedNom,expectedPrenom);
		Mockito.verify(realisateurDAOMock).findByNomAndPrenom(expectedNom,expectedPrenom);
		
		Assertions.assertNull(realDTO);
	}

	/** Teste la récupération d'un réalisateur qui renvoie une exception */
	@Test
	public void findRealisateurByNomAndPrenomExceptionTest() throws ServiceException{
		String realNom = "Cameron";
		String realPrenom = "James";
		Mockito.when(realisateurDAOMock.findByNomAndPrenom(realNom, realPrenom)).thenThrow(getDataAccessException());
		
		Assertions.assertThrows(ServiceException.class, ()->{
			myFilmsServiceMock.findRealisateurByNomAndPrenom(realNom, realPrenom);
		});
		Mockito.verify(realisateurDAOMock).findByNomAndPrenom(realNom, realPrenom);
	}
	
	/** Teste la récupération d'un réalisateur à partir de son id */
	@Test
	public void findRealisateurByIdTest() throws ServiceException {
		long realId = 1;
		Realisateur expectedRealMock = getJamesCameronRealisateur();
		String expectedNom = expectedRealMock.getNom();
		String expectedPrenom = expectedRealMock.getPrenom() ;

		Mockito.when(realisateurDAOMock.findById(realId)).thenReturn(Optional.of(expectedRealMock));
		
		RealisateurDTO realDTO = myFilmsServiceMock.findRealById(realId);
		Mockito.verify(realisateurDAOMock).findById(realId);
		
		Assertions.assertNotNull(realDTO);
		Assertions.assertEquals(expectedRealMock.getId(), realDTO.getId());
		Assertions.assertEquals(expectedPrenom, realDTO.getPrenom());
		Assertions.assertEquals(expectedNom, realDTO.getNom());
		Assertions.assertEquals(expectedRealMock.getDateNaissance(), realDTO.getDateNaissance());
		Assertions.assertEquals(expectedRealMock.getCelebre(), realDTO.isCelebre());
	
	}

	/** Teste la recuperation d'un film qui n'existe pas */
	@Test
	public void findRealisateurByIdNotExisitingTest() throws ServiceException {
		long realId = 404;
		Mockito.when(realisateurDAOMock.findById(realId)).thenReturn(Optional.empty());
		
		RealisateurDTO realDTO = myFilmsServiceMock.findRealById(realId);
		Mockito.verify(realisateurDAOMock).findById(realId);
		
		Assertions.assertNull(realDTO);		
	}

	/** Teste la recuperation d'un film qui renvoie une exception */
	@Test
	public void findRealisateurByIdExceptionTest() throws ServiceException {
		long realId = 1;
		Mockito.when(realisateurDAOMock.findById(realId)).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findRealById(realId);
		});
		Mockito.verify(realisateurDAOMock).findById(realId);
	}
	
	/** Teste la récupération des films par l'ID du réalisateur */
	@Test
	public void findFilmByRealisateurIdTest() throws ServiceException{
		long realId = 2;
		List<Film> mockFilms = List.of(getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(), getLeRetourDuRoiFilm());
		Mockito.when(filmDAOMock.findByRealisateurId(realId)).thenReturn(mockFilms);
		
		List<FilmDTO> films = myFilmsServiceMock.findFilmByRealisateurId(realId);
		Mockito.verify(filmDAOMock).findByRealisateurId(realId);
		
		Assertions.assertNotNull(films);
		Assertions.assertEquals(3, films.size());
		for (FilmDTO film : films) {
			Assertions.assertNotNull(film);
			Assertions.assertTrue(film.getId() > 0);
			Assertions.assertTrue(film.getDuree() > 0);
			Assertions.assertNotNull(film.getRealisateur());
			Assertions.assertTrue(film.getRealisateur().getId() > 0);
			Assertions.assertNotNull(film.getRealisateur().getNom());
		}
	}

	
	/** Teste le cas ou le DAO remonte une liste vide */
	@Test
	public void findFilmByRealisateurIdEmptyTest() throws ServiceException {
		long realId = 2;
		Mockito.when(filmDAOMock.findByRealisateurId(realId)).thenReturn(new ArrayList<Film>());

		List<FilmDTO> films = myFilmsServiceMock.findFilmByRealisateurId(realId);
		Mockito.verify(filmDAOMock).findByRealisateurId(realId);

		Assertions.assertNotNull(films);
		Assertions.assertEquals(0, films.size());
	}

	/** Teste le cas ou le DAO remonte une exception */
	@Test
	public void findFilmByRealisateurIdExceptionTest() throws ServiceException {
		long realId = 2;
		Mockito.when(filmDAOMock.findByRealisateurId(realId)).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findFilmByRealisateurId(realId);
		});
		Mockito.verify(filmDAOMock).findByRealisateurId(realId);
	}
	
	/** teste la mise à jour d'un réalisateur de False à True */
	@Test
	public void updateRealisateurCelebreTrueTest() throws ServiceException {
		long realId = 2;
		List<Film> mockFilms = List.of(getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(), getLeRetourDuRoiFilm());
		Mockito.when(filmDAOMock.findByRealisateurId(realId)).thenReturn(mockFilms);
		
		Realisateur real = getPeterJacksonRealisateur();
		real.setCelebre(false);
		Assertions.assertFalse(real.getCelebre());
		
		real = myFilmsServiceMock.updateRealisateurCelebre(real);
		Mockito.verify(filmDAOMock).findByRealisateurId(realId);
		Assertions.assertTrue(real.getCelebre());
	}
	
	/** teste la mise à jour d'un réalisateur de True à False */
	@Test
	public void updateRealisateurCelebreFalseTest() throws ServiceException {
		long realId = 1;
		List<Film> mockFilms = List.of(getTitanicFilm(), getAvatarFilm());
		Mockito.when(filmDAOMock.findByRealisateurId(realId)).thenReturn(mockFilms);
		
		Realisateur real = getJamesCameronRealisateur();
		real.setCelebre(true);
		Assertions.assertTrue(real.getCelebre());
		
		real = myFilmsServiceMock.updateRealisateurCelebre(real);
		Mockito.verify(filmDAOMock).findByRealisateurId(realId);
		Assertions.assertFalse(real.getCelebre());
	}
	
	/** Teste la mise à jour d'un réalisateur avec un réalisateur null*/
	@Test
	public void updateRealisateurCelebreNullTest() {
		
		Realisateur real = null;
		Assertions.assertNull(real);
		
		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.updateRealisateurCelebre(real);
		});
	}
	
	/** Teste la mise à jour d'une liste de réalisateurs */
	@Test
	public void updateRealisateurCelebreListeTest() throws ServiceException {
		Realisateur expectedResult = getPeterJacksonRealisateur();
		List<Film> mockFilms1 = List.of(getTitanicFilm(), getTerminatorFilm());
		List<Film> mockFilms2 = List.of(getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(), getLeRetourDuRoiFilm());
		Mockito.when(filmDAOMock.findByRealisateurId(1)).thenReturn(mockFilms1);
		Mockito.when(filmDAOMock.findByRealisateurId(2)).thenReturn(mockFilms2);
		
		List<Realisateur> reals = List.of(getJamesCameronRealisateur(), getPeterJacksonRealisateur());
		reals.get(0).setCelebre(true);
		reals.get(1).setCelebre(false);
		Assertions.assertTrue(reals.get(0).getCelebre());
		Assertions.assertFalse(reals.get(1).getCelebre());
		
		reals = myFilmsServiceMock.updateRealisateurCelebres(reals);
		Mockito.verify(filmDAOMock).findByRealisateurId(1);
		Mockito.verify(filmDAOMock).findByRealisateurId(2);
		Assertions.assertEquals(reals.size(), 1);
		Assertions.assertTrue(reals.get(0).getCelebre());
		Assertions.assertEquals(expectedResult.getId(), reals.get(0).getId());
		Assertions.assertEquals(expectedResult.getNom(), reals.get(0).getNom());
		Assertions.assertEquals(expectedResult.getPrenom(), reals.get(0).getPrenom());
		Assertions.assertEquals(expectedResult.getDateNaissance(), reals.get(0).getDateNaissance());		
	}
	
	/** Teste la mise à jour d'un réalisateur */
	@Test
	public void updateRealisateurTest() throws ServiceException {
		long realId = 1;
		Realisateur mockRealisateur = getJamesCameronRealisateur(); mockRealisateur.setCelebre(true);
		Realisateur expectedRealisateur = getJamesCameronRealisateur(); 

		Mockito.when(filmDAOMock.findByRealisateurId(realId)).thenReturn(List.of(getAvatarFilm(), getTerminatorFilm(), getTitanicFilm()));
		Mockito.when(realisateurDAOMock.findById(realId)).thenReturn(Optional.of(mockRealisateur));
		Mockito.when(realisateurDAOMock.update(mockRealisateur)).thenReturn(expectedRealisateur);
		
		RealisateurDTO realDTO = myFilmsServiceMock.updateRealisateur(realId);
		Mockito.verify(realisateurDAOMock).findById(realId);
		Mockito.verify(realisateurDAOMock).update(mockRealisateur);

		System.out.println(mockRealisateur);
		System.out.println(realDTO);
		Assertions.assertTrue(mockRealisateur.getCelebre());
		Assertions.assertFalse(realDTO.isCelebre());
	}
	
	/** Teste l'update d'un réalisateur qui n'existe pas dans la base de donnée */
	@Test
	public void updateRealisateurEmptyTest() throws ServiceException {
		long realId = 3;
		Mockito.when(realisateurDAOMock.findById(realId)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.updateRealisateur(realId);
		});
		Mockito.verify(realisateurDAOMock).findById(realId);
	}
	
	/** Teste l'update d'un réalisateur avec une erreur lors de l'update- */
	@Test
	public void updateRealisateurExceptionTest() throws ServiceException {
		long realId = 1;
		Realisateur mockRealisateur = getJamesCameronRealisateur();
		Mockito.when(realisateurDAOMock.findById(realId)).thenReturn(Optional.of(mockRealisateur));
		Mockito.when(realisateurDAOMock.update(mockRealisateur)).thenThrow(getDataAccessException());
		
		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.updateRealisateur(realId);
		});
		Mockito.verify(realisateurDAOMock).findById(realId);
		Mockito.verify(realisateurDAOMock).update(mockRealisateur);
	}
	
	/** Teste la création d'un réalisateur */
	@Test
	public void createRealisateurTest() throws ServiceException {
		RealisateurForm realFormMock = getQuentinTarantinoRealForm();
		Realisateur expectedResultMock = getQuentinTarantinoRealisateur();
		Mockito.when(realisateurDAOMock.save(Mockito.any(Realisateur.class))).thenReturn(expectedResultMock);
		
		RealisateurDTO realDTO = myFilmsServiceMock.createRealisateur(realFormMock);
		Mockito.verify(realisateurDAOMock).save(Mockito.any(Realisateur.class));
		
		Assertions.assertNotNull(realDTO);
		Assertions.assertEquals(realDTO.getId(), expectedResultMock.getId());
		Assertions.assertEquals(realDTO.getNom(), expectedResultMock.getNom());
		Assertions.assertEquals(realDTO.getPrenom(), expectedResultMock.getPrenom());
		Assertions.assertEquals(realDTO.getDateNaissance(), expectedResultMock.getDateNaissance());
		Assertions.assertEquals(realDTO.isCelebre(), expectedResultMock.getCelebre());
	}
	
	/** Teste la création d'un réalisateur qui releve une erreur*/
	@Test
	public void createRealisateurExceptionTest() throws ServiceException {
		RealisateurForm realFormMock = getQuentinTarantinoRealForm();
		Mockito.when(realisateurDAOMock.save(Mockito.any(Realisateur.class))).thenThrow(getDataAccessException());
		
		Assertions.assertThrows(ServiceException.class, () ->{
			myFilmsServiceMock.createRealisateur(realFormMock);	
		});
		Mockito.verify(realisateurDAOMock).save(Mockito.any(Realisateur.class));
	}
	
	/** Teste la suppression d'un réalisateur */
	@Test
	public void deleteRealisateurTest() throws ServiceException {
		long realId = 1;
		List<Film> filmsMock = List.of(getAvatarFilm());
		Realisateur realMock = getJamesCameronRealisateur();
		
		Mockito.when(realisateurDAOMock.findById(realId)).thenReturn(Optional.of(realMock));
		Mockito.when(filmDAOMock.findByRealisateurId(realMock.getId())).thenReturn(filmsMock);
		Mockito.doNothing().when(filmDAOMock).delete(Mockito.any(Film.class));
		
		myFilmsServiceMock.deleteReal(realId);
		Mockito.verify(filmDAOMock).delete(Mockito.any(Film.class));
		Mockito.verify(realisateurDAOMock).deleteReal(realMock);
		Mockito.verify(realisateurDAOMock).findById(realId);
	}
	
	/** Teste la suppression d'un réalisateur qui renvoie une exception */
	@Test
	public void deleteRealExceptionTest() throws ServiceException {
		long realId = 1;
		List<Film> filmsMock = List.of(getAvatarFilm());
		Realisateur realMock = getJamesCameronRealisateur();
		
		Mockito.when(realisateurDAOMock.findById(realId)).thenReturn(Optional.of(realMock));
		Mockito.when(filmDAOMock.findByRealisateurId(realMock.getId())).thenReturn(filmsMock);
		Mockito.doThrow(getDataAccessException()).when(realisateurDAOMock).deleteReal(Mockito.any(Realisateur.class));

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.deleteReal(realId);
		});
//		Mockito.verify(filmDAOMock).delete(filmMock);
//		Assertions.fail("Corriger le contenu de ce test");
	}
	
	/** partie GENRE */
	/** Teste la récupération des films par l'ID du genre */
	@Test
	public void findFilmByGenreIdTest() throws ServiceException{
		long genreId = 2;
		List<Film> mockFilms = List.of(getLaCommunauteDeLAnneauFilm(), getLesDeuxToursFilm(), getLeRetourDuRoiFilm());
		Mockito.when(filmDAOMock.findByGenreId(genreId)).thenReturn(mockFilms);
		
		List<FilmDTO> films = myFilmsServiceMock.findFilmByGenreId(genreId);
		Mockito.verify(filmDAOMock).findByGenreId(genreId);
		
		Assertions.assertNotNull(films);
		Assertions.assertEquals(3, films.size());
		for (FilmDTO film : films) {
			Assertions.assertNotNull(film);
			Assertions.assertTrue(film.getId() > 0);
			Assertions.assertTrue(film.getDuree() > 0);
			Assertions.assertNotNull(film.getRealisateur());
			Assertions.assertTrue(film.getRealisateur().getId() > 0);
			Assertions.assertNotNull(film.getRealisateur().getNom());
			Assertions.assertNotNull(film.getGenre());
			Assertions.assertTrue(film.getGenre().getId() > 0);
			Assertions.assertNotNull(film.getGenre().getGenre());
		}
	}

	
	/** Teste le cas ou le DAO remonte une liste vide */
	@Test
	public void findFilmByGenreIdEmptyTest() throws ServiceException {
		long genreId = 2;
		Mockito.when(filmDAOMock.findByGenreId(genreId)).thenReturn(new ArrayList<Film>());

		List<FilmDTO> films = myFilmsServiceMock.findFilmByGenreId(genreId);
		Mockito.verify(filmDAOMock).findByGenreId(genreId);

		Assertions.assertNotNull(films);
		Assertions.assertEquals(0, films.size());
	}

	/** Teste le cas ou le DAO remonte une exception */
	@Test
	public void findFilmByGenreIdExceptionTest() throws ServiceException {
		long genreId = 2;
		Mockito.when(filmDAOMock.findByGenreId(genreId)).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findFilmByGenreId(genreId);
		});
		Mockito.verify(filmDAOMock).findByGenreId(genreId);
	}
	
	/** Teste la recuperation des genres */
	@Test
	public void findAllGenreTest() throws ServiceException {
		// liste des genres que devrait renvoyer findAll
		List<Genre> genreMocks = List.of(getScienceFictionGenre(), getFantasyGenre());
		
		// quand on appelle le DAO pour findAll, renvoie la liste fournie
		Mockito.when(genreDAOMock.findAll()).thenReturn(genreMocks);
		
		// on appelle le service mock comme on le ferait
		List<GenreDTO> genres = myFilmsServiceMock.findAllGenres();
		
		// on vérifie que la méthode findAll a été appelée sur genreDAOMock
		Mockito.verify(genreDAOMock).findAll();
		
		// on vérifie que genres n'est pas null
		Assertions.assertNotNull(genres);
		Assertions.assertEquals(2, genres.size());
		
		for (GenreDTO genre: genres) {
			Assertions.assertNotNull(genre);
			Assertions.assertNotNull(genre.getGenre());
			Assertions.assertTrue(genre.getId() > 0);
		}
	}

	/** Teste le cas ou le DAO remonte une liste vide */
	@Test
	public void findAllGenreEmptyTest() throws ServiceException {
		Mockito.when(genreDAOMock.findAll()).thenReturn(new ArrayList<Genre>());

		List<GenreDTO> genres = myFilmsServiceMock.findAllGenres();
		Mockito.verify(genreDAOMock).findAll();

		Assertions.assertNotNull(genres);
		Assertions.assertEquals(0, genres.size());
	}

	/** Teste le cas ou le DAO remonte une exception */
	@Test
	public void findAllGenresExceptionTest() throws ServiceException {
		Mockito.when(genreDAOMock.findAll()).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findAllGenres();
		});
		Mockito.verify(genreDAOMock).findAll();
	}
	
	/** Teste la recuperation d'un genre a partir de son id */
	@Test
	public void findGenreByIdTest() throws ServiceException {
		long genreId = 1;
		Genre expectedGenreMock = getScienceFictionGenre();
		Mockito.when(genreDAOMock.findById(genreId)).thenReturn(Optional.of(expectedGenreMock));

		GenreDTO genreDTO = myFilmsServiceMock.findGenreById(genreId);
		Mockito.verify(genreDAOMock).findById(genreId);

		Assertions.assertNotNull(genreDTO);
		Assertions.assertEquals(expectedGenreMock.getId(), genreDTO.getId());
		Assertions.assertNotNull(genreDTO.getGenre());
	}

	/** Teste la recuperation d'un genre qui n'existe pas */
	@Test
	public void findGenreByIdNotExisitingTest() throws ServiceException {
		long genreId = 404;
		Mockito.when(genreDAOMock.findById(genreId)).thenReturn(Optional.empty());
		
		GenreDTO genreDTO = myFilmsServiceMock.findGenreById(genreId);
		Mockito.verify(genreDAOMock).findById(genreId);
		
		Assertions.assertNull(genreDTO);		
	}

	/** Teste la recuperation d'un genre qui renvoie une exception */
	@Test
	public void findGenreByIdExceptionTest() throws ServiceException {
		long genreId = 1;
		Mockito.when(genreDAOMock.findById(genreId)).thenThrow(getDataAccessException());

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.findGenreById(genreId);
		});
		Mockito.verify(genreDAOMock).findById(genreId);
	}
	
	/** Teste la creation d'un genre */
	@Test
	public void createGenreTest() throws ServiceException {
		GenreForm genreFormMock = getActionGenreForm();
		Genre expectedGenreMock = getActionGenre();
		Mockito.when(genreDAOMock.save(Mockito.any(Genre.class))).thenReturn(expectedGenreMock);

		GenreDTO genreDTO = myFilmsServiceMock.createGenre(genreFormMock);
		Mockito.verify(genreDAOMock).save(Mockito.any(Genre.class));

		Assertions.assertNotNull(genreDTO);
		Assertions.assertEquals(expectedGenreMock.getId(), genreDTO.getId());
		Assertions.assertNotNull(expectedGenreMock.getGenre());
	}
	
	/** Teste la création d'un genre qui releve une erreur*/
	@Test
	public void createGenreExceptionTest() throws ServiceException {
		GenreForm genreFormMock = getActionGenreForm();
		Mockito.when(genreDAOMock.save(Mockito.any(Genre.class))).thenThrow(getDataAccessException());
		
		Assertions.assertThrows(ServiceException.class, () ->{
			myFilmsServiceMock.createGenre(genreFormMock);	
		});
		Mockito.verify(genreDAOMock).save(Mockito.any(Genre.class));
	}
	
	/** Teste la mise à jour d'un genre */
	@Test
	public void updateGenreTest() throws ServiceException {
		GenreForm genreFormMock = getActionGenreForm();
		Genre expectedGenreMock = getActionGenre();
		expectedGenreMock.setId(1);
		Mockito.when(genreDAOMock.updateGenre(Mockito.any(Genre.class))).thenReturn(expectedGenreMock);
				
		GenreDTO genreDTO = myFilmsServiceMock.updateGenre(genreFormMock, 1);
		Mockito.verify(genreDAOMock).updateGenre(Mockito.any(Genre.class));
		
		Assertions.assertNotNull(genreDTO);
		Assertions.assertEquals(expectedGenreMock.getId(), genreDTO.getId());
		Assertions.assertNotNull(genreDTO.getGenre());
	}

/** Teste la mise à jour d'un genre qui renvoie une exception */
	@Test
	public void updateGenreExceptionTest() throws ServiceException {
		GenreForm genreFormMock = getActionGenreForm();
		Mockito.doThrow(getDataAccessException()).when(genreDAOMock).updateGenre(Mockito.any(Genre.class));

		Assertions.assertThrows(ServiceException.class, () -> {
			myFilmsServiceMock.updateGenre(genreFormMock, 1);
		});
		Mockito.verify(genreDAOMock).updateGenre(Mockito.any(Genre.class));
	}
	
	private DataAccessException getDataAccessException() {
		return new EmptyResultDataAccessException(1);
	}
	
	private Realisateur getJamesCameronRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(1);
		realisateur.setNom("Cameron");
		realisateur.setPrenom("James");
		realisateur.setDateNaissance(LocalDate.of(1954, 8, 16));
		return realisateur;
	}

	private Realisateur getPeterJacksonRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(2);
		realisateur.setNom("Jackson");
		realisateur.setPrenom("Peter");
		realisateur.setDateNaissance(LocalDate.of(1961, 10, 31));
		return realisateur;
	}
	
	private Realisateur getQuentinTarantinoRealisateur() {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(3);
		realisateur.setNom("Tarantino");
		realisateur.setPrenom("Quentin");
		realisateur.setDateNaissance(LocalDate.of(1963, 3, 27));
		return realisateur;
	}
	
	private RealisateurForm getQuentinTarantinoRealForm() {
		RealisateurForm realisateur = new RealisateurForm();
		realisateur.setNom("Tarantino");
		realisateur.setPrenom("Quentin");
		realisateur.setDateNaissance("1963-03-27");
		return realisateur;
	}

	private Film getAvatarFilm() {
		Film film = new Film();
		film.setId(1);
		film.setTitre("Avatar");
		film.setDuree(162);
		film.setRealisateur(getJamesCameronRealisateur());
		film.setGenre(getScienceFictionGenre());
		return film;
	}

	private Film getLaCommunauteDeLAnneauFilm() {
		Film film = new Film();
		film.setId(2);
		film.setTitre("La communauté de l'anneau");
		film.setDuree(178);
		film.setRealisateur(getPeterJacksonRealisateur());
		film.setGenre(getFantasyGenre());
		return film;
	}

	private Film getLesDeuxToursFilm() {
		Film film = new Film();
		film.setId(3);
		film.setTitre("Les deux tours");
		film.setDuree(179);
		film.setRealisateur(getPeterJacksonRealisateur());
		film.setGenre(getFantasyGenre());
		return film;
	}

	private Film getLeRetourDuRoiFilm() {
		Film film = new Film();
		film.setId(4);
		film.setTitre("Le retour du roi");
		film.setDuree(201);
		film.setRealisateur(getPeterJacksonRealisateur());
		film.setGenre(getFantasyGenre());
		return film;
	}

	private Film getTitanicFilm() {
		Film film = new Film();
		film.setId(5);
		film.setTitre("Titanic");
		film.setDuree(195);
		film.setRealisateur(getJamesCameronRealisateur());
		film.setGenre(getScienceFictionGenre());
		return film;
	}

	private Film getTerminatorFilm() {
		Film film = new Film();
		film.setId(6);
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateur(getJamesCameronRealisateur());
		film.setGenre(getScienceFictionGenre());
		return film;
	}

	private FilmForm getTerminatorFilmForm() {
		FilmForm filmForm = new FilmForm();
		filmForm.setTitre("Terminator");
		filmForm.setDuree(107);
		filmForm.setRealisateurId(getJamesCameronRealisateur().getId());
		filmForm.setGenreId(getScienceFictionGenre().getId());
		return filmForm;
	}
	
	private Genre getScienceFictionGenre() {
		Genre genre = new Genre();
		genre.setId(1);
		genre.setGenre("science fiction");
		return genre;
	}
	
	private Genre getFantasyGenre() {
		Genre genre = new Genre();
		genre.setId(2);
		genre.setGenre("fantasy");
		return genre;
	}
	
	private Genre getActionGenre() {
		Genre genre = new Genre();
		genre.setId(3);
		genre.setGenre("action");
		return genre;
	}
	
	private GenreForm getActionGenreForm() {
		GenreForm genreForm = new GenreForm();
		genreForm.setGenre("action");
		return genreForm;
	}

}
