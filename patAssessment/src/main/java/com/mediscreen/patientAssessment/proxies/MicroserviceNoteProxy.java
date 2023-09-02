package com.mediscreen.patientAssessment.proxies;

import com.mediscreen.patientAssessment.beans.NoteBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "history-microservice", url = "localhost:8082")
@Component
public interface MicroserviceNoteProxy {

    @GetMapping("/patHistories/patientId/{id}")
    List<NoteBean> getAllNotesByPatientId(@PathVariable("id") Integer patientId);

}
