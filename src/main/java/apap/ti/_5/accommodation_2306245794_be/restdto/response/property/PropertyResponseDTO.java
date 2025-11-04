package apap.ti._5.accommodation_2306245794_be.restdto.response.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyResponseDTO {
    private String propertyId;
    private String propertyName;
    private int type;
    private int activeStatus;
    private int totalRoom;
}