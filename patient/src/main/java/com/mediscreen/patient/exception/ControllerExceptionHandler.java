package com.mediscreen.patient.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that takes care of catching the API exceptions and assigning them the code and content of the request status.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);


    // ========================================================================

    /**
     * Handles a MethodArgumentTypeMismatchException.
     *
     * @param ex       the exception caught
     * @param request  the request that contains the metadata of the thrown exception
     * @return         the simplified custom error message of the thrown exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        logger.info("### Exception thrown with status code {} in {} and with message --> {}", message.getStatusCode(), request.getDescription(false), message.getMessage());
        return message;
    }

    // ========================================================================

    /**
     * Handles a MethodArgumentNotValidException.
     *
     * @param ex       the exception caught
     * @param request  the request that contains the metadata of the thrown exception
     * @return         the simplified custom error message of the thrown exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getBindingResult().getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(" / ")),
                request.getDescription(false)
        );

        logger.info("### Exception thrown with status code {} in {} and with message --> {}", message.getStatusCode(), request.getDescription(false), message.getMessage());
        return message;
    }

    // ========================================================================

    /**
     * Handles a PatientNotFoundException.
     *
     * @param ex       the exception caught
     * @param request  the request that contains the metadata of the thrown exception
     * @return         the simplified custom error message of the thrown exception
     */
    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(PatientNotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        logger.info("### Exception thrown with status code {} in {} and with message --> {}", message.getStatusCode(), request.getDescription(false), message.getMessage());
        return message;
    }

    // ========================================================================

    /**
     * Handles global exceptions.
     *
     * @param ex       the exception caught
     * @param request  the request that contains the metadata of the thrown exception
     * @return         the simplified custom error message of the thrown exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalException(Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        logger.info("### Exception thrown with status code {} in {} and with message --> {}", message.getStatusCode(), request.getDescription(false), message.getMessage());
        return message;
    }

}
