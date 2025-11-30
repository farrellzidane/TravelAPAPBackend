package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
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
 * Unit tests for RBAC authorization logic in RoomRestServiceRBACImpl.
 * Focus: Testing that authorization checks work correctly for different roles.
 */
@ExtendWith(MockitoExtension.class)
class RoomRestServiceRBACImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private RoomRestServiceRBACImpl roomRestService;

    private UserProfileDTO superAdminUser;
    private UserProfileDTO ownerUser;
    private UserProfileDTO customerUser;
    private UserProfileDTO unauthorizedUser;
    private UUID ownerId;
    private UUID roomId;
    private UUID roomTypeId;
    private UUID propertyId;
    private Room testRoom;
    private RoomType testRoomType;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        roomId = UUID.randomUUID();
        roomTypeId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        superAdminUser = new UserProfileDTO(UUID.randomUUID(), "superadmin", "SuperAdmin", "superadmin@test.com", "Male", "SUPERADMIN", LocalDateTime.now(), LocalDateTime.now(), false);
        ownerUser = new UserProfileDTO(ownerId, "owner", "Owner", "owner@test.com", "Male", "ACCOMMODATION_OWNER", LocalDateTime.now(), LocalDateTime.now(), false);
        customerUser = new UserProfileDTO(UUID.randomUUID(), "customer", "Customer", "customer@test.com", "Male", "CUSTOMER", LocalDateTime.now(), LocalDateTime.now(), false);
        unauthorizedUser = new UserProfileDTO(UUID.randomUUID(), "unauthorized", "Unauthorized", "unauthorized@test.com", "Male", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now(), false);

        testProperty = new Property();
        testProperty.setPropertyID(propertyId);
        testProperty.setOwnerID(ownerId);

        testRoomType = new RoomType();
        testRoomType.setRoomTypeID(roomTypeId);
        testRoomType.setProperty(testProperty);

        testRoom = new Room();
        testRoom.setRoomID(roomId);
        testRoom.setRoomType(testRoomType);
    }

    // ============================================
    // GET ROOM BY ID - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testGetRoomById_UnauthorizedRole_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isAccommodationOwner(unauthorizedUser)).thenReturn(false);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomRestService.getRoomById(roomId));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // GET ROOMS BY ROOM TYPE - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testGetRoomsByRoomType_AccommodationOwner_WithoutOwnership_AccessDenied() {
        UUID otherOwnerId = UUID.randomUUID();
        testProperty.setOwnerID(otherOwnerId);

        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(ownerUser)).thenReturn(true);
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomRestService.getRoomsByRoomType(roomTypeId));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    @Test
    void testGetRoomsByRoomType_UnauthorizedRole_AccessDenied() {
        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isAccommodationOwner(unauthorizedUser)).thenReturn(false);
        when(authService.isCustomer(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomRestService.getRoomsByRoomType(roomTypeId));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // CREATE MAINTENANCE - AUTHORIZATION TESTS
    // ============================================

    @Test
    void testCreateMaintenance_AccommodationOwner_WithoutOwnership_AccessDenied() {
        UUID otherOwnerId = UUID.randomUUID();
        testProperty.setOwnerID(otherOwnerId);

        CreateMaintenanceRequestDTO dto = CreateMaintenanceRequestDTO.builder()
                .roomID(roomId.toString())
                .maintenanceStart(LocalDateTime.now())
                .maintenanceEnd(LocalDateTime.now().plusDays(7))
                .build();

        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(ownerUser)).thenReturn(true);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomRestService.createMaintenance(dto));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    @Test
    void testCreateMaintenance_Customer_AccessDenied() {
        CreateMaintenanceRequestDTO dto = CreateMaintenanceRequestDTO.builder()
                .roomID(roomId.toString())
                .maintenanceStart(LocalDateTime.now())
                .maintenanceEnd(LocalDateTime.now().plusDays(7))
                .build();

        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(customerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomRestService.createMaintenance(dto));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    @Test
    void testCreateMaintenance_UnauthorizedRole_AccessDenied() {
        CreateMaintenanceRequestDTO dto = CreateMaintenanceRequestDTO.builder()
                .roomID(roomId.toString())
                .maintenanceStart(LocalDateTime.now())
                .maintenanceEnd(LocalDateTime.now().plusDays(7))
                .build();

        when(authService.getAuthenticatedUser()).thenReturn(unauthorizedUser);
        when(authService.isSuperAdmin(unauthorizedUser)).thenReturn(false);
        when(authService.isAccommodationOwner(unauthorizedUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> roomRestService.createMaintenance(dto));
        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // OWNERSHIP VALIDATION TESTS
    // ============================================

    @Test
    void testOwnershipValidation_RoomBelongsToOwner() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        Optional<Room> room = roomRepository.findById(roomId);
        assertTrue(room.isPresent());
        assertEquals(ownerId, room.get().getRoomType().getProperty().getOwnerID());
    }

    @Test
    void testOwnershipValidation_RoomNotBelongsToOwner() {
        UUID otherOwnerId = UUID.randomUUID();
        testProperty.setOwnerID(otherOwnerId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        Optional<Room> room = roomRepository.findById(roomId);
        assertTrue(room.isPresent());
        assertNotEquals(ownerId, room.get().getRoomType().getProperty().getOwnerID());
    }

    @Test
    void testOwnershipValidation_RoomTypeNotFound() {
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.empty());

        Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
        assertFalse(roomType.isPresent());
    }

    @Test
    void testOwnershipValidation_RoomNotFound() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        Optional<Room> room = roomRepository.findById(roomId);
        assertFalse(room.isPresent());
    }

    // ============================================
    // ROLE CHECK TESTS
    // ============================================

    @Test
    void testRoleCheck_SuperAdmin() {
        when(authService.isSuperAdmin(superAdminUser)).thenReturn(true);

        assertTrue(authService.isSuperAdmin(superAdminUser));
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
