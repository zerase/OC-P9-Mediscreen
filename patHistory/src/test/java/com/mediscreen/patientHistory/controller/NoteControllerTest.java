package com.mediscreen.patientHistory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.patientHistory.exception.DataNotFoundException;
import com.mediscreen.patientHistory.model.Note;
import com.mediscreen.patientHistory.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
public class NoteControllerTest {

    @MockBean
    private NoteService noteService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDateTime creationDate = LocalDateTime.of(2022, 12, 31, 8, 20);
    private final LocalDateTime modificationDate = LocalDateTime.of(2022, 12, 31, 8, 20);
    private final Note note1 = new Note("generatedId1", 1, creationDate, modificationDate, "Recommendation made to patient 1");
    private final Note note2 = new Note("generatedId2", 2, creationDate, modificationDate, "Recommendation A made to patient 2");
    private final Note note3 = new Note("generatedId3", 2, creationDate, modificationDate, "Recommendation B made to patient 2");


    // === TEST ADD NEW NOTE ==================================================
    @Test
    void addNewNote_shouldReturnHttpStatus201Created_whenRequestIsSuccessful() throws Exception {
        Note noteToAdd = new Note(1, creationDate, modificationDate, "Recommendation made to patient 1");
        when(noteService.createNote(any(Note.class))).thenReturn(note1);

        mockMvc.perform(post("/patHistories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteToAdd)))
                .andExpect(status().isCreated());

        verify(noteService).createNote(any(Note.class));
    }

    @Test
    void addNewNote_shouldReturnHttpStatus400BadRequest_whenRequestFailedWithValidationErrors() throws Exception {
        Note noteToAdd = new Note(null, null, null, null);

        mockMvc.perform(post("/patHistories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteToAdd)))
                .andExpect(status().isBadRequest());

        verify(noteService, times(0)).createNote(any(Note.class));
    }

    // === TEST GET ALL NOTES BY PATIENT ID ===================================
    @Test
    void getAllNotesByPatientId_shouldReturnHttpStatus200Ok_whenRequestIsSuccessful() throws Exception {
        List<Note> expectedNotesListWithSamePatientId = Arrays.asList(note2, note3);
        when(noteService.readAllNotesByPatientId(anyInt())).thenReturn(expectedNotesListWithSamePatientId);

        mockMvc.perform(get("/patHistories?patientId=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(noteService).readAllNotesByPatientId(2);
    }

    @Test
    void getAllNotesByPatientId_shouldReturnHttpStatus204NoContent_whenRequestIsSuccessfulWithListOfNotesEmpty() throws Exception {
        when(noteService.readAllNotesByPatientId(anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/patHistories?patientId=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(noteService).readAllNotesByPatientId(2);
    }

    // === TEST GET NOTE BY ID ================================================
    @Test
    void getNoteById_shouldReturnHttpStatus200Ok_whenRequestIsSuccessful() throws Exception {
        String noteId = note1.getId();
        when(noteService.readNoteById(anyString())).thenReturn(note1);

        mockMvc.perform(get("/patHistories/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(noteService).readNoteById(noteId);
    }

    @Test
    void getNoteById_shouldReturnHttpStatus404NotFound_whenRequestFailedWithInvalidGivenId() throws Exception {
        String unknownNoteId = "unknown123";
        when(noteService.readNoteById(anyString())).thenThrow(DataNotFoundException.class);

        mockMvc.perform(get("/patHistories/{id}", unknownNoteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService).readNoteById(unknownNoteId);
    }

    // === TEST UPDATE NOTE BY ID =============================================
    @Test
    void updateNoteById_shouldReturnHttpStatus200Ok_whenRequestIsSuccessful() throws Exception {
        String noteId = "generatedId1";
        LocalDateTime modificationDateUpdated = LocalDateTime.of(2022, 12, 31, 18, 20);
        Note updatedNote = new Note("generatedId1", 1, creationDate, modificationDateUpdated, "Recommendation updated");
        when(noteService.updateNote(anyString(), any(Note.class))).thenReturn(updatedNote);

        mockMvc.perform(put("/patHistories/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedNote)))
                .andExpect(status().isOk());

        verify(noteService).updateNote(anyString(), any(Note.class));
    }

    @Test
    void updateNoteById_shouldReturnHttpStatus400BadRequest_whenRequestFailedWithValidationErrors() throws Exception {
        String noteId = "generatedId1";
        Note updatedNote = new Note("generatedId1", null, null, null, null);

        mockMvc.perform(put("/patHistories/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedNote)))
                .andExpect(status().isBadRequest());

        verify(noteService, times(0)).updateNote(anyString(), any(Note.class));
    }

    @Test
    void updateNoteById_shouldReturnHttpStatus404NotFound_whenRequestFailedWithInvalidGivenId() throws Exception {
        String noteId = "000";
        LocalDateTime modificationDateUpdated = LocalDateTime.of(2022, 12, 31, 18, 20);
        Note updatedNote = new Note("000", 1, creationDate, modificationDateUpdated, "Recommendation updated");
        when(noteService.updateNote(anyString(), any(Note.class))).thenThrow(DataNotFoundException.class);

        mockMvc.perform(put("/patHistories/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedNote)))
                .andExpect(status().isNotFound());

        verify(noteService).updateNote(anyString(), any(Note.class));
    }

    // === TEST DELETE NOTE BY ID =============================================
    @Test
    void deleteNoteById_shouldReturnHttpStatus204NoContent_whenRequestIsSuccessful() throws Exception {
        String noteId = note1.getId();
        doNothing().when(noteService).deleteNote(anyString());

        mockMvc.perform(delete("/patHistories/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(noteService).deleteNote(noteId);
    }

    @Test
    void deleteNoteById_shouldReturnHttpStatus404NotFound_whenRequestFailed() throws Exception {
        String noteId = "unknownId";
        doThrow(DataNotFoundException.class).when(noteService).deleteNote(anyString());

        mockMvc.perform(delete("/patHistories/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService).deleteNote(noteId);
    }

    // === TEST DELETE ALL NOTES BY PATIENT ID ================================
    @Test
    void deleteAllNotesByPatientId_shouldReturnHttpStatus204NoContent_whenRequestIsSuccessful() throws Exception {
        Integer patientId = 2;
        doNothing().when(noteService).deleteNotesByPatientId(anyInt());

        mockMvc.perform(delete("/patHistories?patientId={id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(noteService).deleteNotesByPatientId(patientId);
    }
}
