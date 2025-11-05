package apap.ti._5.accommodation_2306245794_be.restdto.request.room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class AddRoomTypesRequestDTO {
    @NotBlank(message = "Property ID is required.")
    private String propertyId;

    @Valid
    @NotNull
    @Size(min = 1, message = "At least one new room type must be provided.")
    private List<CreateRoomTypeRequestDTO> newRoomTypes; 
}