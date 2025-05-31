package com.common.exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.common.utils.Constant;
import com.common.utils.LogManager;
import com.common.utils.Logger;
import com.common.utils.MessageType;
import com.common.utils.RestResponse;
import com.common.utils.RestUtils;
import com.common.utils.Utils;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger LOGGER = Logger.getLogger(RestExceptionHandler.class);

	@Autowired
	private Environment env;

	@Value("${application.base.url}")
	private String environment;

	@Value("${spring.servlet.multipart.max-file-size}")
	private String maxFileSize;

	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(
				new RestResponse<>(convertConstraintViolation(ex), null, Boolean.FALSE, status.value()),
				HttpStatus.BAD_REQUEST);
	}

	protected String convertConstraintViolation(MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<String> errorMessages = new ArrayList<>();
		for (FieldError c : fieldErrors) {
			errorMessages.add(c.getField().concat(" - ")
					.concat(c.getDefaultMessage().charAt(0) == '{'
							? env.resolvePlaceholders("$".concat(c.getDefaultMessage()))
							: c.getDefaultMessage()));
		}
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br>" + errorMessages.toString(),
				environment + ": MethodArgumentNotValidException");
		return String.join(", ", errorMessages);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<RestResponse<?>> handle(ConstraintViolationException constraintViolationException) {
		Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
		String errorMessage = "";
		if (!violations.isEmpty()) {
			List<String> errorMessages = new ArrayList<>();
			violations.forEach(violation -> errorMessages.add(violation.getMessage().charAt(0) == '{'
					? env.resolvePlaceholders("$".concat(violation.getMessage()))
					: violation.getMessage()));
			errorMessage = String.join(", ", errorMessages);
		} else {
			errorMessage = "ConstraintViolationException occurred.";
		}
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br>" + errorMessage,
				environment + ": ConstraintViolationException");
		return RestUtils.errorResponse(errorMessage, null, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { DataIntegrityViolationException.class })
	protected ResponseEntity<RestResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {
		LOGGER.error(ex.getRootCause().getLocalizedMessage(), ex);
		LogManager.error(getClass(), ex.getRootCause().getLocalizedMessage(), MessageType.Error);
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br>" + ex.getRootCause().getLocalizedMessage(),
				environment + ": DataIntegrityViolationException");
		return RestUtils.errorResponse(ex.getRootCause().getLocalizedMessage(), ex.getLocalizedMessage(),
				HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(value = { LazyInitializationException.class })
	protected ResponseEntity<?> handleLazyInitializationException(LazyInitializationException ex, WebRequest request) {
		LOGGER.error(ex.getLocalizedMessage(), ex);
		LogManager.error(getClass(), ex.getLocalizedMessage(), MessageType.Error);
		String trace = Utils.displayErrorForWeb(ex.getStackTrace());
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br/>" + ex.getLocalizedMessage()
						+ "<br>StackTrace:<br/>" + trace,
				environment + ": LazyInitializationException");
		return RestUtils.errorResponse(ex.getLocalizedMessage(), ex.getLocalizedMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(value = { IncorrectResultSizeDataAccessException.class })
	protected ResponseEntity<RestResponse<?>> handleIncorrectResultSizeDataAccessException(
			IncorrectResultSizeDataAccessException ex, WebRequest request) {
		LOGGER.error(ex.getRootCause().getLocalizedMessage(), ex);
		LogManager.error(getClass(), ex.getRootCause().getLocalizedMessage(), MessageType.Error);
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br>" + ex.getRootCause().getLocalizedMessage(),
				environment + ": IncorrectResultSizeDataAccessException");
		return RestUtils.errorResponse(ex.getRootCause().getLocalizedMessage(), ex.getLocalizedMessage(),
				HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(value = { SQLException.class })
	protected ResponseEntity<RestResponse<?>> handleSQLException(SQLException ex, WebRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		LogManager.error(getClass(), ex.getLocalizedMessage(), MessageType.Error);
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br>" + ex.getLocalizedMessage(),
				environment + ": SQLException");
		return RestUtils.errorResponse(ex.getLocalizedMessage(), ex.getLocalizedMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<RestResponse<?>> handleUnknownException(Exception ex, WebRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		LogManager.error(getClass(), ex.getMessage(), MessageType.Error);
		LOGGER.error(ex.getClass().getName());
		String trace = Utils.displayErrorForWeb(ex.getStackTrace());
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br/>" + ex.getLocalizedMessage()
						+ "<br>StackTrace:<br/>" + trace,
				environment + ": Exception");
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), BaseException.DEFAULT_HTTP_STATUS);
	}

	@ExceptionHandler(value = { NullPointerException.class })
	protected ResponseEntity<RestResponse<?>> handleNullPointerException(NullPointerException ex, WebRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		LogManager.error(getClass(), ex.getMessage(), MessageType.Error);
		LOGGER.error(ex.getClass().getName());
		String trace = Utils.displayErrorForWeb(ex.getStackTrace());
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br/>" + ex.getLocalizedMessage()
						+ "<br>StackTrace:<br/>" + trace,
				environment + ": NullPointerException");
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), BaseException.DEFAULT_HTTP_STATUS);
	}

	/*
	 * @ExceptionHandler(MaxUploadSizeExceededException.class) public
	 * ResponseEntity<RestResponse<?>>
	 * handleMaxSizeException(MaxUploadSizeExceededException exc, HttpServletRequest
	 * request, HttpServletResponse response) { LogManager.info(getClass(),
	 * "FileUploadException exception handler executed", MessageType.Info); String
	 * message = "Unable to upload. File is too large. Maximum upload size is " +
	 * maxFileSize; sendEmail(LOGGER.getValue(Constant.REQUEST_KEY), "RequestKey: "
	 * + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: " +
	 * LOGGER.getValue(Constant.REQUEST_ID) + "<br>" + message, environment +
	 * ": MaxUploadSizeExceededException"); return RestUtils.errorResponse(message,
	 * null, HttpStatus.EXPECTATION_FAILED); }
	 */
	
	@ExceptionHandler(value = { PessimisticLockingFailureException.class, UnexpectedRollbackException.class })
	protected ResponseEntity<RestResponse<?>> handlePessimisticLockingFailureException(RuntimeException ex,
			WebRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		LogManager.error(getClass(), ex.getMessage(), MessageType.Error);
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br/>" + ex.getLocalizedMessage(),
				environment + ": PessimisticLockingFailureException/UnexpectedRollbackException");
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<RestResponse<?>> handleMultipartException(MultipartException ex, HttpServletRequest request,
			HttpServletResponse response) {
		LogManager.info(getClass(), "MultipartException exception handler executed", MessageType.Info);
		String message = "Unable to upload. MultipartException occurred.";
		sendEmail(LOGGER.getValue(Constant.REQUEST_KEY),
				"RequestKey: " + LOGGER.getValue(Constant.REQUEST_KEY) + "<br>RequestId: "
						+ LOGGER.getValue(Constant.REQUEST_ID) + "<br>" + message,
				environment + ": MultipartException");
		return RestUtils.errorResponse(message, null, HttpStatus.EXPECTATION_FAILED);
	}

	private void sendEmail(String requestKey, String message, String subject) {
		
	}
}
