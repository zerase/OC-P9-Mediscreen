package com.mediscreen.patient.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * Class that provides details of an error thrown by an exception as a short custom message.
 */
@Schema(description = "Error model information")
@AllArgsConstructor
@Getter
public class ErrorMessage {

    /**
     * The status code number of the response to the request triggered by the exception.
     */
    @Schema(description = "Status code number of the error", example = "404")
    private int statusCode;

    /**
     * The timestamp of the response to the request triggered by the exception.
     */
    @Schema(description = "Timestamp of the error", example = "2022-12-31T12:34:56.789+00:00")
    private Date timestamp;

    /**
     * The message of the response to the request triggered by the exception that should help developers to debug.
     */
    @Schema(description = "Message of the error", example = "Patient doesn't exist")
    private String message;

    /**
     * The description of the response to the request triggered by the exception that should help developers to debug.
     */
    @Schema(description = "Short description of the error as an uri", example = "uri=/patients/123")
    private String description;

}
