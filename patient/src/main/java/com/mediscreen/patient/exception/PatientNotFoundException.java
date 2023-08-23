package com.mediscreen.patient.exception;

/**
 * Exception to be thrown when the application doesn't find a given patient in database.
 */
public class PatientNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new patient not found exception.
     *
     * @param message  the message that gives a description of the exception that was thrown
     */
    public PatientNotFoundException(String message) {
        super(message);
    }

}
