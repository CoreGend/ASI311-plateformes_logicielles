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

import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.form.RealisateurForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Api(tags = "Realisateurs")
@Tag(name="Realisateurs", description="Opération sur les réalisateurs")
public interface RealisateurResource {
	@GetMapping
	@ApiOperation(value = "Lister les réalisateurs", notes="Permet de renvoyer la liste de tous les réalisateurs.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code=200, message="La liste des réalisateurs a été renvoyée correctement"),
			@ApiResponse(code=404, message="La liste des réalisateurs n'a pas été trouvée")
	})
	public ResponseEntity<List<RealisateurDTO>> getAllRealisateurs()
			throws ControllerException;
	
	@GetMapping("/{id}")
	@ApiOperation(value="Trouver un réalisateur", notes="Permet de renvoyer un réalisateur à partir de son identifiant",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="Le réalisateur a bien été trouvé et renvoyé"),
			@ApiResponse(code=404, message="Le réalisateur n'a pas été trouvé")
	})
	ResponseEntity<RealisateurDTO> getRealById(
			@ApiParam(name="id", value="id du réalisateur recherché")
			@PathVariable long id)
		throws ControllerException;
	
	@PostMapping
	@ApiOperation(value="Créer un réalisateur", notes="Permet d'ajouter un réalisateur à la base de données.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=201, message="Le réalisateur a été ajouté à la base de données")
	})
	@Transactional
	ResponseEntity<RealisateurDTO> createRealisateur(
			@RequestBody @Valid RealisateurForm realisateurForm)
		throws ControllerException;
	
	@PostMapping("/u/{id}")
	@ApiOperation(value="Mettre à jour un réalisateur", notes="Permet de mettre à jour un réalisateur de la base de données",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=204, message="Le réalisateur a bien été mis à jour")
	})
	@Transactional
	ResponseEntity<RealisateurDTO> updateRealisateur(
			@PathVariable long id)
		throws ControllerException;
	
	@PostMapping("/{id}")
	@ApiOperation(value="Mettre à jour un réalisateur", notes="Permet de mettre à jour un réalisateur dans la base de données.",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=201, message="Le réalisateur a été mis à jour")
	})
	ResponseEntity<RealisateurDTO> updateReal(@RequestBody @Valid RealisateurForm realisateurForm, @PathVariable long id) throws ControllerException;

	@GetMapping("/d/{id}")
	@ApiOperation(value="Supprimer un réalisateur", notes="Permet de supprimer un réalsiateur de la base de données (supprime aussi tous les films associés)",
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value= {
			@ApiResponse(code=204, message="Le réalisateur a bien été supprimé")
	})
	@Transactional
	ResponseEntity<?> deleteReal(@PathVariable long id) throws ControllerException;

}
