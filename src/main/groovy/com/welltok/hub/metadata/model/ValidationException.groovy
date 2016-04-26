package com.welltok.hub.metadata.model

class ValidationException extends Exception{

	List<ValidationError> errors
	
	ValidationException(String message, List<ValidationError> e) {
		
		super(message)
		errors = e
		
	}
}
