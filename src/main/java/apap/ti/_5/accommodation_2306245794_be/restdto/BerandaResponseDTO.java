package apap.ti._5.accommodation_2306245794_be.restdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BerandaResponseDTO {
    private long totalProperties;
    private long totalBookings;
}