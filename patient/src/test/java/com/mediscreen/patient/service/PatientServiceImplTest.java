package com.mediscreen.patient.service;

import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @InjectMocks
    PatientServiceImpl patientServiceUnderTest;
    @Mock
    PatientRepository patientRepository;

    private final Patient patientTest1 = new Patient("TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
    private final Patient patientTest2 = new Patient("TestBorderline", "Test", LocalDate.parse("1945-06-24"), "M", "2 High St", "200-333-4444");
    private final Patient patientTest3 = new Patient("TestInDanger", "Test", LocalDate.parse("2004-06-18"), "M", "3 Club Road", "300-444-5555");
    private final Patient patientTest4 = new Patient("TestEarlyOnset", "Test", LocalDate.parse("2002-06-28"), "F", "4 Valley Dr", "400-555-6666");
    private final List<Patient> expectedPatientsListTest = Arrays.asList(patientTest1, patientTest2, patientTest3, patientTest4);


    @Test
    void testReadAllPatients_shouldReturnAllPatientsList() {
        Mockito.when(patientRepository.findAll()).thenReturn(expectedPatientsListTest);

        List<Patient> actualAllPatientsResult = patientServiceUnderTest.readAllPatients();

        verify(patientRepository, times(1)).findAll();
        assertThat(actualAllPatientsResult).isNotNull();
        assertThat(actualAllPatientsResult).isEqualTo(expectedPatientsListTest);
        assertThat(actualAllPatientsResult.size()).isEqualTo(4);
    }

    @Test
    void testReadAllPatients_shouldReturnEmptyList_whenDatabaseIsEmpty() {
        Mockito.when(patientRepository.findAll()).thenReturn(new ArrayList<>());

        List<Patient> actualAllPatientsResult = patientServiceUnderTest.readAllPatients();

        verify(patientRepository, times(1)).findAll();
        assertThat(actualAllPatientsResult).isNotNull();
        assertThat(actualAllPatientsResult.size()).isEqualTo(0);
    }

    @Test
    void testReadPatientById_shouldReturnExistingPatientSuccessfully() {
        patientTest1.setId(11);
        Mockito.when(patientRepository.findById(any(Integer.class))).thenReturn(Optional.of(patientTest1));

        Patient actualPatientResult = patientServiceUnderTest.readPatient(11);

        verify(patientRepository, times(1)).findById(any(Integer.class));
        assertThat(actualPatientResult).isNotNull();
        assertThat(actualPatientResult.getId()).isEqualTo(11);
        assertThat(actualPatientResult.getLastName()).isEqualToIgnoringCase("TestNone");
    }

    @Test
    void testReadPatientById_shouldThrowException_whenIdIsUnknown() {
        Mockito.when(patientRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Throwable actualPatientResult = catchThrowable(() -> patientServiceUnderTest.readPatient(11));

        verify(patientRepository, times(1)).findById(any(Integer.class));
        assertThat(actualPatientResult)
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
