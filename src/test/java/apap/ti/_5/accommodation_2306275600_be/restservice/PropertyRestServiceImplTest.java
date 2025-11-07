package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
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
class PropertyRestServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private RoomTypeRestService roomTypeRestService;

    @Mock
    private RoomRestService roomRestService;

    @InjectMocks
    private PropertyRestServiceImpl propertyRestService;

    private Property testProperty;
    private UUID testOwnerID;
    private String testPropertyID;
    private CreatePropertyRequestDTO createPropertyRequestDTO;
    private UpdatePropertyRequestDTO updatePropertyRequestDTO;

    @BeforeEach
    void setUp() {
        testOwnerID = UUID.randomUUID();
        testPropertyID = "HOT-1234-001";
        
        testProperty = Property.builder()
                .propertyID(testPropertyID)
                .propertyName("Test Hotel")
                .type(1)
                .address("Jl. Test No. 123")
                .province(1)
                .description("Test Description")
                .totalRoom(10)
                .activeStatus(1)
                .income(0)
                .ownerName("Test Owner")
                .ownerID(testOwnerID)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void whenGetAllProperties_thenReturnPropertyList() {
        // Arrange
        Property property2 = Property.builder()
                .propertyID("VIL-5678-002")
                .propertyName("Test Villa")
                .type(2)
                .address("Jl. Villa No. 456")
                .province(2)
                .description("Villa Description")
                .totalRoom(5)
                .activeStatus(1)
                .income(0)
                .ownerName("Villa Owner")
                .ownerID(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        List<Property> properties = Arrays.asList(testProperty, property2);
        when(propertyRepository.findAll()).thenReturn(properties);

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Hotel", result.get(0).getPropertyName());
        assertEquals("Test Villa", result.get(1).getPropertyName());
        assertEquals("Hotel", result.get(0).getTypeName());
        assertEquals("Villa", result.get(1).getTypeName());
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllPropertiesEmpty_thenReturnEmptyList() {
        // Arrange
        when(propertyRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void whenGetPropertiesByOwner_thenReturnPropertyList() {
        // Arrange
        List<Property> properties = Collections.singletonList(testProperty);
        when(propertyRepository.findByOwnerID(testOwnerID)).thenReturn(properties);

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getPropertiesByOwner(testOwnerID.toString());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getPropertyName());
        assertEquals(testOwnerID.toString(), result.get(0).getOwnerID());
        verify(propertyRepository, times(1)).findByOwnerID(testOwnerID);
    }

    @Test
    void whenGetPropertiesByOwnerNotFound_thenReturnEmptyList() {
        // Arrange
        UUID randomOwnerID = UUID.randomUUID();
        when(propertyRepository.findByOwnerID(randomOwnerID)).thenReturn(new ArrayList<>());

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getPropertiesByOwner(randomOwnerID.toString());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(propertyRepository, times(1)).findByOwnerID(randomOwnerID);
    }

    @Test
    void whenGetPropertyByIdExists_thenReturnPropertyWithRoomTypes() {
        // Arrange
        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));

        RoomTypeResponseDTO roomType = RoomTypeResponseDTO.builder()
                .roomTypeID("RT-001")
                .name("Deluxe")
                .floor(1)
                .capacity(2)
                .price(500000)
                .facility("AC, TV")
                .description("Deluxe Room")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRestService.getRoomTypesByProperty(testPropertyID))
                .thenReturn(Collections.singletonList(roomType));

        RoomResponseDTO room = RoomResponseDTO.builder()
                .roomID("R-001")
                .roomTypeID("RT-001")
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        when(roomRestService.getRoomsByRoomType("RT-001"))
                .thenReturn(Collections.singletonList(room));

        // Act
        PropertyResponseDTO result = propertyRestService.getPropertyById(testPropertyID);

        // Assert
        assertNotNull(result);
        assertEquals(testPropertyID, result.getPropertyID());
        assertEquals("Test Hotel", result.getPropertyName());
        assertNotNull(result.getRoomTypes());
        assertEquals(1, result.getRoomTypes().size());
        assertEquals("Deluxe", result.getRoomTypes().get(0).getRoomTypeName());
        assertEquals(1, result.getRoomTypes().get(0).getListRoom().size());
        verify(propertyRepository, times(1)).findById(testPropertyID);
        verify(roomTypeRestService, times(1)).getRoomTypesByProperty(testPropertyID);
        verify(roomRestService, times(1)).getRoomsByRoomType("RT-001");
    }

    @Test
    void whenGetPropertyByIdNotFound_thenReturnNull() {
        // Arrange
        when(propertyRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act
        PropertyResponseDTO result = propertyRestService.getPropertyById("INVALID-ID");

        // Assert
        assertNull(result);
        verify(propertyRepository, times(1)).findById("INVALID-ID");
        verify(roomTypeRestService, never()).getRoomTypesByProperty(anyString());
    }

    @Test
    void whenCreateProperty_thenReturnCreatedProperty() {
        // Arrange
        AddRoomRequestDTO roomTypeRequest = AddRoomRequestDTO.builder()
                .roomTypeName("Deluxe")
                .floor(1)
                .capacity(2)
                .price(500000)
                .facility("AC, TV")
                .roomTypeDescription("Deluxe Room")
                .unitCount(2)
                .build();

        createPropertyRequestDTO = CreatePropertyRequestDTO.builder()
                .propertyName("New Hotel")
                .type(1)
                .address("Jl. New Hotel No. 1")
                .province(1)
                .description("New Hotel Description")
                .totalRoom(2)
                .ownerName("New Owner")
                .ownerID(testOwnerID.toString())
                .roomTypes(Collections.singletonList(roomTypeRequest))
                .build();

        Property savedProperty = Property.builder()
                .propertyID("HOT-" + testOwnerID.toString().substring(testOwnerID.toString().length() - 4) + "-001")
                .propertyName("New Hotel")
                .type(1)
                .address("Jl. New Hotel No. 1")
                .province(1)
                .description("New Hotel Description")
                .totalRoom(2)
                .activeStatus(1)
                .income(0)
                .ownerName("New Owner")
                .ownerID(testOwnerID)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

        RoomTypeResponseDTO roomTypeResponse = RoomTypeResponseDTO.builder()
                .roomTypeID("RT-001")
                .name("Deluxe")
                .floor(1)
                .capacity(2)
                .price(500000)
                .facility("AC, TV")
                .description("Deluxe Room")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class)))
                .thenReturn(roomTypeResponse);

        RoomResponseDTO roomResponse = RoomResponseDTO.builder()
                .roomID("R-001")
                .roomTypeID("RT-001")
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        when(roomRestService.createRoom(any(AddRoomRequestDTO.class)))
                .thenReturn(roomResponse);

        // Act
        PropertyResponseDTO result = propertyRestService.createProperty(createPropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("New Hotel", result.getPropertyName());
        assertEquals(1, result.getType());
        assertEquals("Hotel", result.getTypeName());
        assertEquals(2, result.getTotalRoom());
        assertEquals(1, result.getActiveStatus());
        assertEquals("Active", result.getActiveStatusName());
        assertNotNull(result.getRoomTypes());
        assertEquals(1, result.getRoomTypes().size());
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(roomTypeRestService, times(1)).createRoomType(any(CreateRoomTypeRequestDTO.class));
        verify(roomRestService, times(2)).createRoom(any(AddRoomRequestDTO.class));
    }

    @Test
    void whenCreatePropertyWithDuplicateRoomTypeFloor_thenThrowException() {
        // Arrange
        AddRoomRequestDTO roomType1 = AddRoomRequestDTO.builder()
                .roomTypeName("Deluxe")
                .floor(1)
                .capacity(2)
                .price(500000)
                .facility("AC, TV")
                .roomTypeDescription("Deluxe Room")
                .unitCount(2)
                .build();

        AddRoomRequestDTO roomType2 = AddRoomRequestDTO.builder()
                .roomTypeName("Deluxe")
                .floor(1)
                .capacity(3)
                .price(600000)
                .facility("AC, TV, Mini Bar")
                .roomTypeDescription("Deluxe Room Plus")
                .unitCount(3)
                .build();

        createPropertyRequestDTO = CreatePropertyRequestDTO.builder()
                .propertyName("New Hotel")
                .type(1)
                .address("Jl. New Hotel No. 1")
                .province(1)
                .description("New Hotel Description")
                .totalRoom(5)
                .ownerName("New Owner")
                .ownerID(testOwnerID.toString())
                .roomTypes(Arrays.asList(roomType1, roomType2))
                .build();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            propertyRestService.createProperty(createPropertyRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Duplikasi kombinasi tipe kamar–lantai tidak diperbolehkan"));
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void whenCreatePropertyWithMismatchedTotalRoom_thenThrowException() {
        // Arrange
        AddRoomRequestDTO roomTypeRequest = AddRoomRequestDTO.builder()
                .roomTypeName("Deluxe")
                .floor(1)
                .capacity(2)
                .price(500000)
                .facility("AC, TV")
                .roomTypeDescription("Deluxe Room")
                .unitCount(2)
                .build();

        createPropertyRequestDTO = CreatePropertyRequestDTO.builder()
                .propertyName("New Hotel")
                .type(1)
                .address("Jl. New Hotel No. 1")
                .province(1)
                .description("New Hotel Description")
                .totalRoom(5) // Mismatch: unitCount = 2, but totalRoom = 5
                .ownerName("New Owner")
                .ownerID(testOwnerID.toString())
                .roomTypes(Collections.singletonList(roomTypeRequest))
                .build();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            propertyRestService.createProperty(createPropertyRequestDTO);
        });

        assertTrue(exception.getMessage().contains("Total room tidak sesuai"));
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void whenCreatePropertyTypeVilla_thenGenerateCorrectID() {
        // Arrange
        AddRoomRequestDTO roomTypeRequest = AddRoomRequestDTO.builder()
                .roomTypeName("Standard")
                .floor(1)
                .capacity(2)
                .price(300000)
                .facility("AC")
                .roomTypeDescription("Standard Room")
                .unitCount(1)
                .build();

        createPropertyRequestDTO = CreatePropertyRequestDTO.builder()
                .propertyName("Test Villa")
                .type(2) // Villa
                .address("Jl. Villa No. 1")
                .province(1)
                .description("Villa Description")
                .totalRoom(1)
                .ownerName("Villa Owner")
                .ownerID(testOwnerID.toString())
                .roomTypes(Collections.singletonList(roomTypeRequest))
                .build();

        Property savedProperty = Property.builder()
                .propertyID("VIL-" + testOwnerID.toString().substring(testOwnerID.toString().length() - 4) + "-001")
                .propertyName("Test Villa")
                .type(2)
                .address("Jl. Villa No. 1")
                .province(1)
                .description("Villa Description")
                .totalRoom(1)
                .activeStatus(1)
                .income(0)
                .ownerName("Villa Owner")
                .ownerID(testOwnerID)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

        RoomTypeResponseDTO roomTypeResponse = RoomTypeResponseDTO.builder()
                .roomTypeID("RT-001")
                .name("Standard")
                .floor(1)
                .capacity(2)
                .price(300000)
                .facility("AC")
                .description("Standard Room")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class)))
                .thenReturn(roomTypeResponse);

        RoomResponseDTO roomResponse = RoomResponseDTO.builder()
                .roomID("R-001")
                .roomTypeID("RT-001")
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        when(roomRestService.createRoom(any(AddRoomRequestDTO.class)))
                .thenReturn(roomResponse);

        // Act
        PropertyResponseDTO result = propertyRestService.createProperty(createPropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getPropertyID().startsWith("VIL-"));
        assertEquals("Villa", result.getTypeName());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void whenCreatePropertyTypeApartemen_thenGenerateCorrectID() {
        // Arrange
        AddRoomRequestDTO roomTypeRequest = AddRoomRequestDTO.builder()
                .roomTypeName("Studio")
                .floor(1)
                .capacity(1)
                .price(400000)
                .facility("AC, Kitchen")
                .roomTypeDescription("Studio Apartment")
                .unitCount(1)
                .build();

        createPropertyRequestDTO = CreatePropertyRequestDTO.builder()
                .propertyName("Test Apartment")
                .type(3) // Apartemen
                .address("Jl. Apartment No. 1")
                .province(1)
                .description("Apartment Description")
                .totalRoom(1)
                .ownerName("Apartment Owner")
                .ownerID(testOwnerID.toString())
                .roomTypes(Collections.singletonList(roomTypeRequest))
                .build();

        Property savedProperty = Property.builder()
                .propertyID("APT-" + testOwnerID.toString().substring(testOwnerID.toString().length() - 4) + "-001")
                .propertyName("Test Apartment")
                .type(3)
                .address("Jl. Apartment No. 1")
                .province(1)
                .description("Apartment Description")
                .totalRoom(1)
                .activeStatus(1)
                .income(0)
                .ownerName("Apartment Owner")
                .ownerID(testOwnerID)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

        RoomTypeResponseDTO roomTypeResponse = RoomTypeResponseDTO.builder()
                .roomTypeID("RT-001")
                .name("Studio")
                .floor(1)
                .capacity(1)
                .price(400000)
                .facility("AC, Kitchen")
                .description("Studio Apartment")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class)))
                .thenReturn(roomTypeResponse);

        RoomResponseDTO roomResponse = RoomResponseDTO.builder()
                .roomID("R-001")
                .roomTypeID("RT-001")
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        when(roomRestService.createRoom(any(AddRoomRequestDTO.class)))
                .thenReturn(roomResponse);

        // Act
        PropertyResponseDTO result = propertyRestService.createProperty(createPropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getPropertyID().startsWith("APT-"));
        assertEquals("Apartemen", result.getTypeName());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void whenUpdatePropertyExists_thenReturnUpdatedProperty() {
        // Arrange
        updatePropertyRequestDTO = UpdatePropertyRequestDTO.builder()
                .propertyName("Updated Hotel Name")
                .type(1)
                .address("Updated Address")
                .province(2)
                .description("Updated Description")
                .totalRoom(15)
                .activeStatus(1)
                .build();

        Property updatedProperty = testProperty.toBuilder()
                .propertyName("Updated Hotel Name")
                .address("Updated Address")
                .province(2)
                .description("Updated Description")
                .totalRoom(15)
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);
        when(roomTypeRestService.getRoomTypesByProperty(testPropertyID)).thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyRestService.updateProperty(testPropertyID, updatePropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Hotel Name", result.getPropertyName());
        assertEquals("Updated Address", result.getAddress());
        assertEquals(2, result.getProvince());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(15, result.getTotalRoom());
        verify(propertyRepository, times(1)).findById(testPropertyID);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void whenUpdatePropertyWithPartialData_thenUpdateOnlyProvidedFields() {
        // Arrange
        updatePropertyRequestDTO = UpdatePropertyRequestDTO.builder()
                .propertyName("Updated Name Only")
                .build();

        Property updatedProperty = testProperty.toBuilder()
                .propertyName("Updated Name Only")
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);
        when(roomTypeRestService.getRoomTypesByProperty(testPropertyID)).thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyRestService.updateProperty(testPropertyID, updatePropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name Only", result.getPropertyName());
        assertEquals(testProperty.getAddress(), result.getAddress()); // Unchanged
        assertEquals(testProperty.getProvince(), result.getProvince()); // Unchanged
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void whenUpdatePropertyWithRoomTypes_thenUpdateRoomTypesAlso() {
        // Arrange
        UpdateRoomTypeRequestDTO roomTypeUpdate = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID("RT-001")
                .capacity(2)
                .price(600000)
                .facility("AC, TV, Mini Bar")
                .description("Updated Deluxe Room")
                .build();

        updatePropertyRequestDTO = UpdatePropertyRequestDTO.builder()
                .propertyName("Updated Hotel")
                .roomTypes(Collections.singletonList(roomTypeUpdate))
                .build();

        Property updatedProperty = testProperty.toBuilder()
                .propertyName("Updated Hotel")
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);

        RoomTypeResponseDTO roomTypeResponse = RoomTypeResponseDTO.builder()
                .roomTypeID("RT-001")
                .name("Updated Deluxe")
                .floor(1)
                .capacity(2)
                .price(600000)
                .facility("AC, TV")
                .description("Deluxe Room")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRestService.getRoomTypesByProperty(testPropertyID))
                .thenReturn(Collections.singletonList(roomTypeResponse));
        when(roomRestService.getRoomsByRoomType("RT-001")).thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyRestService.updateProperty(testPropertyID, updatePropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Hotel", result.getPropertyName());
        verify(roomTypeRestService, times(1)).updateRoomType(eq("RT-001"), any(UpdateRoomTypeRequestDTO.class));
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void whenUpdatePropertyNotFound_thenReturnNull() {
        // Arrange
        updatePropertyRequestDTO = UpdatePropertyRequestDTO.builder()
                .propertyName("Updated Name")
                .build();

        when(propertyRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act
        PropertyResponseDTO result = propertyRestService.updateProperty("INVALID-ID", updatePropertyRequestDTO);

        // Assert
        assertNull(result);
        verify(propertyRepository, times(1)).findById("INVALID-ID");
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void whenDeletePropertyExists_thenSetStatusToInactive() {
        // Arrange
        Property deletedProperty = testProperty.toBuilder()
                .activeStatus(0)
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(deletedProperty);

        // Act
        PropertyResponseDTO result = propertyRestService.deleteProperty(testPropertyID);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getActiveStatus());
        assertEquals("Non-Active", result.getActiveStatusName());
        verify(propertyRepository, times(1)).findById(testPropertyID);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void whenDeletePropertyNotFound_thenReturnNull() {
        // Arrange
        when(propertyRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act
        PropertyResponseDTO result = propertyRestService.deleteProperty("INVALID-ID");

        // Assert
        assertNull(result);
        verify(propertyRepository, times(1)).findById("INVALID-ID");
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void whenConvertPropertyWithType1_thenReturnHotel() {
        // Arrange
        testProperty.setType(1);
        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(testProperty));

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        // Assert
        assertEquals("Hotel", result.get(0).getTypeName());
    }

    @Test
    void whenConvertPropertyWithType2_thenReturnVilla() {
        // Arrange
        testProperty.setType(2);
        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(testProperty));

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        // Assert
        assertEquals("Villa", result.get(0).getTypeName());
    }

    @Test
    void whenConvertPropertyWithType3_thenReturnApartemen() {
        // Arrange
        testProperty.setType(3);
        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(testProperty));

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        // Assert
        assertEquals("Apartemen", result.get(0).getTypeName());
    }

    @Test
    void whenConvertPropertyWithUnknownType_thenReturnUnknown() {
        // Arrange
        testProperty.setType(99);
        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(testProperty));

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        // Assert
        assertEquals("Unknown", result.get(0).getTypeName());
    }

    @Test
    void whenConvertPropertyWithActiveStatus0_thenReturnNonActive() {
        // Arrange
        testProperty.setActiveStatus(0);
        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(testProperty));

        // Act
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();

        // Assert
        assertEquals("Non-Active", result.get(0).getActiveStatusName());
    }

    @Test
    void whenCreatePropertyWithMultipleRoomTypes_thenCreateAllRoomTypesAndRooms() {
        // Arrange
        AddRoomRequestDTO roomType1 = AddRoomRequestDTO.builder()
                .roomTypeName("Deluxe")
                .floor(1)
                .capacity(2)
                .price(500000)
                .facility("AC, TV")
                .roomTypeDescription("Deluxe Room")
                .unitCount(2)
                .build();

        AddRoomRequestDTO roomType2 = AddRoomRequestDTO.builder()
                .roomTypeName("Suite")
                .floor(2)
                .capacity(4)
                .price(800000)
                .facility("AC, TV, Mini Bar")
                .roomTypeDescription("Suite Room")
                .unitCount(1)
                .build();

        createPropertyRequestDTO = CreatePropertyRequestDTO.builder()
                .propertyName("Multi Room Hotel")
                .type(1)
                .address("Jl. Multi No. 1")
                .province(1)
                .description("Hotel with multiple room types")
                .totalRoom(3)
                .ownerName("Multi Owner")
                .ownerID(testOwnerID.toString())
                .roomTypes(Arrays.asList(roomType1, roomType2))
                .build();

        Property savedProperty = Property.builder()
                .propertyID("HOT-" + testOwnerID.toString().substring(testOwnerID.toString().length() - 4) + "-001")
                .propertyName("Multi Room Hotel")
                .type(1)
                .address("Jl. Multi No. 1")
                .province(1)
                .description("Hotel with multiple room types")
                .totalRoom(3)
                .activeStatus(1)
                .income(0)
                .ownerName("Multi Owner")
                .ownerID(testOwnerID)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

        RoomTypeResponseDTO roomTypeResponse1 = RoomTypeResponseDTO.builder()
                .roomTypeID("RT-001")
                .name("Deluxe")
                .floor(1)
                .capacity(2)
                .price(500000)
                .facility("AC, TV")
                .description("Deluxe Room")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        RoomTypeResponseDTO roomTypeResponse2 = RoomTypeResponseDTO.builder()
                .roomTypeID("RT-002")
                .name("Suite")
                .floor(2)
                .capacity(4)
                .price(800000)
                .facility("AC, TV, Mini Bar")
                .description("Suite Room")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class)))
                .thenReturn(roomTypeResponse1, roomTypeResponse2);

        RoomResponseDTO roomResponse = RoomResponseDTO.builder()
                .roomID("R-001")
                .roomTypeID("RT-001")
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        when(roomRestService.createRoom(any(AddRoomRequestDTO.class)))
                .thenReturn(roomResponse);

        // Act
        PropertyResponseDTO result = propertyRestService.createProperty(createPropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Multi Room Hotel", result.getPropertyName());
        assertEquals(3, result.getTotalRoom());
        assertNotNull(result.getRoomTypes());
        assertEquals(2, result.getRoomTypes().size());
        verify(roomTypeRestService, times(2)).createRoomType(any(CreateRoomTypeRequestDTO.class));
        verify(roomRestService, times(3)).createRoom(any(AddRoomRequestDTO.class)); // 2 + 1 rooms
    }

    @Test
    void whenUpdatePropertyChangeType_thenTypeIsUpdated() {
        // Arrange
        updatePropertyRequestDTO = UpdatePropertyRequestDTO.builder()
                .type(2) // Change from Hotel to Villa
                .build();

        Property updatedProperty = testProperty.toBuilder()
                .type(2)
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);
        when(roomTypeRestService.getRoomTypesByProperty(testPropertyID)).thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyRestService.updateProperty(testPropertyID, updatePropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getType());
        assertEquals("Villa", result.getTypeName());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void whenUpdatePropertyChangeActiveStatus_thenActiveStatusIsUpdated() {
        // Arrange
        updatePropertyRequestDTO = UpdatePropertyRequestDTO.builder()
                .activeStatus(0) // Change to inactive
                .build();

        Property updatedProperty = testProperty.toBuilder()
                .activeStatus(0)
                .updatedDate(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(testPropertyID)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);
        when(roomTypeRestService.getRoomTypesByProperty(testPropertyID)).thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyRestService.updateProperty(testPropertyID, updatePropertyRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getActiveStatus());
        assertEquals("Non-Active", result.getActiveStatusName());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }
}
