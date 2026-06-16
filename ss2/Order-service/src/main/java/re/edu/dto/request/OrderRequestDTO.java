package re.edu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {
    @NotNull(message = "mã khóa học không được để trống")
    private int courseId;
    @NotNull(message = "Mã học sinh không dược để trống")
    private int studentId;
}
