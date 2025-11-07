package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.PropertyRestService;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomRestService;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomTypeRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyRestController.class)
class PropertyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PropertyRestService propertyRestService;

    @MockBean
    private RoomTypeRestService roomTypeRestService;

    @MockBean
    private RoomRestService roomRestService;

    @Test
    void testCreateProperty_Success() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        request.setPropertyName("Test Hotel");
        request.setType(1);
        request.setAddress("Jl. Test No. 123");
        request.setProvince(1);
        request.setDescription("Test Description");
        request.setTotalRoom(10);
        request.setOwnerName("John Doe");
        request.setOwnerID("OWNER-001");
        
        AddRoomRequestDTO roomType = new AddRoomRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setRoomTypeName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        roomType.setFacility("AC, TV, Wifi");
        roomType.setRoomTypeDescription("Comfortable single room");
        request.setRoomTypes(Arrays.asList(roomType));

        PropertyResponseDTO response = new PropertyResponseDTO();
        response.setPropertyID("PROP-001");
        response.setPropertyName("Test Hotel");
        
        when(propertyRestService.createProperty(any())).thenReturn(response);

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.propertyID").value("PROP-001"));
    }

    @Test
    void testCreateProperty_ValidationError() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateProperty_NoRoomTypes() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        request.setPropertyName("Test Hotel");
        request.setType(1);
        request.setAddress("Jl. Test No. 123");
        request.setProvince(1);
        request.setTotalRoom(10);
        request.setOwnerName("John Doe");
        request.setOwnerID("OWNER-001");
        request.setRoomTypes(new ArrayList<>());

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateProperty_InternalError() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        request.setPropertyName("Test Hotel");
        request.setType(1);
        request.setAddress("Jl. Test No. 123");
        request.setProvince(1);
        request.setTotalRoom(10);
        request.setOwnerName("John Doe");
        request.setOwnerID("OWNER-001");
        
        AddRoomRequestDTO roomType = new AddRoomRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setRoomTypeName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        roomType.setFacility("AC, TV, Wifi");
        roomType.setRoomTypeDescription("Comfortable single room");
        request.setRoomTypes(Arrays.asList(roomType));

        when(propertyRestService.createProperty(any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void testGetAllProperties_Success() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setPropertyName("Test Hotel");
        
        List<PropertyResponseDTO> properties = Arrays.asList(property);
        when(propertyRestService.getAllProperties()).thenReturn(properties);

        mockMvc.perform(get("/api/property"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].propertyID").value("PROP-001"));
    }

    @Test
    void testGetAllProperties_WithFilters() throws Exception {
        List<PropertyResponseDTO> properties = new ArrayList<>();
        when(propertyRestService.getAllProperties()).thenReturn(properties);

        mockMvc.perform(get("/api/property")
                .param("search", "hotel")
                .param("type", "1")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetProperty_Success() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setPropertyName("Test Hotel");
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(get("/api/property/PROP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.propertyID").value("PROP-001"));
    }

    @Test
    void testGetProperty_WithDateParams() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(get("/api/property/PROP-001")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-01-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetProperty_InternalError() throws Exception {
        when(propertyRestService.getPropertyById("PROP-001")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/property/PROP-001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void testGetPropertiesByOwner_Success() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setOwnerID("OWNER-001");
        
        List<PropertyResponseDTO> properties = Arrays.asList(property);
        when(propertyRestService.getPropertiesByOwner("OWNER-001")).thenReturn(properties);

        mockMvc.perform(get("/api/property/owner/OWNER-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetUpdatePropertyForm_Success() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(get("/api/property/update/PROP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetUpdatePropertyForm_InternalError() throws Exception {
        when(propertyRestService.getPropertyById("PROP-001")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/property/update/PROP-001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void testGetAddRoomTypeForm_Success() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setActiveStatus(1);
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(get("/api/property/updateroom/PROP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAddRoomTypeForm_PropertyNotFound() throws Exception {
        when(propertyRestService.getPropertyById("INVALID")).thenReturn(null);

        mockMvc.perform(get("/api/property/updateroom/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testGetAddRoomTypeForm_InactiveProperty() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setActiveStatus(0);
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(get("/api/property/updateroom/PROP-001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUpdateProperty_Success() throws Exception {
        UpdatePropertyRequestDTO request = new UpdatePropertyRequestDTO();
        request.setPropertyName("Updated Hotel");
        request.setType(1);
        request.setAddress("New Address");
        request.setProvince(1);
        request.setTotalRoom(15);
        request.setActiveStatus(1);

        PropertyResponseDTO response = new PropertyResponseDTO();
        response.setPropertyID("PROP-001");
        response.setPropertyName("Updated Hotel");
        
        when(propertyRestService.updateProperty(anyString(), any())).thenReturn(response);

        mockMvc.perform(put("/api/property/update/PROP-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.propertyName").value("Updated Hotel"));
    }

    @Test
    void testUpdateProperty_ValidationError() throws Exception {
        UpdatePropertyRequestDTO request = new UpdatePropertyRequestDTO();
        request.setPropertyName("Updated Hotel");
        request.setType(5); // Invalid - must be 1-3
        request.setActiveStatus(0);

        mockMvc.perform(put("/api/property/update/PROP-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUpdateProperty_InternalError() throws Exception {
        UpdatePropertyRequestDTO request = new UpdatePropertyRequestDTO();
        request.setPropertyName("Updated Hotel");

        when(propertyRestService.updateProperty(anyString(), any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/property/update/PROP-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void testAddRoomTypeWithRooms_Success() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-001");
        
        CreateRoomTypeRequestDTO roomType = new CreateRoomTypeRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        request.setRoomTypes(Arrays.asList(roomType));

        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setPropertyName("Test Hotel");
        property.setType(1);
        property.setActiveStatus(1);
        property.setTotalRoom(10);
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);
        when(roomTypeRestService.getRoomTypesByProperty("PROP-001")).thenReturn(new ArrayList<>());
        
        RoomTypeResponseDTO roomTypeResponse = new RoomTypeResponseDTO();
        roomTypeResponse.setRoomTypeID("RT-001");
        when(roomTypeRestService.createRoomType(any())).thenReturn(roomTypeResponse);
        
        RoomResponseDTO roomResponse = new RoomResponseDTO();
        roomResponse.setRoomID("ROOM-001");
        when(roomRestService.createRoom(any())).thenReturn(roomResponse);
        
        when(propertyRestService.updateProperty(anyString(), any())).thenReturn(property);

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    void testAddRoomTypeWithRooms_ValidationError() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-001");
        request.setRoomTypes(new ArrayList<>()); // Empty list should trigger validation error

        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setActiveStatus(1);
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testDeleteProperty_Success() throws Exception {
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);
        when(propertyRestService.deleteProperty("PROP-001")).thenReturn(property);

        mockMvc.perform(delete("/api/property/delete/PROP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testDeleteProperty_InternalError() throws Exception {
        when(propertyRestService.getPropertyById("PROP-001")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(delete("/api/property/delete/PROP-001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    // Additional tests for better coverage
    @Test
    void testCreateProperty_InvalidRoomTypeName() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        request.setPropertyName("Test Hotel");
        request.setType(1); // Hotel
        request.setAddress("Jl. Test No. 123");
        request.setProvince(1);
        request.setTotalRoom(10);
        request.setOwnerName("John Doe");
        request.setOwnerID("OWNER-001");
        
        AddRoomRequestDTO roomType = new AddRoomRequestDTO();
        roomType.setName("LUXURY"); // Wrong type for Hotel
        roomType.setRoomTypeName("LUXURY");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        roomType.setFacility("AC, TV, Wifi");
        roomType.setRoomTypeDescription("Luxury room");
        request.setRoomTypes(Arrays.asList(roomType));

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateProperty_InvalidUnitCount() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        request.setPropertyName("Test Hotel");
        request.setType(1);
        request.setAddress("Jl. Test No. 123");
        request.setProvince(1);
        request.setTotalRoom(10);
        request.setOwnerName("John Doe");
        request.setOwnerID("OWNER-001");
        
        AddRoomRequestDTO roomType = new AddRoomRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setRoomTypeName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(0); // Invalid - must be at least 1
        roomType.setFacility("AC, TV, Wifi");
        roomType.setRoomTypeDescription("Single room");
        request.setRoomTypes(Arrays.asList(roomType));

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateProperty_Villa_ValidRoomTypes() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        request.setPropertyName("Test Villa");
        request.setType(2); // Villa
        request.setAddress("Jl. Test No. 123");
        request.setProvince(1);
        request.setTotalRoom(5);
        request.setOwnerName("John Doe");
        request.setOwnerID("OWNER-001");
        
        AddRoomRequestDTO roomType1 = new AddRoomRequestDTO();
        roomType1.setName("LUXURY");
        roomType1.setRoomTypeName("LUXURY");
        roomType1.setPrice(2000000);
        roomType1.setCapacity(4);
        roomType1.setFloor(1);
        roomType1.setUnitCount(2);
        roomType1.setFacility("Pool, Garden");
        roomType1.setRoomTypeDescription("Luxury villa");
        
        AddRoomRequestDTO roomType2 = new AddRoomRequestDTO();
        roomType2.setName("BEACHFRONT");
        roomType2.setRoomTypeName("BEACHFRONT");
        roomType2.setPrice(3000000);
        roomType2.setCapacity(6);
        roomType2.setFloor(1);
        roomType2.setUnitCount(1);
        roomType2.setFacility("Beach Access");
        roomType2.setRoomTypeDescription("Beachfront villa");
        
        request.setRoomTypes(Arrays.asList(roomType1, roomType2));

        PropertyResponseDTO response = new PropertyResponseDTO();
        response.setPropertyID("PROP-002");
        response.setPropertyName("Test Villa");
        
        when(propertyRestService.createProperty(any())).thenReturn(response);

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    void testCreateProperty_Apartment_ValidRoomTypes() throws Exception {
        CreatePropertyRequestDTO request = new CreatePropertyRequestDTO();
        request.setPropertyName("Test Apartment");
        request.setType(3); // Apartment
        request.setAddress("Jl. Test No. 123");
        request.setProvince(1);
        request.setTotalRoom(20);
        request.setOwnerName("John Doe");
        request.setOwnerID("OWNER-001");
        
        AddRoomRequestDTO roomType1 = new AddRoomRequestDTO();
        roomType1.setName("STUDIO");
        roomType1.setRoomTypeName("STUDIO");
        roomType1.setPrice(3000000);
        roomType1.setCapacity(2);
        roomType1.setFloor(1);
        roomType1.setUnitCount(5);
        roomType1.setFacility("Kitchen, AC");
        roomType1.setRoomTypeDescription("Studio apartment");
        
        AddRoomRequestDTO roomType2 = new AddRoomRequestDTO();
        roomType2.setName("1BR");
        roomType2.setRoomTypeName("1BR");
        roomType2.setPrice(5000000);
        roomType2.setCapacity(3);
        roomType2.setFloor(2);
        roomType2.setUnitCount(3);
        roomType2.setFacility("Kitchen, AC, Balcony");
        roomType2.setRoomTypeDescription("1 bedroom apartment");
        
        request.setRoomTypes(Arrays.asList(roomType1, roomType2));

        PropertyResponseDTO response = new PropertyResponseDTO();
        response.setPropertyID("PROP-003");
        response.setPropertyName("Test Apartment");
        
        when(propertyRestService.createProperty(any())).thenReturn(response);

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    void testAddRoomTypeWithRooms_PropertyNotFound() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-999");
        
        CreateRoomTypeRequestDTO roomType = new CreateRoomTypeRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        request.setRoomTypes(Arrays.asList(roomType));
        
        when(propertyRestService.getPropertyById("PROP-999")).thenReturn(null);

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testAddRoomTypeWithRooms_InactiveProperty() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-001");
        
        CreateRoomTypeRequestDTO roomType = new CreateRoomTypeRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        request.setRoomTypes(Arrays.asList(roomType));
        
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setActiveStatus(0); // Inactive
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testAddRoomTypeWithRooms_InvalidUnitCount() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-001");
        
        CreateRoomTypeRequestDTO roomType = new CreateRoomTypeRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(0); // Invalid
        request.setRoomTypes(Arrays.asList(roomType));
        
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setType(1);
        property.setActiveStatus(1);
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testAddRoomTypeWithRooms_InvalidRoomTypeName() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-001");
        
        CreateRoomTypeRequestDTO roomType = new CreateRoomTypeRequestDTO();
        roomType.setName("LUXURY"); // Wrong for Hotel
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        request.setRoomTypes(Arrays.asList(roomType));
        
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setType(1); // Hotel
        property.setActiveStatus(1);
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testAddRoomTypeWithRooms_DuplicateInRequest() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-001");
        
        CreateRoomTypeRequestDTO roomType1 = new CreateRoomTypeRequestDTO();
        roomType1.setName("SINGLE_ROOM");
        roomType1.setPrice(500000);
        roomType1.setCapacity(1);
        roomType1.setFloor(1);
        roomType1.setUnitCount(5);
        
        CreateRoomTypeRequestDTO roomType2 = new CreateRoomTypeRequestDTO();
        roomType2.setName("SINGLE_ROOM"); // Duplicate name+floor
        roomType2.setPrice(600000);
        roomType2.setCapacity(1);
        roomType2.setFloor(1); // Same floor
        roomType2.setUnitCount(3);
        
        request.setRoomTypes(Arrays.asList(roomType1, roomType2));
        
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setType(1);
        property.setActiveStatus(1);
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testAddRoomTypeWithRooms_DuplicateWithExisting() throws Exception {
        PropertyRestController.AddRoomTypeRequestWrapper request = new PropertyRestController.AddRoomTypeRequestWrapper();
        request.setPropertyId("PROP-001");
        
        CreateRoomTypeRequestDTO roomType = new CreateRoomTypeRequestDTO();
        roomType.setName("SINGLE_ROOM");
        roomType.setPrice(500000);
        roomType.setCapacity(1);
        roomType.setFloor(1);
        roomType.setUnitCount(5);
        request.setRoomTypes(Arrays.asList(roomType));
        
        PropertyResponseDTO property = new PropertyResponseDTO();
        property.setPropertyID("PROP-001");
        property.setType(1);
        property.setActiveStatus(1);
        
        RoomTypeResponseDTO existingRoomType = new RoomTypeResponseDTO();
        existingRoomType.setName("SINGLE_ROOM");
        existingRoomType.setFloor(1); // Same combination
        
        when(propertyRestService.getPropertyById("PROP-001")).thenReturn(property);
        when(roomTypeRestService.getRoomTypesByProperty("PROP-001")).thenReturn(Arrays.asList(existingRoomType));

        mockMvc.perform(post("/api/property/updateroom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

