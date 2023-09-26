package com.mediscreen.patient.controller;

import com.mediscreen.patient.exception.ErrorMessage;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages endpoints of the Patient API.
 */
@Tag(name = "Patient", description = "Patient management API")
@RestController
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    /**
     * Component that handles business logic operations for patients.
     */
    private final PatientService patientService;

    /**
     * Instantiates a new patient controller.
     *
     * @param patientService  the service linked to patient business logic
     */
    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    // === ADD NEW PATIENT ====================================================

    /**
     * Adds patient information to database.
     *
     * @param patientToAdd  the patient information to add to database
     * @return              the newly created patient information and status of the request
     */
    @Operation(
            summary = "Create a new patient",
            description = "Create and save a new Patient object. The response is a Patient object with an automatically generated id, first name, last name, date of birth, gender, address, phone number."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad request - The query failed due to a validation error on an input.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "Conflict - The Patient object with the given entries already exists.", content = {@Content(schema = @Schema())})
    })
    @PostMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> addNewPatient(@Valid @RequestBody Patient patientToAdd) {
        logger.debug("### Request called --> POST /patients");

        Patient addedPatient = patientService.createPatient(patientToAdd);

        logger.info("### New patient successfully added");
        return new ResponseEntity<>(addedPatient, HttpStatus.CREATED);
    }

    // === GET ALL PATIENTS ===================================================

    /**
     * Lists information of all patients in database.
     *
     * @return  the list of all patients with their information and status of the request
     */
    @Operation(
            summary = "Retrieve all patients",
            description = "Get all Patient objects. The response is a collection of Patient objects, each containing entries with id, last name, first name, date of birth, gender, address, phone number."
    )
    @Parameters({
            @Parameter(name = "lastName", description = "The keyword to be searched on")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "204", description = "No Content - No patient found", content = {@Content(schema = @Schema())})
    })
    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getAllPatients(@RequestParam(value = "lastName", required = false) String keyword) {
        logger.debug("### Request called --> GET /patients");

        List<Patient> allPatientsList = new ArrayList<>();

        if(keyword == null) {
            allPatientsList = patientService.readAllPatients();
        } else {
            allPatientsList = patientService.readAllPatientsByLastName(keyword);
        }

        if (allPatientsList.isEmpty()) {
            logger.info("### Empty list of patients returned");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("### Retrieved list of all patients successfully");
        return new ResponseEntity<>(allPatientsList, HttpStatus.OK);
    }

    // === GET PATIENT ========================================================

    /**
     * Gets information of the patient with the given id.
     *
     * @param patientId  the id of the patient to retrieve information from database
     * @return           the information of the patient with the given id and status of the request
     */
    @Operation(
            summary = "Retrieve a patient by id",
            description = "Get a Patient object by specifying its id. The response is a Patient object with id, first name, last name, date of birth, gender, address, phone number."
    )
    @Parameters({
            @Parameter(name = "id", description = "Identifier of a specific patient", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid id supplied", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - Patient not found or doesn't exist", content = {@Content(schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping(value = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") Integer patientId) {
        logger.debug("### Request called --> GET /patients/{}", patientId);

        Patient retrievedPatient = patientService.readPatient(patientId);

        logger.info("### Patient returned successfully");
        return new ResponseEntity<>(retrievedPatient, HttpStatus.OK);
    }

    // === UPDATE PATIENT =====================================================

    /**
     * Updates information of the patient with the given id.
     *
     * @param patientId  the id of the patient to update
     * @param patient    the patient whose information have to be updated in database
     * @return           the updated information of the patient with the given id and status of the request
     */
    @Operation(
            summary = "Update an existing patient by id",
            description = "Update and save a Patient object. The response is a Patient object with id, first name, last name, date of birth, gender, address, phone number."
    )
    @Parameters({
            @Parameter(name = "id", description = "Identifier of a specific patient", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad request - The query failed due to a validation error on an input.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - Patient not found or doesn't exist", content = {@Content(schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PutMapping(value = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> updatePatientById(@PathVariable("id") Integer patientId, @Valid @RequestBody Patient patient) {
        logger.debug("### Request called --> PUT /patients/{}", patientId);

        Patient patientUpdated = patientService.updatePatient(patientId, patient);

        logger.info("### Updated patient successfully");
        return new ResponseEntity<>(patientUpdated, HttpStatus.OK);
    }

    // === DELETE PATIENT =====================================================

    /**
     * Delete information of the patient with the given id.
     *
     * @param patientId  the id of the patient whose data is to be deleted from database
     * @return           the status of the request
     */
    @Operation(
            summary = "Delete an existing patient by id",
            description = "Delete a Patient object."
    )
    @Parameters({
            @Parameter(name = "id", description = "Identifier of a specific patient", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found - Patient not found or doesn't exist", content = {@Content(schema = @Schema(implementation = ErrorMessage.class))})
    })
    @DeleteMapping(value = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> deletePatientById(@PathVariable("id") Integer patientId) {
        logger.debug("### Request called --> DELETE /patients/{}", patientId);

        patientService.deletePatient(patientId);

        logger.info("### Patient deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
