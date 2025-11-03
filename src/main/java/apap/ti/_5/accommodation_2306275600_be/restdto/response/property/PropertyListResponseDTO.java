package apap.ti._5.accommodation_2306275600_be.restdto.response.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyListResponseDTO {
    private String propertyID;
    private String propertyName;
    private int type;
    private int activeStatus;
    private int totalRoom;
    private LocalDateTime updatedDate;
}