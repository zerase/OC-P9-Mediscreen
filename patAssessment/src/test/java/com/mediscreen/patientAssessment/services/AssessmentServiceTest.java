package com.mediscreen.patientAssessment.services;

import com.mediscreen.patientAssessment.beans.AssessmentDTO;
import com.mediscreen.patientAssessment.beans.NoteBean;
import com.mediscreen.patientAssessment.beans.PatientBean;
import com.mediscreen.patientAssessment.constants.RiskLevels;
import com.mediscreen.patientAssessment.exceptions.PatientNotFoundException;
import com.mediscreen.patientAssessment.proxies.MicroserviceNoteProxy;
import com.mediscreen.patientAssessment.proxies.MicroservicePatientProxy;
import feign.FeignException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssessmentServiceTest {

    @InjectMocks
    AssessmentService serviceUnderTest;
    @Mock
    MicroservicePatientProxy patientProxy;
    @Mock
    MicroserviceNoteProxy noteProxy;


    // ========================================================================

    @Test
    void retrievePatientById_shouldReturnPatientWithTheGivenIdSuccessfully() {
        int patientId = 1;
        PatientBean expectedPatient = new PatientBean(1, "TestNone", "Test", LocalDate.parse("1966-12-31"), "F", "1 Brookside St", "100-222-3333");
        when(patientProxy.getPatientById(any(Integer.class))).thenReturn(expectedPatient);

        PatientBean actualResult = serviceUnderTest.retrievePatientById(patientId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Retrieved patient").isNotNull();
            softly.assertThat(actualResult.getId()).as("Retrieved patient id")
                    .isEqualTo(expectedPatient.getId());
            softly.assertThat(actualResult.getLastName()).as("Retrieved patient last name")
                    .isEqualTo(expectedPatient.getLastName());
        });
        verify(patientProxy).getPatientById(1);
    }

    @Test
    void retrievePatientById_shouldThrowAnException_whenGivenIdIsUnknown() {
        int unknownPatientId = 123;
        when(patientProxy.getPatientById(any(Integer.class))).thenThrow(FeignException.class);

        Throwable actualResult = catchThrowable(() -> serviceUnderTest.retrievePatientById(unknownPatientId));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Thrown exception")
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient not found");
        });
        verify(patientProxy).getPatientById(123);
    }

    // ========================================================================

    @Test
    void calculateAgeFromDateOfBirth_shouldReturnCorrectAge() {
        int expectedAge = 30;
        LocalDate dateOfBirthOfThePatient = LocalDate.now().minusYears(expectedAge);

        int actualResult = serviceUnderTest.calculateAgeFromDateOfBirth(dateOfBirthOfThePatient);

        assertThat(actualResult).as("Calculated age").isEqualTo(expectedAge);
    }

    // ========================================================================

    @Test
    void retrieveNotesByPatientId_shouldReturnListOfNotesOfPatientWithTheGivenIdSuccessfully() {
        int patientId = 1;
        NoteBean note1 = new NoteBean("IdOfTheNote1", 1, LocalDateTime.now(), LocalDateTime.now(), "Content of the note A");
        NoteBean note2 = new NoteBean("IdOfTheNote2", 1, LocalDateTime.now(), LocalDateTime.now(), "Content of the note B");
        List<NoteBean> expectedNotes = Arrays.asList(note1, note2);
        when(noteProxy.getAllNotesByPatientId(any(Integer.class))).thenReturn(expectedNotes);

        List<NoteBean> actualResult = serviceUnderTest.retrieveNotesByPatientId(patientId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Retrieved Notes list")
                    .isNotNull()
                    .hasSize(expectedNotes.size())
                    .contains(note1, note2);
            softly.assertThat(actualResult.get(0).getContent()).as("Content of note1")
                    .contains("note A");
            softly.assertThat(actualResult.get(1).getContent()).as("Content of note2")
                    .contains("note B");
        });
        verify(noteProxy).getAllNotesByPatientId(1);
    }

    @Test
    void retrieveNotesByPatientId_shouldThrowAnException_whenGivenIdIsUnknown() {
        int unknownPatientId = 123;
        when(noteProxy.getAllNotesByPatientId(any(Integer.class))).thenThrow(FeignException.class);

        Throwable actualResult = catchThrowable(() -> serviceUnderTest.retrieveNotesByPatientId(unknownPatientId));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Thrown exception")
                    .isInstanceOf(PatientNotFoundException.class)
                    .hasMessageContaining("Patient not found");
        });
        verify(noteProxy).getAllNotesByPatientId(123);
    }

    // ========================================================================

    @Test
    void countTriggerTermsPresentInList_shouldReturnNumberOfTriggerTermsPresent() {
        NoteBean note1 = new NoteBean("idOfTheNote1", 123, LocalDateTime.now(), LocalDateTime.now(), "Le patient s'est plaint de son POIDS. Taux CHOLESTÉROL normal");
        NoteBean note2 = new NoteBean("idOfTheNote2", 123, LocalDateTime.now(), LocalDateTime.now(), "Le patient a eu une RÉACTION allergique aux médicaments");
        NoteBean note3 = new NoteBean("idOfTheNote3", 123, LocalDateTime.now(), LocalDateTime.now(), "Tests de laboratoire indiquant un taux de CHOLESTÉROL élevé et HÉMOGLOBINE A1C supérieur au niveau recommandé. Le patient est FUMEUR depuis peu");
        List<NoteBean> allNotesOfPatient = Arrays.asList(note1, note2, note3);

        int actualResult = serviceUnderTest.countTriggerTermsPresentInList(allNotesOfPatient);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Number of trigger terms")
                    .isNotNull()
                    .isEqualTo(5);
        });
    }

    @Test
    void countTriggerTermsPresentInList_shouldReturnNumberOfTriggerTermsPresent_whenListIsEmpty() {
        List<NoteBean> allNotesOfPatient = new ArrayList<>();

        int actualResult = serviceUnderTest.countTriggerTermsPresentInList(allNotesOfPatient);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Number of trigger terms")
                    .isNotNull()
                    .isEqualTo(0);
        });
    }

    // ========================================================================

    @ParameterizedTest(name = "Case when TriggerTermCount={0} --> RiskLevel={1}")
    @CsvSource(value = {"0:None", "1:None", "2:None", "3:In danger", "4:In danger", "5:Early onset", "6:Early onset", "7:Early onset", "8:Early onset", "9:Early onset", "10:Early onset", "11:Early onset", "12:Undefined"}, delimiter = ':')
    void determineDiabetesRiskLevel_shouldReturnRiskLevelValue_whenPatientIsMaleAndAgeIsLessThan30(int inputTriggerTermCount, String expectedRiskLevelValue) {
        String gender = "M";
        int patientAge = 29;

        String actualResult = serviceUnderTest.determineDiabetesRiskLevel(gender, patientAge, inputTriggerTermCount);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Diabetes Risk level")
                    .isNotNull()
                    .isEqualTo(expectedRiskLevelValue);
        });
    }

    @ParameterizedTest(name = "Case when TriggerTermCount={0} --> RiskLevel={1}")
    @CsvSource(value = {"0:None", "1:None", "2:Borderline", "3:Borderline", "4:Borderline", "5:Borderline", "6:In danger", "7:In danger", "8:Early onset", "9:Early onset", "10:Early onset", "11:Early onset", "12:Undefined"}, delimiter = ':')
    void determineDiabetesRiskLevel_shouldReturnRiskLevelValue_whenPatientIsMaleAndAgeIsGreaterThanOrEqualTo30(int inputTriggerTermCount, String expectedRiskLevelValue) {
        String gender = "M";
        int patientAge = 30;

        String actualResult = serviceUnderTest.determineDiabetesRiskLevel(gender, patientAge, inputTriggerTermCount);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Diabetes Risk level")
                    .isNotNull()
                    .isEqualTo(expectedRiskLevelValue);
        });
    }

    @ParameterizedTest(name = "Case when TriggerTermCount={0} --> RiskLevel={1}")
    @CsvSource(value = {"0:None", "1:None", "2:None", "3:None", "4:In danger", "5:In danger", "6:In danger", "7:Early onset", "8:Early onset", "9:Early onset", "10:Early onset", "11:Early onset", "12:Undefined"}, delimiter = ':')
    void determineDiabetesRiskLevel_shouldReturnRiskLevelValue_whenPatientIsFemaleAndAgeIsLessThan30(int inputTriggerTermCount, String expectedRiskLevelValue) {
        String gender = "F";
        int patientAge = 29;

        String actualResult = serviceUnderTest.determineDiabetesRiskLevel(gender, patientAge, inputTriggerTermCount);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Diabetes Risk level")
                    .isNotNull()
                    .isEqualTo(expectedRiskLevelValue);
        });
    }

    @ParameterizedTest(name = "Case when TriggerTermCount={0} --> RiskLevel={1}")
    @CsvSource(value = {"0:None", "1:None", "2:Borderline", "3:Borderline", "4:Borderline", "5:Borderline", "6:In danger", "7:In danger", "8:Early onset", "9:Early onset", "10:Early onset", "11:Early onset", "12:Undefined"}, delimiter = ':')
    void determineDiabetesRiskLevel_shouldReturnRiskLevelValue_whenPatientIsFemaleAndAgeIsGreaterThanOrEqualTo30(int inputTriggerTermCount, String expectedRiskLevelValue) {
        String gender = "F";
        int patientAge = 30;

        String actualResult = serviceUnderTest.determineDiabetesRiskLevel(gender, patientAge, inputTriggerTermCount);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("Diabetes Risk level")
                    .isNotNull()
                    .isEqualTo(expectedRiskLevelValue);
        });
    }

    // ========================================================================

    @Test
    void assessDiabetesRiskLevelByPatientId_shouldReturnDiabetesAssessmentOfPatientTestNoneCorrectly() {
        int patientId = 1;
        int expectedAge = 52;
        PatientBean patientNone = new PatientBean(1, "TestNone", "Test", LocalDate.now().minusYears(expectedAge), "F", "1 Brookside St", "100-222-3333");
        NoteBean note = new NoteBean("IdOfTheNote1", 1, LocalDateTime.now(), LocalDateTime.now(), "Patient states that they are 'feeling terrific' Weight at or below recommended level");
        List<NoteBean> notesOfPatientNone = List.of(note);
        when(patientProxy.getPatientById(any(Integer.class))).thenReturn(patientNone);
        when(noteProxy.getAllNotesByPatientId(any(Integer.class))).thenReturn(notesOfPatientNone);

        AssessmentDTO actualResult = serviceUnderTest.assessDiabetesRiskLevelByPatientId(patientId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("AssessmentDto").isNotNull();
            softly.assertThat(actualResult.getPatientDTO()).as("Patient in AssessmentDto")
                    .isEqualTo(patientNone);
            softly.assertThat(actualResult.getPatientAge()).as("Age in AssessmentDto")
                    .isEqualTo(expectedAge);
            softly.assertThat(actualResult.getDiabetesRiskLevelAssessment()).as("Diabetes risk level in AssessmentDto")
                    .isEqualTo(RiskLevels.NONE.getRiskLevel());
        });
        verify(patientProxy).getPatientById(1);
        verify(noteProxy).getAllNotesByPatientId(1);
    }

    @Test
    void assessDiabetesRiskLevelByPatientId_shouldReturnDiabetesAssessmentOfPatientTestBorderlineCorrectly() {
        int patientId = 2;
        int expectedAge = 73;
        PatientBean patientBorderline = new PatientBean(2, "TestBorderline", "Test", LocalDate.now().minusYears(expectedAge), "M", "2 High St", "200-333-4444");
        NoteBean noteA = new NoteBean("IdOfTheNote2", 2, LocalDateTime.now(), LocalDateTime.now(), "Patient states that they are feeling a great deal of stress at work Patient also complains that their hearing seems Abnormal as of late");
        NoteBean noteB = new NoteBean("IdOfTheNote3", 2, LocalDateTime.now(), LocalDateTime.now(), "Patient states that they have had a Reaction to medication within last 3 months Patient also complains that their hearing continues to be problematic");
        List<NoteBean> notesOfPatientBorderLine = List.of(noteA, noteB);
        when(patientProxy.getPatientById(any(Integer.class))).thenReturn(patientBorderline);
        when(noteProxy.getAllNotesByPatientId(any(Integer.class))).thenReturn(notesOfPatientBorderLine);

        AssessmentDTO actualResult = serviceUnderTest.assessDiabetesRiskLevelByPatientId(patientId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("AssessmentDto").isNotNull();
            softly.assertThat(actualResult.getPatientDTO()).as("Patient in AssessmentDto")
                    .isEqualTo(patientBorderline);
            softly.assertThat(actualResult.getPatientAge()).as("Age in AssessmentDto")
                    .isEqualTo(expectedAge);
            softly.assertThat(actualResult.getDiabetesRiskLevelAssessment()).as("Diabetes risk level in AssessmentDto")
                    .isEqualTo(RiskLevels.BORDERLINE.getRiskLevel());
        });
        verify(patientProxy).getPatientById(2);
        verify(noteProxy).getAllNotesByPatientId(2);
    }

    @Test
    void assessDiabetesRiskLevelByPatientId_shouldReturnDiabetesAssessmentOfPatientTestInDangerCorrectly() {
        int patientId = 3;
        int expectedAge = 14;
        PatientBean patientInDanger = new PatientBean(3, "TestInDanger", "Test", LocalDate.now().minusYears(expectedAge), "M", "3 Club Road", "300-444-5555");
        NoteBean noteA = new NoteBean("IdOfTheNote4", 3, LocalDateTime.now(), LocalDateTime.now(), "Patient states that they are short term Smoker ");
        NoteBean noteB = new NoteBean("IdOfTheNote5", 3, LocalDateTime.now(), LocalDateTime.now(), "Patient states that they quit within last year Patient also complains that of Abnormal breathing spells Lab reports Cholesterol LDL high");
        List<NoteBean> notesOfPatientInDanger = List.of(noteA, noteB);
        when(patientProxy.getPatientById(any(Integer.class))).thenReturn(patientInDanger);
        when(noteProxy.getAllNotesByPatientId(any(Integer.class))).thenReturn(notesOfPatientInDanger);

        AssessmentDTO actualResult = serviceUnderTest.assessDiabetesRiskLevelByPatientId(patientId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("AssessmentDto").isNotNull();
            softly.assertThat(actualResult.getPatientDTO()).as("Patient in AssessmentDto")
                    .isEqualTo(patientInDanger);
            softly.assertThat(actualResult.getPatientAge()).as("Age in AssessmentDto")
                    .isEqualTo(expectedAge);
            softly.assertThat(actualResult.getDiabetesRiskLevelAssessment()).as("Diabetes risk level in AssessmentDto")
                    .isEqualTo(RiskLevels.IN_DANGER.getRiskLevel());
        });
        verify(patientProxy).getPatientById(3);
        verify(noteProxy).getAllNotesByPatientId(3);
    }

    @Test
    void assessDiabetesRiskLevelByPatientId_shouldReturnDiabetesAssessmentOfPatientTestEarlyOnsetCorrectly() {
        int patientId = 4;
        int expectedAge = 16;
        PatientBean patientEarlyOnset = new PatientBean(4, "TestEarlyOnset", "Test", LocalDate.now().minusYears(expectedAge), "F", "4 Valley Dr", "400-555-6666");
        NoteBean noteA = new NoteBean("IdOfTheNote6", 4, LocalDateTime.now(), LocalDateTime.now(), "Patient states that walking up stairs has become difficult Patient also complains that they are having shortness of breath Lab results indicate Antibodies present elevated Reaction to medication");
        NoteBean noteB = new NoteBean("IdOfTheNote7", 4, LocalDateTime.now(), LocalDateTime.now(), "Patient states that they are experiencing back pain when seated for a long time");
        NoteBean noteC = new NoteBean("IdOfTheNote8", 4, LocalDateTime.now(), LocalDateTime.now(), "Patient states that they are a short term Smoker Hemoglobin A1C above recommended level");
        NoteBean noteD = new NoteBean("IdOfTheNote9", 4, LocalDateTime.now(), LocalDateTime.now(), "Patient states that Body Height, Body Weight, Cholesterol, Dizziness and Reaction");
        List<NoteBean> notesOfPatientEarlyOnset = List.of(noteA, noteB, noteC, noteD);
        when(patientProxy.getPatientById(any(Integer.class))).thenReturn(patientEarlyOnset);
        when(noteProxy.getAllNotesByPatientId(any(Integer.class))).thenReturn(notesOfPatientEarlyOnset);

        AssessmentDTO actualResult = serviceUnderTest.assessDiabetesRiskLevelByPatientId(patientId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualResult).as("AssessmentDto").isNotNull();
            softly.assertThat(actualResult.getPatientDTO()).as("Patient in AssessmentDto")
                    .isEqualTo(patientEarlyOnset);
            softly.assertThat(actualResult.getPatientAge()).as("Age in AssessmentDto")
                    .isEqualTo(expectedAge);
            softly.assertThat(actualResult.getDiabetesRiskLevelAssessment()).as("Diabetes risk level in AssessmentDto")
                    .isEqualTo(RiskLevels.EARLY_ONSET.getRiskLevel());
        });
        verify(patientProxy).getPatientById(4);
        verify(noteProxy).getAllNotesByPatientId(4);
    }
}
