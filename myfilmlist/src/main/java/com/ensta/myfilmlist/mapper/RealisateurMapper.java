package com.ensta.myfilmlist.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.form.RealisateurForm;
import com.ensta.myfilmlist.model.Realisateur;

/**
 * Effectue les conversions des Realisateurs entre les couches de l'application.
 */
public class RealisateurMapper {

	/**
	 * Convertit une liste de realisateurs en liste de DTO.
	 * 
	 * @param realisateurs la liste des realisateurs
	 * @return Une liste non nulle de dtos construite a partir de la liste des realisateurs.
	 */
	public static List<RealisateurDTO> convertRealisateurToRealisateurDTOs(List<Realisateur> realisateurs) {
		return realisateurs.stream()
				.map(RealisateurMapper::convertRealisateurToRealisateurDTO)
				.collect(Collectors.toList());
	}

	/**
	 * Convertit un realisateur en DTO.
	 * 
	 * @param realisateur le realisateur a convertir
	 * @return Un DTO construit a partir des donnees du realisateur.
	 */
	public static RealisateurDTO convertRealisateurToRealisateurDTO(Realisateur realisateur) {
		RealisateurDTO realisateurDTO = new RealisateurDTO();
		realisateurDTO.setId(realisateur.getId());
		realisateurDTO.setNom(realisateur.getNom());
		realisateurDTO.setPrenom(realisateur.getPrenom());
		realisateurDTO.setDateNaissance(realisateur.getDateNaissance());
		realisateurDTO.setCelebre(realisateur.getCelebre());

		return realisateurDTO;
	}

	/**
	 * Convertit un DTO en realisateur.
	 * 
	 * @param realisateurDTO le DTO a convertir
	 * @return Un Realisateur construit a partir des donnes du DTO.
	 */
	public static Realisateur convertRealisateurDTOToRealisateur(RealisateurDTO realisateurDTO) {
		Realisateur realisateur = new Realisateur();
		realisateur.setId(realisateurDTO.getId());
		realisateur.setNom(realisateurDTO.getNom());
		realisateur.setPrenom(realisateurDTO.getPrenom());
		realisateur.setDateNaissance(realisateurDTO.getDateNaissance());
		realisateur.setCelebre(realisateurDTO.isCelebre());

		return realisateur;
	}
	
	/**
	 * Convertit un Form en réalisateur.
	 * --
	 * @param realisateurForm le Form à convertir
	 * @return un réalisateur construit à partir des données du Form.
	 */
	public static Realisateur convertRealisateurFormToRealisateur(RealisateurForm realisateurForm) {
		Realisateur realisateur = new Realisateur();
		realisateur.setPrenom(realisateurForm.getPrenom());
		realisateur.setNom(realisateurForm.getNom());
		realisateur.setCelebre(false);
		realisateur.setDateNaissance(LocalDate.parse(realisateurForm.getDateNaissance()));
		
		return realisateur;
	}
}
