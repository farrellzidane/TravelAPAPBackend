package apap.ti._5.accommodation_2306275600_be.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import apap.ti._5.accommodation_2306275600_be.model.TopUpTransaction;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopUpBillIntegrationServiceImpl implements TopUpBillIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${bill.service.url:http://localhost:8080}")
    private String billServiceUrl;

    @Value("${bill.service.api-key:TOPUP_API_KEY}")
    private String apiKey;

    @Override
    public void createBillForTopUp(TopUpTransaction topUpTransaction) {
        try {
            // Get customer ID
            UUID customerId = topUpTransaction.getCustomer() != null 
                ? topUpTransaction.getCustomer().getId() 
                : UUID.fromString(topUpTransaction.getEndUserId());
            
            System.out.println("🔔 Creating Bill for TopUp Transaction:");
            System.out.println("   TopUp ID: " + topUpTransaction.getId());
            System.out.println("   Customer ID: " + customerId);
            System.out.println("   Amount: Rp " + String.format("%,d", topUpTransaction.getAmount()));
            
            // Build request DTO
            CreateBillRequestDTO request = CreateBillRequestDTO.builder()
                    .apiKey(apiKey)
                    .customerId(customerId)
                    .serviceName("TopUp") // Match the service name enum
                    .serviceReferenceId(topUpTransaction.getId())
                    .description(String.format("Top-Up Transaction via %s - %s", 
                            topUpTransaction.getPaymentMethod().getProvider(),
                            topUpTransaction.getPaymentMethod().getMethodName()))
                    .amount(topUpTransaction.getAmount())
                    .build();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateBillRequestDTO> entity = new HttpEntity<>(request, headers);

            // Call Bill service - "fire and forget"
            String url = billServiceUrl + "/api/bill/create";
            
            System.out.println("   Calling URL: " + url);
            System.out.println("   API Key: " + apiKey);

            restTemplate.postForEntity(url, entity, String.class);
            
            System.out.println("✅ Bill created successfully for TopUp");
            
        } catch (Exception e) {
            // Fire and forget - we log the error but don't throw it
            System.err.println("⚠️ Failed to create bill for TopUp (non-blocking):");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("   TopUp transaction will continue with status: Pending");
        }
    }
}
