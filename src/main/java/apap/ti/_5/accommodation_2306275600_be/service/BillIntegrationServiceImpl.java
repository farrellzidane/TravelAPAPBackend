package apap.ti._5.accommodation_2306275600_be.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillIntegrationServiceImpl implements BillIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${bill.service.url:http://2306275380-be.hafizmuh.site}")
    private String billServiceUrl;

    @Value("${bill.service.api-key:ACCOMMODATION_API_KEY}")
    private String apiKey;

    @Override
    public void createBillForBooking(Booking booking) {
        try {
            // Build request DTO
            CreateBillRequestDTO request = CreateBillRequestDTO.builder()
                    .apiKey(apiKey)
                    .customerId(booking.getCustomerID())
                    .serviceName("accomodation_booking") // Sesuai dengan enum ServiceType
                    .serviceReferenceId(booking.getBookingID().toString())
                    .description(String.format("Accommodation Booking - %s (%s to %s)", 
                            booking.getRoom().getRoomType().getName(),
                            booking.getCheckInDate().toLocalDate(),
                            booking.getCheckOutDate().toLocalDate()))
                    .amount(Long.valueOf(booking.getTotalPrice()))
                    .build();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateBillRequestDTO> entity = new HttpEntity<>(request, headers);

            // Call Bill service - "fire and forget"
            String url = billServiceUrl + "/api/bill/create";
            
            System.out.println("🔔 Calling Bill Service:");
            System.out.println("   URL: " + url);
            System.out.println("   Booking ID: " + booking.getBookingID());
            System.out.println("   Amount: Rp " + String.format("%,d", booking.getTotalPrice()));

            restTemplate.postForEntity(url, entity, String.class);
            
            System.out.println("✅ Bill service called successfully (fire and forget)");
            
        } catch (Exception e) {
            // Fire and forget - we log the error but don't throw it
            System.err.println("⚠️ Failed to call Bill service (non-blocking): " + e.getMessage());
            System.err.println("   Booking will continue with status: Waiting for Payment");
        }
    }
}
