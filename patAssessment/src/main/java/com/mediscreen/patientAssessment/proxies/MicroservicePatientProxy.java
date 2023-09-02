package com.mediscreen.patientAssessment.proxies;

import com.mediscreen.patientAssessment.beans.PatientBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-microservice", url = "localhost:8081")
@Component
public interface MicroservicePatientProxy {

    @GetMapping({"/patients/{id}"})
    PatientBean getPatientById(@PathVariable("id") final Integer patientId);

}
