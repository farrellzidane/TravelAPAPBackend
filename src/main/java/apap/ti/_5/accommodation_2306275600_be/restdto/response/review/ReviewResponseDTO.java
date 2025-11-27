package apap.ti._5.accommodation_2306275600_be.restdto.response.review;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    
    private String reviewID;
    private UUID bookingID;
    private String customerID;
    private String customerName;
    private String propertyName;
    private Double overallRating;
    private Integer cleanlinessRating;
    private Integer facilityRating;
    private Integer serviceRating;
    private Integer valueRating;
    private String comment;
    private LocalDateTime createdDate;
}
