package com.mediscreen.patient.controller;

import com.mediscreen.patient.model.Patient;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

/**
 * Tests that the endpoints of the API use methods from all application layers as expected.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
@Sql({"/data.sql"})
public class PatientControllerIT {

    //@Autowired
    //private MockMvc mockMvc;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    private static final String BASE_URL = "http://localhost:";

    private String createUrlWithPort(String uri) {
        return BASE_URL + port + uri;
    }


    // === REQUEST GET /PATIENTS ==============================================
    @Test
    void whenGetAllPatientsIsRequested_thenShouldReturnStatus200_withExpectedListOfPatients() throws Exception {
        String getAllPatientsUrl = createUrlWithPort("/patients");

        ResponseEntity<Object> response = restTemplate.getForEntity(getAllPatientsUrl, Object.class);
        Collection<Patient> patients = (Collection<Patient>) response.getBody();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode()).as("Response status code").isEqualTo(HttpStatus.OK);
            softly.assertThat(patients).as("Patients list").isNotNull();
            softly.assertThat(patients.size()).as("Patients list size").isEqualTo(4); // We have the four patients present in the data.sql initialization file
        });
    }

    // === REQUEST GET /PATIENTS/{ID} =========================================
    @Test
    void whenGetPatientIsRequested_thenShouldReturnStatus200_withExpectedPatient() throws Exception {
        String getPatientUrl = createUrlWithPort("/patients/1");

        ResponseEntity<Object> response = restTemplate.getForEntity(getPatientUrl, Object.class);
        Object patient = response.getBody();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode()).as("Response status code").isEqualTo(HttpStatus.OK);
            softly.assertThat(patient).as("Patient returned").isNotNull();
            softly.assertThat(patient).as("Patients family name").hasFieldOrPropertyWithValue("lastName", "TestNone");
            softly.assertThat(patient).as("Patients given name").hasFieldOrPropertyWithValue("firstName", "Test");
        });
    }

}
