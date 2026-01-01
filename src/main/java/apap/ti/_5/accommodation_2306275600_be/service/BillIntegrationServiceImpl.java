package apap.ti._5.accommodation_2306275600_be.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import apap.ti._5.accommodation_2306275600_be.model.Bill;
import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.repository.BillRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillIntegrationServiceImpl implements BillIntegrationService {

    private final RestTemplate restTemplate;
    private final BillRepository billRepository;

    @Value("${bill.service.url:http://2306275380-be.hafizmuh.site}")
    private String billServiceUrl;

    @Value("${bill.service.api-key:ACCOMMODATION_API_KEY}")
    private String apiKey;

    @Override
    public void createBillForBooking(Booking booking) {
        try {
            // First, create bill in local database
            Bill bill = Bill.builder()
                    .customer(booking.getCustomer())
                    .serviceName("Accommodation")
                    .serviceReferenceId(booking.getBookingID().toString())
                    .description(String.format("Accommodation Booking - %s (%s to %s)", 
                            booking.getRoom().getRoomType().getName(),
                            booking.getCheckInDate().toLocalDate(),
                            booking.getCheckOutDate().toLocalDate()))
                    .amount(Long.valueOf(booking.getTotalPrice()))
                    .status(0) // 0 = Unpaid
                    .build();
            
            Bill savedBill = billRepository.save(bill);
            
            System.out.println("✅ Bill created in local database:");
            System.out.println("   Bill ID: " + savedBill.getBillId());
            System.out.println("   Booking ID: " + booking.getBookingID());
            System.out.println("   Amount: Rp " + String.format("%,d", booking.getTotalPrice()));
            
            // Optional: Also notify external Bill service if configured
            try {
                notifyExternalBillService(booking);
            } catch (Exception e) {
                // Log error but don't fail the bill creation
                System.err.println("⚠️ Failed to notify external Bill service (non-critical): " + e.getMessage());
            }
            
        } catch (Exception e) {
            // Log error and rethrow to ensure booking knows bill creation failed
            System.err.println("❌ CRITICAL: Failed to create bill in local database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create bill for booking", e);
        }
    }
    
    private void notifyExternalBillService(Booking booking) {
        // Build request DTO
        CreateBillRequestDTO request = CreateBillRequestDTO.builder()
                .apiKey(apiKey)
                .customerId(booking.getCustomerID())
                .serviceName("Accommodation")
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

        // Call external Bill service
        String url = billServiceUrl + "/api/bill/create";
        
        System.out.println("🔔 Notifying external Bill Service:");
        System.out.println("   URL: " + url);

        restTemplate.postForEntity(url, entity, String.class);
        
        System.out.println("✅ External Bill service notified successfully");
    }
}
