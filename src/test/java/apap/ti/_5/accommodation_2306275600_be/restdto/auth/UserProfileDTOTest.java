package apap.ti._5.accommodation_2306275600_be.restdto.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileDTOTest {

    private UUID testUserId;
    private String testUsername;
    private String testName;
    private String testEmail;
    private String testGender;
    private String testRole;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;
    private boolean testIsDeleted;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUsername = "testuser123";
        testName = "Test User";
        testEmail = "test.user@example.com";
        testGender = "Male";
        testRole = "Accommodation Owner";
        testCreatedAt = LocalDateTime.of(2024, 1, 15, 9, 30);
        testUpdatedAt = LocalDateTime.of(2024, 7, 20, 16, 45);
        testIsDeleted = false;
    }

    @Test
    void testUserProfileDTOCreation() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
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
    }

    @Test
    void testUserProfileDTOWithNullUserId() {
        UserProfileDTO dto = new UserProfileDTO(
            null,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNull(dto.userId());
        assertEquals(testUsername, dto.username());
    }

    @Test
    void testUserProfileDTOWithNullUsername() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            null,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNull(dto.username());
        assertEquals(testUserId, dto.userId());
    }

    @Test
    void testUserProfileDTOWithNullName() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            null,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNull(dto.name());
    }

    @Test
    void testUserProfileDTOWithNullEmail() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            null,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNull(dto.email());
    }

    @Test
    void testUserProfileDTOWithNullGender() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            null,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNull(dto.gender());
    }

    @Test
    void testUserProfileDTOWithNullRole() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            null,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNull(dto.role());
    }

    @Test
    void testUserProfileDTOWithNullCreatedAt() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            null,
            testUpdatedAt,
            testIsDeleted
        );

        assertNull(dto.createdAt());
    }

    @Test
    void testUserProfileDTOWithNullUpdatedAt() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            null,
            testIsDeleted
        );

        assertNull(dto.updatedAt());
    }

    @Test
    void testUserProfileDTOWithDeletedTrue() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            true
        );

        assertTrue(dto.isDeleted());
    }

    @Test
    void testUserProfileDTOWithDeletedFalse() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            false
        );

        assertFalse(dto.isDeleted());
    }

    @Test
    void testUserProfileDTOEquality() {
        UserProfileDTO dto1 = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        UserProfileDTO dto2 = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testUserProfileDTOInequality() {
        UserProfileDTO dto1 = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        UserProfileDTO dto2 = new UserProfileDTO(
            UUID.randomUUID(), // Different userId
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserProfileDTOToString() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        String toString = dto.toString();
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("UserProfileDTO"));
    }

    @Test
    void testUserProfileDTOWithEmptyStrings() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            "",
            "",
            "",
            "",
            "",
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals("", dto.username());
        assertEquals("", dto.name());
        assertEquals("", dto.email());
        assertEquals("", dto.gender());
        assertEquals("", dto.role());
    }

    @Test
    void testUserProfileDTOWithFemaleGender() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            "janesmith",
            "Jane Smith",
            "jane.smith@example.com",
            "Female",
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals("Female", dto.gender());
        assertEquals("Jane Smith", dto.name());
    }

    @Test
    void testUserProfileDTOWithCustomerRole() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            "Customer",
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals("Customer", dto.role());
    }

    @Test
    void testUserProfileDTOWithSuperAdminRole() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            "admin",
            "Admin User",
            "admin@example.com",
            testGender,
            "Super Admin",
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals("Super Admin", dto.role());
        assertEquals("admin", dto.username());
    }

    @Test
    void testUserProfileDTOWithSameCreatedAndUpdatedAt() {
        LocalDateTime sameTime = LocalDateTime.now();
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            sameTime,
            sameTime,
            testIsDeleted
        );

        assertEquals(dto.createdAt(), dto.updatedAt());
    }

    @Test
    void testUserProfileDTOWithAllNullValues() {
        UserProfileDTO dto = new UserProfileDTO(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false
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
    }

    @Test
    void testUserProfileDTOWithSpecialCharactersInStrings() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            "user_123@special",
            "Name with Ñ and é",
            "test+filter@subdomain.example.com",
            "Other",
            "Accommodation Owner",
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals("user_123@special", dto.username());
        assertEquals("Name with Ñ and é", dto.name());
        assertEquals("test+filter@subdomain.example.com", dto.email());
        assertEquals("Other", dto.gender());
    }

    @Test
    void testUserProfileDTOEqualityWithSelf() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals(dto, dto);
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void testUserProfileDTONotEqualsNull() {
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNotEquals(null, dto);
    }

    @Test
    void testUserProfileDTOInequalityDifferentUsername() {
        UserProfileDTO dto1 = new UserProfileDTO(
            testUserId,
            "username1",
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        UserProfileDTO dto2 = new UserProfileDTO(
            testUserId,
            "username2",
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserProfileDTOInequalityDifferentIsDeleted() {
        UserProfileDTO dto1 = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            true
        );

        UserProfileDTO dto2 = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            false
        );

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserProfileDTOWithLongEmail() {
        String longEmail = "very.long.email.address.with.multiple.parts@subdomain.example.domain.com";
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            longEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals(longEmail, dto.email());
    }

    @Test
    void testUserProfileDTOWithLongName() {
        String longName = "Very Long Name With Multiple Parts And Spaces";
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            longName,
            testEmail,
            testGender,
            testRole,
            testCreatedAt,
            testUpdatedAt,
            testIsDeleted
        );

        assertEquals(longName, dto.name());
    }

    @Test
    void testUserProfileDTOWithUpdatedAtBeforeCreatedAt() {
        LocalDateTime earlier = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime later = LocalDateTime.of(2024, 12, 31, 23, 59);
        
        UserProfileDTO dto = new UserProfileDTO(
            testUserId,
            testUsername,
            testName,
            testEmail,
            testGender,
            testRole,
            later,
            earlier,
            testIsDeleted
        );

        assertTrue(dto.createdAt().isAfter(dto.updatedAt()));
    }

    @Test
    void testUserProfileDTOWithMinimalValidData() {
        UUID userId = UUID.randomUUID();
        UserProfileDTO dto = new UserProfileDTO(
            userId,
            "u",
            "N",
            "e@d.c",
            "M",
            "R",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        assertEquals(userId, dto.userId());
        assertEquals("u", dto.username());
        assertEquals("N", dto.name());
        assertEquals("e@d.c", dto.email());
        assertEquals("M", dto.gender());
        assertEquals("R", dto.role());
    }
}
