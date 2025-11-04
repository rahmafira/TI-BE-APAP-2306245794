package apap.ti._5.accommodation_2306245794_be.restdto.response.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyHeaderDTO {
    private String propertyId;
    private String propertyName;
    private int type;
}