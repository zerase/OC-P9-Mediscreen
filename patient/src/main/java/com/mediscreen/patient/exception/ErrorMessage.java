package com.mediscreen.patient.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * Class that provides details of an error thrown by an exception as a short custom message.
 */
@AllArgsConstructor
@Getter
public class ErrorMessage {

    /**
     * The status code number of the response to the request triggered by the exception.
     */
    private int statusCode;

    /**
     * The timestamp of the response to the request triggered by the exception.
     */
    private Date timestamp;

    /**
     * The message of the response to the request triggered by the exception.
     */
    private String message;

    /**
     * The description of the response to the request triggered by the exception.
     */
    private String description;

}
