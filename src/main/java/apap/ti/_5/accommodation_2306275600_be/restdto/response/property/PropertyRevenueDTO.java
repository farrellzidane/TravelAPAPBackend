package apap.ti._5.accommodation_2306275600_be.restdto.response.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRevenueDTO {
    private String propertyID;
    private String propertyName;
    private String propertyType;
    private int totalBookings;
    private int totalRevenue;
    private double percentage; // Percentage of total revenue
}