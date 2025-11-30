package apap.ti._5.accommodation_2306275600_be.restcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import apap.ti._5.accommodation_2306275600_be.restcontroller.PropertyRestController.AddRoomTypeRequestWrapper;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.PropertyRestServiceRBAC;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.RoomRestServiceRBAC;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.RoomTypeRestServiceRBAC;

@ExtendWith(MockitoExtension.class)
class PropertyRestControllerTest {

    @Mock
    private PropertyRestServiceRBAC propertyRestService;

    @Mock
    private RoomTypeRestServiceRBAC roomTypeRestService;

    @Mock
    private RoomRestServiceRBAC roomRestService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PropertyRestController propertyRestController;

    private UUID testPropertyId;
    private UUID testOwnerId;
    private PropertyResponseDTO mockProperty;
    private CreatePropertyRequestDTO mockCreatePropertyRequest;
    private UpdatePropertyRequestDTO mockUpdatePropertyRequest;

    @BeforeEach
    void setUp() {
        testPropertyId = UUID.randomUUID();
        testOwnerId = UUID.randomUUID();

        // Setup mock property
        mockProperty = PropertyResponseDTO.builder()
                .propertyID(testPropertyId.toString())
                .propertyName("Test Hotel")
                .type(1) // Hotel
                .address("Test Address")
                .province(1)
                .description("Test Description")
                .totalRoom(10)
                .activeStatus(1)
                .ownerName("Test Owner")
                .ownerID(testOwnerId.toString())
                .build();

        // Setup mock create request
        AddRoomRequestDTO roomRequest = AddRoomRequestDTO.builder()
                .roomTypeName("Single Room")
                .floor(1)
                .unitCount(5)
                .price(500000)
                .capacity(2)
                .build();

        mockCreatePropertyRequest = CreatePropertyRequestDTO.builder()
                .propertyName("Test Hotel")
                .type(1)
                .address("Test Address")
                .province(1)
                .description("Test Description")
                .totalRoom(5)
                .ownerName("Test Owner")
                .ownerID(testOwnerId.toString())
                .roomTypes(Arrays.asList(roomRequest))
                .build();

        // Setup mock update request
        mockUpdatePropertyRequest = UpdatePropertyRequestDTO.builder()
                .propertyName("Updated Hotel")
                .type(1)
                .address("Updated Address")
                .province(1)
                .description("Updated Description")
                .totalRoom(15)
                .activeStatus(1)
                .build();
    }

    // ========== CREATE PROPERTY TESTS ==========

    @Test
    void testCreateProperty_Success() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatus());
        assertEquals(mockProperty, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("berhasil ditambahkan"));
        verify(propertyRestService, times(1)).createProperty(any(CreatePropertyRequestDTO.class));
    }

    @Test
    void testCreateProperty_WithValidationErrors() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        FieldError error = new FieldError("createPropertyRequestDTO", "propertyName", "Property name is required");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error));

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Property name is required"));
        verify(propertyRestService, never()).createProperty(any());
    }

    @Test
    void testCreateProperty_WithNoRoomTypes() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setRoomTypes(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("minimal 1 tipe kamar"));
        verify(propertyRestService, never()).createProperty(any());
    }

    @Test
    void testCreateProperty_WithEmptyRoomTypes() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setRoomTypes(new ArrayList<>());

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("minimal 1 tipe kamar"));
    }

    @Test
    void testCreateProperty_WithInvalidUnitCount() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.getRoomTypes().get(0).setUnitCount(0);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("minimal 1 kamar"));
    }

    @Test
    void testCreateProperty_WithNullUnitCount() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.getRoomTypes().get(0).setUnitCount(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("minimal 1 kamar"));
    }

    @Test
    void testCreateProperty_WithInvalidRoomTypeName() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.getRoomTypes().get(0).setRoomTypeName("Invalid Room Type");

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tidak sesuai dengan tipe properti"));
    }

    @Test
    void testCreateProperty_ServiceReturnsNull() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Gagal Dibuat"));
    }

    @Test
    void testCreateProperty_ServiceThrowsException() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("gagal dibuat"));
    }

    @Test
    void testCreateProperty_VillaWithValidRoomType() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setType(2); // Villa
        mockCreatePropertyRequest.getRoomTypes().get(0).setRoomTypeName("Luxury");
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateProperty_ApartmentWithValidRoomType() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setType(3); // Apartment
        mockCreatePropertyRequest.getRoomTypes().get(0).setRoomTypeName("Studio");
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // ========== GET ALL PROPERTIES TESTS ==========

    @Test
    void testGetAllProperties_Success() {
        // Arrange
        List<PropertyResponseDTO> properties = Arrays.asList(mockProperty);
        when(propertyRestService.getAllProperties()).thenReturn(properties);

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getAllProperties(null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("Berhasil Ditemukan"));
        verify(propertyRestService, times(1)).getAllProperties();
        verify(propertyRestService, never()).getFilteredProperties(any(), any(), any());
    }

    @Test
    void testGetAllProperties_WithNameFilter() {
        // Arrange
        List<PropertyResponseDTO> properties = Arrays.asList(mockProperty);
        when(propertyRestService.getFilteredProperties("Test", null, null)).thenReturn(properties);

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getAllProperties("Test", null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        verify(propertyRestService, times(1)).getFilteredProperties("Test", null, null);
        verify(propertyRestService, never()).getAllProperties();
    }

    @Test
    void testGetAllProperties_WithTypeFilter() {
        // Arrange
        List<PropertyResponseDTO> properties = Arrays.asList(mockProperty);
        when(propertyRestService.getFilteredProperties(null, 1, null)).thenReturn(properties);

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getAllProperties(null, 1, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(propertyRestService, times(1)).getFilteredProperties(null, 1, null);
    }

    @Test
    void testGetAllProperties_WithProvinceFilter() {
        // Arrange
        List<PropertyResponseDTO> properties = Arrays.asList(mockProperty);
        when(propertyRestService.getFilteredProperties(null, null, 1)).thenReturn(properties);

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getAllProperties(null, null, 1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(propertyRestService, times(1)).getFilteredProperties(null, null, 1);
    }

    @Test
    void testGetAllProperties_WithAllFilters() {
        // Arrange
        List<PropertyResponseDTO> properties = Arrays.asList(mockProperty);
        when(propertyRestService.getFilteredProperties("Test", 1, 1)).thenReturn(properties);

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getAllProperties("Test", 1, 1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(propertyRestService, times(1)).getFilteredProperties("Test", 1, 1);
    }

    @Test
    void testGetAllProperties_EmptyList() {
        // Arrange
        when(propertyRestService.getAllProperties()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getAllProperties(null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getData().size());
    }

    // ========== GET PROPERTY BY ID TESTS ==========

    @Test
    void testGetProperty_Success() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getProperty(testPropertyId, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProperty, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Berhasil Ditemukan"));
        verify(propertyRestService, times(1)).getPropertyById(testPropertyId);
    }

    @Test
    void testGetProperty_WithDateFilters() {
        // Arrange
        String checkIn = "2025-12-15T14:00:00";
        String checkOut = "2025-12-20T12:00:00";
        when(propertyRestService.getPropertyById(eq(testPropertyId), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getProperty(testPropertyId, checkIn, checkOut);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(propertyRestService, times(1)).getPropertyById(eq(testPropertyId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetProperty_NotFound() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getProperty(testPropertyId, null, null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Tidak Ditemukan"));
    }

    @Test
    void testGetProperty_ExceptionThrown() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getProperty(testPropertyId, null, null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("kesalahan pada server"));
    }

    // ========== GET PROPERTIES BY OWNER TESTS ==========

    @Test
    void testGetPropertiesByOwner_Success() {
        // Arrange
        List<PropertyResponseDTO> properties = Arrays.asList(mockProperty);
        when(propertyRestService.getPropertiesByOwner(testOwnerId)).thenReturn(properties);

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getPropertiesByOwner(testOwnerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("Owner Berhasil Ditemukan"));
        verify(propertyRestService, times(1)).getPropertiesByOwner(testOwnerId);
    }

    @Test
    void testGetPropertiesByOwner_EmptyList() {
        // Arrange
        when(propertyRestService.getPropertiesByOwner(testOwnerId)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = 
            propertyRestController.getPropertiesByOwner(testOwnerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getData().size());
    }

    // ========== GET UPDATE PROPERTY FORM TESTS ==========

    @Test
    void testGetUpdatePropertyForm_Success() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getUpdatePropertyForm(testPropertyId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProperty, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("berhasil ditemukan untuk update"));
    }

    @Test
    void testGetUpdatePropertyForm_NotFound() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getUpdatePropertyForm(testPropertyId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Tidak Ditemukan"));
    }

    @Test
    void testGetUpdatePropertyForm_ExceptionThrown() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getUpdatePropertyForm(testPropertyId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("kesalahan pada server"));
    }

    // ========== GET ADD ROOM TYPE FORM TESTS ==========

    @Test
    void testGetAddRoomTypeForm_Success() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getAddRoomTypeForm(testPropertyId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProperty, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Form Add Room Type Siap Digunakan"));
    }

    @Test
    void testGetAddRoomTypeForm_PropertyNotFound() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getAddRoomTypeForm(testPropertyId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Tidak Ditemukan"));
    }

    @Test
    void testGetAddRoomTypeForm_InactiveProperty() {
        // Arrange
        PropertyResponseDTO inactiveProperty = PropertyResponseDTO.builder()
                .propertyID(testPropertyId.toString())
                .propertyName("Inactive Hotel")
                .type(1)
                .activeStatus(0)
                .build();
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(inactiveProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.getAddRoomTypeForm(testPropertyId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Tidak dapat menambah tipe kamar pada property yang tidak aktif"));
    }

    // ========== UPDATE PROPERTY TESTS ==========

    @Test
    void testUpdateProperty_Success() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.updateProperty(eq(testPropertyId), any(UpdatePropertyRequestDTO.class)))
            .thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.updateProperty(testPropertyId, mockUpdatePropertyRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProperty, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Berhasil Diupdate"));
        verify(propertyRestService, times(1)).updateProperty(eq(testPropertyId), any(UpdatePropertyRequestDTO.class));
    }

    @Test
    void testUpdateProperty_WithValidationErrors() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        FieldError error = new FieldError("updatePropertyRequestDTO", "propertyName", "Property name is required");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error));

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.updateProperty(testPropertyId, mockUpdatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Property name is required"));
        verify(propertyRestService, never()).updateProperty(any(), any());
    }

    @Test
    void testUpdateProperty_NotFound() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.updateProperty(eq(testPropertyId), any(UpdatePropertyRequestDTO.class)))
            .thenReturn(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.updateProperty(testPropertyId, mockUpdatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Tidak Ditemukan"));
    }

    @Test
    void testUpdateProperty_ExceptionThrown() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.updateProperty(eq(testPropertyId), any(UpdatePropertyRequestDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.updateProperty(testPropertyId, mockUpdatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("kesalahan pada server"));
    }

    // ========== ADD ROOM TYPE WITH ROOMS TESTS ==========

    @Test
    void testAddRoomTypeWithRooms_Success() {
        // Arrange
        CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                .name("Double Room")
                .floor(2)
                .unitCount(3)
                .price(750000)
                .capacity(2)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomTypeDTO));

        RoomTypeResponseDTO mockRoomType = RoomTypeResponseDTO.builder()
                .roomTypeID(UUID.randomUUID().toString())
                .name("Double Room")
                .floor(2)
                .build();

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);
        when(roomTypeRestService.getRoomTypesByProperty(testPropertyId)).thenReturn(new ArrayList<>());
        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class))).thenReturn(mockRoomType);
        when(propertyRestService.updateProperty(eq(testPropertyId), any(UpdatePropertyRequestDTO.class)))
            .thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("berhasil ditambahkan"));
        verify(roomTypeRestService, times(1)).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testAddRoomTypeWithRooms_ValidationErrors() {
        // Arrange
        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        FieldError error = new FieldError("wrapper", "propertyId", "Property ID is required");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error));

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Property ID is required"));
    }

    @Test
    void testAddRoomTypeWithRooms_PropertyNotFound() {
        // Arrange
        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(new CreateRoomTypeRequestDTO()));

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(null);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tidak ditemukan"));
    }

    @Test
    void testAddRoomTypeWithRooms_InactiveProperty() {
        // Arrange
        PropertyResponseDTO inactiveProperty = PropertyResponseDTO.builder()
                .propertyID(testPropertyId.toString())
                .propertyName("Inactive Hotel")
                .type(1)
                .activeStatus(0)
                .build();
        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(new CreateRoomTypeRequestDTO()));

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(inactiveProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tidak aktif"));
    }

    @Test
    void testAddRoomTypeWithRooms_EmptyRoomTypes() {
        // Arrange
        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(new ArrayList<>());

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Minimal harus ada 1 tipe kamar"));
    }

    @Test
    void testAddRoomTypeWithRooms_NullRoomTypes() {
        // Arrange
        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(null);

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Minimal harus ada 1 tipe kamar"));
    }

    @Test
    void testAddRoomTypeWithRooms_InvalidUnitCount() {
        // Arrange
        CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                .name("Double Room")
                .floor(2)
                .unitCount(0)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomTypeDTO));

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("minimal 1 unit kamar"));
    }

    @Test
    void testAddRoomTypeWithRooms_NullUnitCount() {
        // Arrange
        CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                .name("Double Room")
                .floor(2)
                .unitCount(null)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomTypeDTO));

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("minimal 1 unit kamar"));
    }

    @Test
    void testAddRoomTypeWithRooms_InvalidRoomTypeName() {
        // Arrange
        CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                .name("Invalid Room Type")
                .floor(2)
                .unitCount(3)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomTypeDTO));

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tidak sesuai dengan tipe properti"));
    }

    @Test
    void testAddRoomTypeWithRooms_DuplicateInRequest() {
        // Arrange
        CreateRoomTypeRequestDTO roomType1 = CreateRoomTypeRequestDTO.builder()
                .name("Double Room")
                .floor(2)
                .unitCount(3)
                .build();

        CreateRoomTypeRequestDTO roomType2 = CreateRoomTypeRequestDTO.builder()
                .name("Double Room")
                .floor(2)
                .unitCount(2)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomType1, roomType2));

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Duplikasi kombinasi tipe kamar–lantai dalam form"));
    }

    @Test
    void testAddRoomTypeWithRooms_DuplicateWithExisting() {
        // Arrange
        CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                .name("Double Room")
                .floor(2)
                .unitCount(3)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomTypeDTO));

        RoomTypeResponseDTO existingRoomType = RoomTypeResponseDTO.builder()
                .name("Double Room")
                .floor(2)
                .build();

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);
        when(roomTypeRestService.getRoomTypesByProperty(testPropertyId))
            .thenReturn(Arrays.asList(existingRoomType));

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Duplikasi kombinasi property-tipe kamar-lantai"));
    }

    @Test
    void testAddRoomTypeWithRooms_ExceptionThrown() {
        // Arrange
        CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                .name("Double Room")
                .floor(2)
                .unitCount(3)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomTypeDTO));

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Gagal menambah tipe kamar"));
    }

    // ========== DELETE PROPERTY TESTS ==========

    @Test
    void testDeleteProperty_Success() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);
        when(propertyRestService.deleteProperty(testPropertyId)).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.deleteProperty(testPropertyId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProperty, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Berhasil Dihapus"));
        verify(propertyRestService, times(1)).deleteProperty(testPropertyId);
    }

    @Test
    void testDeleteProperty_NotFound() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(null);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.deleteProperty(testPropertyId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Tidak Ditemukan"));
        verify(propertyRestService, never()).deleteProperty(any());
    }

    @Test
    void testDeleteProperty_ExceptionThrown() {
        // Arrange
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(mockProperty);
        when(propertyRestService.deleteProperty(testPropertyId))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.deleteProperty(testPropertyId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("kesalahan pada server"));
    }

    // ========== ROOM TYPE VALIDATION TESTS ==========

    @Test
    void testCreateProperty_HotelWithAllValidRoomTypes() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setType(1); // Hotel
        
        List<AddRoomRequestDTO> validRoomTypes = Arrays.asList(
            createAddRoomRequest("Single Room", 1, 2),
            createAddRoomRequest("Double Room", 2, 2),
            createAddRoomRequest("Deluxe Room", 3, 2),
            createAddRoomRequest("Superior Room", 4, 2),
            createAddRoomRequest("Suite", 5, 2),
            createAddRoomRequest("Family Room", 6, 2)
        );
        mockCreatePropertyRequest.setRoomTypes(validRoomTypes);
        mockCreatePropertyRequest.setTotalRoom(12);
        
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateProperty_VillaWithAllValidRoomTypes() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setType(2); // Villa
        
        List<AddRoomRequestDTO> validRoomTypes = Arrays.asList(
            createAddRoomRequest("Luxury", 1, 2),
            createAddRoomRequest("Beachfront", 1, 2),
            createAddRoomRequest("Mountside", 1, 2),
            createAddRoomRequest("Eco Friendly", 1, 2),
            createAddRoomRequest("Romantic", 1, 2)
        );
        mockCreatePropertyRequest.setRoomTypes(validRoomTypes);
        mockCreatePropertyRequest.setTotalRoom(10);
        
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateProperty_ApartmentWithAllValidRoomTypes() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setType(3); // Apartment
        
        List<AddRoomRequestDTO> validRoomTypes = Arrays.asList(
            createAddRoomRequest("Studio", 1, 2),
            createAddRoomRequest("1BR", 2, 2),
            createAddRoomRequest("2BR", 3, 2),
            createAddRoomRequest("3BR", 4, 2),
            createAddRoomRequest("Penthouse", 5, 1)
        );
        mockCreatePropertyRequest.setRoomTypes(validRoomTypes);
        mockCreatePropertyRequest.setTotalRoom(11);
        
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(mockProperty);

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateProperty_InvalidPropertyType() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        mockCreatePropertyRequest.setType(99); // Invalid type
        mockCreatePropertyRequest.getRoomTypes().get(0).setRoomTypeName("Any Room");

        // Act
        ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> response = 
            propertyRestController.createProperty(mockCreatePropertyRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tidak sesuai dengan tipe properti"));
    }

    @Test
    void testAddRoomTypeWithRooms_VillaWithEcoFriendlyHyphenated() {
        // Arrange
        PropertyResponseDTO villaProperty = PropertyResponseDTO.builder()
                .propertyID(testPropertyId.toString())
                .propertyName("Test Villa")
                .type(2) // Villa
                .activeStatus(1)
                .totalRoom(5)
                .build();
        CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                .name("Eco-Friendly")
                .floor(1)
                .unitCount(2)
                .build();

        AddRoomTypeRequestWrapper wrapper = new AddRoomTypeRequestWrapper();
        wrapper.setPropertyId(testPropertyId.toString());
        wrapper.setRoomTypes(Arrays.asList(roomTypeDTO));

        RoomTypeResponseDTO mockRoomType = RoomTypeResponseDTO.builder()
                .roomTypeID(UUID.randomUUID().toString())
                .name("Eco-Friendly")
                .floor(1)
                .build();

        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(propertyRestService.getPropertyById(testPropertyId)).thenReturn(villaProperty);
        when(roomTypeRestService.getRoomTypesByProperty(testPropertyId)).thenReturn(new ArrayList<>());
        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class))).thenReturn(mockRoomType);
        when(propertyRestService.updateProperty(eq(testPropertyId), any(UpdatePropertyRequestDTO.class)))
            .thenReturn(villaProperty);

        // Act
        ResponseEntity<BaseResponseDTO<String>> response = 
            propertyRestController.addRoomTypeWithRooms(wrapper, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // Helper method
    private AddRoomRequestDTO createAddRoomRequest(String name, Integer floor, Integer unitCount) {
        return AddRoomRequestDTO.builder()
                .roomTypeName(name)
                .floor(floor)
                .unitCount(unitCount)
                .price(500000)
                .capacity(2)
                .build();
    }
}
