package apap.ti._5.accommodation_2306245794_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailDTO {
    private String bookingId;
    private String propertyName;
    private String roomName;
    private String roomId; 
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalDays;
    private int status;
    private boolean isBreakfast;
    private int totalPrice;
    private int extraPay;
    private int refund;
    private int capacity;
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;
}