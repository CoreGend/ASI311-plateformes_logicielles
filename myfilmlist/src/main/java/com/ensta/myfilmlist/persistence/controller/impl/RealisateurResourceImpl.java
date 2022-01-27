package com.ensta.myfilmlist.persistence.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ensta.myfilmlist.dto.RealisateurDTO;
import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.RealisateurForm;
import com.ensta.myfilmlist.persistence.controller.RealisateurResource;
import com.ensta.myfilmlist.service.MyFilmsService;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/realisateur")
public class RealisateurResourceImpl implements RealisateurResource{
	@Autowired
	private MyFilmsService myFilmsService;
	
	@Override
	public ResponseEntity<List<RealisateurDTO>> getAllRealisateurs()
		throws ControllerException{
		try {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(myFilmsService.findAllRealisateurs());
		} catch(ServiceException e) {
//			System.out.println("Could not connect to database");
//			e.printStackTrace();
			throw new ControllerException();
		}
	}
	
	@Override
	public ResponseEntity<RealisateurDTO> getRealById(long id)
			throws ControllerException{
		RealisateurDTO realisateurDTO;
		try {
			realisateurDTO = myFilmsService.findRealById(id);
		} catch(ServiceException e) {
//			System.out.println("Erreur de traitement");
//			e.printStackTrace();
			throw new ControllerException();
		}
		
		if(realisateurDTO == null)
			throw new ControllerException();
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(realisateurDTO);
	}
	
	@Override
	public ResponseEntity<RealisateurDTO> createRealisateur(
			@Validated RealisateurForm realisateurForm)
		throws ControllerException{
		RealisateurDTO realisateurDTO;
		try {
			realisateurDTO = myFilmsService.createRealisateur(realisateurForm);
		} catch(Exception e) {
//			System.out.println("Erreur de traitement");
//			e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(realisateurDTO);
	}
	
	@Override
	public ResponseEntity<RealisateurDTO> updateRealisateur(long id) 
			throws ControllerException{
		RealisateurDTO r;
		try {
			r = myFilmsService.updateRealisateur(id);
		}catch(ServiceException e) {
//			System.out.println("Erreur de traitement");
//			e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(r);
	}
	
	@Override
	@Transactional
	public ResponseEntity<RealisateurDTO> updateReal(@Validated RealisateurForm realisateurForm, long id)
		throws ControllerException{
		RealisateurDTO realDTO;
		try {
			realDTO = myFilmsService.updateReal(realisateurForm, id);
		} catch (ServiceException e) {
			throw new ControllerException();
		}
	return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(realDTO);
	}
	
	@Override
	@Transactional
	public ResponseEntity<?> deleteReal(long id) throws ControllerException{
		try {
			System.out.println("suppression du real d'id " + id);
			myFilmsService.deleteReal(id);
		}catch(ServiceException e) {
//			System.out.println("Erreur de traitement");
			// e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.body(null);
	}
}
