package com.mediscreen.patient.repository;

import com.mediscreen.patient.model.Patient;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

//@ExtendWith(SpringExtension.class)
//@RunWith(SpringRunner.class) // This annotation is for JUnit4
/**
 * Tests that the methods used by the data persistence layer of the API work as expected.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private PatientRepository repositoryUnderTest;

    private final Patient patient1 = new Patient("TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
    private final Patient patient2 = new Patient("TestBorderline", "Test", LocalDate.parse("1945-06-24"), "M", "2 High St", "200-333-4444");
    private final Patient patient3 = new Patient("TestInDanger", "Test", LocalDate.parse("2004-06-18"), "M", "3 Club Road", "300-444-5555");
    private final Patient patient4 = new Patient("TestEarlyOnset", "Test", LocalDate.parse("2002-06-28"), "F", "4 Valley Dr", "400-555-6666");


    // === save() =============================================================
    @Test
    void save_shouldStoreNewPatientInDatabase() {
        Patient result = repositoryUnderTest.save(patient1);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.getId()).as("Stored patient id").isNotNull();
            softly.assertThat(result).as("Stored patient")
                    .hasFieldOrPropertyWithValue("lastName", "TestNone")
                    .hasFieldOrPropertyWithValue("firstName", "Test")
                    .hasFieldOrPropertyWithValue("dateOfBirth", LocalDate.parse("1966-12-31"))
                    .hasFieldOrPropertyWithValue("gender", "F")
                    .hasFieldOrPropertyWithValue("address", "1 Brookside St")
                    .hasFieldOrPropertyWithValue("phoneNumber", "100-222-3333");
        });
    }

    @Test
    void save_shouldUpdateGivenPatientInDatabase() {
        entityManager.persist(patient1);
        Patient patientWithNewValues = new Patient(1, "TestNone Updated", "Test Updated", LocalDate.parse("1950-12-31"), "M", "1 Brookside St Updated", "111-222-3333");

        Patient result = repositoryUnderTest.save(patientWithNewValues);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.getId()).as("Updated patient id")
                    .isEqualTo(patient1.getId());
            softly.assertThat(result).as("Updated patient")
                    .hasFieldOrPropertyWithValue("lastName", "TestNone Updated")
                    .hasFieldOrPropertyWithValue("firstName", "Test Updated")
                    .hasFieldOrPropertyWithValue("dateOfBirth", LocalDate.parse("1950-12-31"))
                    .hasFieldOrPropertyWithValue("gender", "M")
                    .hasFieldOrPropertyWithValue("address", "1 Brookside St Updated")
                    .hasFieldOrPropertyWithValue("phoneNumber", "111-222-3333");
        });
    }

    // === findAll() ==========================================================
    @Test
    void findAll_shouldReturnNoPatients_whenDatabaseIsEmpty() {
        Iterable<Patient> result = repositoryUnderTest.findAll();

        assertThat(result).as("All patients list").isEmpty();
    }

    @Test
    void findAll_shouldReturnAllPatients_whenDatabaseHasData() {
        entityManager.persist(patient1);
        entityManager.persist(patient2);
        entityManager.persist(patient3);
        entityManager.persist(patient4);

        Iterable<Patient> result = repositoryUnderTest.findAll();

        SoftAssertions.assertSoftly(softly -> {
        softly.assertThat(result).as("All patients list")
                .hasSize(4)
                .contains(patient1, patient2, patient3, patient4);
        });
    }

    // === findAllByLastNameIgnoreCase() ======================================
    @ParameterizedTest(name = "Case {index} when input name is {0}")
    @ValueSource(strings = {"TestToto", "testtoto", "TESTTOTO", "tESttOto", "TèstTôto"})
    void findAllByLastNameIgnoringCase_shouldReturnAllPatientsWithTheSameLastName(String inputName) {
        Patient inputPatient1 = new Patient("TestToto", "Test", LocalDate.of(1950,12,31), "M", "XXX Fake Address", "000-000-0000");
        Patient inputPatient2 = new Patient("Toto", "Test", LocalDate.of(1950,12,31), "M", "XXX Fake Address", "000-000-0000");
        entityManager.persist(inputPatient1);
        entityManager.persist(inputPatient2);

        Iterable<Patient> result = repositoryUnderTest.findAllByLastNameIgnoreCase(inputName);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("All patients with same last name")
                    .hasSize(1)
                    .contains(inputPatient1)
                    .doesNotContain(inputPatient2);
        });
    }

    // === findById() =========================================================
    @Test
    void findById_shouldReturnThePatientWithTheGivenId_whenGivenIdIsPresentInDatabase() {
        entityManager.persist(patient1);
        int patientId = patient1.getId();

        Optional<Patient> result = repositoryUnderTest.findById(patientId);

        assertThat(result.get()).as("Found patient").isEqualTo(patient1);
    }

    @Test
    void findById_shouldReturnNoValue_whenGivenIdIsNotPresentInDatabase() {
        int unknownPatientId = 0;

        Optional<Patient> result = repositoryUnderTest.findById(unknownPatientId);

        assertThat(result).as("Unknown patient").isEqualTo(Optional.empty());
    }

    // === delete() ===========================================================
    @Test
    void delete_shouldDeleteGivenPatient() {
        entityManager.persist(patient1);

        repositoryUnderTest.delete(patient1);

        entityManager.flush();
        Patient patientFromDb = entityManager.find(Patient.class, patient1.getId());
        assertThat(patientFromDb).as("Deleted patient").isNull();
    }

    // === deleteById() =======================================================
    @Test
    void deleteById_shouldDeletePatientWithTheGivenId() {
        entityManager.persist(patient1);
        int patientId = patient1.getId();

        repositoryUnderTest.deleteById(patientId);

        entityManager.flush();
        Patient patientFromDb = entityManager.find(Patient.class, patientId);
        assertThat(patientFromDb).as("Deleted patient").isNull();
    }

}
