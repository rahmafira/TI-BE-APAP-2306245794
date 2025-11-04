package apap.ti._5.accommodation_2306245794_be.restdto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class UpdatePropertyRequestDTO {
    @NotBlank(message = "Property ID is required for an update.")
    private String propertyId; 

    @NotBlank(message = "Property Name cannot be empty.")
    private String propertyName;

    @NotBlank(message = "Address cannot be empty.")
    private String address;

    @NotBlank(message = "Description cannot be empty.")
    private String description;

    @Valid
    @NotNull(message = "Room types list cannot be null.")
    @Size(min = 1, message = "Property must have at least one room type.")
    private List<UpdateRoomTypeRequestDTO> listRoomType;
}