package apap.ti._5.accommodation_2306245794_be.restdto.response.property;

import apap.ti._5.accommodation_2306245794_be.restdto.response.roomtype.RoomTypeDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDetailDTO {
    private String propertyId;
    private String propertyName;
    private String description;
    private int income;
    private int type;
    private int province;
    private String address;
    private int totalRoom;
    private int activeStatus;
    private String ownerName;
    private UUID ownerId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<RoomTypeDetailDTO> listRoomType;
}
