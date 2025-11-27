package apap.ti._5.accommodation_2306275600_be.restdto.request.review;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequestDTO {
    
    @NotNull(message = "Booking ID is required")
    private String bookingID;
    
    @NotNull(message = "Cleanliness rating is required")
    @Min(value = 1, message = "Cleanliness rating must be between 1 and 5")
    @Max(value = 5, message = "Cleanliness rating must be between 1 and 5")
    private Integer cleanlinessRating;
    
    @NotNull(message = "Facility rating is required")
    @Min(value = 1, message = "Facility rating must be between 1 and 5")
    @Max(value = 5, message = "Facility rating must be between 1 and 5")
    private Integer facilityRating;
    
    @NotNull(message = "Service rating is required")
    @Min(value = 1, message = "Service rating must be between 1 and 5")
    @Max(value = 5, message = "Service rating must be between 1 and 5")
    private Integer serviceRating;
    
    @NotNull(message = "Value rating is required")
    @Min(value = 1, message = "Value rating must be between 1 and 5")
    @Max(value = 5, message = "Value rating must be between 1 and 5")
    private Integer valueRating;
    
    private String comment;
}
