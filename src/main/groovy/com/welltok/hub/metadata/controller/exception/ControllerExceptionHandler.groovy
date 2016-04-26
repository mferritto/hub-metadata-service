package com.welltok.hub.metadata.controller.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import com.welltok.hub.metadata.model.ApiException
import com.welltok.hub.metadata.model.DataAlreadyUpdatedException
import com.welltok.hub.metadata.model.DataNotFoundException
import com.welltok.hub.metadata.model.NothingChangedException
import com.welltok.hub.metadata.model.ValidationException

@ControllerAdvice
class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Catch the ValidationException and return custom 422 response status.
	 * 
	 * @param e - the exception thrown
	 * @param request - the webrequest
	 * @return - custom json response
	 */
	@ExceptionHandler( ValidationException.class )
	protected ResponseEntity<Object> handleValidationError(Exception e, WebRequest request) {
		def exception = (ValidationException) e

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		def apiException = new ApiException()
		apiException.statusCode = 422
		apiException.message = e.message
		apiException.stackTrace = e.stackTrace.join("\n")
		apiException.exceptionClass = e.class.toString()
		apiException.errors = exception.errors

		return handleExceptionInternal(e, apiException, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	/**
	 * Catch the DataNotFoundException and return custom 404 response status.
	 *
	 * @param e - the exception thrown
	 * @param request - the webrequest
	 * @return - custom json response
	 */	
	@ExceptionHandler( DataNotFoundException.class )
	protected ResponseEntity<Object> handleDataNotFoundException(Exception e, WebRequest request) {
		def exception = (DataNotFoundException) e

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		def apiException = new ApiException()
		apiException.statusCode = 404
		apiException.message = e.message
		apiException.stackTrace = e.stackTrace.join("\n")
		apiException.exceptionClass = e.class.toString()

		return handleExceptionInternal(e, apiException, headers, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler( DataAlreadyUpdatedException.class )
	protected ResponseEntity<Object> handleDataAlreadyUpdatedException(Exception e, WebRequest request) {
		def exception = (DataAlreadyUpdatedException) e

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		def apiException = new ApiException()
		apiException.statusCode = 409
		apiException.message = e.message
		apiException.stackTrace = e.stackTrace.join("\n")
		apiException.exceptionClass = e.class.toString()

		return handleExceptionInternal(e, apiException, headers, HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler( NothingChangedException.class )
	protected ResponseEntity<Object> handleNothingChangedException(Exception e, WebRequest request) {
		def exception = (NothingChangedException) e

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		def apiException = new ApiException()
		apiException.statusCode = 304
		apiException.message = e.message
		apiException.stackTrace = e.stackTrace.join("\n")
		apiException.exceptionClass = e.class.toString()

		return handleExceptionInternal(e, apiException, headers, HttpStatus.NOT_MODIFIED, request);
	}

	/**
	 * Catch the GenericException and return custom 500 response status
	 * Always at the end of the class
	 * 
	 */
	@ExceptionHandler( Exception.class )
	protected ResponseEntity<Object> handleGenericException(Exception e, WebRequest request) {
		def exception = (Exception) e

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		def apiException = new ApiException()
		apiException.statusCode = 500
		apiException.message = e.message
		apiException.stackTrace = e.stackTrace.join("\n")
		apiException.exceptionClass = e.class.toString()


		return handleExceptionInternal(e, apiException, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
}

