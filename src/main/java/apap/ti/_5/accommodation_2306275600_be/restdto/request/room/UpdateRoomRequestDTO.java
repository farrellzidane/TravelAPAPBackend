package apap.ti._5.accommodation_2306275600_be.restdto.request.room;

import java.time.LocalDateTime;

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
public class UpdateRoomRequestDTO {
    
    private String roomID;
    
    private String name;
    
    @Min(value = 0, message = "Active room must be 0 or 1")
    @Max(value = 1, message = "Active room must be 0 or 1")
    private Integer activeRoom;

    @Min(value = 0, message = "Availability status must be 0 or 1")
    @Max(value = 1, message = "Availability status must be 0 or 1")
    private Integer availabilityStatus;
        
    private LocalDateTime maintenanceStart;
    private LocalDateTime maintenanceEnd;
}