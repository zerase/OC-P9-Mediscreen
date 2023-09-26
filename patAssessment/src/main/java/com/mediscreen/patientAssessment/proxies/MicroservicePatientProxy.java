package com.mediscreen.patientAssessment.proxies;

import com.mediscreen.patientAssessment.beans.PatientBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "patient-microservice", url = "localhost:8081")  // ligne non Docker
@FeignClient(name = "patient-microservice", url = "patient-ms:8081") // ligne Docker
@Component
public interface MicroservicePatientProxy {

    @GetMapping({"/patients/{id}"})
    PatientBean getPatientById(@PathVariable("id") final Integer patientId);

    @GetMapping({"/patients"})
    List<PatientBean> getAllPatients(@RequestParam(value = "lastName", required = false) String nameSearched);

}
