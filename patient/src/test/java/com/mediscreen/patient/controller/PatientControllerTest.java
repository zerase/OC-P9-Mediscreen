package com.mediscreen.patient.controller;

import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    private final MockMvc mockMvc;
    @MockBean
    private final PatientService patientServiceMock;

    @Autowired
    public PatientControllerTest(MockMvc mockMvc, PatientService patientServiceMock) {
        this.mockMvc = mockMvc;
        this.patientServiceMock = patientServiceMock;
    }

    private final Patient patientTest1 = new Patient(11, "TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
    private final Patient patientTest2 = new Patient(12, "TestBorderline", "Test", LocalDate.parse("1945-06-24"), "M", "2 High St", "200-333-4444");
    private final Patient patientTest3 = new Patient(13, "TestInDanger", "Test", LocalDate.parse("2004-06-18"), "M", "3 Club Road", "300-444-5555");
    private final Patient patientTest4 = new Patient(14, "TestEarlyOnset", "Test", LocalDate.parse("2002-06-28"), "F", "4 Valley Dr", "400-555-6666");
    private final List<Patient>expectedPatientsListTest = Arrays.asList(patientTest1, patientTest2, patientTest3, patientTest4);

    private final static String PATIENT_LIST_URI = "/patient/list/";
    private final static String PATIENT_GET_URI = "/patient/get/";


    /*@BeforeEach
    void setUp() {
        given(patientServiceMock.readAllPatients()).willReturn(expectedPatientsListTest);
    }*/

    @Test
    void testGetAllPatients_shouldReturnStatusOk() throws Exception {
        when(patientServiceMock.readAllPatients()).thenReturn(expectedPatientsListTest);

        mockMvc.perform(MockMvcRequestBuilders.get(PATIENT_LIST_URI).contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].lastName", is("TestNone")))
                .andExpect(jsonPath("$[0].firstName", is("Test")))
                .andExpect(jsonPath("$[0].birthDate", is("1966-12-31")))
                .andExpect(jsonPath("$[0].gender", is("F")))
                .andExpect(jsonPath("$[0].address", is("1 Brookside St")))
                .andExpect(jsonPath("$[0].phoneNumber", is("100-222-3333")));

        verify(patientServiceMock, times(1)).readAllPatients();
    }

    @Test
    void testGetPatientById_shouldReturnStatusOk() throws Exception {
        when(patientServiceMock.readPatient(any(Integer.class))).thenReturn(patientTest1);

        mockMvc.perform(MockMvcRequestBuilders.get(PATIENT_GET_URI + 11).contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName", is("TestNone")))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.birthDate", is("1966-12-31")))
                .andExpect(jsonPath("$.gender", is("F")))
                .andExpect(jsonPath("$.address", is("1 Brookside St")))
                .andExpect(jsonPath("$.phoneNumber", is("100-222-3333")));

        verify(patientServiceMock, times(1)).readPatient(11);
    }

    @Test
    void testGetPatientById_shouldReturnStatusNotFound_whenIdIsUnknown() throws Exception {
        when(patientServiceMock.readPatient(0)).thenThrow(PatientNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(PATIENT_GET_URI + 0).contentType(MediaType.ALL))
                .andExpect(status().isNotFound());

        verify(patientServiceMock, times(1)).readPatient(0);
    }

    @Test
    void testGetPatientById_shouldReturnStatusBadRequest_whenIdIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATIENT_GET_URI + "zzz").contentType(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(patientServiceMock, times(0)).readPatient(any());
    }
}
