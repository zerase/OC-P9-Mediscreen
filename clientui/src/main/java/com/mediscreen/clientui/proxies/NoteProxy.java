package com.mediscreen.clientui.proxies;

import com.mediscreen.clientui.beans.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "history-microservice", url = "localhost:8082")
public interface NoteProxy {

    @PostMapping({"/patHistories"})
    NoteDto addNote(NoteDto noteDto);

    @GetMapping({"/patHistories"})
    List<NoteDto> getAllNotes();

    @GetMapping({"/patHistories/patientId/{id}"})
    List<NoteDto> getAllNotesByPatientId(@PathVariable("id") Integer patientId);

    @GetMapping({"/patHistories/{id}"})
    NoteDto getNoteById(@PathVariable("id") String noteId);

    @PutMapping({"/patHistories/{id}"})
    NoteDto updateNote(@PathVariable("id") String noteId, @Valid @RequestBody NoteDto noteDto);

    @DeleteMapping({"/patHistories/{id}"})
    void deleteNote(@PathVariable("id") String noteId);

}
