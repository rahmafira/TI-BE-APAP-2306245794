package apap.ti._5.accommodation_2306245794_be.restdto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateMaintenanceRequestDTO {
    @NotBlank(message = "Room ID is required.")
    private String roomId;

    @NotNull(message = "Maintenance start date is required.")
    @FutureOrPresent(message = "Maintenance start date cannot be in the past.")
    private LocalDateTime maintenanceStart;

    @NotNull(message = "Maintenance end date is required.")
    @FutureOrPresent(message = "Maintenance end date cannot be in the past.")
    private LocalDateTime maintenanceEnd;
}