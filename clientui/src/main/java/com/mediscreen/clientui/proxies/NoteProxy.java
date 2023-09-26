package com.mediscreen.clientui.proxies;

import com.mediscreen.clientui.beans.NoteBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//@FeignClient(name = "history-microservice", url = "localhost:8082") // ligne non Docker
@FeignClient(name = "history-microservice", url = "history-ms:8082") // ligne Docker
public interface NoteProxy {

    @PostMapping({"/patHistories"})
    NoteBean addNewNote(NoteBean noteBean);

    @GetMapping({"/patHistories"})
    List<NoteBean> getAllNotesByPatientId(@RequestParam(value = "patientId", required = true) Integer patientId);

    @GetMapping({"/patHistories/{id}"})
    NoteBean getNoteById(@PathVariable("id") String noteId);

    @PutMapping({"/patHistories/{id}"})
    NoteBean updateNoteById(@PathVariable("id") String noteId, @Valid @RequestBody NoteBean noteBean);

    @DeleteMapping({"/patHistories/{id}"})
    void deleteNoteById(@PathVariable("id") String noteId);

    @DeleteMapping({"/patHistories"})
    void deleteAllNotesByPatientId(@RequestParam(value = "patientId", required = true) Integer patientId);

}
