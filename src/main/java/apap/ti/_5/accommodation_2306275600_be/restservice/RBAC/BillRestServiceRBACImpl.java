package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.repository.BillRepository;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.CustomerRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.PayBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.UpdateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.BillRestServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class BillRestServiceRBACImpl extends BillRestServiceImpl implements BillRestServiceRBAC {

    private final AuthService authService;

    public BillRestServiceRBACImpl(
            BillRepository billRepository,
            CustomerRepository customerRepository,
            BookingRepository bookingRepository,
            RestTemplate restTemplate,
            AuthService authService
    ) {
        super(billRepository, customerRepository, bookingRepository, restTemplate);
        this.authService = authService;
    }

    // [POST] Create Bill - API Key authentication (no role check)
    @Override
    public BillResponseDTO createBill(CreateBillRequestDTO dto) {
        // API Key validation is done in parent class
        return super.createBill(dto);
    }

    // [GET] Get All Bills - Superadmin only
    @Override
    public List<BillResponseDTO> getAllBills(UUID customerId, String serviceName, Integer status) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();

        if (!authService.isSuperAdmin(user)) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }

        return super.getAllBills(customerId, serviceName, status);
    }

    // [GET] Get Customer Bills - Customer only (their own bills)
    @Override
    public List<BillResponseDTO> getCustomerBills(UUID customerId, Integer status, String sortBy, String sortDirection) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();

        if (!authService.isCustomer(user)) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }

        // Customer can only view their own bills
        if (!user.userId().equals(customerId)) {
            throw new AccessDeniedException("You can only view your own bills");
        }

        return super.getCustomerBills(customerId, status, sortBy, sortDirection);
    }

    // [GET] Get Service Bills - Accommodation Owner only (for their service)
    @Override
    public List<BillResponseDTO> getServiceBills(String serviceName, Integer status, UUID customerId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();

        if (!authService.isAccommodationOwner(user)) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }

        // Service owners can only view bills for their service
        if (!"Accommodation".equalsIgnoreCase(serviceName)) {
            throw new AccessDeniedException("You can only view bills for your service");
        }

        return super.getServiceBills(serviceName, status, customerId);
    }

    // [GET] Get Bill Detail - Superadmin, Customer (their own), Accommodation Owner (their service)
    @Override
    public BillDetailResponseDTO getBillDetail(UUID billId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();

        // Fetch bill detail
        BillDetailResponseDTO bill = super.getBillDetail(billId);

        // Check authorization
        boolean hasAccess = false;

        if (authService.isSuperAdmin(user)) {
            hasAccess = true; // Superadmin can see all bills
        } else if (authService.isCustomer(user)) {
            // Customer can only see their own bills
            hasAccess = bill.getCustomerId().equals(user.userId());
        } else if (authService.isAccommodationOwner(user)) {
            // Accommodation Owner can only see bills for their service
            hasAccess = "Accommodation".equalsIgnoreCase(bill.getServiceName());
        }

        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini");
        }

        return bill;
    }

    // [POST] Pay Bill - Customer only (their own bill)
    @Override
    public BillResponseDTO payBill(UUID billId, PayBillRequestDTO dto, UUID authenticatedCustomerId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();

        if (!authService.isCustomer(user)) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }

        // Pass the authenticated customer ID to the parent method for validation
        return super.payBill(billId, dto, user.userId());
    }

    // [PUT] Update Bill - API Key authentication (no role check)
    @Override
    public BillResponseDTO updateBill(UUID billId, UpdateBillRequestDTO dto) {
        // API Key validation is done in parent class
        return super.updateBill(billId, dto);
    }
}
