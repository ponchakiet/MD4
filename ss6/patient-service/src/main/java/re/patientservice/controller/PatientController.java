package re.patientservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.patientservice.dto.PatientRequestDTO;
import re.patientservice.service.PatientService;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientRequestDTO> addPatient(@RequestBody PatientRequestDTO patientDTO) {
        PatientRequestDTO savedPatient = patientService.createPatient(patientDTO);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }
}