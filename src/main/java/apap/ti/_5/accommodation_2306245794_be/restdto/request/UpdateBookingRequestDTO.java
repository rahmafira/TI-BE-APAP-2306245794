package apap.ti._5.accommodation_2306245794_be.restdto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateBookingRequestDTO {
    @NotBlank 
    private String bookingId;

    @NotBlank 
    private String roomId;

    @NotNull 
    private LocalDate checkInDate;

    @NotNull 
    private LocalDate checkOutDate;

    @NotNull 
    private UUID customerId;

    @NotBlank 
    private String customerName;

    @NotBlank 
    @Email 
    private String customerEmail;

    @NotBlank 
    private String customerPhone;

    @NotNull 
    private Boolean isBreakfast;

    @NotNull 
    @Min(1) 
    private Integer capacity;
}