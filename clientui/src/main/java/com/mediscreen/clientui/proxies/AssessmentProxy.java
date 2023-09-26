package com.mediscreen.clientui.proxies;

import com.mediscreen.clientui.beans.AssessmentBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "assessment-microservice", url = "localhost:8083") // ligne non Docker
@FeignClient(name = "assessment-microservice", url = "assessment-ms:8083")  // ligne Docker
public interface AssessmentProxy {

    @GetMapping({"/assess/id/{id}"})
    AssessmentBean getPatientAssessment(@PathVariable("id") Integer patientId);

}
