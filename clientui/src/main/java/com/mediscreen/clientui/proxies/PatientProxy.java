package com.mediscreen.clientui.proxies;

import com.mediscreen.clientui.beans.PatientBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//@FeignClient(name = "patient-microservice", url = "localhost:8081") // ligne non Docker
@FeignClient(name = "patient-microservice", url = "patient-ms:8081")  // ligne Docker
public interface PatientProxy {

    @GetMapping({"/patients"})
    List<PatientBean> getAllPatients(@RequestParam(value = "lastName", required = false) String nameSearched);

    @GetMapping({"/patients/{id}"})
    PatientBean getPatientById(@PathVariable("id") Integer id);

    @PutMapping({"/patients/{id}"})
    PatientBean updatePatientById(@PathVariable("id") Integer id, @Valid @RequestBody PatientBean patientToUpdate);

    @PostMapping({"/patients"})
    PatientBean addNewPatient(@Valid @RequestBody PatientBean patientToAdd);

    @DeleteMapping({"/patients/{id}"})
    void deletePatientById(@PathVariable("id") Integer id);

}
