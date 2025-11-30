package apap.ti._5.accommodation_2306275600_be.restservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;

@ExtendWith(MockitoExtension.class)
class RoomTypeRestServiceImplTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomTypeRestServiceImpl roomTypeRestService;

    private RoomType testRoomType;
    private Property testProperty;
    private Room testRoom;
    private UUID roomTypeId;
    private UUID propertyId;

    @BeforeEach
    void setUp() {
        roomTypeId = UUID.randomUUID();
        propertyId = UUID.randomUUID();

        testProperty = Property.builder()
            .propertyID(propertyId)
            .propertyName("Test Hotel")
            .type(1)
            .address("Test Address")
            .province(1)
            .description("Test Description")
            .totalRoom(10)
            .activeStatus(1)
            .income(0)
            .ownerID(UUID.randomUUID())
            .ownerName("Test Owner")
            .createdDate(LocalDateTime.now())
            .build();

        testRoomType = RoomType.builder()
            .roomTypeID(roomTypeId)
            .name("Deluxe")
            .capacity(2)
            .price(500000)
            .facility("AC, TV")
            .description("Deluxe Room")
            .floor(1)
            .property(testProperty)
            .createdDate(LocalDateTime.now())
            .build();

        testRoom = Room.builder()
            .roomID(UUID.randomUUID())
            .name("101")
            .availabilityStatus(1)
            .activeRoom(1)
            .roomType(testRoomType)
            .createdDate(LocalDateTime.now())
            .build();
    }

    // ============================================
    // CREATE ROOM TYPE TESTS
    // ============================================

    @Test
    void testCreateRoomType_Success() {
        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
            .name("Deluxe")
            .price(500000)
            .description("Deluxe Room")
            .capacity(2)
            .facility("AC, TV")
            .floor(1)
            .propertyID(propertyId.toString())
            .unitCount(5)
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(testRoomType);
        when(roomTypeRepository.findByProperty_PropertyID(propertyId))
            .thenReturn(Collections.emptyList());
        when(roomRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        RoomTypeResponseDTO result = roomTypeRestService.createRoomType(requestDTO);

        assertNotNull(result);
        assertEquals(roomTypeId.toString(), result.getRoomTypeID());
        assertEquals("Deluxe", result.getName());
        verify(propertyRepository).findById(propertyId);
        verify(roomTypeRepository).save(any(RoomType.class));
        verify(roomRepository).saveAll(anyList());
    }

    @Test
    void testCreateRoomType_PropertyNotFound_ThrowsException() {
        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
            .name("Deluxe")
            .price(500000)
            .description("Deluxe Room")
            .capacity(2)
            .facility("AC, TV")
            .floor(1)
            .propertyID(propertyId.toString())
            .unitCount(5)
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roomTypeRestService.createRoomType(requestDTO));
        
        assertTrue(exception.getMessage().contains("Property not found"));
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    void testCreateRoomType_WithMultipleUnits() {
        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
            .name("Suite")
            .price(1000000)
            .description("Suite Room")
            .capacity(4)
            .facility("AC, TV, Minibar")
            .floor(2)
            .propertyID(propertyId.toString())
            .unitCount(3)
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(testRoomType);
        when(roomTypeRepository.findByProperty_PropertyID(propertyId))
            .thenReturn(Collections.emptyList());
        when(roomRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        RoomTypeResponseDTO result = roomTypeRestService.createRoomType(requestDTO);

        assertNotNull(result);
        verify(roomRepository).saveAll(argThat(rooms -> {
            if (rooms instanceof List) {
                return ((List<?>) rooms).size() == 3;
            }
            return false;
        }));
    }

    // ============================================
    // GET ROOM TYPE BY ID TESTS
    // ============================================

    @Test
    void testGetRoomTypeById_Success() {
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));

        RoomTypeResponseDTO result = roomTypeRestService.getRoomTypeById(roomTypeId);

        assertNotNull(result);
        assertEquals(roomTypeId.toString(), result.getRoomTypeID());
        assertEquals("Deluxe", result.getName());
        verify(roomTypeRepository).findById(roomTypeId);
    }

    @Test
    void testGetRoomTypeById_NotFound_ThrowsException() {
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roomTypeRestService.getRoomTypeById(roomTypeId));
        
        assertTrue(exception.getMessage().contains("Room type not found"));
    }

    // ============================================
    // GET ALL ROOM TYPES TESTS
    // ============================================

    @Test
    void testGetAllRoomTypes_ReturnsRoomTypes() {
        when(roomTypeRepository.findAll()).thenReturn(Arrays.asList(testRoomType));

        List<RoomTypeResponseDTO> result = roomTypeRestService.getAllRoomTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roomTypeId.toString(), result.get(0).getRoomTypeID());
        verify(roomTypeRepository).findAll();
    }

    @Test
    void testGetAllRoomTypes_EmptyList() {
        when(roomTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoomTypeResponseDTO> result = roomTypeRestService.getAllRoomTypes();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(roomTypeRepository).findAll();
    }

    // ============================================
    // GET ROOM TYPES BY PROPERTY TESTS
    // ============================================

    @Test
    void testGetRoomTypesByProperty_Success() {
        when(roomTypeRepository.findByProperty_PropertyID(propertyId))
            .thenReturn(Arrays.asList(testRoomType));

        List<RoomTypeResponseDTO> result = roomTypeRestService.getRoomTypesByProperty(propertyId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomTypeRepository).findByProperty_PropertyID(propertyId);
    }

    @Test
    void testGetRoomTypesByProperty_EmptyList() {
        when(roomTypeRepository.findByProperty_PropertyID(propertyId))
            .thenReturn(Collections.emptyList());

        List<RoomTypeResponseDTO> result = roomTypeRestService.getRoomTypesByProperty(propertyId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ============================================
    // UPDATE ROOM TYPE TESTS
    // ============================================

    @Test
    void testUpdateRoomType_Success() {
        UpdateRoomTypeRequestDTO requestDTO = UpdateRoomTypeRequestDTO.builder()
            .roomTypeID(roomTypeId.toString())
            .capacity(3)
            .price(600000)
            .facility("AC, TV, Minibar")
            .description("Updated Deluxe Room")
            .build();

        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(testRoomType);

        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(roomTypeId, requestDTO);

        assertNotNull(result);
        verify(roomTypeRepository).findById(roomTypeId);
        verify(roomTypeRepository).save(any(RoomType.class));
    }

    @Test
    void testUpdateRoomType_NotFound_ThrowsException() {
        UpdateRoomTypeRequestDTO requestDTO = UpdateRoomTypeRequestDTO.builder()
            .roomTypeID(roomTypeId.toString())
            .capacity(3)
            .price(600000)
            .facility("AC, TV, Minibar")
            .build();

        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roomTypeRestService.updateRoomType(roomTypeId, requestDTO));
        
        assertTrue(exception.getMessage().contains("Room type not found"));
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    void testUpdateRoomType_PartialUpdate() {
        UpdateRoomTypeRequestDTO requestDTO = UpdateRoomTypeRequestDTO.builder()
            .roomTypeID(roomTypeId.toString())
            .price(600000)
            .build();

        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(testRoomType);

        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(roomTypeId, requestDTO);

        assertNotNull(result);
        verify(roomTypeRepository).save(argThat(roomType -> 
            roomType.getCapacity() == testRoomType.getCapacity() &&
            roomType.getFacility().equals(testRoomType.getFacility())
        ));
    }

    // ============================================
    // DELETE ROOM TYPE TESTS
    // ============================================

    @Test
    void testDeleteRoomType_Success() {
        when(roomTypeRepository.existsById(roomTypeId)).thenReturn(true);

        assertDoesNotThrow(() -> roomTypeRestService.deleteRoomType(roomTypeId));

        verify(roomTypeRepository).existsById(roomTypeId);
        verify(roomTypeRepository).deleteById(roomTypeId);
    }

    @Test
    void testDeleteRoomType_NotFound_ThrowsException() {
        when(roomTypeRepository.existsById(roomTypeId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roomTypeRestService.deleteRoomType(roomTypeId));
        
        assertTrue(exception.getMessage().contains("Room type not found"));
        verify(roomTypeRepository, never()).deleteById(any());
    }

    // ============================================
    // IS DUPLICATE ROOM TYPE FLOOR TESTS
    // ============================================

    @Test
    void testIsDuplicateRoomTypeFloor_NoDuplicate() {
        when(roomTypeRepository.findByProperty_PropertyID(propertyId))
            .thenReturn(Arrays.asList(testRoomType));

        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(propertyId, "Suite", 1);

        assertFalse(result);
        verify(roomTypeRepository).findByProperty_PropertyID(propertyId);
    }

    @Test
    void testIsDuplicateRoomTypeFloor_Duplicate() {
        when(roomTypeRepository.findByProperty_PropertyID(propertyId))
            .thenReturn(Arrays.asList(testRoomType));

        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(propertyId, "Deluxe", 1);

        assertTrue(result);
        verify(roomTypeRepository).findByProperty_PropertyID(propertyId);
    }

    @Test
    void testIsDuplicateRoomTypeFloor_SameNameDifferentFloor() {
        when(roomTypeRepository.findByProperty_PropertyID(propertyId))
            .thenReturn(Arrays.asList(testRoomType));

        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(propertyId, "Deluxe", 2);

        assertFalse(result);
    }

    @Test
    void testIsDuplicateRoomTypeFloor_NullFloor() {
        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(propertyId, "Deluxe", null);

        assertFalse(result);
        verify(roomTypeRepository, never()).findByProperty_PropertyID(any());
    }
}
