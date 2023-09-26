package com.mediscreen.patientHistory.service;

import com.mediscreen.patientHistory.exception.DataNotFoundException;
import com.mediscreen.patientHistory.model.Note;
import com.mediscreen.patientHistory.repository.NoteRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
public class NoteServiceImplTest {

    @InjectMocks
    private NoteServiceImpl serviceUnderTest;
    @Mock
    private NoteRepository noteRepository;

    private final LocalDateTime creationDate = LocalDateTime.of(2022, 12, 31, 8, 20);
    private final LocalDateTime modificationDate = LocalDateTime.of(2022, 12, 31, 8, 20);
    private final Note note1 = new Note("generatedId1", 1, creationDate, modificationDate, "Recommendation made to patient 1");
    private final Note note2 = new Note("generatedId2", 2, creationDate, modificationDate, "Recommendation A made to patient 2");
    private final Note note3 = new Note("generatedId3", 2, creationDate, modificationDate, "Recommendation B made to patient 2");


    // === TEST CREATE NOTE OPERATION =========================================
    @Test
    void createNote_shouldReturnCreatedNoteCorrectly() {
        Note noteToSave = new Note(1, creationDate, modificationDate, "Recommendation made to patient 1");
        when(noteRepository.insert(any(Note.class))).thenReturn(note1);

        Note result = serviceUnderTest.createNote(noteToSave);

        SoftAssertions.assertSoftly(softly -> softly.assertThat(result).as("Note saved in database")
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(note1));
        verify(noteRepository).insert(noteToSave);
    }

    // === TEST READ ALL BY PATIENT ID OPERATION ==============================
    @Test
    void readAllNotesByPatientId_shouldReturnEmptyList_whenDatabaseDoesNotFindDataWithGivenPatientId() {
        Integer patientId = 2;
        when(noteRepository.findAllByPatientIdOrderByDateOfCreationDesc(anyInt())).thenReturn(new ArrayList<>());

        List<Note> result = serviceUnderTest.readAllNotesByPatientId(patientId);

        SoftAssertions.assertSoftly(softly -> softly.assertThat(result).as("Notes found from database")
                .isNotNull()
                .isEmpty());
        verify(noteRepository).findAllByPatientIdOrderByDateOfCreationDesc(patientId);
    }

    @Test
    void readAllNotesByPatientId_shouldReturnListOfNotesWithSamePatientId_whenDatabaseFindDataWithGivenPatientId() {
        Integer patientId = 2;
        List<Note> expectedNotesList = Arrays.asList(note2, note3);
        when(noteRepository.findAllByPatientIdOrderByDateOfCreationDesc(anyInt())).thenReturn(expectedNotesList);

        List<Note> result = serviceUnderTest.readAllNotesByPatientId(patientId);

        SoftAssertions.assertSoftly(softly -> softly.assertThat(result).as("Notes found from database")
                .isNotNull()
                .hasSize(expectedNotesList.size())
                .contains(note2, note3)
                .doesNotContain(note1));
        verify(noteRepository).findAllByPatientIdOrderByDateOfCreationDesc(patientId);
    }

    // === TEST READ ONE BY ID OPERATION ======================================
    @Test
    void readNoteById_shouldReturnTheNoteWithTheGivenId_whenGivenIdIsPresentInDatabase() {
        String noteId = "generatedId1";
        when(noteRepository.findById(anyString())).thenReturn(Optional.of(note1));

        Note result = serviceUnderTest.readNoteById(noteId);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).as("Note found").isNotNull();
            softly.assertThat(result.getPatientId()).as("Note found patientId").isEqualTo(note1.getPatientId());
            softly.assertThat(result.getContent()).as("Note found content").isEqualTo(note1.getContent());
        });
        verify(noteRepository).findById(noteId);
    }

    @Test
    void readNoteById_shouldThrowAnException_whenGivenIdIsNotPresentInDatabase() {
        String unknownNoteId = "unknown123";
        when(noteRepository.findById(anyString())).thenReturn(Optional.empty());

        Throwable result = catchThrowable(() -> serviceUnderTest.readNoteById(unknownNoteId));

        SoftAssertions.assertSoftly(softly -> softly.assertThat(result).as("Thrown exception")
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Note not found"));
        verify(noteRepository).findById(unknownNoteId);
    }

    // === TEST UPDATE NOTE OPERATION =========================================
    @Test
    void updateNote_shouldReturnUpdatedNote_whenGivenIdIsPresentInDatabase() {
        LocalDateTime modificationDateUpdated = LocalDateTime.of(2022, 12, 31, 18, 20);
        Note noteToUpdate = new Note("generatedId1", 1, creationDate, modificationDateUpdated, "Recommendation updated");
        Note noteUpdated = new Note("generatedId1", 1, creationDate, modificationDateUpdated, "Recommendation updated");
        String noteId = note1.getId();
        when(noteRepository.findById(anyString())).thenReturn(Optional.of(note1));
        when(noteRepository.save(any(Note.class))).thenReturn(noteUpdated);

        Note result = serviceUnderTest.updateNote(noteId, noteToUpdate);

        SoftAssertions.assertSoftly(softly -> softly.assertThat(result).as("Patient updated")
                .isNotNull()
                .isEqualTo(noteUpdated));
        verify(noteRepository).findById(noteId);
        verify(noteRepository).save(noteToUpdate);
    }

    @Test
    void updateNote_shouldThrowAnException_whenGivenIdIsNotPresentInDatabase() {
        String unknownNoteId = "000";
        when(noteRepository.findById(anyString())).thenReturn(Optional.empty());

        Throwable result = catchThrowable(() -> serviceUnderTest.updateNote(unknownNoteId, note1));

        SoftAssertions.assertSoftly(softly -> softly.assertThat(result).as("Thrown exception")
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Note not found"));
        verify(noteRepository).findById(unknownNoteId);
        verify(noteRepository, times(0)).save(note1);
    }

    // === TEST DELETE NOTE OPERATION =========================================
    @Test
    void deleteNote_shouldDeleteNote_whenGivenIdIsPresentInDatabase() {
        String noteId = note1.getId();
        when(noteRepository.existsById(anyString())).thenReturn(true);

        serviceUnderTest.deleteNote(noteId);

        verify(noteRepository).existsById(noteId);
        verify(noteRepository).deleteById(noteId);
    }

    @Test
    void deleteNote_shouldThrowAnException_whenGivenIdIsNotPresentInDatabase() {
        String unknownNoteId = "unknownId";
        when(noteRepository.existsById(anyString())).thenReturn(false);

        Throwable result = catchThrowable(() -> serviceUnderTest.deleteNote(unknownNoteId));

        SoftAssertions.assertSoftly(softly -> softly.assertThat(result).as("Thrown exception")
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Note not found"));
        verify(noteRepository).existsById(unknownNoteId);
        verify(noteRepository, times(0)).deleteById(unknownNoteId);
    }

    // === TEST DELETE NOTES BY PATIENT ID OPERATION ==========================
    @Test
    void deleteNotesByPatientId_shouldDeleteNotesWithTheSamePatientId() {
        Integer patientId = 2;

        serviceUnderTest.deleteNotesByPatientId(patientId);

        verify(noteRepository).deleteAllByPatientId(patientId);
    }
}
