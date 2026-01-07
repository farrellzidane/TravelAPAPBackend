package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.exceptions.BillAlreadyPaidException;
import apap.ti._5.accommodation_2306275600_be.exceptions.BillNotFoundException;
import apap.ti._5.accommodation_2306275600_be.exceptions.InsufficientBalanceException;
import apap.ti._5.accommodation_2306275600_be.model.Bill;
import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.model.Customer;
import apap.ti._5.accommodation_2306275600_be.repository.BillRepository;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.CustomerRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.PayBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.UpdateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class BillRestServiceImpl implements BillRestService {

    protected final BillRepository billRepository;
    protected final CustomerRepository customerRepository;
    protected final BookingRepository bookingRepository;
    protected final RestTemplate restTemplate;

    @Value("${bill.service.api-key:ACCOMMODATION_API_KEY}")
    private String apiKey;
    
    @Value("${bill.service.topup-api-key:TOPUP_API_KEY}")
    private String topUpApiKey;

    private static final Set<String> VALID_SERVICE_NAMES = Set.of(
        "Accommodation", "TopUp", "Flight", "Insurance", "VehicleRental", "TourPackage"
    );

    @Override
    public BillResponseDTO createBill(CreateBillRequestDTO dto) {
        // Validation: Check API Key (accept multiple API keys)
        if (!apiKey.equals(dto.getApiKey()) && !topUpApiKey.equals(dto.getApiKey())) {
            throw new RuntimeException("Invalid API Key");
        }

        // Validation: All fields required
        if (dto.getCustomerId() == null || dto.getServiceName() == null || 
            dto.getServiceReferenceId() == null || dto.getDescription() == null || 
            dto.getAmount() == null) {
            throw new RuntimeException("All fields are required");
        }

        // Validation: Amount > 0
        if (dto.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        // Validation: Service name must be valid
        if (!VALID_SERVICE_NAMES.contains(dto.getServiceName())) {
            throw new RuntimeException("Service name must be one of: " + String.join(", ", VALID_SERVICE_NAMES));
        }

        // Load customer
        Customer customer = customerRepository.findById(UUID.fromString(dto.getCustomerId().toString()))
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + dto.getCustomerId()));

        // Create Bill entity
        Bill bill = Bill.builder()
            .customer(customer)
            .serviceName(dto.getServiceName())
            .serviceReferenceId(dto.getServiceReferenceId())
            .description(dto.getDescription())
            .amount(dto.getAmount())
            .status(0) // 0 = Unpaid
            .build();

        // Save bill
        Bill savedBill = billRepository.save(bill);

        System.out.println("✅ Bill Created Successfully:");
        System.out.println("   Bill ID: " + savedBill.getBillId());
        System.out.println("   Customer: " + customer.getName());
        System.out.println("   Service: " + savedBill.getServiceName());
        System.out.println("   Amount: Rp " + String.format("%,d", savedBill.getAmount()));

        return convertToResponseDTO(savedBill);
    }

    @Override
    public List<BillResponseDTO> getAllBills(UUID customerId, String serviceName, Integer status) {
        List<Bill> bills = billRepository.findAllNotDeleted();

        // Apply filters
        if (customerId != null) {
            bills = bills.stream()
                .filter(b -> b.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList());
        }

        if (serviceName != null && !serviceName.trim().isEmpty()) {
            bills = bills.stream()
                .filter(b -> b.getServiceName().equalsIgnoreCase(serviceName.trim()))
                .collect(Collectors.toList());
        }

        if (status != null) {
            bills = bills.stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
        }

        if (bills.isEmpty()) {
            throw new BillNotFoundException("No Bill Found");
        }

        return bills.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<BillResponseDTO> getCustomerBills(UUID customerId, Integer status, String sortBy, String sortDirection) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new BillNotFoundException("No Bill Found"));

        List<Bill> bills;

        // Fetch with filters and sorting
        if (status != null && sortBy != null) {
            if ("createdAt".equalsIgnoreCase(sortBy)) {
                bills = "asc".equalsIgnoreCase(sortDirection)
                    ? billRepository.findByCustomerAndStatusOrderByCreatedAtAsc(customer, status)
                    : billRepository.findByCustomerAndStatusOrderByCreatedAtDesc(customer, status);
            } else {
                bills = billRepository.findByCustomerAndStatusNotDeleted(customer, status);
            }
        } else if (status != null) {
            bills = billRepository.findByCustomerAndStatusNotDeleted(customer, status);
        } else if (sortBy != null) {
            if ("createdAt".equalsIgnoreCase(sortBy)) {
                bills = "asc".equalsIgnoreCase(sortDirection)
                    ? billRepository.findByCustomerOrderByCreatedAtAsc(customer)
                    : billRepository.findByCustomerOrderByCreatedAtDesc(customer);
            } else if ("serviceName".equalsIgnoreCase(sortBy)) {
                bills = "asc".equalsIgnoreCase(sortDirection)
                    ? billRepository.findByCustomerOrderByServiceNameAsc(customer)
                    : billRepository.findByCustomerOrderByServiceNameDesc(customer);
            } else {
                bills = billRepository.findByCustomerNotDeleted(customer);
            }
        } else {
            bills = billRepository.findByCustomerNotDeleted(customer);
        }

        if (bills.isEmpty()) {
            throw new BillNotFoundException("No Bill Found");
        }

        return bills.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<BillResponseDTO> getServiceBills(String serviceName, Integer status, UUID customerId) {
        List<Bill> bills;

        if (status != null && customerId != null) {
            bills = billRepository.findByServiceNameAndCustomerIdNotDeleted(serviceName, customerId)
                .stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
        } else if (status != null) {
            bills = billRepository.findByServiceNameAndStatusNotDeleted(serviceName, status);
        } else if (customerId != null) {
            bills = billRepository.findByServiceNameAndCustomerIdNotDeleted(serviceName, customerId);
        } else {
            bills = billRepository.findByServiceNameNotDeleted(serviceName);
        }

        return bills.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public BillDetailResponseDTO getBillDetail(UUID billId) {
        Bill bill = billRepository.findByIdNotDeleted(billId)
            .orElseThrow(() -> new BillNotFoundException("No Bill Found"));

        return convertToDetailResponseDTO(bill);
    }

    @Override
    public BillResponseDTO payBill(UUID billId, PayBillRequestDTO dto, UUID authenticatedCustomerId) {
        // Load bill
        Bill bill = billRepository.findByIdNotDeleted(billId)
            .orElseThrow(() -> new BillNotFoundException("No Bill Found"));

        // Validation: Bill must be Unpaid
        if (bill.getStatus() != 0) {
            throw new BillAlreadyPaidException("Bill has already been paid");
        }

        // Validation: Customer ID must match
        if (!bill.getCustomer().getId().equals(authenticatedCustomerId)) {
            throw new RuntimeException("Unauthorized: You can only pay your own bills");
        }

        // Check customer balance
        Customer customer = bill.getCustomer();
        BigDecimal customerBalance = customer.getSaldo();
        BigDecimal billAmount = BigDecimal.valueOf(bill.getAmount());

        if (customerBalance.compareTo(billAmount) < 0) {
            throw new InsufficientBalanceException("User balance insufficient, please Top Up balance.");
        }

        // Deduct balance
        customer.setSaldo(customerBalance.subtract(billAmount));
        customerRepository.save(customer);

        // Update bill status
        bill.setStatus(1); // 1 = Paid
        bill.setPaymentTimestamp(LocalDateTime.now());
        Bill paidBill = billRepository.save(bill);

        System.out.println("✅ Bill Paid Successfully:");
        System.out.println("   Bill ID: " + paidBill.getBillId());
        System.out.println("   Customer: " + customer.getName());
        System.out.println("   Amount Paid: Rp " + String.format("%,d", paidBill.getAmount()));
        System.out.println("   Remaining Balance: Rp " + String.format("%,.2f", customer.getSaldo()));

        // Update booking status if this is an Accommodation bill
        if ("Accommodation".equals(paidBill.getServiceName())) {
            try {
                UUID bookingId = UUID.fromString(paidBill.getServiceReferenceId());
                Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
                
                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    
                    // Only update if booking is in "Waiting for Payment" status
                    if (booking.getStatus() == 0) {
                        booking.setStatus(1); // Change to "Payment Confirmed"
                        bookingRepository.save(booking);
                        
                        System.out.println("✅ Booking Status Updated:");
                        System.out.println("   Booking ID: " + booking.getBookingID());
                        System.out.println("   New Status: Payment Confirmed (1)");
                    }
                } else {
                    System.err.println("⚠️ Warning: Booking not found for ID: " + paidBill.getServiceReferenceId());
                }
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ Warning: Invalid booking ID format: " + paidBill.getServiceReferenceId());
            } catch (Exception e) {
                System.err.println("⚠️ Warning: Failed to update booking status: " + e.getMessage());
            }
        }

        // TODO: Call service callback if needed
        // callServiceCallback(paidBill);

        return convertToResponseDTO(paidBill);
    }

    @Override
    public BillResponseDTO updateBill(UUID billId, UpdateBillRequestDTO dto) {
        // Validation: Check API Key
        if (!apiKey.equals(dto.getApiKey())) {
            throw new RuntimeException("Invalid API Key");
        }

        // Load bill
        Bill bill = billRepository.findByIdNotDeleted(billId)
            .orElseThrow(() -> new BillNotFoundException("No Bill Found"));

        // Validation: Cannot update Paid bill
        if (bill.getStatus() == 1) {
            throw new BillAlreadyPaidException("Cannot update a bill that has already been paid");
        }

        // Validation: All fields required
        if (dto.getCustomerId() == null || dto.getServiceName() == null || 
            dto.getServiceReferenceId() == null || dto.getDescription() == null || 
            dto.getAmount() == null) {
            throw new RuntimeException("All fields are required");
        }

        // Validation: Amount > 0
        if (dto.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        // Validation: Service name must be valid
        if (!VALID_SERVICE_NAMES.contains(dto.getServiceName())) {
            throw new RuntimeException("Service name must be one of: " + String.join(", ", VALID_SERVICE_NAMES));
        }

        // Load customer if changed
        if (!bill.getCustomer().getId().equals(UUID.fromString(dto.getCustomerId()))) {
            Customer newCustomer = customerRepository.findById(UUID.fromString(dto.getCustomerId()))
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + dto.getCustomerId()));
            bill.setCustomer(newCustomer);
        }

        // Update fields
        bill.setServiceName(dto.getServiceName());
        bill.setServiceReferenceId(dto.getServiceReferenceId());
        bill.setDescription(dto.getDescription());
        bill.setAmount(dto.getAmount());

        // Save
        Bill updatedBill = billRepository.save(bill);

        System.out.println("✅ Bill Updated Successfully:");
        System.out.println("   Bill ID: " + updatedBill.getBillId());

        return convertToResponseDTO(updatedBill);
    }

    // Helper methods
    private BillResponseDTO convertToResponseDTO(Bill bill) {
        return BillResponseDTO.builder()
            .billId(bill.getBillId())
            .customerId(bill.getCustomer().getId())
            .customerName(bill.getCustomer().getName())
            .serviceName(bill.getServiceName())
            .serviceReferenceId(bill.getServiceReferenceId())
            .description(bill.getDescription())
            .amount(bill.getAmount())
            .status(bill.getStatus())
            .statusText(bill.getStatus() == 0 ? "Unpaid" : "Paid")
            .createdAt(bill.getCreatedAt())
            .updatedAt(bill.getUpdatedAt())
            .paymentTimestamp(bill.getPaymentTimestamp())
            .build();
    }

    private BillDetailResponseDTO convertToDetailResponseDTO(Bill bill) {
        return BillDetailResponseDTO.builder()
            .billId(bill.getBillId())
            .customerId(bill.getCustomer().getId())
            .customerName(bill.getCustomer().getName())
            .customerEmail(bill.getCustomer().getEmail())
            .serviceName(bill.getServiceName())
            .serviceReferenceId(bill.getServiceReferenceId())
            .description(bill.getDescription())
            .amount(bill.getAmount())
            .status(bill.getStatus())
            .statusText(bill.getStatus() == 0 ? "Unpaid" : "Paid")
            .createdAt(bill.getCreatedAt())
            .updatedAt(bill.getUpdatedAt())
            .paymentTimestamp(bill.getPaymentTimestamp())
            .build();
    }
}
