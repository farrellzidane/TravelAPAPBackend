package apap.ti._5.accommodation_2306275600_be.restdto.response.booking;

import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyRevenueDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingChartResponseDTO {
    private String period;
    private int month;
    private int year;
    private int totalProperties;
    private int totalRevenue;
    private List<PropertyRevenueDTO> propertyRevenues;
}