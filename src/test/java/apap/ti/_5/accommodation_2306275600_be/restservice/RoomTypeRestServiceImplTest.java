package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private String testRoomTypeID;
    private String testPropertyID;

    @BeforeEach
    void setUp() {
        testPropertyID = "HOT-1234-001";
        testRoomTypeID = "001–Deluxe–1";

        testProperty = Property.builder()
                .propertyID(testPropertyID)
                .propertyName("Test Hotel")
                .type(1)
                .address("Test Address")
                .province(1)
                .description("Test Description")
                .totalRoom(10)
                .activeStatus(1)
                .income(0)
                .ownerName("Test Owner")
                .ownerID(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        testRoomType = RoomType.builder()
                .roomTypeID(testRoomTypeID)
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void whenCreateRoomType_thenReturnCreatedRoomType() {
        // Arrange
        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .propertyID(testPropertyID)
                .unitCount(5)
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(testRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.createRoomType(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testRoomTypeID, result.getRoomTypeID());
        assertEquals("Deluxe", result.getName());
        assertEquals(500000, result.getPrice());
        assertEquals("Deluxe Room", result.getDescription());
        assertEquals(2, result.getCapacity());
        assertEquals("AC, TV", result.getFacility());
        assertEquals(1, result.getFloor());
        assertEquals(testPropertyID, result.getPropertyID());
        assertEquals("Test Hotel", result.getPropertyName());
        verify(propertyRepository, times(1)).findById(testPropertyID);
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void whenCreateRoomTypeWithPropertyNotFound_thenThrowException() {
        // Arrange
        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .propertyID("INVALID-PROPERTY")
                .unitCount(5)
                .build();

        when(propertyRepository.findById("INVALID-PROPERTY")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomTypeRestService.createRoomType(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Property not found"));
        verify(propertyRepository, times(1)).findById("INVALID-PROPERTY");
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    void whenCreateRoomTypeWithSpacesInName_thenGenerateIDWithUnderscore() {
        // Arrange
        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
                .name("Super Deluxe")
                .price(800000)
                .description("Super Deluxe Room")
                .capacity(3)
                .facility("AC, TV, Mini Bar")
                .floor(2)
                .propertyID(testPropertyID)
                .unitCount(3)
                .build();

        RoomType roomTypeWithSpaces = RoomType.builder()
                .roomTypeID("001–Super_Deluxe–2")
                .name("Super Deluxe")
                .price(800000)
                .description("Super Deluxe Room")
                .capacity(3)
                .facility("AC, TV, Mini Bar")
                .floor(2)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(roomTypeWithSpaces);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.createRoomType(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoomTypeID().contains("Super_Deluxe"));
        assertEquals("Super Deluxe", result.getName());
        assertEquals(2, result.getFloor());
    }

    @Test
    void whenGetRoomTypeById_thenReturnRoomType() {
        // Arrange
        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.getRoomTypeById(testRoomTypeID);

        // Assert
        assertNotNull(result);
        assertEquals(testRoomTypeID, result.getRoomTypeID());
        assertEquals("Deluxe", result.getName());
        assertEquals(500000, result.getPrice());
        assertEquals(2, result.getCapacity());
        assertEquals("AC, TV", result.getFacility());
        assertEquals(1, result.getFloor());
        assertEquals(testPropertyID, result.getPropertyID());
        assertEquals("Test Hotel", result.getPropertyName());
        verify(roomTypeRepository, times(1)).findById(testRoomTypeID);
    }

    @Test
    void whenGetRoomTypeByIdNotFound_thenThrowException() {
        // Arrange
        when(roomTypeRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomTypeRestService.getRoomTypeById("INVALID-ID");
        });

        assertTrue(exception.getMessage().contains("Room type not found"));
        verify(roomTypeRepository, times(1)).findById("INVALID-ID");
    }

    @Test
    void whenGetAllRoomTypes_thenReturnRoomTypeList() {
        // Arrange
        RoomType roomType2 = RoomType.builder()
                .roomTypeID("001–Suite–2")
                .name("Suite")
                .price(800000)
                .description("Suite Room")
                .capacity(4)
                .facility("AC, TV, Mini Bar")
                .floor(2)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        List<RoomType> roomTypes = Arrays.asList(testRoomType, roomType2);
        when(roomTypeRepository.findAll()).thenReturn(roomTypes);

        // Act
        List<RoomTypeResponseDTO> result = roomTypeRestService.getAllRoomTypes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Deluxe", result.get(0).getName());
        assertEquals("Suite", result.get(1).getName());
        verify(roomTypeRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllRoomTypesEmpty_thenReturnEmptyList() {
        // Arrange
        when(roomTypeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeRestService.getAllRoomTypes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomTypeRepository, times(1)).findAll();
    }

    @Test
    void whenGetRoomTypesByProperty_thenReturnFilteredRoomTypes() {
        // Arrange
        RoomType roomType2 = RoomType.builder()
                .roomTypeID("001–Suite–2")
                .name("Suite")
                .price(800000)
                .description("Suite Room")
                .capacity(4)
                .facility("AC, TV, Mini Bar")
                .floor(2)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        List<RoomType> roomTypes = Arrays.asList(testRoomType, roomType2);
        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID)).thenReturn(roomTypes);

        // Act
        List<RoomTypeResponseDTO> result = roomTypeRestService.getRoomTypesByProperty(testPropertyID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testPropertyID, result.get(0).getPropertyID());
        assertEquals(testPropertyID, result.get(1).getPropertyID());
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID(testPropertyID);
    }

    @Test
    void whenGetRoomTypesByPropertyEmpty_thenReturnEmptyList() {
        // Arrange
        when(roomTypeRepository.findByProperty_PropertyID("INVALID-PROPERTY")).thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeRestService.getRoomTypesByProperty("INVALID-PROPERTY");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID("INVALID-PROPERTY");
    }

    @Test
    void whenUpdateRoomType_thenReturnUpdatedRoomType() {
        // Arrange
        UpdateRoomTypeRequestDTO updateDTO = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .capacity(3)
                .price(600000)
                .facility("AC, TV, Mini Bar")
                .description("Updated Deluxe Room")
                .build();

        RoomType updatedRoomType = testRoomType.toBuilder()
                .capacity(3)
                .price(600000)
                .facility("AC, TV, Mini Bar")
                .description("Updated Deluxe Room")
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(updatedRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(testRoomTypeID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getCapacity());
        assertEquals(600000, result.getPrice());
        assertEquals("AC, TV, Mini Bar", result.getFacility());
        assertEquals("Updated Deluxe Room", result.getDescription());
        verify(roomTypeRepository, times(1)).findById(testRoomTypeID);
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void whenUpdateRoomTypeWithPartialData_thenUpdateOnlyProvidedFields() {
        // Arrange
        UpdateRoomTypeRequestDTO updateDTO = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .price(600000)
                .build();

        RoomType updatedRoomType = testRoomType.toBuilder()
                .price(600000)
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(updatedRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(testRoomTypeID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(600000, result.getPrice());
        assertEquals(2, result.getCapacity()); // Unchanged
        assertEquals("AC, TV", result.getFacility()); // Unchanged
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void whenUpdateRoomTypeNotFound_thenThrowException() {
        // Arrange
        UpdateRoomTypeRequestDTO updateDTO = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID("INVALID-ID")
                .price(600000)
                .build();

        when(roomTypeRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomTypeRestService.updateRoomType("INVALID-ID", updateDTO);
        });

        assertTrue(exception.getMessage().contains("Room type not found"));
        verify(roomTypeRepository, times(1)).findById("INVALID-ID");
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    void whenDeleteRoomTypeExists_thenDeleteSuccessfully() {
        // Arrange
        when(roomTypeRepository.existsById(testRoomTypeID)).thenReturn(true);
        doNothing().when(roomTypeRepository).deleteById(testRoomTypeID);

        // Act
        assertDoesNotThrow(() -> roomTypeRestService.deleteRoomType(testRoomTypeID));

        // Assert
        verify(roomTypeRepository, times(1)).existsById(testRoomTypeID);
        verify(roomTypeRepository, times(1)).deleteById(testRoomTypeID);
    }

    @Test
    void whenDeleteRoomTypeNotFound_thenThrowException() {
        // Arrange
        when(roomTypeRepository.existsById("INVALID-ID")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomTypeRestService.deleteRoomType("INVALID-ID");
        });

        assertTrue(exception.getMessage().contains("Room type not found"));
        verify(roomTypeRepository, times(1)).existsById("INVALID-ID");
        verify(roomTypeRepository, never()).deleteById(anyString());
    }

    @Test
    void whenIsDuplicateRoomTypeFloorWithDuplicate_thenReturnTrue() {
        // Arrange
        RoomType existingRoomType = RoomType.builder()
                .roomTypeID("001–Deluxe–1")
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID))
                .thenReturn(Collections.singletonList(existingRoomType));

        // Act
        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(testPropertyID, "Deluxe", 1);

        // Assert
        assertTrue(result);
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID(testPropertyID);
    }

    @Test
    void whenIsDuplicateRoomTypeFloorWithNoDuplicate_thenReturnFalse() {
        // Arrange
        RoomType existingRoomType = RoomType.builder()
                .roomTypeID("001–Deluxe–1")
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID))
                .thenReturn(Collections.singletonList(existingRoomType));

        // Act - Same name but different floor
        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(testPropertyID, "Deluxe", 2);

        // Assert
        assertFalse(result);
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID(testPropertyID);
    }

    @Test
    void whenIsDuplicateRoomTypeFloorWithDifferentName_thenReturnFalse() {
        // Arrange
        RoomType existingRoomType = RoomType.builder()
                .roomTypeID("001–Deluxe–1")
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID))
                .thenReturn(Collections.singletonList(existingRoomType));

        // Act - Different name, same floor
        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(testPropertyID, "Suite", 1);

        // Assert
        assertFalse(result);
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID(testPropertyID);
    }

    @Test
    void whenIsDuplicateRoomTypeFloorWithNullFloor_thenReturnFalse() {
        // Arrange - No need to mock, should return false immediately

        // Act
        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(testPropertyID, "Deluxe", null);

        // Assert
        assertFalse(result);
        verify(roomTypeRepository, never()).findByProperty_PropertyID(anyString());
    }

    @Test
    void whenIsDuplicateRoomTypeFloorWithEmptyList_thenReturnFalse() {
        // Arrange
        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID))
                .thenReturn(new ArrayList<>());

        // Act
        boolean result = roomTypeRestService.isDuplicateRoomTypeFloor(testPropertyID, "Deluxe", 1);

        // Assert
        assertFalse(result);
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID(testPropertyID);
    }

    @Test
    void whenCreateRoomTypeOnDifferentFloors_thenGenerateCorrectIDs() {
        // Arrange - Floor 3
        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
                .name("Standard")
                .price(300000)
                .description("Standard Room")
                .capacity(1)
                .facility("AC")
                .floor(3)
                .propertyID(testPropertyID)
                .unitCount(2)
                .build();

        RoomType floor3RoomType = RoomType.builder()
                .roomTypeID("001–Standard–3")
                .name("Standard")
                .price(300000)
                .description("Standard Room")
                .capacity(1)
                .facility("AC")
                .floor(3)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(floor3RoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.createRoomType(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoomTypeID().endsWith("–3"));
        assertEquals(3, result.getFloor());
    }

    @Test
    void whenUpdateRoomTypeWithAllFields_thenUpdateAllFields() {
        // Arrange
        UpdateRoomTypeRequestDTO updateDTO = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .capacity(4)
                .price(700000)
                .facility("AC, TV, Mini Bar, WiFi")
                .description("Premium Deluxe Room")
                .build();

        RoomType updatedRoomType = testRoomType.toBuilder()
                .capacity(4)
                .price(700000)
                .facility("AC, TV, Mini Bar, WiFi")
                .description("Premium Deluxe Room")
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(updatedRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(testRoomTypeID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getCapacity());
        assertEquals(700000, result.getPrice());
        assertEquals("AC, TV, Mini Bar, WiFi", result.getFacility());
        assertEquals("Premium Deluxe Room", result.getDescription());
        assertEquals("Deluxe", result.getName()); // Name should not change
        assertEquals(1, result.getFloor()); // Floor should not change
    }

    @Test
    void whenConvertRoomTypeWithNullProperty_thenReturnNullPropertyFields() {
        // Arrange
        RoomType roomTypeWithoutProperty = RoomType.builder()
                .roomTypeID("001–Deluxe–1")
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .property(null)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(roomTypeWithoutProperty));

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.getRoomTypeById(testRoomTypeID);

        // Assert
        assertNotNull(result);
        assertNull(result.getPropertyID());
        assertNull(result.getPropertyName());
    }

    @Test
    void whenCreateRoomTypeWithComplexPropertyID_thenGenerateCorrectID() {
        // Arrange
        Property villaProperty = Property.builder()
                .propertyID("VIL-5678-123")
                .propertyName("Test Villa")
                .type(2)
                .address("Test Address")
                .province(1)
                .description("Test Description")
                .totalRoom(5)
                .activeStatus(1)
                .income(0)
                .ownerName("Test Owner")
                .ownerID(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        CreateRoomTypeRequestDTO requestDTO = CreateRoomTypeRequestDTO.builder()
                .name("Luxury Suite")
                .price(1000000)
                .description("Luxury Suite Room")
                .capacity(4)
                .facility("AC, TV, Mini Bar, WiFi, Jacuzzi")
                .floor(5)
                .propertyID("VIL-5678-123")
                .unitCount(1)
                .build();

        RoomType villaRoomType = RoomType.builder()
                .roomTypeID("123–Luxury_Suite–5")
                .name("Luxury Suite")
                .price(1000000)
                .description("Luxury Suite Room")
                .capacity(4)
                .facility("AC, TV, Mini Bar, WiFi, Jacuzzi")
                .floor(5)
                .property(villaProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById("VIL-5678-123")).thenReturn(Optional.of(villaProperty));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(villaRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.createRoomType(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoomTypeID().startsWith("123–"));
        assertTrue(result.getRoomTypeID().contains("Luxury_Suite"));
        assertEquals(5, result.getFloor());
    }

    @Test
    void whenUpdateRoomTypeWithOnlyCapacity_thenUpdateOnlyCapacity() {
        // Arrange
        UpdateRoomTypeRequestDTO updateDTO = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .capacity(5)
                .build();

        RoomType updatedRoomType = testRoomType.toBuilder()
                .capacity(5)
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(updatedRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(testRoomTypeID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getCapacity());
        assertEquals(500000, result.getPrice()); // Unchanged
        assertEquals("AC, TV", result.getFacility()); // Unchanged
        assertEquals("Deluxe Room", result.getDescription()); // Unchanged
    }

    @Test
    void whenUpdateRoomTypeWithOnlyFacility_thenUpdateOnlyFacility() {
        // Arrange
        UpdateRoomTypeRequestDTO updateDTO = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .facility("AC, TV, WiFi")
                .build();

        RoomType updatedRoomType = testRoomType.toBuilder()
                .facility("AC, TV, WiFi")
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(updatedRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(testRoomTypeID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("AC, TV, WiFi", result.getFacility());
        assertEquals(2, result.getCapacity()); // Unchanged
        assertEquals(500000, result.getPrice()); // Unchanged
    }

    @Test
    void whenUpdateRoomTypeWithOnlyDescription_thenUpdateOnlyDescription() {
        // Arrange
        UpdateRoomTypeRequestDTO updateDTO = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .description("Updated Description")
                .build();

        RoomType updatedRoomType = testRoomType.toBuilder()
                .description("Updated Description")
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(updatedRoomType);

        // Act
        RoomTypeResponseDTO result = roomTypeRestService.updateRoomType(testRoomTypeID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Description", result.getDescription());
        assertEquals(2, result.getCapacity()); // Unchanged
        assertEquals(500000, result.getPrice()); // Unchanged
        assertEquals("AC, TV", result.getFacility()); // Unchanged
    }
}
