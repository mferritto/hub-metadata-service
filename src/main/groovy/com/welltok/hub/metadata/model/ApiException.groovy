package com.welltok.hub.metadata.model

class ApiException {

	String statusCode
	String message
	String stackTrace
	String exceptionClass
	List<ValidationError> errors
}
