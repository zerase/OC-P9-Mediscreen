package com.mediscreen.clientui.proxies;

import com.mediscreen.clientui.beans.PatientBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "patient-microservice", url = "localhost:8081")
public interface PatientMicroserviceProxy {

    @GetMapping("/patients")
    List<PatientBean> getAllPatients();

    @GetMapping("/patients/{id}")
    PatientBean getPatient(@PathVariable("id") Integer id);

    @PutMapping("/patients/{id}")
    PatientBean updatePatient(@PathVariable("id") Integer id, @Valid @RequestBody PatientBean patientBean);

    @PostMapping("/patients")
    PatientBean addPatient(@Valid @RequestBody PatientBean patientBean);

    @DeleteMapping("/patients/{id}")
    void deletePatient(@PathVariable("id") Integer id);

}
