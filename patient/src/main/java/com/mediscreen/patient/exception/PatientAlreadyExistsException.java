package com.mediscreen.patient.exception;

/**
 * Exception to be thrown when trying to save a patient that already exists in database.
 */
public class PatientAlreadyExistsException extends RuntimeException {

    /**
     * Instantiates a new patient already exists exception.
     *
     * @param message  the message that gives a description of the exception that was thrown
     */
    public PatientAlreadyExistsException(String message) {
        super(message);
    }

}
