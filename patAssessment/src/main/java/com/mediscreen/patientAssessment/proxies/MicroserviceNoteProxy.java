package com.mediscreen.patientAssessment.proxies;

import com.mediscreen.patientAssessment.beans.NoteBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(value = "history-microservice", url = "localhost:8082") // ligne non Docker
@FeignClient(value = "history-microservice", url = "history-ms:8082") // ligne Docker
@Component
public interface MicroserviceNoteProxy {

    @GetMapping({"/patHistories"})
    List<NoteBean> getAllNotesByPatientId(@RequestParam(value = "patientId") Integer patientId);

}
