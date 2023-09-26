package com.mediscreen.patientAssessment.controllers;

import com.mediscreen.patientAssessment.beans.AssessmentDTO;
import com.mediscreen.patientAssessment.services.AssessmentService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Manages endpoints of the Assessment API.
 */
@Tag(name = "Assessment", description = "Assessment management API")
@RestController
public class AssessmentController {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentController.class);

    /**
     * Component that handles business logic operations for diabetes report assessment.
     */
    private final AssessmentService assessmentService;

    /**
     * Instantiates a new assessment controller.
     *
     * @param assessmentService  the service linked to assessment business logic
     */
    @Autowired
    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }


    // === GET ASSESSMENT BY PATIENT ID =======================================

    /**
     * Gets assessment of the patient with the given id.
     *
     * @param patientId  the id of the patient whose assessment we wish to retrieve
     * @return           the report assessing the patient's diabetes risk level
     */
    @Operation(
            summary = "Retrieve assessment of a patient by his id",
            description = "Get a AssessmentDTO object by specifying a patient identifier. The response is a AssessmentDTO object with Patient information, age of the patient, diabetes risk level."
    )
    @Parameters({
            @Parameter(name = "id", description = "Id of a specific patient", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Not Found - Patient not found or doesn't exist", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/assess/id/{id}")
    public ResponseEntity<AssessmentDTO> getPatientAssessmentByPatientId(@PathVariable("id") Integer patientId) {
        logger.debug("### Request called --> GET /assess/id/{}", patientId);

        AssessmentDTO patientAssessment = assessmentService.assessDiabetesRiskLevelByPatientId(patientId);

        logger.info("### Assessment report returned --> {}", patientAssessment);
        return new ResponseEntity<>(patientAssessment, HttpStatus.OK);
    }

    // === GET ASSESSMENT BY LAST NAME ========================================

    /**
     * Gets assessment of the patient with the given lastName.
     * @param lastName  the last name of the patient whose assessment we wish to retrieve
     * @return          the report assessing the patient's diabetes risk level
     */
    @Operation(
            summary = "Retrieve assessment of a patient by his last name",
            description = "Get a AssessmentDTO object by specifying a patient name. The response is a list of AssessmentDTO objects with Patient information, age of the patient, diabetes risk level."
    )
    @Parameters({
            @Parameter(name = "lastName", description = "Last name of a patient", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Not Found - Patient not found or doesn't exist", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/assess/lastName/{lastName}")
    public ResponseEntity<List<AssessmentDTO>> getPatientAssessmentByLastName(@PathVariable("lastName") String lastName) {
        logger.debug("### Request called --> GET /assess/lastName/{}", lastName);

        List<AssessmentDTO> patientAssessment = assessmentService.assessDiabetesRiskLevelByLastName(lastName);

        logger.info("### Assessment report returned --> {}", patientAssessment);
        return new ResponseEntity<>(patientAssessment, HttpStatus.OK);
    }

}
