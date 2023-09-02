package com.mediscreen.patientAssessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PatientAssessmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatientAssessmentApplication.class, args);
	}

}
