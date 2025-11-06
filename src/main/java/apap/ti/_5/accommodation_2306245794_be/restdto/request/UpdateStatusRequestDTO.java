package apap.ti._5.accommodation_2306245794_be.restdto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusRequestDTO {
    @NotBlank(message = "Booking ID is required.")
    private String bookingId;
}