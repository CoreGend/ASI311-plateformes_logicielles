package com.ensta.myfilmlist;

import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.form.GenreForm;
import com.ensta.myfilmlist.form.RealisateurForm;
import com.ensta.myfilmlist.service.MyFilmsService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests des services web de l'application MyFilms.
 */
@WebMvcTest
@AutoConfigureMockMvc
public class MyFilmsControllerTests {

	/** Permet de simuler les appels HTTP */
	@Autowired
	private MockMvc mockMvc;

	/** Mock du bean Spring correspondant au service */
	@MockBean
	private MyFilmsService myFilmsServiceMock;

	/** Permet de convertir des objets en JSON */
	@Autowired
	private ObjectMapper objectMapper;

	/** Teste la recuperation des films */
	@Test
	public void findAllFilmsTest() throws Exception {
		List<FilmDTO> films = List.of(getAvatarFilm(), getLaCommunauteDeLAnneauFilm());
		Mockito.when(myFilmsServiceMock.findAllFilms()).thenReturn(films);

		final String expectedResponseContent = objectMapper.writeValueAsString(films);

		mockMvc.perform(MockMvcRequestBuilders.get("/film"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findAllFilms();
	}

	/** Teste la recuperation des films lorsqu'une exception est renvoyee par la couche service */
	@Test
	public void findAllFilmsServiceExceptionTest() throws Exception {
		Mockito.when(myFilmsServiceMock.findAllFilms()).thenThrow(new ServiceException("findAllFilmsExceptionTest"));

		// Verifier le code retour de la requete

		mockMvc.perform(MockMvcRequestBuilders.get("/film"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findAllFilms();
	}

	/** Teste la recuperation d'un film par son id */
	@Test
	public void getFilmByIdTest() throws Exception {
		long filmId = 1;
		FilmDTO expectedFilm = getAvatarFilm();
		Mockito.when(myFilmsServiceMock.findFilmById(filmId)).thenReturn(expectedFilm);

		final String expectedResponseContent = objectMapper.writeValueAsString(expectedFilm);

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findFilmById(filmId);
	}

	/** Teste la recuperation d'un film qui n'existe pas par son id */
	@Test
	public void getFilmByIdNotExisitingTest() throws Exception {
		long filmId = 404;
		Mockito.when(myFilmsServiceMock.findFilmById(filmId)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
		Mockito.verify(myFilmsServiceMock).findFilmById(filmId);
	}

	/** Teste la recuperation d'un film avec un id qui vaut 0 : doit renvoyer une erreur 400 */
	@Test
	public void getFilmByIdZeroTest() throws Exception {
		long filmId = 0;

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	/** Teste le handler par defaut en cas d'Exception : on doit renvoyer une reponse valide a l'utilisateur */
	@Test
	public void getFilmByIdHandleExceptionTest() throws Exception {
		long filmId = 1;
		Mockito.when(myFilmsServiceMock.findFilmById(filmId)).thenThrow(RuntimeException.class);

		mockMvc.perform(MockMvcRequestBuilders.get("/film/{id}", filmId))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findFilmById(filmId);
	}
	
	/** Teste la creation d'un film */
	@Test
	public void createFilmTest() throws Exception {
		FilmDTO expectedFilm = getTerminatorFilm();
		Mockito.when(myFilmsServiceMock.createFilm(Mockito.any(FilmForm.class))).thenReturn(expectedFilm);
		
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre(expectedFilm.getTitre());
		filmFormParams.setDuree(expectedFilm.getDuree());
		filmFormParams.setRealisateurId(expectedFilm.getRealisateur().getId());
		filmFormParams.setGenreId(expectedFilm.getGenre().getId());
		
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedFilm);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).createFilm(Mockito.any(FilmForm.class));
		
		// Creer le film correspondant a l'attribut "expectedFilm"
		// Verifier que la methode createFilm du mock du service a bien ete appelee
	}

	/** Teste la creation d'un film avec un titre vide */
	@Test
	public void createFilmTitreBlankTest() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(1);
		filmFormParams.setGenreId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createFilm(Mockito.any(FilmForm.class));
	}

	/** Teste la creation d'un film avec une duree negative */
	@Test
	public void createFilmDureeNegativeTest() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(-1);
		filmFormParams.setRealisateurId(1);
		filmFormParams.setGenreId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createFilm(Mockito.any(FilmForm.class));
	}

	/** Teste la creation d'un film avec un realisateurId qui vaut 0 */
	@Test
	public void createFilmRealisateurId0Test() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(0);
		filmFormParams.setGenreId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createFilm(Mockito.any(FilmForm.class));
	}
	
	/** Teste la creation d'un film avec un genreId qui vaut 0 */
	@Test
	public void createFilmGenreId0Test() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(1);
		filmFormParams.setGenreId(0);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createFilm(Mockito.any(FilmForm.class));
	}

	/** Teste la creation d'un film qui leve une ServiceException */
	@Test
	public void createFilmExceptionTest() throws Exception {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Mockito.when(myFilmsServiceMock.createFilm(Mockito.any(FilmForm.class))).thenThrow(new ServiceException("createFilmExceptionTest"));

		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre(filmFormMock.getTitre());
		filmFormParams.setDuree(filmFormMock.getDuree());
		filmFormParams.setRealisateurId(filmFormMock.getRealisateurId());
		filmFormParams.setGenreId(filmFormMock.getGenreId());
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).createFilm(Mockito.any(FilmForm.class));
	}
	
	/** Teste la mise à jour d'un film */
	@Test
	public void updateFilmTest() throws Exception {
		FilmDTO expectedFilm = getTerminatorFilm();
		Mockito.when(myFilmsServiceMock.updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong())).thenReturn(expectedFilm);
		
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre(expectedFilm.getTitre());
		filmFormParams.setDuree(expectedFilm.getDuree());
		filmFormParams.setRealisateurId(expectedFilm.getRealisateur().getId());
		filmFormParams.setGenreId(expectedFilm.getGenre().getId());
		
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedFilm);

		mockMvc.perform(MockMvcRequestBuilders.post("/film/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong());
	}

	/** Teste la mise à jour d'un film avec un titre vide */
	@Test
	public void updateFilmTitreBlankTest() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(1);
		filmFormParams.setGenreId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong());
	}

	/** Teste la mise à jour d'un film avec une duree negative */
	@Test
	public void updateFilmDureeNegativeTest() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(-1);
		filmFormParams.setRealisateurId(1);
		filmFormParams.setGenreId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/film/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong());
	}

	/** Teste la mise à jour d'un film avec un realisateurId qui vaut 0 */
	@Test
	public void updateFilmRealisateurId0Test() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(0);
		filmFormParams.setGenreId(1);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong());
	}
	
	/** Teste la mise à jour d'un film avec un genreId qui vaut 0 */
	@Test
	public void updateFilmGenreId0Test() throws Exception {
		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre("Terminator");
		filmFormParams.setDuree(107);
		filmFormParams.setRealisateurId(1);
		filmFormParams.setGenreId(0);
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong());
	}

	/** Teste la mise à jour d'un film qui leve une ServiceException */
	@Test
	public void updateFilmExceptionTest() throws Exception {
		FilmForm filmFormMock = getTerminatorFilmForm();
		Mockito.doThrow(new ServiceException("updateFilmExceptionTest")).when(myFilmsServiceMock).updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong());

		FilmForm filmFormParams = new FilmForm();
		filmFormParams.setTitre(filmFormMock.getTitre());
		filmFormParams.setDuree(filmFormMock.getDuree());
		filmFormParams.setRealisateurId(filmFormMock.getRealisateurId());
		filmFormParams.setGenreId(filmFormMock.getGenreId());
		final String paramsContent = objectMapper.writeValueAsString(filmFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/film/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).updateFilm(Mockito.any(FilmForm.class), Mockito.anyLong());
	}

	/** Teste la suppression d'un film par son id */
	@Test
	public void deleteFilmTest() throws Exception {
		long filmId = 1;
		Mockito.doNothing().when(myFilmsServiceMock).deleteFilm(filmId);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/film/d/{id}", filmId))
			.andExpect(MockMvcResultMatchers.status().isNoContent());
		Mockito.verify(myFilmsServiceMock).deleteFilm(filmId);
	}
	
	/** Teste la suppression d'un film qui n'existe pas par son id */
	@Test
	public void deleteFilmNotExistingTest() throws Exception {
		long filmId = 404;
		Mockito.doThrow(new ServiceException()).when(myFilmsServiceMock).deleteFilm(filmId);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/film/d/{id}", filmId))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).deleteFilm(filmId);
	}
	
	/** Teste la récupération des films par l'id du réalisateur */
	@Test
	public void getFilmByRealisateurIdTest() throws Exception{
		long realId = 1;
		List<FilmDTO> films = List.of(getAvatarFilm());
		Mockito.when(myFilmsServiceMock.findFilmByRealisateurId(realId)).thenReturn(films);
		
		final String expectedResponseContent = objectMapper.writeValueAsString(films);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/film/r/{id}", realId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findFilmByRealisateurId(realId);
	}
	
	/** Teste la récupération des films d'un réalisateur qui n'existe pas par son id */
	@Test
	public void getFilmByRealisateurIdNotExistingTest() throws Exception {
		long filmId = 404;
		Mockito.when(myFilmsServiceMock.findFilmByRealisateurId(filmId)).thenThrow(new ServiceException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/film/r/{id}", filmId))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findFilmByRealisateurId(filmId);
	}
	
	/** Teste la récupération des réalisateurs */
	@Test
	public void findAllRealisateursTest() throws Exception {
		List<RealisateurDTO> reals = List.of(getJamesCameronRealisateur(), getPeterJacksonRealisateur());
		Mockito.when(myFilmsServiceMock.findAllRealisateurs()).thenReturn(reals);
		
		final String expectedResponseContent = objectMapper.writeValueAsString(reals);
				
		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findAllRealisateurs();
		
	}
	
	/** Teste la récupération des réalisateurs lorsqu'une exception est renvoyée par la couche service */
	@Test
	public void findAllRealisateursExceptionTest() throws Exception {
		Mockito.when(myFilmsServiceMock.findAllRealisateurs()).thenThrow(new ServiceException("findAllRealisateursExceptionTest"));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findAllRealisateurs();
	}
	
	/** Teste la récupération d'un réalisateur par son id */
	@Test
	public void getRealisateurByIdTest() throws Exception{
		long realId = 1;
		RealisateurDTO expectedReal = getJamesCameronRealisateur();
		Mockito.when(myFilmsServiceMock.findRealById(realId)).thenReturn(expectedReal);
		
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedReal);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur/{id}", realId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findRealById(realId);
	}
	
	/** Teste la récupération d'un réalisateur qui n'existe pas par son id */
	@Test
	public void getRealisateurByIdNotExistingTest() throws Exception{
		long realId = 404;
		Mockito.when(myFilmsServiceMock.findRealById(realId)).thenReturn(null);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur/{id}", realId))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findRealById(realId);
	}

	/** Teste la récupération d'un réalisateur avec un id qui vaut 0 : doit renvoyer une erreur 400 */
	@Test
	public void getRealisateurByIdZeroTest() throws Exception{
		long filmId = 0;
		
		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur/{id}", filmId))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	/** Teste le handler par defaut en cas d'Exception : on doit renvoyer une reponse valide a l'utilisateur */
	@Test
	public void getRealisateurByIdHandleExceptionTest() throws Exception {
		long realId = 1;
		Mockito.when(myFilmsServiceMock.findRealById(realId)).thenThrow(ServiceException.class);

		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur/{id}", realId))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findRealById(realId);
	}
	
	/** Teste la création d'un réalisateur */
	@Test
	public void createRealisateurTest() throws Exception {
		RealisateurDTO expectedReal = getQuentinTarantinoRealisateur();
		Mockito.when(myFilmsServiceMock.createRealisateur(Mockito.any(RealisateurForm.class))).thenReturn(expectedReal);
		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setPrenom("Quentin");
		realisateurFormParams.setNom("Tarantino");
		realisateurFormParams.setDateNaissance("1963-03-27");
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedReal);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(paramsContent))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).createRealisateur(Mockito.any(RealisateurForm.class));
	}
	
	/** Teste la creation d'un realisateur avec un prénom vide */
	@Test
	public void createRealisateurPrenomBlankTest() throws Exception {		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setPrenom("");
		realisateurFormParams.setNom("Tarantino");
		realisateurFormParams.setDateNaissance("1963-03-27");
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(paramsContent))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createRealisateur(Mockito.any(RealisateurForm.class));
	}
	
	/** Teste la creation d'un realisateur avec un nom vide */
	@Test
	public void createRealisateurNomBlankTest() throws Exception {		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setPrenom("Quentin");
		realisateurFormParams.setNom("");
		realisateurFormParams.setDateNaissance("1963-03-27");
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(paramsContent))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createRealisateur(Mockito.any(RealisateurForm.class));
	}
	
	/** Teste la creation d'un realisateur avec une date de naissance vide */
	@Test
	public void createRealisateurDateNaissanceBlankTest() throws Exception {		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setPrenom("Quentin");
		realisateurFormParams.setNom("Tarantino");
		realisateurFormParams.setDateNaissance("");
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(paramsContent))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createRealisateur(Mockito.any(RealisateurForm.class));
	}
	
	/** Teste la creation d'un réalisateur qui leve une ServiceException */
	@Test
	public void createRealisateurExceptionTest() throws Exception {
		RealisateurForm realFormMock = getQuentinTarantinoRealForm();
		Mockito.when(myFilmsServiceMock.createRealisateur(Mockito.any(RealisateurForm.class))).thenThrow(new ServiceException("createRealisateurExceptionTest"));
		
		RealisateurForm realFormParams = new RealisateurForm();
		realFormParams.setPrenom(realFormMock.getPrenom());
		realFormParams.setNom(realFormMock.getNom());
		realFormParams.setDateNaissance(realFormMock.getDateNaissance());
		
		final String paramsContent = objectMapper.writeValueAsString(realFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).createRealisateur(Mockito.any(RealisateurForm.class));
	}
	
	/** Teste la mise à jour d'un réalisateur */
	@Test
	public void updateRealisateurTest() throws Exception {
		RealisateurDTO expectedReal = getQuentinTarantinoRealisateur();
		Mockito.when(myFilmsServiceMock.updateRealisateur(expectedReal.getId())).thenReturn(expectedReal);
				
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedReal);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur/u/{id}", expectedReal.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).updateRealisateur(expectedReal.getId());
	}
	
	/** Teste la mise à jour d'un réalisateur qui leve une ServiceException */
	@Test
	public void updateRealisateurExceptionTest() throws Exception {
		long realId = 1;
		Mockito.when(myFilmsServiceMock.updateRealisateur(realId)).thenThrow(new ServiceException("createRealisateurExceptionTest"));
						
		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur/u/{id}", realId))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).updateRealisateur(realId);
	}
	
	/** Teste la mise à jour d'un realisateur */
	@Test
	public void updateRealTest() throws Exception {
		RealisateurDTO expectedRealisateur = getQuentinTarantinoRealisateur();
		Mockito.when(myFilmsServiceMock.updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong())).thenReturn(expectedRealisateur);
		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setNom(expectedRealisateur.getNom());
		realisateurFormParams.setPrenom(expectedRealisateur.getPrenom());
		realisateurFormParams.setDateNaissance(expectedRealisateur.getDateNaissance().toString());
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedRealisateur);

		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong());
	}
	
	/** Teste la mise à jour d'un realisateur avec un mauvais nom*/
	@Test
	public void updateRealNomErreurTest() throws Exception {
		RealisateurDTO expectedRealisateur = getQuentinTarantinoRealisateur();
		Mockito.when(myFilmsServiceMock.updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong())).thenReturn(expectedRealisateur);
		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setNom("");
		realisateurFormParams.setPrenom(expectedRealisateur.getPrenom());
		realisateurFormParams.setDateNaissance(expectedRealisateur.getDateNaissance().toString());
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong());
	}
	
	/** Teste la mise à jour d'un realisateur avec un mauvais prénom */
	@Test
	public void updateRealPrenomErreurTest() throws Exception {
		RealisateurDTO expectedRealisateur = getQuentinTarantinoRealisateur();
		Mockito.when(myFilmsServiceMock.updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong())).thenReturn(expectedRealisateur);
		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setNom(expectedRealisateur.getNom());
		realisateurFormParams.setPrenom("");
		realisateurFormParams.setDateNaissance(expectedRealisateur.getDateNaissance().toString());
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong());
	}
	
	/** Teste la mise à jour d'un realisateur avec une mauvaise date de naissance */
	@Test
	public void updateRealDateNaissanceErreurTest() throws Exception {
		RealisateurDTO expectedRealisateur = getQuentinTarantinoRealisateur();
		Mockito.when(myFilmsServiceMock.updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong())).thenReturn(expectedRealisateur);
		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setNom(expectedRealisateur.getNom());
		realisateurFormParams.setPrenom(expectedRealisateur.getPrenom());
		realisateurFormParams.setDateNaissance("");
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong());
	}
	
	/** Teste la mise à jour d'un realisateur qui soulève une exception */
	@Test
	public void updateRealExceptionTest() throws Exception {
		RealisateurDTO expectedRealisateur = getQuentinTarantinoRealisateur();
		Mockito.when(myFilmsServiceMock.updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong())).thenThrow(new ServiceException("createRealisateurExceptionTest"));
		
		RealisateurForm realisateurFormParams = new RealisateurForm();
		realisateurFormParams.setNom(expectedRealisateur.getNom());
		realisateurFormParams.setPrenom(expectedRealisateur.getPrenom());
		realisateurFormParams.setDateNaissance(expectedRealisateur.getDateNaissance().toString());
		
		final String paramsContent = objectMapper.writeValueAsString(realisateurFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/realisateur/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).updateReal(Mockito.any(RealisateurForm.class), Mockito.anyLong());
	}
	
	/** Teste la suppression d'un réalisateur par son id */
	@Test
	public void deleteRealTest() throws Exception {
		long realId = 1;
		Mockito.doNothing().when(myFilmsServiceMock).deleteReal(realId);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur/d/{id}", realId))
			.andExpect(MockMvcResultMatchers.status().isNoContent());
		Mockito.verify(myFilmsServiceMock).deleteReal(realId);
	}
	
	/** Teste la suppression d'un réalisateur qui n'existe pas par son id */
	@Test
	public void deleteRealNotExistingTest() throws Exception {
		long realId = 404;
		Mockito.doThrow(new ServiceException()).when(myFilmsServiceMock).deleteReal(realId);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/realisateur/d/{id}", realId))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).deleteReal(realId);
	}
	
	/** Teste la récupération des films par l'id du genre */
	@Test
	public void getFilmByGenreIdTest() throws Exception{
		long genreId = 1;
		List<FilmDTO> films = List.of(getAvatarFilm());
		Mockito.when(myFilmsServiceMock.findFilmByGenreId(genreId)).thenReturn(films);
		
		final String expectedResponseContent = objectMapper.writeValueAsString(films);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/film/g/{id}", genreId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findFilmByGenreId(genreId);
	}
	
	/** Teste la récupération des films d'un genre qui n'existe pas par son id */
	@Test
	public void getFilmByGenreIdNotExistingTest() throws Exception {
		long filmId = 404;
		Mockito.when(myFilmsServiceMock.findFilmByGenreId(filmId)).thenThrow(new ServiceException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/film/g/{id}", filmId))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findFilmByGenreId(filmId);
	}
	
	/** Teste la récupération des genres */
	@Test
	public void findAllGenresTest() throws Exception {
		List<GenreDTO> reals = List.of(getScienceFictionGenre(), getFantasyGenre());
		Mockito.when(myFilmsServiceMock.findAllGenres()).thenReturn(reals);
		
		final String expectedResponseContent = objectMapper.writeValueAsString(reals);
				
		mockMvc.perform(MockMvcRequestBuilders.get("/genre"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findAllGenres();
		
	}
	
	/** Teste la récupération des genres lorsqu'une exception est renvoyée par la couche service */
	@Test
	public void findAllGenresExceptionTest() throws Exception {
		Mockito.when(myFilmsServiceMock.findAllGenres()).thenThrow(new ServiceException("findAllGenresExceptionTest"));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/genre"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findAllGenres();
	}
	
	/** Teste la récupération d'un genre par son id */
	@Test
	public void getGenreByIdTest() throws Exception{
		long genreId = 1;
		GenreDTO expectedGenre= getScienceFictionGenre();
		Mockito.when(myFilmsServiceMock.findGenreById(genreId)).thenReturn(expectedGenre);
		
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedGenre);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/genre/{id}", genreId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).findGenreById(genreId);
	}
	
	/** Teste la récupération d'un genre qui n'existe pas par son id */
	@Test
	public void getGenreByIdNotExistingTest() throws Exception{
		long genreId = 404;
		Mockito.when(myFilmsServiceMock.findGenreById(genreId)).thenReturn(null);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/genre/{id}", genreId))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
		Mockito.verify(myFilmsServiceMock).findGenreById(genreId);
	}

	/** Teste la récupération d'un genre avec un id qui vaut 0 : doit renvoyer une erreur 400 */
	@Test
	public void getGenreByIdZeroTest() throws Exception{
		long genreId = 0;
		
		mockMvc.perform(MockMvcRequestBuilders.get("/genre/{id}", genreId))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	/** Teste le handler par defaut en cas d'Exception : on doit renvoyer une reponse valide a l'utilisateur */
	@Test
	public void getGenreByIdHandleExceptionTest() throws Exception {
		long genreId = 1;
		Mockito.when(myFilmsServiceMock.findGenreById(genreId)).thenThrow(ServiceException.class);

		mockMvc.perform(MockMvcRequestBuilders.get("/genre/{id}", genreId))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).findGenreById(genreId);
	}
	
	/** Teste la suppression d'un genre par son id */
	@Test
	public void deleteGenreTest() throws Exception {
		long genreId = 1;
		Mockito.doNothing().when(myFilmsServiceMock).deleteGenre(genreId);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/genre/d/{id}", genreId))
			.andExpect(MockMvcResultMatchers.status().isNoContent());
		Mockito.verify(myFilmsServiceMock).deleteGenre(genreId);
	}
	
	/** Teste la suppression d'un genre qui n'existe pas par son id */
	@Test
	public void deleteGenreNotExistingTest() throws Exception {
		long genreId = 404;
		Mockito.doThrow(new ServiceException()).when(myFilmsServiceMock).deleteGenre(genreId);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/genre/d/{id}", genreId))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).deleteGenre(genreId);
	}
	
	/** Teste la création d'un genre */
	@Test
	public void createGenreTest() throws Exception {
		GenreDTO expectedGenre = getActionGenre();
		Mockito.when(myFilmsServiceMock.createGenre(Mockito.any(GenreForm.class))).thenReturn(expectedGenre);
		
		GenreForm genreFormParams = new GenreForm();
		genreFormParams.setGenre(expectedGenre.getGenre());
				
		final String paramsContent = objectMapper.writeValueAsString(genreFormParams);
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedGenre);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/genre")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(paramsContent))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).createGenre(Mockito.any(GenreForm.class));
	}
	
	/** Teste la creation d'un genre avec un genre vide */
	@Test
	public void createGenrePrenomBlankTest() throws Exception {		
		GenreForm genreFormParams = new GenreForm();
		genreFormParams.setGenre("");
		
		final String paramsContent = objectMapper.writeValueAsString(genreFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/genre")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(paramsContent))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).createGenre(Mockito.any(GenreForm.class));
	}
	
	/** Teste la creation d'un genre qui leve une ServiceException */
	@Test
	public void createGenreExceptionTest() throws Exception {
		GenreForm realFormMock = getActionGenreForm();
		Mockito.when(myFilmsServiceMock.createGenre(Mockito.any(GenreForm.class))).thenThrow(new ServiceException("createGenreExceptionTest"));
		
		GenreForm realFormParams = new GenreForm();
		realFormParams.setGenre(realFormMock.getGenre());
		
		final String paramsContent = objectMapper.writeValueAsString(realFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/genre")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).createGenre(Mockito.any(GenreForm.class));
	}
	
	/** Teste la mise à jour d'un genre */
	@Test
	public void updateGenreTest() throws Exception {
		GenreDTO expectedGenre = getActionGenre();
		Mockito.when(myFilmsServiceMock.updateGenre(Mockito.any(GenreForm.class), Mockito.anyLong())).thenReturn(expectedGenre);
		
		GenreForm genreFormParams = new GenreForm();
		genreFormParams.setGenre(expectedGenre.getGenre());
		
		final String paramsContent = objectMapper.writeValueAsString(genreFormParams);
		final String expectedResponseContent = objectMapper.writeValueAsString(expectedGenre);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/genre/{id}", expectedGenre.getId())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(paramsContent))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.content().json(expectedResponseContent));
		Mockito.verify(myFilmsServiceMock).updateGenre(Mockito.any(GenreForm.class), Mockito.anyLong());
	}
	
	/** Teste la mise à jour d'un genre qui leve une ServiceException */
	@Test
	public void updateGenreExceptionTest() throws Exception {
		long genreId = 1;
		Mockito.when(myFilmsServiceMock.updateGenre(Mockito.any(GenreForm.class), Mockito.anyLong())).thenThrow(new ServiceException("createGenreExceptionTest"));
		
		GenreDTO expectedGenre = getScienceFictionGenre();
		GenreForm genreFormParams = new GenreForm();
		genreFormParams.setGenre(expectedGenre.getGenre());
		
		final String paramsContent = objectMapper.writeValueAsString(genreFormParams);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/genre/{id}", genreId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock).updateGenre(Mockito.any(GenreForm.class), Mockito.anyLong());
	}
	
	/** Teste la mise à jour d'un genre avec un mauvais genre */
	@Test
	public void updateGenreNomErreurTest() throws Exception {
		GenreDTO expectedGenre = getActionGenre();
		Mockito.when(myFilmsServiceMock.updateGenre(Mockito.any(GenreForm.class), Mockito.anyLong())).thenReturn(expectedGenre);
		
		GenreForm genreFormParams = new GenreForm();
		genreFormParams.setGenre("");
		
		final String paramsContent = objectMapper.writeValueAsString(genreFormParams);

		mockMvc.perform(MockMvcRequestBuilders.post("/genre/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(paramsContent))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		Mockito.verify(myFilmsServiceMock, never()).updateGenre(Mockito.any(GenreForm.class), Mockito.anyLong());
	}
	
	private RealisateurDTO getJamesCameronRealisateur() {
		RealisateurDTO realisateur = new RealisateurDTO();
		realisateur.setId(1);
		realisateur.setNom("Cameron");
		realisateur.setPrenom("James");
		realisateur.setDateNaissance(LocalDate.of(1954, 8, 16));
		return realisateur;
	}

	private RealisateurDTO getPeterJacksonRealisateur() {
		RealisateurDTO realisateur = new RealisateurDTO();
		realisateur.setId(2);
		realisateur.setNom("Jackson");
		realisateur.setPrenom("Peter");
		realisateur.setDateNaissance(LocalDate.of(1961, 10, 31));
		return realisateur;
	}

	private FilmDTO getAvatarFilm() {
		FilmDTO film = new FilmDTO();
		film.setId(1);
		film.setTitre("Avatar");
		film.setDuree(162);
		film.setRealisateur(getJamesCameronRealisateur());
		film.setGenre(getScienceFictionGenre());
		return film;
	}

	private FilmDTO getLaCommunauteDeLAnneauFilm() {
		FilmDTO film = new FilmDTO();
		film.setId(2);
		film.setTitre("La communauté de l'anneau");
		film.setDuree(178);
		film.setRealisateur(getPeterJacksonRealisateur());
		film.setGenre(getFantasyGenre());
		return film;
	}

	private FilmDTO getTerminatorFilm() {
		FilmDTO film = new FilmDTO();
		film.setId(6);
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateur(getJamesCameronRealisateur());
		film.setGenre(getFantasyGenre());
		return film;
	}

	private FilmForm getTerminatorFilmForm() {
		FilmForm film = new FilmForm();
		film.setTitre("Terminator");
		film.setDuree(107);
		film.setRealisateurId(getJamesCameronRealisateur().getId());
		film.setGenreId(getScienceFictionGenre().getId());
		return film;
	}
	
	private RealisateurDTO getQuentinTarantinoRealisateur() {
		RealisateurDTO realisateur = new RealisateurDTO();
		realisateur.setId(3);
		realisateur.setNom("Tarantino");
		realisateur.setPrenom("Quentin");
		realisateur.setDateNaissance(LocalDate.of(1963, 3, 27));
		realisateur.setCelebre(false);
		return realisateur;
	}
	
	private RealisateurForm getQuentinTarantinoRealForm() {
		RealisateurForm realisateur = new RealisateurForm();
		realisateur.setNom("Tarantino");
		realisateur.setPrenom("Quentin");
		realisateur.setDateNaissance("1963-03-27");
		return realisateur;
	}
	
	private GenreDTO getScienceFictionGenre() {
		GenreDTO genre = new GenreDTO();
		genre.setId(1);
		genre.setGenre("science fiction");
		return genre;
	}
	
	private GenreDTO getFantasyGenre() {
		GenreDTO genre = new GenreDTO();
		genre.setId(2);
		genre.setGenre("fantasy");
		return genre;
	}
	
	private GenreDTO getActionGenre() {
		GenreDTO genre = new GenreDTO();
		genre.setId(3);
		genre.setGenre("action");
		return genre;
	}
	
	private GenreForm getActionGenreForm() {
		GenreForm genre = new GenreForm();
		genre.setGenre("action");
		return genre;
	}
	
}
