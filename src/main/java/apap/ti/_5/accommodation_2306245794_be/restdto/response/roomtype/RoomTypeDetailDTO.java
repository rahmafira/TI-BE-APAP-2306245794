package apap.ti._5.accommodation_2306245794_be.restdto.response.roomtype;

import apap.ti._5.accommodation_2306245794_be.restdto.response.room.RoomDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeDetailDTO {
    private String roomTypeId;
    private String name;
    private String description;
    private int price;
    private List<RoomDetailDTO> listRoom;
}