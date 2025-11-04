package apap.ti._5.accommodation_2306245794_be.restdto.response.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDetailDTO {
    private String roomId;
    private String name;
    private int availabilityStatus;
}
