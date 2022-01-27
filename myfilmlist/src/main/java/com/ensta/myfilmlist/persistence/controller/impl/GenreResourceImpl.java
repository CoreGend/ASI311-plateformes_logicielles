package com.ensta.myfilmlist.persistence.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ensta.myfilmlist.dto.GenreDTO;
import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.GenreForm;
import com.ensta.myfilmlist.persistence.controller.GenreResource;
import com.ensta.myfilmlist.service.MyFilmsService;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/genre")
public class GenreResourceImpl implements GenreResource{
	@Autowired
	private MyFilmsService myFilmsService;
	
	@Override
	public ResponseEntity<List<GenreDTO>> getAllGenres()
		throws ControllerException{
		try {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(myFilmsService.findAllGenres());
		}catch(ServiceException e) {
//			System.out.println("Could not connect to database");
//			e.printStackTrace();
			throw new ControllerException();
		}
	}
	
	@Override
	public ResponseEntity<GenreDTO> getGenreById(long id)
		throws ControllerException{
		GenreDTO genreDTO;
		try {
			genreDTO = myFilmsService.findGenreById(id);
		}catch(Exception e) {
//		System.out.println("Erreur de récupération du genre");
			throw new ControllerException();
		}
		if (genreDTO==null)
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(null);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(genreDTO);
	}
	
	@Override
	public ResponseEntity<GenreDTO> createGenre(
			@Validated GenreForm genreForm)
		throws ControllerException{
		GenreDTO genreDTO;
		try {
			genreDTO=myFilmsService.createGenre(genreForm);
		} catch(ServiceException e) {
	//		System.out.println("Erreur de création du genre");
	//		e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(genreDTO);
	}
	
	@Override
	public ResponseEntity<?> deleteGenre(long id) throws ControllerException{
		try {
			myFilmsService.deleteGenre(id);
		}catch(ServiceException e) {
			// System.out.println("Erreur de traitement");
			// e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.body(null);
	}
	
	@Override
	public ResponseEntity<GenreDTO> updateGenre(
			@Validated GenreForm f, long id)
		throws ControllerException{
		GenreDTO genreDTO;
		try {
			genreDTO = myFilmsService.updateGenre(f, id);
		} catch(ServiceException e) {
			// System.out.println("Impossible de mettre à jour le genre");
			// e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(genreDTO);
	}
	
}