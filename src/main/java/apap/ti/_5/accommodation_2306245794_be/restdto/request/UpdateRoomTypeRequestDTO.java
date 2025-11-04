package apap.ti._5.accommodation_2306245794_be.restdto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateRoomTypeRequestDTO {
    @NotBlank(message = "Internal Room Type ID is missing.")
    private String roomTypeId;

    @NotBlank(message = "Facility field cannot be empty.")
    private String facility; 

    @NotBlank(message = "Description for room type cannot be empty.")
    private String description;

    @NotNull(message = "Capacity is required.")
    @Min(value = 1, message = "Capacity must be at least 1.")
    private Integer capacity;

    @NotNull(message = "Price is required.")
    @PositiveOrZero(message = "Price must be zero or a positive number.")
    private Integer price;
}