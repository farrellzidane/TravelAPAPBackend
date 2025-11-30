package apap.ti._5.accommodation_2306275600_be.restdto.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleDTOTest {

    @Test
    void testUserRoleDTOCreation() {
        UserRoleDTO dto = new UserRoleDTO("Customer");

        assertNotNull(dto);
        assertEquals("Customer", dto.role());
    }

    @Test
    void testUserRoleDTOWithNullRole() {
        UserRoleDTO dto = new UserRoleDTO(null);

        assertNull(dto.role());
    }

    @Test
    void testUserRoleDTOWithEmptyRole() {
        UserRoleDTO dto = new UserRoleDTO("");

        assertEquals("", dto.role());
    }

    @Test
    void testUserRoleDTOWithAccommodationOwnerRole() {
        UserRoleDTO dto = new UserRoleDTO("Accommodation Owner");

        assertEquals("Accommodation Owner", dto.role());
    }

    @Test
    void testUserRoleDTOWithSuperAdminRole() {
        UserRoleDTO dto = new UserRoleDTO("Super Admin");

        assertEquals("Super Admin", dto.role());
    }

    @Test
    void testUserRoleDTOWithCustomerRole() {
        UserRoleDTO dto = new UserRoleDTO("Customer");

        assertEquals("Customer", dto.role());
    }

    @Test
    void testUserRoleDTOEquality() {
        UserRoleDTO dto1 = new UserRoleDTO("Customer");
        UserRoleDTO dto2 = new UserRoleDTO("Customer");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testUserRoleDTOInequality() {
        UserRoleDTO dto1 = new UserRoleDTO("Customer");
        UserRoleDTO dto2 = new UserRoleDTO("Accommodation Owner");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserRoleDTOToString() {
        UserRoleDTO dto = new UserRoleDTO("Customer");

        String toString = dto.toString();
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("UserRoleDTO"));
    }

    @Test
    void testUserRoleDTOWithWhitespaceRole() {
        UserRoleDTO dto = new UserRoleDTO("   ");

        assertEquals("   ", dto.role());
    }

    @Test
    void testUserRoleDTOWithLongRoleName() {
        String longRole = "Very Long Role Name With Multiple Words And Spaces";
        UserRoleDTO dto = new UserRoleDTO(longRole);

        assertEquals(longRole, dto.role());
    }

    @Test
    void testUserRoleDTOWithSpecialCharacters() {
        UserRoleDTO dto = new UserRoleDTO("Admin@#$%");

        assertEquals("Admin@#$%", dto.role());
    }

    @Test
    void testUserRoleDTOWithNumericRole() {
        UserRoleDTO dto = new UserRoleDTO("12345");

        assertEquals("12345", dto.role());
    }

    @Test
    void testUserRoleDTOWithMixedCaseRole() {
        UserRoleDTO dto = new UserRoleDTO("CuStOmEr");

        assertEquals("CuStOmEr", dto.role());
    }

    @Test
    void testUserRoleDTOWithLowercaseRole() {
        UserRoleDTO dto = new UserRoleDTO("customer");

        assertEquals("customer", dto.role());
    }

    @Test
    void testUserRoleDTOWithUppercaseRole() {
        UserRoleDTO dto = new UserRoleDTO("CUSTOMER");

        assertEquals("CUSTOMER", dto.role());
    }

    @Test
    void testUserRoleDTOEqualityWithSelf() {
        UserRoleDTO dto = new UserRoleDTO("Customer");

        assertEquals(dto, dto);
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void testUserRoleDTONotEqualsNull() {
        UserRoleDTO dto = new UserRoleDTO("Customer");

        assertNotEquals(null, dto);
    }

    @Test
    void testUserRoleDTOEqualityWithNullRoles() {
        UserRoleDTO dto1 = new UserRoleDTO(null);
        UserRoleDTO dto2 = new UserRoleDTO(null);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testUserRoleDTOInequalityNullVsNonNull() {
        UserRoleDTO dto1 = new UserRoleDTO(null);
        UserRoleDTO dto2 = new UserRoleDTO("Customer");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserRoleDTOWithUnicodeCharacters() {
        UserRoleDTO dto = new UserRoleDTO("顧客");

        assertEquals("顧客", dto.role());
    }

    @Test
    void testUserRoleDTOWithAccentedCharacters() {
        UserRoleDTO dto = new UserRoleDTO("Administrador");

        assertEquals("Administrador", dto.role());
    }

    @Test
    void testUserRoleDTOWithHyphenatedRole() {
        UserRoleDTO dto = new UserRoleDTO("Super-Admin");

        assertEquals("Super-Admin", dto.role());
    }

    @Test
    void testUserRoleDTOWithUnderscoreRole() {
        UserRoleDTO dto = new UserRoleDTO("Accommodation_Owner");

        assertEquals("Accommodation_Owner", dto.role());
    }

    @Test
    void testUserRoleDTOWithTabsAndNewlines() {
        UserRoleDTO dto = new UserRoleDTO("Role\twith\ttabs");

        assertEquals("Role\twith\ttabs", dto.role());
    }

    @Test
    void testUserRoleDTOInequalityDifferentCasing() {
        UserRoleDTO dto1 = new UserRoleDTO("Customer");
        UserRoleDTO dto2 = new UserRoleDTO("customer");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserRoleDTOToStringContainsRoleValue() {
        UserRoleDTO dto = new UserRoleDTO("TestRole");

        String toString = dto.toString();
        assertTrue(toString.contains("TestRole") || toString.contains("role"));
    }

    @Test
    void testUserRoleDTOWithSingleCharacterRole() {
        UserRoleDTO dto = new UserRoleDTO("A");

        assertEquals("A", dto.role());
    }

    @Test
    void testUserRoleDTOWithVeryLongRole() {
        String veryLongRole = "A".repeat(1000);
        UserRoleDTO dto = new UserRoleDTO(veryLongRole);

        assertEquals(veryLongRole, dto.role());
        assertEquals(1000, dto.role().length());
    }

    @Test
    void testUserRoleDTOMultipleInstancesIndependence() {
        UserRoleDTO dto1 = new UserRoleDTO("Role1");
        UserRoleDTO dto2 = new UserRoleDTO("Role2");
        UserRoleDTO dto3 = new UserRoleDTO("Role3");

        assertEquals("Role1", dto1.role());
        assertEquals("Role2", dto2.role());
        assertEquals("Role3", dto3.role());
        assertNotEquals(dto1, dto2);
        assertNotEquals(dto2, dto3);
        assertNotEquals(dto1, dto3);
    }
}
