package com.mediscreen.clientui.proxies;

import com.mediscreen.clientui.beans.PatientBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "patient-microservice", url = "localhost:8081")
public interface PatientMicroserviceProxy {

    @GetMapping("/patient/list")
    List<PatientBean> getAllPatients();

    @GetMapping("/patient/get/{id}")
    PatientBean getPatient(@PathVariable("id") Integer id);

    @PutMapping("/patient/update/{id}")
    PatientBean updatePatient(@PathVariable("id") Integer id, @Valid @RequestBody PatientBean patientBean);

    @PostMapping("/patient/add")
    PatientBean addPatient(@Valid @RequestBody PatientBean patientBean);

    @DeleteMapping("/patient/delete/{id}")
    void deletePatient(@PathVariable("id") Integer id);

}
