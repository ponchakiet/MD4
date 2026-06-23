package re.appointmentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import re.appointmentservice.dto.AppointmentRequestDTO;
import re.appointmentservice.entity.Appointment;
import re.appointmentservice.repository.AppointmentRepository;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate;

    public Appointment createAppointment(AppointmentRequestDTO dto) {
        Boolean isPatientExist = restTemplate.getForObject(
                "http://PATIENT-SERVICE/api/v1/patients/exists/" + dto.getPatientId(), Boolean.class);

        Boolean isDoctorExist = restTemplate.getForObject(
                "http://DOCTOR-SERVICE/api/v1/doctors/exists/" + dto.getDoctorId(), Boolean.class);

        if (Boolean.TRUE.equals(isPatientExist) && Boolean.TRUE.equals(isDoctorExist)) {
            Appointment appointment = Appointment.builder()
                    .patientId(dto.getPatientId())
                    .doctorId(dto.getDoctorId())
                    .appointmentDate(dto.getAppointmentDate())
                    .reason(dto.getReason())
                    .status("PENDING")
                    .build();
            return appointmentRepository.save(appointment);
        } else {
            throw new RuntimeException("Thông tin Bệnh nhân hoặc Bác sĩ không hợp lệ!");
        }
    }
}