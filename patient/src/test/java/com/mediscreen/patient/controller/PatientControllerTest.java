package com.mediscreen.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.patient.exception.PatientAlreadyExistsException;
import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests that the methods used by the presentation layer of the API work as expected.
 */
//@ExtendWith(SpringExtension.class)
@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @MockBean
    private PatientService patientService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final Patient patient1 = new Patient(1, "TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
    private final Patient patient2 = new Patient(2, "TestBorderline", "Test", LocalDate.parse("1945-06-24"), "M", "2 High St", "200-333-4444");
    private final Patient patient3 = new Patient(3, "TestInDanger", "Test", LocalDate.parse("2004-06-18"), "M", "3 Club Road", "300-444-5555");
    private final Patient patient4 = new Patient(4, "TestEarlyOnset", "Test", LocalDate.parse("2002-06-28"), "F", "4 Valley Dr", "400-555-6666");
    private final List<Patient>expectedPatientsList = Arrays.asList(patient1, patient2, patient3, patient4);


    // === ADD PATIENT ========================================================
    @Test
    void addNewPatient_shouldReturnHttpStatus201Created_whenRequestIsSuccessful() throws Exception {
        Patient patientToAdd = new Patient("TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
        when(patientService.createPatient(any(Patient.class))).thenReturn(patient1);

        mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientToAdd)))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(patientService).createPatient(any(Patient.class));
    }

    @Test
    void addNewPatient_shouldReturnHttpStatus400BadRequest_whenRequestFailedWithValidationErrors() throws Exception {
        Patient patientToAdd = new Patient(null, null, null, null, "", "");

        mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientToAdd)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(containsString("is required"))
                ).andDo(print());

        verify(patientService, times(0)).createPatient(any(Patient.class));
    }

    @Test
    void addNewPatient_shouldReturnHttpStatus409Conflict_whenRequestFailed() throws Exception {
        Patient patientToAdd = new Patient("TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
        when(patientService.createPatient(any(Patient.class))).thenThrow(PatientAlreadyExistsException.class);

        mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientToAdd)))
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        verify(patientService).createPatient(any(Patient.class));
    }

    // === GET ALL PATIENTS ===================================================
    @Test
    void getAllPatients_shouldReturnHttpStatus200Ok_whenRequestIsSuccessfulWithListOfAllPatientsContainingData() throws Exception {
        when(patientService.readAllPatients()).thenReturn(expectedPatientsList);

        mockMvc.perform(get("/patients")).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(4)),
                        jsonPath("$[0].lastName", is(patient1.getLastName())),
                        jsonPath("$[0].firstName", is(patient1.getFirstName())),
                        jsonPath("$[0].dateOfBirth", is(patient1.getDateOfBirth().toString())),
                        jsonPath("$[0].gender", is(patient1.getGender())),
                        jsonPath("$[0].address", is(patient1.getAddress())),
                        jsonPath("$[0].phoneNumber", is(patient1.getPhoneNumber())),
                        jsonPath("$[1].lastName", is(patient2.getLastName())),
                        jsonPath("$[2].lastName", is(patient3.getLastName())),
                        jsonPath("$[3].lastName", is(patient4.getLastName()))
                );

        verify(patientService).readAllPatients();
    }

    @Test
    void getAllPatients_shouldReturnHttpStatus200Ok_whenRequestIsSuccessfulWithRequestParameterContainingData() throws Exception {
        Patient patientA = new Patient(5, "Doe", "John", LocalDate.parse("1990-12-31"), "M", "", "");
        Patient patientB = new Patient(6, "Doe", "Jane", LocalDate.parse("1990-12-31"), "F", "", "");
        List<Patient> expectedPatientsListWithSameName = Arrays.asList(patientA, patientB);
        when(patientService.readAllPatientsByLastName(anyString())).thenReturn(expectedPatientsListWithSameName);

        mockMvc.perform(get("/patients?lastName=Doe")).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].lastName", is(patientA.getLastName())),
                        jsonPath("$[0].firstName", is(patientA.getFirstName())),
                        jsonPath("$[1].lastName", is(patientB.getLastName())),
                        jsonPath("$[1].firstName", is(patientB.getFirstName()))
                );

        verify(patientService).readAllPatientsByLastName(anyString());
    }

    @Test
    void getAllPatients_shouldReturnHttpStatus204NoContent_whenRequestIsSuccessfulWithListOfAllPatientsEmpty() throws Exception {
        when(patientService.readAllPatients()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/patients")).andDo(print())
                .andExpect(status().isNoContent());

        verify(patientService).readAllPatients();
    }

    @Test
    void getAllPatients_shouldReturnHttpStatus500InternalServerError_whenRequestFailedWithListOfAllPatientsNull() throws Exception {
        when(patientService.readAllPatients()).thenReturn(null);

        mockMvc.perform(get("/patients")).andDo(print())
                .andExpectAll(
                        status().isInternalServerError(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        verify(patientService).readAllPatients();
    }

    // === GET PATIENT ========================================================
    @Test
    void getPatientById_shouldReturnHttpStatus200Ok_whenRequestIsSuccessful() throws Exception {
        int patientId = 1;
        when(patientService.readPatient(anyInt())).thenReturn(patient1);

        mockMvc.perform(get("/patients/{id}", patientId)).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasEntry("id", 1)),
                        jsonPath("$", hasEntry("lastName", "TestNone")),
                        jsonPath("$", hasEntry("firstName", "Test")),
                        jsonPath("$", hasEntry("dateOfBirth", "1966-12-31")),
                        jsonPath("$", hasEntry("gender", "F")),
                        jsonPath("$", hasEntry("address", "1 Brookside St")),
                        jsonPath("$", hasEntry("phoneNumber", "100-222-3333"))
                );

        verify(patientService).readPatient(patientId);
    }

    @Test
    void getPatientById_shouldReturnHttpStatus400BadRequest_whenRequestFailedWithWrongTypeOfGivenId() throws Exception {
        mockMvc.perform(get("/patients/abc")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        verify(patientService, times(0)).readPatient(anyInt());
    }

    @Test
    void getPatientById_shouldReturnHttpStatus404NotFound_whenRequestFailedWithInvalidGivenId() throws Exception {
        int patientId = 0;
        when(patientService.readPatient(anyInt())).thenThrow(PatientNotFoundException.class);

        mockMvc.perform(get("/patients/{id}", patientId)).andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        verify(patientService).readPatient(patientId);
    }

    // === UPDATE PATIENT =====================================================
    @Test
    void updatePatientById_shouldReturnHttpStatus200Ok_whenRequestIsSuccessful() throws Exception {
        int patientId = 1;
        Patient updatedPatient = new Patient(1, "TestNone Updated", "Test Updated", LocalDate.parse("1950-12-31"), "M", "1 Brookside St Updated", "111-222-3333");
        when(patientService.updatePatient(anyInt(), any(Patient.class))).thenReturn(updatedPatient);

        mockMvc.perform(put("/patients/{id}", patientId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasEntry("id", 1)),
                        jsonPath("$", hasEntry("lastName", "TestNone Updated")),
                        jsonPath("$", hasEntry("firstName", "Test Updated")),
                        jsonPath("$", hasEntry("dateOfBirth", "1950-12-31")),
                        jsonPath("$", hasEntry("gender", "M")),
                        jsonPath("$", hasEntry("address", "1 Brookside St Updated")),
                        jsonPath("$", hasEntry("phoneNumber", "111-222-3333"))
                ).andDo(print());

        verify(patientService).updatePatient(anyInt(), any(Patient.class));
    }

    @Test
    void updatePatientById_shouldReturnHttpStatus400BadRequest_whenRequestFailedWithValidationErrors() throws Exception {
        int patientId = 1;
        Patient updatedPatient = new Patient(1, null, null, null, null, "", "");

        mockMvc.perform(put("/patients/{id}", patientId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(containsString("is required"))
                ).andDo(print());

        verify(patientService, times(0)).updatePatient(anyInt(), any(Patient.class));
    }

    @Test
    void updatePatientById_shouldReturnHttpStatus404NotFound_whenRequestFailedWithInvalidGivenId() throws Exception {
        int patientId = 0;
        Patient updatedPatient = new Patient(0, "TestNone Updated", "Test Updated", LocalDate.parse("1950-12-31"), "M", "1 Brookside St Updated", "111-222-3333");
        when(patientService.updatePatient(anyInt(), any(Patient.class))).thenThrow(PatientNotFoundException.class);

        mockMvc.perform(put("/patients/{id}", patientId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andDo(print());

        verify(patientService).updatePatient(anyInt(), any(Patient.class));
    }

    // === DELETE PATIENT =====================================================
    @Test
    void deletePatientById_shouldReturnHttpStatus204NoContent_whenRequestIsSuccessful() throws Exception {
        int patientId = 1;
        doNothing().when(patientService).deletePatient(anyInt());

        mockMvc.perform(delete("/patients/{id}", patientId))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(patientService).deletePatient(patientId);
    }

    @Test
    void deletePatientById_shouldReturnHttpStatus404NotFound_whenRequestFailed() throws Exception {
        int patientId = 0;
        doThrow(PatientNotFoundException.class).when(patientService).deletePatient(anyInt());

        mockMvc.perform(delete("/patients/{id}", patientId))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(patientService).deletePatient(patientId);
    }

}
