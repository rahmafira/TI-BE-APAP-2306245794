package apap.ti._5.accommodation_2306245794_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private String bookingId;
    private String propertyName;
    private String roomName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private int totalPrice;
    private int status;
}