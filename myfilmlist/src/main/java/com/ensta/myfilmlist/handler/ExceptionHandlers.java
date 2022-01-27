package com.ensta.myfilmlist.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ensta.myfilmlist.exception.ControllerException;

@RestControllerAdvice
public class ExceptionHandlers {
	@ExceptionHandler(ControllerException.class)
	public ResponseEntity<String> handleControllerException
		(ControllerException e, WebRequest w){
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body("Erreur dans la requête");
	}
	
	@ExceptionHandler(BindException.class)
	public ResponseEntity<String> handleBindException
		(BindException e, WebRequest w){
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body("Les paramètres fournis sont incorrects");
	}
}
