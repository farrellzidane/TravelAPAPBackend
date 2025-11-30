package apap.ti._5.accommodation_2306275600_be.external;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;

/**
 * Test class to verify AuthServiceMock interface contract.
 * Uses a mock implementation to test interface methods.
 */
class AuthServiceMockTest {

    /**
     * Mock implementation of AuthServiceMock for testing interface contract
     */
    private static class TestAuthServiceMockImpl implements AuthServiceMock {
        
        private boolean shouldThrowException = false;

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        @Override
        public UserProfileDTO getSuperAdminUser() throws AccessDeniedException {
            if (shouldThrowException) {
                throw new AccessDeniedException("Access denied for superadmin");
            }
            return new UserProfileDTO(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "superadmin",
                "Super Admin",
                "superadmin@example.com",
                "M",
                "Superadmin",
                LocalDateTime.now().minusDays(100),
                LocalDateTime.now(),
                false
            );
        }

        @Override
        public UserProfileDTO getAccommodationOwnerUser() throws AccessDeniedException {
            if (shouldThrowException) {
                throw new AccessDeniedException("Access denied for accommodation owner");
            }
            return new UserProfileDTO(
                UUID.fromString("1a2b3c4d-5e6f-7080-90a0-b1c2d3e4f501"),
                "owner",
                "Accommodation Owner",
                "owner@example.com",
                "F",
                "Accommodation Owner",
                LocalDateTime.now().minusDays(50),
                LocalDateTime.now(),
                false
            );
        }

        @Override
        public CustomerProfileDTO getCustomerUser() throws AccessDeniedException {
            if (shouldThrowException) {
                throw new AccessDeniedException("Access denied for customer");
            }
            return new CustomerProfileDTO(
                UUID.fromString("c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1"),
                "customer",
                "Customer User",
                "customer@example.com",
                "M",
                "Customer",
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                false,
                100000L
            );
        }
    }

    @Test
    void testGetSuperAdminUser_Success() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO result = authServiceMock.getSuperAdminUser();

        // Assert
        assertNotNull(result);
        assertEquals("Superadmin", result.role());
        assertEquals("superadmin", result.username());
        assertEquals("Super Admin", result.name());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), result.userId());
    }

    @Test
    void testGetAccommodationOwnerUser_Success() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO result = authServiceMock.getAccommodationOwnerUser();

        // Assert
        assertNotNull(result);
        assertEquals("Accommodation Owner", result.role());
        assertEquals("owner", result.username());
        assertEquals("Accommodation Owner", result.name());
        assertEquals(UUID.fromString("1a2b3c4d-5e6f-7080-90a0-b1c2d3e4f501"), result.userId());
        assertEquals("F", result.gender());
    }

    @Test
    void testGetCustomerUser_Success() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        CustomerProfileDTO result = authServiceMock.getCustomerUser();

        // Assert
        assertNotNull(result);
        assertEquals("Customer", result.role());
        assertEquals("customer", result.username());
        assertEquals("Customer User", result.name());
        assertEquals(UUID.fromString("c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1"), result.userId());
        assertEquals(100000L, result.saldo());
        assertFalse(result.isDeleted());
    }

    @Test
    void testGetSuperAdminUser_ThrowsAccessDeniedException() {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();
        authServiceMock.setShouldThrowException(true);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authServiceMock.getSuperAdminUser()
        );
        assertTrue(exception.getMessage().contains("Access denied for superadmin"));
    }

    @Test
    void testGetAccommodationOwnerUser_ThrowsAccessDeniedException() {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();
        authServiceMock.setShouldThrowException(true);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authServiceMock.getAccommodationOwnerUser()
        );
        assertTrue(exception.getMessage().contains("Access denied for accommodation owner"));
    }

    @Test
    void testGetCustomerUser_ThrowsAccessDeniedException() {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();
        authServiceMock.setShouldThrowException(true);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authServiceMock.getCustomerUser()
        );
        assertTrue(exception.getMessage().contains("Access denied for customer"));
    }

    @Test
    void testGetSuperAdminUser_VerifyFields() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO result = authServiceMock.getSuperAdminUser();

        // Assert
        assertEquals("superadmin@example.com", result.email());
        assertEquals("M", result.gender());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        assertFalse(result.isDeleted());
    }

    @Test
    void testGetAccommodationOwnerUser_VerifyFields() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO result = authServiceMock.getAccommodationOwnerUser();

        // Assert
        assertEquals("owner@example.com", result.email());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        assertFalse(result.isDeleted());
    }

    @Test
    void testGetCustomerUser_VerifyFields() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        CustomerProfileDTO result = authServiceMock.getCustomerUser();

        // Assert
        assertEquals("customer@example.com", result.email());
        assertEquals("M", result.gender());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
    }

    @Test
    void testGetSuperAdminUser_ConsistentResults() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO result1 = authServiceMock.getSuperAdminUser();
        UserProfileDTO result2 = authServiceMock.getSuperAdminUser();

        // Assert
        assertEquals(result1.userId(), result2.userId());
        assertEquals(result1.username(), result2.username());
        assertEquals(result1.role(), result2.role());
    }

    @Test
    void testGetAccommodationOwnerUser_ConsistentResults() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO result1 = authServiceMock.getAccommodationOwnerUser();
        UserProfileDTO result2 = authServiceMock.getAccommodationOwnerUser();

        // Assert
        assertEquals(result1.userId(), result2.userId());
        assertEquals(result1.username(), result2.username());
        assertEquals(result1.role(), result2.role());
    }

    @Test
    void testGetCustomerUser_ConsistentResults() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        CustomerProfileDTO result1 = authServiceMock.getCustomerUser();
        CustomerProfileDTO result2 = authServiceMock.getCustomerUser();

        // Assert
        assertEquals(result1.userId(), result2.userId());
        assertEquals(result1.username(), result2.username());
        assertEquals(result1.role(), result2.role());
        assertEquals(result1.saldo(), result2.saldo());
    }

    @Test
    void testAllUserTypes_DifferentUserIds() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO superAdmin = authServiceMock.getSuperAdminUser();
        UserProfileDTO owner = authServiceMock.getAccommodationOwnerUser();
        CustomerProfileDTO customer = authServiceMock.getCustomerUser();

        // Assert
        assertNotEquals(superAdmin.userId(), owner.userId());
        assertNotEquals(superAdmin.userId(), customer.userId());
        assertNotEquals(owner.userId(), customer.userId());
    }

    @Test
    void testAllUserTypes_DifferentRoles() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        UserProfileDTO superAdmin = authServiceMock.getSuperAdminUser();
        UserProfileDTO owner = authServiceMock.getAccommodationOwnerUser();
        CustomerProfileDTO customer = authServiceMock.getCustomerUser();

        // Assert
        assertEquals("Superadmin", superAdmin.role());
        assertEquals("Accommodation Owner", owner.role());
        assertEquals("Customer", customer.role());
    }

    @Test
    void testGetCustomerUser_HasSaldo() throws AccessDeniedException {
        // Arrange
        TestAuthServiceMockImpl authServiceMock = new TestAuthServiceMockImpl();

        // Act
        CustomerProfileDTO result = authServiceMock.getCustomerUser();

        // Assert
        assertTrue(result.saldo() >= 0);
        assertEquals(100000L, result.saldo());
    }
}
