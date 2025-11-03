package apap.ti._5.accommodation_2306275600_be.restdto.request.property;

import java.util.List;

import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePropertyRequestDTO {
    
    private String propertyName;
    
    @Min(value = 1, message = "Type must be between 1-3")
    @Max(value = 3, message = "Type must be between 1-3")
    private Integer type;

    private String address;
    
    private Integer province;
    
    private String description;
    
    @Min(value = 0, message = "Total room cannot be negative")
    private Integer totalRoom;
    
    @Min(value = 0, message = "Active status must be 0 or 1")
    @Max(value = 1, message = "Active status must be 0 or 1")
    private Integer activeStatus;

    private List<UpdateRoomTypeRequestDTO> roomTypes;
}