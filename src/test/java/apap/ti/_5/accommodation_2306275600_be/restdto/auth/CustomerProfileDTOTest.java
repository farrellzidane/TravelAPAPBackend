package apap.ti._5.accommodation_2306275600_be.restdto.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerProfileDTOTest {

    private UUID testUserId;
    private String testUsername;
    private String testName;
    private String testEmail;
    private String testGender;
    private String testRole;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;
    private boolean testIsDeleted;
    private long testSaldo;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUsername = "customer123";
        testName = "John Doe";
        testEmail = "john.doe@example.com";
        testGender = "Male";
        testRole = "Customer";
        testCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        testUpdatedAt = LocalDateTime.of(2024, 6, 1, 14, 30);
        testIsDeleted = false;
        testSaldo = 1000000L;
    }

    @Test
    void testCustomerProfileDTOCreation() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNotNull(dto);
        assertEquals(testUserId, dto.userId());
        assertEquals(testUsername, dto.username());
        assertEquals(testName, dto.name());
        assertEquals(testEmail, dto.email());
        assertEquals(testGender, dto.gender());
        assertEquals(testRole, dto.role());
        assertEquals(testCreatedAt, dto.createdAt());
        assertEquals(testUpdatedAt, dto.updatedAt());
        assertEquals(testIsDeleted, dto.isDeleted());
        assertEquals(testSaldo, dto.saldo());
    }

    @Test
    void testCustomerProfileDTOWithNullUserId() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            null,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.userId());
        assertEquals(testUsername, dto.username());
    }

    @Test
    void testCustomerProfileDTOWithNullUsername() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            null,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.username());
        assertEquals(testUserId, dto.userId());
    }

    @Test
    void testCustomerProfileDTOWithNullName() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            null,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.name());
    }

    @Test
    void testCustomerProfileDTOWithNullEmail() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            null,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.email());
    }

    @Test
    void testCustomerProfileDTOWithNullGender() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            null,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.gender());
    }

    @Test
    void testCustomerProfileDTOWithNullRole() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            null,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.role());
    }

    @Test
    void testCustomerProfileDTOWithNullCreatedAt() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            null,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.createdAt());
    }

    @Test
    void testCustomerProfileDTOWithNullUpdatedAt() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            null,
            testIsDeleted,
            testSaldo
        );

        assertNull(dto.updatedAt());
    }

    @Test
    void testCustomerProfileDTOWithDeletedTrue() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            true,
            testSaldo
        );

        assertTrue(dto.isDeleted());
    }

    @Test
    void testCustomerProfileDTOWithDeletedFalse() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            false,
            testSaldo
        );

        assertFalse(dto.isDeleted());
    }

    @Test
    void testCustomerProfileDTOWithZeroSaldo() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            0L
        );

        assertEquals(0L, dto.saldo());
    }

    @Test
    void testCustomerProfileDTOWithNegativeSaldo() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            -5000L
        );

        assertEquals(-5000L, dto.saldo());
    }

    @Test
    void testCustomerProfileDTOWithLargeSaldo() {
        long largeSaldo = 999999999999L;
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            largeSaldo
        );

        assertEquals(largeSaldo, dto.saldo());
    }

    @Test
    void testCustomerProfileDTOEquality() {
        CustomerProfileDTO dto1 = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        CustomerProfileDTO dto2 = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testCustomerProfileDTOInequality() {
        CustomerProfileDTO dto1 = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        CustomerProfileDTO dto2 = new CustomerProfileDTO(
            UUID.randomUUID(), // Different userId
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testCustomerProfileDTOToString() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        String toString = dto.toString();
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("CustomerProfileDTO"));
    }

    @Test
    void testCustomerProfileDTOWithEmptyStrings() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            "",
            "",
            "",
            "",
            "",
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertEquals("", dto.username());
        assertEquals("", dto.name());
        assertEquals("", dto.email());
        assertEquals("", dto.gender());
        assertEquals("", dto.role());
    }

    @Test
    void testCustomerProfileDTOWithFemaleGender() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            "Jane Doe",
            testEmail,
            "Female",
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertEquals("Female", dto.gender());
        assertEquals("Jane Doe", dto.name());
    }

    @Test
    void testCustomerProfileDTOWithDifferentRoles() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            "Accommodation Owner",
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertEquals("Accommodation Owner", dto.role());
    }

    @Test
    void testCustomerProfileDTOWithSameCreatedAndUpdatedAt() {
        LocalDateTime sameTime = LocalDateTime.now();
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            sameTime,
            sameTime,
            testIsDeleted,
            testSaldo
        );

        assertEquals(dto.createdAt(), dto.updatedAt());
    }

    @Test
    void testCustomerProfileDTOWithAllNullValues() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            0L
        );

        assertNull(dto.userId());
        assertNull(dto.username());
        assertNull(dto.name());
        assertNull(dto.email());
        assertNull(dto.gender());
        assertNull(dto.role());
        assertNull(dto.createdAt());
        assertNull(dto.updatedAt());
        assertFalse(dto.isDeleted());
        assertEquals(0L, dto.saldo());
    }

    @Test
    void testCustomerProfileDTOWithSpecialCharactersInStrings() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            "user@#$%",
            "Name with 中文",
            "test+email@domain.co.id",
            "Non-binary",
            "Premium Customer",
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertEquals("user@#$%", dto.username());
        assertEquals("Name with 中文", dto.name());
        assertEquals("test+email@domain.co.id", dto.email());
        assertEquals("Non-binary", dto.gender());
        assertEquals("Premium Customer", dto.role());
    }

    @Test
    void testCustomerProfileDTOEqualityWithSelf() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertEquals(dto, dto);
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void testCustomerProfileDTONotEqualsNull() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            testSaldo
        );

        assertNotEquals(null, dto);
    }

    @Test
    void testCustomerProfileDTOWithMaxLongSaldo() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            Long.MAX_VALUE
        );

        assertEquals(Long.MAX_VALUE, dto.saldo());
    }

    @Test
    void testCustomerProfileDTOWithMinLongSaldo() {
        CustomerProfileDTO dto = new CustomerProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted,
            Long.MIN_VALUE
        );

        assertEquals(Long.MIN_VALUE, dto.saldo());
    }
}
