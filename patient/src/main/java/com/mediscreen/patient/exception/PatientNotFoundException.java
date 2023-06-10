package com.mediscreen.patient.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(PatientNotFoundException.class);

    public PatientNotFoundException(String message) {
        super(message);
        logger.error("# Returned error --> {}", message);
    }

}