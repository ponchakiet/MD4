package re.doctorservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.doctorservice.dto.DoctorResponseDTO;
import re.doctorservice.repository.DoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctor -> new DoctorResponseDTO(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialization()
                ))
                .collect(Collectors.toList());
    }
}