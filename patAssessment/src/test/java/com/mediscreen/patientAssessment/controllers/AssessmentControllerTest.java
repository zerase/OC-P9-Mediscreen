package com.mediscreen.patientAssessment.controllers;

import com.mediscreen.patientAssessment.beans.AssessmentDTO;
import com.mediscreen.patientAssessment.beans.PatientBean;
import com.mediscreen.patientAssessment.constants.RiskLevels;
import com.mediscreen.patientAssessment.services.AssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssessmentController.class)
public class AssessmentControllerTest {

    @MockBean
    private AssessmentService assessmentService;

    @Autowired
    private MockMvc mockMvc;


    // ========================================================================

    @Test
    void getPatientAssessmentByPatientId_shouldReturnAssessmentForPatientWithTheGivenIdWithHttpStatus200Ok() throws Exception {
        int patientId = 3;
        AssessmentDTO assessmentDTO = new AssessmentDTO(
                new PatientBean(3, "TestInDanger", "Test", LocalDate.parse("2004-06-18"), "M", "3 Club Road", "300-444-5555"),
                14,
                RiskLevels.IN_DANGER.getRiskLevel()
        );
        when(assessmentService.assessDiabetesRiskLevelByPatientId(anyInt())).thenReturn(assessmentDTO);

        MvcResult result = mockMvc.perform(get("/assess/id/{id}", patientId))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).as("Content response")
                .contains("lastName", "TestInDanger", "patientAge", "14", "diabetesRiskLevelAssessment", "In danger");
        verify(assessmentService).assessDiabetesRiskLevelByPatientId(3);
    }

    // ========================================================================

    @Test
    void getPatientAssessmentByLastName_shouldReturnAssessmentForPatientWithTheGivenLastNameWithHttpStatus200Ok() throws Exception {
        String lastNameSearched = "TestInDanger";
        AssessmentDTO assessmentDTO = new AssessmentDTO(
                new PatientBean(3, "TestInDanger", "Test", LocalDate.parse("2004-06-18"), "M", "3 Club Road", "300-444-5555"),
                14,
                RiskLevels.IN_DANGER.getRiskLevel()
        );
        List<AssessmentDTO> assessmentDTOList = List.of(assessmentDTO);
        when(assessmentService.assessDiabetesRiskLevelByLastName(anyString())).thenReturn(assessmentDTOList);

        MvcResult result = mockMvc.perform(get("/assess/lastName/{lastName}", lastNameSearched))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).as("Content response")
                .contains("lastName", "TestInDanger", "patientAge", "14", "diabetesRiskLevelAssessment", "In danger");
        verify(assessmentService).assessDiabetesRiskLevelByLastName(lastNameSearched);
    }
}
