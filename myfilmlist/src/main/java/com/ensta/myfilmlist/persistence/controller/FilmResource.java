package com.ensta.myfilmlist.persistence.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.form.FilmForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ensta.myfilmlist.dto.FilmDTO;

//L'API s'appelle "Film" et utilise le Tag "Film"
//Le tag "Film" contient la description de l'API
@Api(tags = "Film")
@Tag(name="Film", description="Opération sur les films")
public interface FilmResource {

	@GetMapping
	@ApiOperation(value = "Lister les films", notes="Permet de renvoyer la liste de tous les films.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code=200, message="La liste des films a été renvoyée correctement"),
			@ApiResponse(code=404, message="La liste des films n'a pas été trouvée")
	})
	ResponseEntity<List<FilmDTO>> getAllFilms()
		throws ControllerException;
	
	@GetMapping("/{id}")
	@ApiOperation(value="Trouver un film", notes="Permet de renvoyer un film à partir de son identifiant",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="Le film a bien été trouvé et renvoyé"),
			@ApiResponse(code=404, message="Le film n'a pas été trouvé")
	})
	ResponseEntity<FilmDTO> getFilmById(
			@ApiParam(name="id", value="id du film recherché")
			@PathVariable long id)
		throws ControllerException;
	
	@PostMapping
	@ApiOperation(value="Créer un film", notes="Permet d'ajouter un film à la base de données.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=201, message="Le film a été ajouté à la base de données")
	})
	ResponseEntity<FilmDTO> createFilm(
			@RequestBody @Valid FilmForm filmForm)
		throws ControllerException;
	
	@GetMapping("/d/{id}")
	@ApiOperation(value="Supprimer un film", notes="Permet de supprimer un film de la base de données",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=204, message="Le film a bien été supprimé")
	})
	@Transactional
	ResponseEntity<?> deleteFilm(
			@PathVariable long id)
		throws ControllerException;

	@GetMapping("/r/{id}")
	@ApiOperation(value="Trouver les films d'un réalisateur donné", 
		notes="Permet de renvoyer tous les films réalisés par le réalisateur en argument",
		produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value={
		@ApiResponse(code=200, message="La liste de film a été trouvée et renvoyée"),
		@ApiResponse(code=404, message="La liste de films n'a pas été trouvée (réalisateur peut être inexistant)")
	})
	ResponseEntity<List<FilmDTO>> getFilmByRealisateurId(
		@ApiParam(name="id", value="id du réalisateur souhaité")
		@PathVariable long id
	)
		throws ControllerException;
	
	@PostMapping("/{id}")
	@ApiOperation(value="Mettre à jour un film", notes="Permet de mettre à jour un film dans la base de données.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=201, message="Le film a été mis à jour")
	})
	ResponseEntity<FilmDTO> updateFilm(@RequestBody @Valid FilmForm f, @PathVariable long id)
			throws ControllerException;
	
	@GetMapping("/g/{id}")
	@ApiOperation(value="Trouver les films d'un genre donné", 
		notes="Permet de renvoyer tous les films réalisés par le genre en argument",
		produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value={
		@ApiResponse(code=200, message="La liste de film a été trouvée et renvoyée"),
		@ApiResponse(code=404, message="La liste de films n'a pas été trouvée (genre peut être inexistant)")
	})
	ResponseEntity<List<FilmDTO>> getFilmByGenreId(
		@ApiParam(name="id", value="id du genre souhaité")
		@PathVariable long id
	)
		throws ControllerException;
}
