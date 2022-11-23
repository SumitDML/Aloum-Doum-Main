package com.aloumDaum.user.exception;

import com.aloumDaum.user.model.response.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

	private static final String EXCEPTION = "exception :";


	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) ->{

			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});
		return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ResponseModel<?>> handleValidationException(final ValidationException exception) {
		final ResponseModel<?> reponseModel = new ResponseModel<>(HttpStatus.BAD_REQUEST, exception.getMessage(), null,null);
		logger.error(EXCEPTION, exception);
		return new ResponseEntity<>(reponseModel, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ResponseModel> handleEntityNotFound(final EntityNotFoundException exception) {
		final ResponseModel reponseModel = new ResponseModel(HttpStatus.NOT_FOUND, exception.getMessage(),null,null);
		logger.error(EXCEPTION, exception);
		return new ResponseEntity<>(reponseModel, HttpStatus.NOT_FOUND);
	}

}