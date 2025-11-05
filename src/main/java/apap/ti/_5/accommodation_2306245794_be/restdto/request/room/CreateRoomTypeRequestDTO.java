package apap.ti._5.accommodation_2306245794_be.restdto.request.room;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateRoomTypeRequestDTO {
    @NotBlank(message = "Room type name cannot be empty")
    private String name;

    @NotBlank(message = "Facility cannot be empty")
    private String facility;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be a positive number or zero")
    private Integer price;

    @NotNull(message = "Floor cannot be null")
    private Integer floor;

    @NotNull(message = "Number of units cannot be null")
    @Min(value = 1, message = "Each room type must have at least 1 unit")
    private Integer numberOfUnits;

    @NotBlank(message = "Description cannot be empty")
    private String description;
}
