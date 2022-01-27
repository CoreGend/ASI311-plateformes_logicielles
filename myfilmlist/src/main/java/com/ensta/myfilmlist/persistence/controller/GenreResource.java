package com.ensta.myfilmlist.persistence.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.form.GenreForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

// L'API s'appelle "Genre" et utilise le tag "Genre"
// Le tag "Genre" contient la description de l'API
@Api(tags="Genre")
@Tag(name="Genre", description="Opérations sur les genres")
public interface GenreResource {
	@GetMapping
	@ApiOperation(value="Lister les genres", notes="Permet de renvoyer la liste de tous les genres.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="La liste des genres a été renvoyée correctement"),
			@ApiResponse(code=404, message="La liste des genres n'a pas été trouvée")
	})
	ResponseEntity<List<GenreDTO>> getAllGenres() throws ControllerException;
	
	@GetMapping("/{id}")
	@ApiOperation(value="Trouver un genre", notes="Permet de renvoyer un genre à partir de son identifiant",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="Le genre a bien été trouvé et renvoyé"),
			@ApiResponse(code=404, message="Le genre n'a pas été trouvé")
	})
	ResponseEntity<GenreDTO> getGenreById(
			@ApiParam(name="id", value="id du genre recherché")
			@PathVariable long id)
		throws ControllerException;
	
	@PostMapping
	@ApiOperation(value="Créer un genre", notes="Permet d'ajouter un genre à la base de données.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=201, message="Le genre a été ajouté à la base de données")
	})
	ResponseEntity<GenreDTO> createGenre(
			@RequestBody @Valid GenreForm genreForm)
		throws ControllerException;
	
	@GetMapping("/d/{id}")
	@ApiOperation(value="Supprimer un genre", notes="Permet de supprimer un genre de la base de données",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=204, message="Le genre a bien été supprimé")
	})
	ResponseEntity<?> deleteGenre(
			@PathVariable long id)
		throws ControllerException;
	
	@PostMapping("/{id}")
	@ApiOperation(value="Mettre à jour un genre", notes="Permet de mettre à jour un genre dans la base de données.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=201, message="Le genre a été mis à jour")
	})
	ResponseEntity<GenreDTO> updateGenre(@RequestBody @Valid GenreForm f, @PathVariable long id)
			throws ControllerException;
}