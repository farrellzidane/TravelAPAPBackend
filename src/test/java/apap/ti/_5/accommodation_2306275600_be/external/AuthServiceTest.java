package apap.ti._5.accommodation_2306275600_be.external;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;

/**
 * Test class to verify AuthService interface contract.
 * Uses a mock implementation to test interface methods.
 */
class AuthServiceTest {

    /**
     * Mock implementation of AuthService for testing interface contract
     */
    private static class TestAuthServiceImpl implements AuthService {
        
        private UserProfileDTO authenticatedUser;
        private boolean shouldThrowException = false;

        public void setAuthenticatedUser(UserProfileDTO user) {
            this.authenticatedUser = user;
        }

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        @Override
        public UserProfileDTO getAuthenticatedUser() throws AccessDeniedException {
            if (shouldThrowException) {
                throw new AccessDeniedException("Access denied");
            }
            if (authenticatedUser == null) {
                throw new AccessDeniedException("No authenticated user");
            }
            return authenticatedUser;
        }

        @Override
        public boolean isSuperAdmin(UserProfileDTO userProfile) {
            return "Superadmin".equals(userProfile.role());
        }

        @Override
        public boolean isAccommodationOwner(UserProfileDTO userProfile) {
            return "Accommodation Owner".equals(userProfile.role());
        }

        @Override
        public boolean isCustomer(UserProfileDTO userProfile) {
            return "Customer".equals(userProfile.role());
        }

        @Override
        public boolean isCustomer(CustomerProfileDTO customerProfile) {
            return "Customer".equals(customerProfile.role());
        }

        @Override
        public boolean isSuperAdmin(UUID userId) {
            try {
                UserProfileDTO user = getAuthenticatedUser();
                return user.userId().equals(userId) && isSuperAdmin(user);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean isAccommodationOwner(UUID userId) {
            try {
                UserProfileDTO user = getAuthenticatedUser();
                return user.userId().equals(userId) && isAccommodationOwner(user);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean isCustomer(UUID userId) {
            try {
                UserProfileDTO user = getAuthenticatedUser();
                return user.userId().equals(userId) && isCustomer(user);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public CustomerProfileDTO getCustomerProfile(UUID userId) throws NoSuchElementException {
            if (userId == null) {
                throw new NoSuchElementException("User ID cannot be null");
            }
            return new CustomerProfileDTO(
                userId,
                "customer_" + userId.toString().substring(0, 8),
                "Customer Name",
                "customer@example.com",
                "M",
                "Customer",
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                false,
                100000L
            );
        }

        @Override
        public List<UserProfileDTO> getAllAccommodationOwner() {
            return Arrays.asList(
                new UserProfileDTO(
                    UUID.randomUUID(),
                    "owner1",
                    "Owner 1",
                    "owner1@example.com",
                    "M",
                    "Accommodation Owner",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    false
                )
            );
        }
    }

    @Test
    void testGetAuthenticatedUser_Success() throws AccessDeniedException {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UUID userId = UUID.randomUUID();
        UserProfileDTO expectedUser = new UserProfileDTO(
            userId,
            "testuser",
            "Test User",
            "test@example.com",
            "M",
            "Customer",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
        authService.setAuthenticatedUser(expectedUser);

        // Act
        UserProfileDTO result = authService.getAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals("testuser", result.username());
    }

    @Test
    void testGetAuthenticatedUser_ThrowsAccessDeniedException() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        authService.setShouldThrowException(true);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.getAuthenticatedUser());
    }

    @Test
    void testIsSuperAdmin_WithUserProfile() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UserProfileDTO superAdmin = new UserProfileDTO(
            UUID.randomUUID(),
            "admin",
            "Admin",
            "admin@example.com",
            "M",
            "Superadmin",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertTrue(authService.isSuperAdmin(superAdmin));
    }

    @Test
    void testIsSuperAdmin_WithUserProfile_NotSuperAdmin() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UserProfileDTO customer = new UserProfileDTO(
            UUID.randomUUID(),
            "customer",
            "Customer",
            "customer@example.com",
            "F",
            "Customer",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertFalse(authService.isSuperAdmin(customer));
    }

    @Test
    void testIsAccommodationOwner_WithUserProfile() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UserProfileDTO owner = new UserProfileDTO(
            UUID.randomUUID(),
            "owner",
            "Owner",
            "owner@example.com",
            "M",
            "Accommodation Owner",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertTrue(authService.isAccommodationOwner(owner));
    }

    @Test
    void testIsCustomer_WithUserProfile() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UserProfileDTO customer = new UserProfileDTO(
            UUID.randomUUID(),
            "customer",
            "Customer",
            "customer@example.com",
            "F",
            "Customer",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertTrue(authService.isCustomer(customer));
    }

    @Test
    void testIsCustomer_WithCustomerProfile() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        CustomerProfileDTO customer = new CustomerProfileDTO(
            UUID.randomUUID(),
            "customer",
            "Customer",
            "customer@example.com",
            "M",
            "Customer",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            50000L
        );

        // Act & Assert
        assertTrue(authService.isCustomer(customer));
    }

    @Test
    void testIsSuperAdmin_WithUserId() throws AccessDeniedException {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UUID userId = UUID.randomUUID();
        UserProfileDTO superAdmin = new UserProfileDTO(
            userId,
            "admin",
            "Admin",
            "admin@example.com",
            "M",
            "Superadmin",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
        authService.setAuthenticatedUser(superAdmin);

        // Act & Assert
        assertTrue(authService.isSuperAdmin(userId));
    }

    @Test
    void testIsSuperAdmin_WithUserId_WrongId() throws AccessDeniedException {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UUID userId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        UserProfileDTO superAdmin = new UserProfileDTO(
            userId,
            "admin",
            "Admin",
            "admin@example.com",
            "M",
            "Superadmin",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
        authService.setAuthenticatedUser(superAdmin);

        // Act & Assert
        assertFalse(authService.isSuperAdmin(differentUserId));
    }

    @Test
    void testIsSuperAdmin_WithUserId_Exception() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        authService.setShouldThrowException(true);
        UUID userId = UUID.randomUUID();

        // Act & Assert
        assertFalse(authService.isSuperAdmin(userId));
    }

    @Test
    void testIsAccommodationOwner_WithUserId() throws AccessDeniedException {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UUID userId = UUID.randomUUID();
        UserProfileDTO owner = new UserProfileDTO(
            userId,
            "owner",
            "Owner",
            "owner@example.com",
            "F",
            "Accommodation Owner",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
        authService.setAuthenticatedUser(owner);

        // Act & Assert
        assertTrue(authService.isAccommodationOwner(userId));
    }

    @Test
    void testIsAccommodationOwner_WithUserId_Exception() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        authService.setShouldThrowException(true);
        UUID userId = UUID.randomUUID();

        // Act & Assert
        assertFalse(authService.isAccommodationOwner(userId));
    }

    @Test
    void testIsCustomer_WithUserId() throws AccessDeniedException {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UUID userId = UUID.randomUUID();
        UserProfileDTO customer = new UserProfileDTO(
            userId,
            "customer",
            "Customer",
            "customer@example.com",
            "M",
            "Customer",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
        authService.setAuthenticatedUser(customer);

        // Act & Assert
        assertTrue(authService.isCustomer(userId));
    }

    @Test
    void testIsCustomer_WithUserId_Exception() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        authService.setShouldThrowException(true);
        UUID userId = UUID.randomUUID();

        // Act & Assert
        assertFalse(authService.isCustomer(userId));
    }

    @Test
    void testGetCustomerProfile_Success() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UUID customerId = UUID.randomUUID();

        // Act
        CustomerProfileDTO result = authService.getCustomerProfile(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(customerId, result.userId());
        assertEquals("Customer", result.role());
        assertEquals(100000L, result.saldo());
    }

    @Test
    void testGetCustomerProfile_NullUserId() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> authService.getCustomerProfile(null));
    }

    @Test
    void testGetAllAccommodationOwner() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();

        // Act
        List<UserProfileDTO> result = authService.getAllAccommodationOwner();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Accommodation Owner", result.get(0).role());
    }

    @Test
    void testGetAuthenticatedUser_NoAuthenticatedUser() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        // Not setting any authenticated user

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.getAuthenticatedUser());
    }

    @Test
    void testIsCustomer_WithCustomerProfile_NotCustomer() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        CustomerProfileDTO notCustomer = new CustomerProfileDTO(
            UUID.randomUUID(),
            "owner",
            "Owner",
            "owner@example.com",
            "M",
            "Accommodation Owner",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            0L
        );

        // Act & Assert
        assertFalse(authService.isCustomer(notCustomer));
    }

    @Test
    void testIsAccommodationOwner_WithUserProfile_NotOwner() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UserProfileDTO customer = new UserProfileDTO(
            UUID.randomUUID(),
            "customer",
            "Customer",
            "customer@example.com",
            "F",
            "Customer",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertFalse(authService.isAccommodationOwner(customer));
    }

    @Test
    void testIsCustomer_WithUserProfile_NotCustomer() {
        // Arrange
        TestAuthServiceImpl authService = new TestAuthServiceImpl();
        UserProfileDTO admin = new UserProfileDTO(
            UUID.randomUUID(),
            "admin",
            "Admin",
            "admin@example.com",
            "M",
            "Superadmin",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertFalse(authService.isCustomer(admin));
    }
}
