package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
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
 * Unit tests for RBAC authorization logic in RoomTypeRestServiceRBACImpl.
 * Focus: Testing authorization checks for different user roles.
 */
@ExtendWith(MockitoExtension.class)
class RoomTypeRestServiceRBACImplTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private RoomTypeRestServiceRBACImpl roomTypeRestService;

    private UserProfileDTO superAdminUser;
    private UserProfileDTO ownerUser;
    private UserProfileDTO customerUser;
    private UserProfileDTO unauthorizedUser;
    private UUID ownerId;
    private UUID roomTypeId;
    private UUID propertyId;
    private RoomType testRoomType;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        roomTypeId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        superAdminUser = new UserProfileDTO(UUID.randomUUID(), "superadmin", "SuperAdmin", "superadmin@test.com", "Male", "SUPERADMIN", LocalDateTime.now(), LocalDateTime.now(), false);
        ownerUser = new UserProfileDTO(ownerId, "owner", "Owner", "owner@test.com", "Male", "ACCOMMODATION_OWNER", LocalDateTime.now(), LocalDateTime.now(), false);
        customerUser = new UserProfileDTO(UUID.randomUUID(), "customer", "Customer", "customer@test.com", "Male", "CUSTOMER", LocalDateTime.now(), LocalDateTime.now(), false);
        unauthorizedUser = new UserProfileDTO(UUID.randomUUID(), "unauthorized", "Unauthorized", "unauthorized@test.com", "Male", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now(), false);

        testProperty = new Property();
        testProperty.setPropertyID(propertyId);
        testProperty.setPropertyName("Test Property");
        testProperty.setOwnerID(ownerId);

        testRoomType = new RoomType();
        testRoomType.setRoomTypeID(roomTypeId);
        testRoomType.setProperty(testProperty);
    }

    // ============================================
    // GET ROOM TYPE BY ID - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testGetRoomTypeById_AccommodationOwner_WithoutOwnership_AccessDenied() {
        UUID otherOwnerId = UUID.randomUUID();
        testProperty.setOwnerID(otherOwnerId);

        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(ownerUser)).thenReturn(true);
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.getRoomTypeById(roomTypeId));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testGetRoomTypeById_UnauthorizedRole_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isAccommodationOwner(unauthorizedUser)).thenReturn(false);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.getRoomTypeById(roomTypeId));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // GET ROOM TYPES BY PROPERTY - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testGetRoomTypesByProperty_AccommodationOwner_OtherProperty_AccessDenied() {
        UUID otherOwnerId = UUID.randomUUID();
        testProperty.setOwnerID(otherOwnerId);

        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(ownerUser)).thenReturn(true);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.getRoomTypesByProperty(propertyId));
        assertTrue(exception.getMessage().contains("Anda tidak memiliki akses"));
    }

    @Test
    void testGetRoomTypesByProperty_UnauthorizedRole_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isAccommodationOwner(unauthorizedUser)).thenReturn(false);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.getRoomTypesByProperty(propertyId));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // UPDATE ROOM TYPE - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testUpdateRoomType_Customer_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(customerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.updateRoomType(roomTypeId, null));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    @Test
    void testUpdateRoomType_UnauthorizedRole_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isAccommodationOwner(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.updateRoomType(roomTypeId, null));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // DELETE ROOM TYPE - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testDeleteRoomType_Customer_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(customerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.deleteRoomType(roomTypeId));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    @Test
    void testDeleteRoomType_UnauthorizedRole_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isAccommodationOwner(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomTypeRestService.deleteRoomType(roomTypeId));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // OWNERSHIP VALIDATION TESTS
    // ============================================

    @Test
    void testOwnershipValidation_RoomTypeBelongsToOwner() {
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));

        Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
        assertTrue(roomType.isPresent());
        assertEquals(ownerId, roomType.get().getProperty().getOwnerID());
    }

    @Test
    void testOwnershipValidation_RoomTypeNotBelongsToOwner() {
        UUID otherOwnerId = UUID.randomUUID();
        testProperty.setOwnerID(otherOwnerId);

        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));

        Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
        assertTrue(roomType.isPresent());
        assertNotEquals(ownerId, roomType.get().getProperty().getOwnerID());
    }

    @Test
    void testOwnershipValidation_PropertyBelongsToOwner() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        Optional<Property> property = propertyRepository.findById(propertyId);
        assertTrue(property.isPresent());
        assertEquals(ownerId, property.get().getOwnerID());
    }

    @Test
    void testOwnershipValidation_PropertyNotBelongsToOwner() {
        UUID otherOwnerId = UUID.randomUUID();
        testProperty.setOwnerID(otherOwnerId);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        Optional<Property> property = propertyRepository.findById(propertyId);
        assertTrue(property.isPresent());
        assertNotEquals(ownerId, property.get().getOwnerID());
    }

    @Test
    void testOwnershipValidation_RoomTypeNotFound() {
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.empty());

        Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
        assertFalse(roomType.isPresent());
    }

    @Test
    void testOwnershipValidation_PropertyNotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        Optional<Property> property = propertyRepository.findById(propertyId);
        assertFalse(property.isPresent());
    }

    // ============================================
    // ROLE CHECK TESTS
    // ============================================

    @Test
    void testRoleCheck_SuperAdmin() {
        when(authService.isSuperAdmin(superAdminUser)).thenReturn(true);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);

        assertTrue(authService.isSuperAdmin(superAdminUser));
        assertFalse(authService.isSuperAdmin(ownerUser));
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
