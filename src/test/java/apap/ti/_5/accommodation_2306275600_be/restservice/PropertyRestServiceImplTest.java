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
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;

@ExtendWith(MockitoExtension.class)
class PropertyRestServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private RoomTypeRestService roomTypeRestService;

    @Mock
    private RoomRestService roomRestService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PropertyRestServiceImpl propertyRestService;

    private Property testProperty;
    private UUID propertyId;
    private UUID ownerId;
    private UUID roomTypeId;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        roomTypeId = UUID.randomUUID();

        testProperty = Property.builder()
            .propertyID(propertyId)
            .propertyName("Test Hotel")
            .type(1)
            .address("Jl. Test No. 123")
            .province(1)
            .description("Test Description")
            .totalRoom(10)
            .activeStatus(1)
            .income(0)
            .ownerID(ownerId)
            .ownerName("Test Owner")
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();
    }

    // ============================================
    // GET ALL PROPERTIES TESTS
    // ============================================

    @Test
    void testGetAllProperties_ReturnsActiveProperties() {
        when(propertyRepository.findByActiveStatusOrderByCreatedDateDesc(1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(propertyId.toString(), result.get(0).getPropertyID());
        assertEquals("Test Hotel", result.get(0).getPropertyName());
        verify(propertyRepository).findByActiveStatusOrderByCreatedDateDesc(1);
    }

    @Test
    void testGetAllProperties_EmptyList() {
        when(propertyRepository.findByActiveStatusOrderByCreatedDateDesc(1))
            .thenReturn(Collections.emptyList());

        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(propertyRepository).findByActiveStatusOrderByCreatedDateDesc(1);
    }

    // ============================================
    // GET FILTERED PROPERTIES TESTS
    // ============================================

    @Test
    void testGetFilteredProperties_AllFilters() {
        when(propertyRepository.findByPropertyNameContainingIgnoreCaseAndTypeAndProvinceAndActiveStatus("Test", 1, 1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties("Test", 1, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByPropertyNameContainingIgnoreCaseAndTypeAndProvinceAndActiveStatus("Test", 1, 1);
    }

    @Test
    void testGetFilteredProperties_NameAndTypeOnly() {
        when(propertyRepository.findByPropertyNameContainingIgnoreCaseAndTypeAndActiveStatus("Test", 1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties("Test", 1, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByPropertyNameContainingIgnoreCaseAndTypeAndActiveStatus("Test", 1);
    }

    @Test
    void testGetFilteredProperties_NameAndProvinceOnly() {
        when(propertyRepository.findByPropertyNameContainingIgnoreCaseAndProvinceAndActiveStatus("Test", 1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties("Test", null, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByPropertyNameContainingIgnoreCaseAndProvinceAndActiveStatus("Test", 1);
    }

    @Test
    void testGetFilteredProperties_TypeAndProvinceOnly() {
        when(propertyRepository.findByTypeAndProvinceAndActiveStatusOrderByCreatedDateDesc(1, 1, 1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties(null, 1, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByTypeAndProvinceAndActiveStatusOrderByCreatedDateDesc(1, 1, 1);
    }

    @Test
    void testGetFilteredProperties_NameOnly() {
        when(propertyRepository.findByPropertyNameContainingIgnoreCaseAndActiveStatus("Test"))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties("Test", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByPropertyNameContainingIgnoreCaseAndActiveStatus("Test");
    }

    @Test
    void testGetFilteredProperties_TypeOnly() {
        when(propertyRepository.findByTypeAndActiveStatusOrderByCreatedDateDesc(1, 1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties(null, 1, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByTypeAndActiveStatusOrderByCreatedDateDesc(1, 1);
    }

    @Test
    void testGetFilteredProperties_ProvinceOnly() {
        when(propertyRepository.findByProvinceAndActiveStatusOrderByCreatedDateDesc(1, 1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties(null, null, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByProvinceAndActiveStatusOrderByCreatedDateDesc(1, 1);
    }

    @Test
    void testGetFilteredProperties_NoFilters() {
        when(propertyRepository.findByActiveStatusOrderByCreatedDateDesc(1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties(null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByActiveStatusOrderByCreatedDateDesc(1);
    }

    @Test
    void testGetFilteredProperties_EmptyName() {
        when(propertyRepository.findByActiveStatusOrderByCreatedDateDesc(1))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getFilteredProperties("", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findByActiveStatusOrderByCreatedDateDesc(1);
    }

    // ============================================
    // GET PROPERTIES BY OWNER TESTS
    // ============================================

    @Test
    void testGetPropertiesByOwner_Success() {
        when(propertyRepository.findByOwnerID(ownerId))
            .thenReturn(Arrays.asList(testProperty));

        List<PropertyResponseDTO> result = propertyRestService.getPropertiesByOwner(ownerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ownerId.toString(), result.get(0).getOwnerID());
        verify(propertyRepository).findByOwnerID(ownerId);
    }

    @Test
    void testGetPropertiesByOwner_EmptyList() {
        when(propertyRepository.findByOwnerID(ownerId))
            .thenReturn(Collections.emptyList());

        List<PropertyResponseDTO> result = propertyRestService.getPropertiesByOwner(ownerId);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(propertyRepository).findByOwnerID(ownerId);
    }

    // ============================================
    // GET PROPERTY BY ID TESTS
    // ============================================

    @Test
    void testGetPropertyById_Success() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(roomTypeRestService.getRoomTypesByProperty(propertyId)).thenReturn(Collections.emptyList());

        PropertyResponseDTO result = propertyRestService.getPropertyById(propertyId);

        assertNotNull(result);
        assertEquals(propertyId.toString(), result.getPropertyID());
        assertEquals("Test Hotel", result.getPropertyName());
        verify(propertyRepository).findById(propertyId);
        verify(roomTypeRestService).getRoomTypesByProperty(propertyId);
    }

    @Test
    void testGetPropertyById_NotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        PropertyResponseDTO result = propertyRestService.getPropertyById(propertyId);

        assertNull(result);
        verify(propertyRepository).findById(propertyId);
        verify(roomTypeRestService, never()).getRoomTypesByProperty(any());
    }

    @Test
    void testGetPropertyById_WithDateFilter() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);

        RoomTypeResponseDTO roomType = RoomTypeResponseDTO.builder()
            .roomTypeID(roomTypeId.toString())
            .name("Deluxe")
            .floor(1)
            .capacity(2)
            .price(500000)
            .facility("AC, TV")
            .description("Deluxe Room")
            .createdDate(LocalDateTime.now())
            .build();

        RoomResponseDTO room = RoomResponseDTO.builder()
            .roomID(UUID.randomUUID().toString())
            .name("101")
            .floor(1)
            .roomTypeName("Deluxe")
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(roomTypeRestService.getRoomTypesByProperty(propertyId))
            .thenReturn(Arrays.asList(roomType));
        when(bookingRepository.findBookedRoomIDsByPropertyAndPeriod(propertyId, checkIn, checkOut))
            .thenReturn(Collections.emptyList());
        when(roomRestService.getRoomsByRoomType(roomTypeId))
            .thenReturn(Arrays.asList(room));

        PropertyResponseDTO result = propertyRestService.getPropertyById(propertyId, checkIn, checkOut);

        assertNotNull(result);
        assertNotNull(result.getRoomTypes());
        assertEquals(1, result.getRoomTypes().size());
        verify(bookingRepository).findBookedRoomIDsByPropertyAndPeriod(propertyId, checkIn, checkOut);
    }

    // ============================================
    // CREATE PROPERTY TESTS
    // ============================================

    @Test
    void testCreateProperty_Success() {
        AddRoomRequestDTO roomTypeData = AddRoomRequestDTO.builder()
            .roomTypeName("Deluxe")
            .price(500000)
            .roomTypeDescription("Deluxe Room")
            .capacity(2)
            .facility("AC, TV")
            .floor(1)
            .unitCount(5)
            .build();

        CreatePropertyRequestDTO requestDTO = CreatePropertyRequestDTO.builder()
            .propertyName("New Hotel")
            .type(1)
            .address("Jl. New No. 456")
            .province(1)
            .description("New Description")
            .totalRoom(5)
            .ownerName("New Owner")
            .ownerID(ownerId.toString())
            .roomTypes(Arrays.asList(roomTypeData))
            .build();

        RoomTypeResponseDTO roomTypeResponse = RoomTypeResponseDTO.builder()
            .roomTypeID(roomTypeId.toString())
            .name("Deluxe")
            .build();

        RoomResponseDTO roomResponse = RoomResponseDTO.builder()
            .roomID(UUID.randomUUID().toString())
            .name("101")
            .build();

        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeRestService.createRoomType(any())).thenReturn(roomTypeResponse);
        when(roomRestService.getRoomsByRoomType(any())).thenReturn(Arrays.asList(roomResponse));

        PropertyResponseDTO result = propertyRestService.createProperty(requestDTO);

        assertNotNull(result);
        verify(propertyRepository).save(any(Property.class));
        verify(roomTypeRestService).createRoomType(any());
    }

    @Test
    void testCreateProperty_DuplicateRoomTypeFloorCombination() {
        AddRoomRequestDTO roomType1 = AddRoomRequestDTO.builder()
            .roomTypeName("Deluxe")
            .price(500000)
            .roomTypeDescription("Deluxe Room")
            .capacity(2)
            .facility("AC, TV")
            .floor(1)
            .unitCount(3)
            .build();

        AddRoomRequestDTO roomType2 = AddRoomRequestDTO.builder()
            .roomTypeName("Deluxe")
            .price(600000)
            .roomTypeDescription("Deluxe Room Premium")
            .capacity(3)
            .facility("AC, TV, Fridge")
            .floor(1)
            .unitCount(2)
            .build();

        CreatePropertyRequestDTO requestDTO = CreatePropertyRequestDTO.builder()
            .propertyName("New Hotel")
            .type(1)
            .address("Jl. New No. 456")
            .province(1)
            .description("New Description")
            .totalRoom(5)
            .ownerName("New Owner")
            .ownerID(ownerId.toString())
            .roomTypes(Arrays.asList(roomType1, roomType2))
            .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> propertyRestService.createProperty(requestDTO));
        
        assertTrue(exception.getMessage().contains("Duplikasi kombinasi tipe kamar–lantai"));
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void testCreateProperty_TotalRoomMismatch() {
        AddRoomRequestDTO roomTypeData = AddRoomRequestDTO.builder()
            .roomTypeName("Deluxe")
            .price(500000)
            .roomTypeDescription("Deluxe Room")
            .capacity(2)
            .facility("AC, TV")
            .floor(1)
            .unitCount(5)
            .build();

        CreatePropertyRequestDTO requestDTO = CreatePropertyRequestDTO.builder()
            .propertyName("New Hotel")
            .type(1)
            .address("Jl. New No. 456")
            .province(1)
            .description("New Description")
            .totalRoom(10) // Mismatch: should be 5
            .ownerName("New Owner")
            .ownerID(ownerId.toString())
            .roomTypes(Arrays.asList(roomTypeData))
            .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> propertyRestService.createProperty(requestDTO));
        
        assertTrue(exception.getMessage().contains("Total room tidak sesuai"));
        verify(propertyRepository, never()).save(any(Property.class));
    }

    // ============================================
    // UPDATE PROPERTY TESTS
    // ============================================

    @Test
    void testUpdateProperty_Success() {
        UpdatePropertyRequestDTO requestDTO = UpdatePropertyRequestDTO.builder()
            .propertyName("Updated Hotel")
            .type(2)
            .address("Jl. Updated No. 789")
            .province(2)
            .description("Updated Description")
            .totalRoom(15)
            .activeStatus(1)
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeRestService.getRoomTypesByProperty(propertyId)).thenReturn(Collections.emptyList());

        PropertyResponseDTO result = propertyRestService.updateProperty(propertyId, requestDTO);

        assertNotNull(result);
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    void testUpdateProperty_NotFound() {
        UpdatePropertyRequestDTO requestDTO = UpdatePropertyRequestDTO.builder()
            .propertyName("Updated Hotel")
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        PropertyResponseDTO result = propertyRestService.updateProperty(propertyId, requestDTO);

        assertNull(result);
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void testUpdateProperty_WithRoomTypes() {
        UpdateRoomTypeRequestDTO roomTypeDTO = UpdateRoomTypeRequestDTO.builder()
            .roomTypeID(roomTypeId.toString())
            .capacity(2)
            .price(600000)
            .facility("AC, TV, Fridge")
            .build();

        UpdatePropertyRequestDTO requestDTO = UpdatePropertyRequestDTO.builder()
            .propertyName("Updated Hotel")
            .roomTypes(Arrays.asList(roomTypeDTO))
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeRestService.getRoomTypesByProperty(propertyId)).thenReturn(Collections.emptyList());

        PropertyResponseDTO result = propertyRestService.updateProperty(propertyId, requestDTO);

        assertNotNull(result);
        verify(roomTypeRestService).updateRoomType(eq(roomTypeId), any());
    }

    @Test
    void testUpdateProperty_PartialUpdate() {
        UpdatePropertyRequestDTO requestDTO = UpdatePropertyRequestDTO.builder()
            .propertyName("Updated Name Only")
            .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeRestService.getRoomTypesByProperty(propertyId)).thenReturn(Collections.emptyList());

        PropertyResponseDTO result = propertyRestService.updateProperty(propertyId, requestDTO);

        assertNotNull(result);
        verify(propertyRepository).save(argThat(property -> 
            property.getType() == testProperty.getType() && 
            property.getAddress().equals(testProperty.getAddress())
        ));
    }

    // ============================================
    // DELETE PROPERTY TESTS
    // ============================================

    @Test
    void testDeleteProperty_Success() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(bookingRepository.existsActiveBookingsByPropertyID(propertyId)).thenReturn(false);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        PropertyResponseDTO result = propertyRestService.deleteProperty(propertyId);

        assertNotNull(result);
        verify(propertyRepository).findById(propertyId);
        verify(bookingRepository).existsActiveBookingsByPropertyID(propertyId);
        verify(propertyRepository).save(argThat(property -> property.getActiveStatus() == 0));
    }

    @Test
    void testDeleteProperty_NotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        PropertyResponseDTO result = propertyRestService.deleteProperty(propertyId);

        assertNull(result);
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void testDeleteProperty_HasActiveBookings() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(bookingRepository.existsActiveBookingsByPropertyID(propertyId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> propertyRestService.deleteProperty(propertyId));
        
        assertTrue(exception.getMessage().contains("Cannot delete property"));
        assertTrue(exception.getMessage().contains("active bookings"));
        verify(propertyRepository, never()).save(any(Property.class));
    }
}
