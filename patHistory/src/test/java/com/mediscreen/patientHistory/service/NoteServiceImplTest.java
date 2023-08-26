package com.mediscreen.patientHistory.service;

import com.mediscreen.patientHistory.exception.DataNotFoundException;
import com.mediscreen.patientHistory.model.Note;
import com.mediscreen.patientHistory.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceImplTest {

    @InjectMocks
    private NoteServiceImpl noteServiceUnderTest;
    @Mock
    private NoteRepository noteRepository;


    @Test
    public void readAllNotes_shouldReturnAllNotesFromDatabase() {
        // GIVEN
        String generatedIdByDatabase1 = "IdOfNote1", generatedIdByDatabase2 = "IdOfNote2";
        Integer patientId1 = 1, patientId2 = 2;
        LocalDateTime noteCreatedDate1 = LocalDateTime.of(2020, 12, 25, 8,20),
                noteCreatedDate2 = LocalDateTime.of(2020, 12, 31, 8, 20);
        LocalDateTime noteUpdatedDate1 = LocalDateTime.of(2020, 12, 25, 8,20),
                noteUpdatedDate2 = LocalDateTime.of(2020, 12, 31, 8, 20);
        String noteContent1 = "Content of the note 1", noteContent2 = "Content of the note 2";

        Note mockNoteInDatabase1 = new Note(generatedIdByDatabase1, patientId1, noteCreatedDate1, noteUpdatedDate1, noteContent1);
        Note mockNoteInDatabase2 = new Note(generatedIdByDatabase2, patientId2, noteCreatedDate2, noteUpdatedDate2, noteContent2);
        List<Note> expectedAllNotesList = Arrays.asList(mockNoteInDatabase1, mockNoteInDatabase2);
        Integer expectedListSize = expectedAllNotesList.size();

        when(noteRepository.findAll()).thenReturn(expectedAllNotesList);

        // WHEN
        List<Note> allNotesListResult = noteServiceUnderTest.readAllNotes();

        // THEN
        assertThat(allNotesListResult).isNotNull();
        assertThat(allNotesListResult.size()).isEqualTo(expectedListSize);
        verify(noteRepository).findAll();
    }


    @Test
    public void readAllNotesOfOnePatient_shouldReturnAllNotesOfOnePatientFromDatabase() {
        // GIVEN
        String generatedIdByDatabase1 = "IdOfNote1", generatedIdByDatabase2 = "IdOfNote2";
        Integer patientId = 1;
        LocalDateTime noteCreatedDate1 = LocalDateTime.of(2020, 12, 25, 8, 20),
                noteCreatedDate2 = LocalDateTime.of(2020, 12, 31, 8, 20);
        LocalDateTime noteUpdatedDate1 = LocalDateTime.of(2020, 12, 25, 8, 20),
                noteUpdatedDate2 = LocalDateTime.of(2020, 12, 31, 8, 20);
        String noteContent1 = "Content of the note 1", noteContent2 = "Content of the note 2";

        Note mockNoteInDatabase1 = new Note(generatedIdByDatabase1, patientId, noteCreatedDate1, noteUpdatedDate1, noteContent1);
        Note mockNoteInDatabase2 = new Note(generatedIdByDatabase2, patientId, noteCreatedDate2, noteUpdatedDate2, noteContent2);
        List<Note> expectedListOfAllNotesOfPatient = Arrays.asList(mockNoteInDatabase1, mockNoteInDatabase2);
        Integer expectedListSize = expectedListOfAllNotesOfPatient.size();

        when(noteRepository.findAllByPatientIdOrderByDateOfCreationDesc(any(Integer.class))).thenReturn(expectedListOfAllNotesOfPatient);

        // WHEN
        List<Note> listOfAllNotesOfPatientResult = noteServiceUnderTest.readAllNotesByPatientId(patientId);

        // THEN
        assertThat(listOfAllNotesOfPatientResult).isNotNull();
        assertThat(listOfAllNotesOfPatientResult.size()).isEqualTo(expectedListSize);
        assertThat(listOfAllNotesOfPatientResult).usingRecursiveComparison().isEqualTo(expectedListOfAllNotesOfPatient);
        verify(noteRepository).findAllByPatientIdOrderByDateOfCreationDesc(any(Integer.class));
    }


    @Test
    public void readNoteById_shouldReturnNoteWithTheGivenIdFromDatabase() {
        // GIVEN
        String generatedIdByDatabase = "IdOfTheNote";
        Integer patientId = 1;
        LocalDateTime noteCreatedDate = LocalDateTime.of(2020, 12, 31, 8, 20);
        LocalDateTime noteUpdatedDate = LocalDateTime.of(2020, 12, 31, 8, 20);
        String noteContent = "Content of the note";

        Note mockNoteInDatabase = new Note(generatedIdByDatabase, patientId, noteCreatedDate, noteUpdatedDate, noteContent);
        Note expectedNote = Optional.of(mockNoteInDatabase).get();
        String noteId = generatedIdByDatabase;

        when(noteRepository.findById(any(String.class))).thenReturn(Optional.of(mockNoteInDatabase));

        // WHEN
        Note noteResult = noteServiceUnderTest.readNoteById(noteId);

        // THEN
        assertThat(noteResult).isNotNull();
        assertThat(noteResult).usingRecursiveComparison().isEqualTo(expectedNote);
        verify(noteRepository).findById(any(String.class));
    }


    @Test
    public void readNoteById_shouldThrowException_whenGivenIdIsUnknown() {
        // GIVEN
        String noteId = "UnknownId";

        when(noteRepository.findById(any(String.class))).thenReturn(Optional.empty());

        // WHEN
        Throwable noteResult = catchThrowable(
                () -> noteServiceUnderTest.readNoteById(noteId)
        );

        // THEN
        assertThat(noteResult)
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("not found");
        verify(noteRepository).findById("UnknownId");
    }


    @Test
    public void createNote_shouldInsertNoteInDatabase() {
        // GIVEN
        String generatedIdByMongoDB = "MongoDBIdForCreateNoteTest";
        Integer patientId = 1;
        LocalDateTime noteCreatedDate = LocalDateTime.of(2022, 12, 31, 8, 20);
        LocalDateTime noteUpdatedDate = LocalDateTime.of(2022, 12, 31, 8, 20);
        String noteContent = "Content of the note";

        Note mockNoteInDatabase = new Note(generatedIdByMongoDB, patientId, noteCreatedDate, noteUpdatedDate, noteContent);
        Note noteToAdd = new Note(patientId, noteCreatedDate, noteUpdatedDate, noteContent);

        when(noteRepository.insert(any(Note.class))).thenReturn(mockNoteInDatabase);

        // WHEN
        Note createdNote = noteServiceUnderTest.createNote(noteToAdd);

        // THEN
        assertThat(createdNote).isNotNull();
        assertThat(createdNote.getId()).isNotNull();
        assertThat(createdNote).usingRecursiveComparison().isEqualTo(mockNoteInDatabase);
        verify(noteRepository).insert(noteToAdd);
    }
}
