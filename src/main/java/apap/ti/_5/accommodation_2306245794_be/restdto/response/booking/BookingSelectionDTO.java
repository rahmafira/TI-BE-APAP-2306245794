package apap.ti._5.accommodation_2306245794_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data 
@AllArgsConstructor 
@NoArgsConstructor
public class BookingSelectionDTO {

    private List<PropertyOption> properties;

    @Data 
    @AllArgsConstructor 
    @NoArgsConstructor
    public static class PropertyOption {
        private String id;
        private String name;
        private List<RoomTypeOption> roomTypes;
    }

    @Data 
    @AllArgsConstructor 
    @NoArgsConstructor
    public static class RoomTypeOption {
        private String id;
        private String name;
        private List<RoomOption> rooms;
    }

    @Data 
    @AllArgsConstructor 
    @NoArgsConstructor
    public static class RoomOption {
        private String id;
        private String name;
        private int capacity;
    }
}