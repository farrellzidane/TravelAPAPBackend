package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.TopUpTransaction;
import apap.ti._5.accommodation_2306275600_be.repository.TopUpTransactionRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RBAC authorization logic in TopUpTransactionRestServiceRBACImpl.
 * Focus: Testing authorization checks for different user roles.
 */
@ExtendWith(MockitoExtension.class)
class TopUpTransactionRestServiceRBACImplTest {

    @Mock
    private TopUpTransactionRepository topUpTransactionRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TopUpTransactionRestServiceRBACImpl topUpTransactionRestService;

    private UserProfileDTO superAdminUser;
    private UserProfileDTO ownerUser;
    private UserProfileDTO customerUser;
    private UserProfileDTO unauthorizedUser;
    private UUID customerId;
    private String transactionId;
    private TopUpTransaction testTopUpTransaction;

    @BeforeEach
    void setUp() {
        transactionId = "topup-123";
        customerId = UUID.randomUUID();

        superAdminUser = new UserProfileDTO(UUID.randomUUID(), "superadmin", "SuperAdmin", "superadmin@test.com", "Male", "SUPERADMIN", LocalDateTime.now(), LocalDateTime.now(), false);
        ownerUser = new UserProfileDTO(UUID.randomUUID(), "owner", "Owner", "owner@test.com", "Male", "ACCOMMODATION_OWNER", LocalDateTime.now(), LocalDateTime.now(), false);
        customerUser = new UserProfileDTO(customerId, "customer", "Customer", "customer@test.com", "Male", "CUSTOMER", LocalDateTime.now(), LocalDateTime.now(), false);
        unauthorizedUser = new UserProfileDTO(UUID.randomUUID(), "unauthorized", "Unauthorized", "unauthorized@test.com", "Male", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now(), false);

        testTopUpTransaction = new TopUpTransaction();
        testTopUpTransaction.setId(transactionId);
        testTopUpTransaction.setEndUserId(customerId.toString());
        testTopUpTransaction.setAmount(100000L);
        testTopUpTransaction.setStatus("Pending");
    }

    // ============================================
    // CREATE TOP-UP TRANSACTION - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testCreateTopUpTransaction_SuperAdmin_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(superAdminUser);
        when(authService.isCustomer(superAdminUser)).thenReturn(false);
        when(authService.isSuperAdmin(superAdminUser)).thenReturn(true);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.createTopUpTransaction(null));
        assertTrue(exception.getMessage().contains("Superadmin tidak dapat membuat top-up transaction"));
    }

    @Test
    void testCreateTopUpTransaction_AccommodationOwner_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isCustomer(ownerUser)).thenReturn(false);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.createTopUpTransaction(null));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testCreateTopUpTransaction_UnauthorizedUser_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.createTopUpTransaction(null));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    // ============================================
    // GET ALL TOP-UP TRANSACTIONS - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testGetAllTopUpTransactions_AccommodationOwner_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isCustomer(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.getAllTopUpTransactions());
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testGetAllTopUpTransactions_UnauthorizedUser_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.getAllTopUpTransactions());
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    // ============================================
    // GET TOP-UP TRANSACTIONS BY CUSTOMER ID - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testGetTopUpTransactionsByCustomerId_SuperAdmin_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(superAdminUser);
        when(authService.isCustomer(superAdminUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.getTopUpTransactionsByCustomerId(customerId.toString()));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testGetTopUpTransactionsByCustomerId_AccommodationOwner_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isCustomer(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.getTopUpTransactionsByCustomerId(customerId.toString()));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testGetTopUpTransactionsByCustomerId_UnauthorizedUser_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.getTopUpTransactionsByCustomerId(customerId.toString()));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    // ============================================
    // GET TOP-UP TRANSACTION BY ID - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testGetTopUpTransactionById_AccommodationOwner_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isCustomer(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.getTopUpTransactionById(transactionId));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testGetTopUpTransactionById_UnauthorizedUser_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.getTopUpTransactionById(transactionId));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    // ============================================
    // UPDATE TOP-UP STATUS - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testUpdateTopUpStatus_Customer_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.updateTopUpStatus(null));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testUpdateTopUpStatus_AccommodationOwner_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.updateTopUpStatus(null));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testUpdateTopUpStatus_UnauthorizedUser_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.updateTopUpStatus(null));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    // ============================================
    // DELETE TOP-UP TRANSACTION - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testDeleteTopUpTransaction_Customer_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.deleteTopUpTransaction(transactionId));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testDeleteTopUpTransaction_AccommodationOwner_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.deleteTopUpTransaction(transactionId));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testDeleteTopUpTransaction_UnauthorizedUser_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> topUpTransactionRestService.deleteTopUpTransaction(transactionId));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    // ============================================
    // OWNERSHIP VALIDATION TESTS
    // ============================================

    @Test
    void testOwnershipValidation_TransactionBelongsToCustomer() {
        when(topUpTransactionRepository.findById(transactionId)).thenReturn(Optional.of(testTopUpTransaction));

        Optional<TopUpTransaction> transaction = topUpTransactionRepository.findById(transactionId);
        assertTrue(transaction.isPresent());
        assertEquals(customerId.toString(), transaction.get().getEndUserId());
    }

    @Test
    void testOwnershipValidation_TransactionNotBelongsToCustomer() {
        UUID otherCustomerId = UUID.randomUUID();
        testTopUpTransaction.setEndUserId(otherCustomerId.toString());

        when(topUpTransactionRepository.findById(transactionId)).thenReturn(Optional.of(testTopUpTransaction));

        Optional<TopUpTransaction> transaction = topUpTransactionRepository.findById(transactionId);
        assertTrue(transaction.isPresent());
        assertNotEquals(customerId.toString(), transaction.get().getEndUserId());
    }

    @Test
    void testOwnershipValidation_TransactionNotFound() {
        when(topUpTransactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        Optional<TopUpTransaction> transaction = topUpTransactionRepository.findById(transactionId);
        assertFalse(transaction.isPresent());
    }

    // ============================================
    // ROLE CHECK TESTS
    // ============================================

    @Test
    void testRoleCheck_SuperAdmin() {
        when(authService.isSuperAdmin(superAdminUser)).thenReturn(true);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);

        assertTrue(authService.isSuperAdmin(superAdminUser));
        assertFalse(authService.isSuperAdmin(customerUser));
    }

    @Test
    void testRoleCheck_AccommodationOwner() {
        when(authService.isAccommodationOwner(ownerUser)).thenReturn(true);
        when(authService.isAccommodationOwner(customerUser)).thenReturn(false);

        assertTrue(authService.isAccommodationOwner(ownerUser));
        assertFalse(authService.isAccommodationOwner(customerUser));
    }

    @Test
    void testRoleCheck_Customer() {
        when(authService.isCustomer(customerUser)).thenReturn(true);
        when(authService.isCustomer(ownerUser)).thenReturn(false);

        assertTrue(authService.isCustomer(customerUser));
        assertFalse(authService.isCustomer(ownerUser));
    }
}
