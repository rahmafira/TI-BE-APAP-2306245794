package apap.ti._5.accommodation_2306245794_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrefilledBookingDTO {
    private String roomId;
    private String roomName;
    private int capacity;
}