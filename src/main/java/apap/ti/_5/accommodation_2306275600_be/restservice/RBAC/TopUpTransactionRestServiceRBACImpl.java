package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.repository.PaymentMethodRepository;
import apap.ti._5.accommodation_2306275600_be.repository.TopUpTransactionRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.CreateTopUpRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.UpdateTopUpStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.topup.TopUpTransactionResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.TopUpTransactionRestServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TopUpTransactionRestServiceRBACImpl extends TopUpTransactionRestServiceImpl implements TopUpTransactionRestServiceRBAC {

    private final AuthService authService;

    public TopUpTransactionRestServiceRBACImpl(
            TopUpTransactionRepository topUpTransactionRepository,
            PaymentMethodRepository paymentMethodRepository,
            AuthService authService
        ) {
        super(topUpTransactionRepository, paymentMethodRepository);
        this.authService = authService;
    }

    // [POST] Create Top-Up Transaction - PBI-BE-TU3 - Customer only
    @Override
    public TopUpTransactionResponseDTO createTopUpTransaction(CreateTopUpRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            if (authService.isSuperAdmin(user)) {
                throw new AccessDeniedException("Superadmin tidak dapat membuat top-up transaction");
            }
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.createTopUpTransaction(dto);
    }

    // [GET] Get All Top-Up Transactions - PBI-BE-TU1 - Superadmin, Customer
    @Override
    public List<TopUpTransactionResponseDTO> getAllTopUpTransactions() throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAllTopUpTransactions();
    }

    // [GET] Get Top-Up Transactions by Customer ID - Customer (own transactions only)
    @Override
    public List<TopUpTransactionResponseDTO> getTopUpTransactionsByCustomerId(String customerId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getTopUpTransactionsByCustomerId(customerId);
    }

    // [GET] Get Top-Up Transaction by ID - PBI-BE-TU2
    // - Superadmin: Dapat melihat semua top-up transaction
    // - Customer: Hanya dapat melihat top-up transaction miliknya sendiri
    @Override
    public TopUpTransactionResponseDTO getTopUpTransactionById(String id) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        TopUpTransactionResponseDTO topUp = super.getTopUpTransactionById(id);
        
        // Jika customer, validasi bahwa top-up transaction adalah miliknya
        if (authService.isCustomer(user)) {
            if (!topUp.getEndUserId().equals(user.userId().toString())) {
                throw new AccessDeniedException("Customer hanya dapat melihat top-up transaction miliknya sendiri");
            }
        }
        
        return topUp;
    }

    // [PUT] Update Top-Up Status - PBI-BE-TU4 - Superadmin
    @Override
    public TopUpTransactionResponseDTO updateTopUpStatus(UpdateTopUpStatusRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updateTopUpStatus(dto);
    }

    // [DELETE] Delete Top-Up Transaction - PBI-BE-TU5 - Superadmin
    @Override
    public void deleteTopUpTransaction(String id) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        super.deleteTopUpTransaction(id);
    }
}
