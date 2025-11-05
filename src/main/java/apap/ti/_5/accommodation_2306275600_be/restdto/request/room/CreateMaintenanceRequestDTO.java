package apap.ti._5.accommodation_2306275600_be.restdto.request.room;

import java.time.LocalDateTime;
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
public class CreateMaintenanceRequestDTO {
    
    @NotBlank(message = "Room ID is required")
    private String roomID;
    
    @NotNull(message = "Maintenance start date is required")
    private LocalDateTime maintenanceStart;
    
    @NotNull(message = "Maintenance end date is required")
    private LocalDateTime maintenanceEnd;
}