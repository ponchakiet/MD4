package re.patientservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.patientservice.dto.PatientRequestDTO;
import re.patientservice.entity.Patient;
import re.patientservice.repository.PatientRepository;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientRequestDTO createPatient(PatientRequestDTO dto) {
        Patient patient = new Patient();
        patient.setFullName(dto.getFullName());
        patient.setAddress(dto.getAddress());
        patient.setMedicalHistory(dto.getMedicalHistory());

        Patient savedPatient = patientRepository.save(patient);

        return PatientRequestDTO.builder()
                .id(savedPatient.getId())
                .fullName(savedPatient.getFullName())
                .address(savedPatient.getAddress())
                .medicalHistory(savedPatient.getMedicalHistory())
                .build();
    }
}