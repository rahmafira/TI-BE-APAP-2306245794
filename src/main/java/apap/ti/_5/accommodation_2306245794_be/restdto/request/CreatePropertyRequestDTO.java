package apap.ti._5.accommodation_2306245794_be.restdto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CreatePropertyRequestDTO {
    @NotBlank(message = "Property name cannot be empty")
    private String propertyName;

    @NotNull(message = "Type cannot be null")
    private Integer type;

    @NotNull(message = "Province cannot be null")
    private Integer province;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Owner ID cannot be null")
    private UUID ownerId;

    @NotBlank(message = "Owner name cannot be empty")
    private String ownerName;

    @Valid
    @NotNull(message = "Room types list cannot be null")
    @Size(min = 1, message = "Property must have at least one room type")
    private List<CreateRoomTypeRequestDTO> listRoomType;
}
