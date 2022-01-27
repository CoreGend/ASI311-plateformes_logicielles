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

import com.ensta.myfilmlist.dto.FilmDTO;
import com.ensta.myfilmlist.exception.ControllerException;
import com.ensta.myfilmlist.exception.ServiceException;
import com.ensta.myfilmlist.form.FilmForm;
import com.ensta.myfilmlist.persistence.controller.FilmResource;
import com.ensta.myfilmlist.service.MyFilmsService;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/film")
public class FilmResourceImpl implements FilmResource {
	@Autowired
	private MyFilmsService myFilmsService;
	
	@Override
	public ResponseEntity<List<FilmDTO>> getAllFilms()
			throws ControllerException{
		try {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(myFilmsService.findAllFilms());
		} catch(ServiceException e) {
//			System.out.println("Could not connect to database");
//			e.printStackTrace();
			throw new ControllerException();
		}
	}
	
	@Override
	public ResponseEntity<FilmDTO> getFilmById(long id)
			throws ControllerException{
		FilmDTO filmDTO;
		try {
			filmDTO = myFilmsService.findFilmById(id);
		} catch(Exception e) {
//			System.out.println("Erreur inconnue");
			throw new ControllerException();
		}
		
		if(filmDTO == null)
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(null);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(filmDTO);
	}
	
	@Override
	@Transactional
	public ResponseEntity<FilmDTO> createFilm(
			@Validated FilmForm filmForm)
		throws ControllerException{
		FilmDTO filmDTO;
		try {
			filmDTO = myFilmsService.createFilm(filmForm);
		} catch(ServiceException e) {
//			System.out.println("Erreur de traitement");
//			e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(filmDTO);
	}
	
	@Override
	public ResponseEntity<?> deleteFilm(long id) throws ControllerException{
		try {
			myFilmsService.deleteFilm(id);
		}catch(ServiceException e) {
//			System.out.println("Erreur de traitement");
			// e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.body(null);
	}

	@Override
	public ResponseEntity<List<FilmDTO>> getFilmByRealisateurId(long id)
		throws ControllerException{
		List<FilmDTO> films;
		try{
			films = myFilmsService.findFilmByRealisateurId(id);
		}catch(ServiceException e){
//			System.out.println("Impossible de récupérer les films");
			// e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(films);
	}

	@Override
	@Transactional
	public ResponseEntity<FilmDTO> updateFilm(@Validated FilmForm f, long id)
		throws ControllerException{
		FilmDTO filmDTO;
		try {
			filmDTO = myFilmsService.updateFilm(f, id);
		} catch (ServiceException e) {
			throw new ControllerException();
		}
	return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(filmDTO);
	}
	
	@Override
	public ResponseEntity<List<FilmDTO>> getFilmByGenreId(long id)
			throws ControllerException{
		List<FilmDTO> films;
		try{
			films = myFilmsService.findFilmByGenreId(id);
		}catch(ServiceException e){
//			System.out.println("Impossible de récupérer les films");
			// e.printStackTrace();
			throw new ControllerException();
		}
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(films);
	}

}
