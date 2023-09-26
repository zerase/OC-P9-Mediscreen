package com.mediscreen.patient.service;

import com.mediscreen.patient.exception.PatientAlreadyExistsException;
import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests that the methods used by the business service layer of the API work as expected.
 */
@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @InjectMocks
    PatientServiceImpl serviceUnderTest;
    @Mock
    PatientRepository patientRepository;

    private final Patient patient1 = new Patient(1, "TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
    private final Patient patient2 = new Patient(2, "TestBorderline", "Test", LocalDate.parse("1945-06-24"), "M", "2 High St", "200-333-4444");
    private final Patient patient3 = new Patient(3, "TestInDanger", "Test", LocalDate.parse("2004-06-18"), "M", "3 Club Road", "300-444-5555");
    private final Patient patient4 = new Patient(4, "TestEarlyOnset", "Test", LocalDate.parse("2002-06-28"), "F", "4 Valley Dr", "400-555-6666");
    private final List<Patient> expectedPatientsList = Arrays.asList(patient1, patient2, patient3, patient4);


    // === CREATE =============================================================
    @Test
    void createPatient_shouldReturnCreatedPatient() {
        Patient patientToSave = new Patient("TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
        when(patientRepository.existsByLastNameAndFirstNameAndDateOfBirth(anyString(), anyString(), any(LocalDate.class))).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        Patient result = serviceUnderTest.createPatient(patientToSave);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Patient saved in database")
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("lastName", "TestNone")
                    .hasFieldOrPropertyWithValue("firstName", "Test")
                    .hasFieldOrPropertyWithValue("dateOfBirth", LocalDate.parse("1966-12-31"))
                    .hasFieldOrPropertyWithValue("gender", "F")
                    .hasFieldOrPropertyWithValue("address", "1 Brookside St")
                    .hasFieldOrPropertyWithValue("phoneNumber", "100-222-3333");

        });
        verify(patientRepository).existsByLastNameAndFirstNameAndDateOfBirth(patientToSave.getLastName(), patientToSave.getFirstName(), patientToSave.getDateOfBirth());
        verify(patientRepository).save(patientToSave);
    }

    @Test
    void createPatient_shouldThrowAnException_whenPatientWithGivenValuesAlreadyExistsInDatabase() {
        Patient patientToSave = new Patient("TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
        when(patientRepository.existsByLastNameAndFirstNameAndDateOfBirth(anyString(), anyString(), any(LocalDate.class))).thenReturn(true);

        Throwable result = catchThrowable(() -> serviceUnderTest.createPatient(patientToSave));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Thrown exception")
                    .isInstanceOf(PatientAlreadyExistsException.class)
                    .hasMessageContaining("Patient already exists");
        });
        verify(patientRepository).existsByLastNameAndFirstNameAndDateOfBirth(patientToSave.getLastName(), patientToSave.getFirstName(), patientToSave.getDateOfBirth());
        verify(patientRepository, times(0)).save(patientToSave);
    }

    // === READ ALL ===========================================================
    @Test
    void readAllPatients_shouldReturnEmptyList_whenDatabaseContainsNoData() {
        when(patientRepository.findAll()).thenReturn(new ArrayList<>());

        List<Patient> result = serviceUnderTest.readAllPatients();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("All patients found from database")
                    .isNotNull()
                    .isEmpty();
        });
        verify(patientRepository).findAll();
    }

    @Test
    void readAllPatients_shouldReturnListOfAllPatients_whenDatabaseContainsData() {
        when(patientRepository.findAll()).thenReturn(expectedPatientsList);

        List<Patient> result = serviceUnderTest.readAllPatients();

        SoftAssertions.assertSoftly(softly -> {
        softly.assertThat(result).as("All patients found from database")
                .isNotNull()
                .hasSize(4)
                .contains(patient1, patient2, patient3, patient4);
        });
        verify(patientRepository).findAll();
    }

    // === READ ALL BY ========================================================
    @Test
    void readAllPatientsByLastName_shouldReturnEmptyList_whenDatabaseDoesNotFindDataWithGivenKeyword() {
        String keyword = "test";
        when(patientRepository.findAllByLastNameIgnoreCase(anyString())).thenReturn(new ArrayList<>());

        List<Patient> result = serviceUnderTest.readAllPatientsByLastName(keyword);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Patients found from database")
                    .isNotNull()
                    .isEmpty();
        });
        verify(patientRepository).findAllByLastNameIgnoreCase(keyword);
    }

    @Test
    void readAllPatientsByLastName_shouldReturnListOfPatientsWithSameLastName_whenDatabaseFindDataWithGivenKeyword() {
        Patient patientA = new Patient(5, "Doe", "John", LocalDate.parse("1990-12-31"), "M", "", "");
        Patient patientB = new Patient(6, "Doe", "Jane", LocalDate.parse("1990-12-31"), "F", "", "");
        List<Patient> patientsWithSameLastNameList = Arrays.asList(patientA, patientB);
        String keyword = "dôé";
        when(patientRepository.findAllByLastNameIgnoreCase(anyString())).thenReturn(patientsWithSameLastNameList);

        List<Patient> result = serviceUnderTest.readAllPatientsByLastName(keyword);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Patients found from database")
                    .isNotNull()
                    .hasSize(2)
                    .contains(patientA, patientB);
        });
        verify(patientRepository).findAllByLastNameIgnoreCase(keyword);
    }

    // === READ ===============================================================
    @Test
    void readPatient_shouldReturnThePatientWithTheGivenId_whenGivenIdIsPresentInDatabase() {
        int patientId = 1;
        when(patientRepository.findById(anyInt())).thenReturn(Optional.of(patient1));

        Patient result = serviceUnderTest.readPatient(patientId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Patient found").isNotNull();
            softly.assertThat(result.getId()).as("Patient found id").isEqualTo(1);
            softly.assertThat(result.getLastName()).as("Patient found last name").isEqualTo("TestNone");
        });
        verify(patientRepository).findById(patientId);
    }

    @Test
    void readPatient_shouldThrowAnException_whenGivenIdIsNotPresentInDatabase() {
        int unknownPatientId = 0;
        when(patientRepository.findById(anyInt())).thenReturn(Optional.empty());

        Throwable result = catchThrowable(() -> serviceUnderTest.readPatient(unknownPatientId));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Thrown exception")
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient not found");
        });
        verify(patientRepository).findById(unknownPatientId);
    }

    // === UPDATE =============================================================
    @Test
    void updatePatient_shouldReturnUpdatedPatient_whenGivenIdIsPresentInDatabase() {
        Patient patientToUpdate = new Patient(1, "TestNone Updated", "Test Updated", LocalDate.parse("1950-12-31"), "M", "1 Brookside St Updated", "111-222-3333");
        int patientId = patient1.getId();
        when(patientRepository.findById(anyInt())).thenReturn(Optional.of(patient1));
        when(patientRepository.save(any(Patient.class))).thenReturn(patientToUpdate);

        Patient result = serviceUnderTest.updatePatient(patientId, patientToUpdate);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Updated patient")
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("lastName", "TestNone Updated")
                    .hasFieldOrPropertyWithValue("firstName", "Test Updated")
                    .hasFieldOrPropertyWithValue("dateOfBirth", LocalDate.parse("1950-12-31"))
                    .hasFieldOrPropertyWithValue("gender", "M")
                    .hasFieldOrPropertyWithValue("address", "1 Brookside St Updated")
                    .hasFieldOrPropertyWithValue("phoneNumber", "111-222-3333");
        });
        verify(patientRepository).findById(patientId);
        verify(patientRepository).save(patientToUpdate);
    }

    @Test
    void updatePatient_shouldThrowAnException_whenGivenIdIsNotPresentInDatabase() {
        int unknownPatientId = 0;
        when(patientRepository.findById(anyInt())).thenReturn(Optional.empty());

        Throwable result = catchThrowable(() -> serviceUnderTest.updatePatient(unknownPatientId, patient1));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Thrown exception")
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient not found");
        });
        verify(patientRepository).findById(unknownPatientId);
        verify(patientRepository, times(0)).save(patient1);
    }

    // === DELETE =============================================================
    @Test
    void deletePatient_shouldDeletePatient_whenGivenIdIsPresentInDatabase() {
        int patientId = patient1.getId();
        when(patientRepository.existsById(anyInt())).thenReturn(true);

        serviceUnderTest.deletePatient(patientId);

        verify(patientRepository).existsById(patientId);
        verify(patientRepository).deleteById(patientId);
    }

    @Test
    void deletePatient_shouldThrowAnException_whenGivenIdIsNotPresentInDatabase() {
        int unknownPatientId = 0;
        when(patientRepository.existsById(anyInt())).thenReturn(false);

        Throwable result = catchThrowable(() -> serviceUnderTest.deletePatient(unknownPatientId));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Thrown exception")
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient not found");
        });
        verify(patientRepository).existsById(unknownPatientId);
        verify(patientRepository, times(0)).deleteById(unknownPatientId);
    }

}
