package re.patientservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequestDTO {
    private Long id;
    private String fullName;
    private String address;
    private String medicalHistory;
}
