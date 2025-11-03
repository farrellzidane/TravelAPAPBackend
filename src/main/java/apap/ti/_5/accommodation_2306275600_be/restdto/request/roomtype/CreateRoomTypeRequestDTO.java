package apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomTypeRequestDTO {
    
    @NotBlank(message = "Room type name is required")
    private String name;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Integer price;
    
    private String description;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    private String facility;
    
    @NotNull(message = "Floor is required")
    @Min(value = 0, message = "Floor cannot be negative")
    private Integer floor;
    
    @NotBlank(message = "Property ID is required")
    private String propertyID;
    
    @NotNull(message = "Unit count is required")
    @Min(value = 1, message = "Unit count must be at least 1")
    private Integer unitCount;
}