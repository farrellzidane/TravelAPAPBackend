package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.RoomTypeRestServiceRBAC;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomTypeRestController.class)
class RoomTypeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomTypeRestServiceRBAC roomTypeRestService;

    private CreateRoomTypeRequestDTO validCreateRequest;
    private UpdateRoomTypeRequestDTO validUpdateRequest;
    private RoomTypeResponseDTO expectedRoomTypeResponse;
    private UUID testRoomTypeId;

    @BeforeEach
    void setUp() {
        testRoomTypeId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        validCreateRequest = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe Suite")
                .price(750000)
                .description("Spacious deluxe suite with ocean view")
                .capacity(3)
                .facility("WiFi, TV, AC, Mini Bar")
                .floor(5)
                .propertyID("property-123")
                .unitCount(10)
                .build();

        validUpdateRequest = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .capacity(4)
                .price(800000)
                .facility("WiFi, TV, AC, Mini Bar, Jacuzzi")
                .description("Updated deluxe suite description")
                .build();

        expectedRoomTypeResponse = RoomTypeResponseDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .name("Deluxe Suite")
                .price(750000)
                .description("Spacious deluxe suite with ocean view")
                .capacity(3)
                .facility("WiFi, TV, AC, Mini Bar")
                .floor(5)
                .propertyID("property-123")
                .propertyName("Grand Hotel")
                .createdDate(now)
                .updatedDate(now)
                .roomIDs(Arrays.asList("room-1", "room-2", "room-3"))
                .build();
    }

    // ============================================
    // CREATE ROOM TYPE TESTS
    // ============================================

    @Test
    void testCreateRoomType_Success() throws Exception {
        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class)))
                .thenReturn(expectedRoomTypeResponse);

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Room type created successfully"))
                .andExpect(jsonPath("$.data.name").value("Deluxe Suite"))
                .andExpect(jsonPath("$.data.price").value(750000))
                .andExpect(jsonPath("$.data.capacity").value(3));

        verify(roomTypeRestService, times(1)).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_ValidationError_MissingName() throws Exception {
        CreateRoomTypeRequestDTO invalidRequest = CreateRoomTypeRequestDTO.builder()
                .name("")
                .price(750000)
                .capacity(3)
                .floor(5)
                .propertyID("property-123")
                .unitCount(10)
                .build();

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Room type name is required; "));

        verify(roomTypeRestService, never()).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_ValidationError_NullPrice() throws Exception {
        CreateRoomTypeRequestDTO invalidRequest = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe Suite")
                .price(null)
                .capacity(3)
                .floor(5)
                .propertyID("property-123")
                .unitCount(10)
                .build();

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Price is required; "));

        verify(roomTypeRestService, never()).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_ValidationError_NegativePrice() throws Exception {
        CreateRoomTypeRequestDTO invalidRequest = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe Suite")
                .price(-1000)
                .capacity(3)
                .floor(5)
                .propertyID("property-123")
                .unitCount(10)
                .build();

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Price cannot be negative; "));

        verify(roomTypeRestService, never()).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_ValidationError_NullCapacity() throws Exception {
        CreateRoomTypeRequestDTO invalidRequest = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe Suite")
                .price(750000)
                .capacity(null)
                .floor(5)
                .propertyID("property-123")
                .unitCount(10)
                .build();

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Capacity is required; "));

        verify(roomTypeRestService, never()).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_ValidationError_ZeroCapacity() throws Exception {
        CreateRoomTypeRequestDTO invalidRequest = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe Suite")
                .price(750000)
                .capacity(0)
                .floor(5)
                .propertyID("property-123")
                .unitCount(10)
                .build();

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Capacity must be at least 1; "));

        verify(roomTypeRestService, never()).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_ValidationError_MissingPropertyID() throws Exception {
        CreateRoomTypeRequestDTO invalidRequest = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe Suite")
                .price(750000)
                .capacity(3)
                .floor(5)
                .propertyID("")
                .unitCount(10)
                .build();

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Property ID is required; "));

        verify(roomTypeRestService, never()).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_ValidationError_ZeroUnitCount() throws Exception {
        CreateRoomTypeRequestDTO invalidRequest = CreateRoomTypeRequestDTO.builder()
                .name("Deluxe Suite")
                .price(750000)
                .capacity(3)
                .floor(5)
                .propertyID("property-123")
                .unitCount(0)
                .build();

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Unit count must be at least 1; "));

        verify(roomTypeRestService, never()).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_RuntimeException() throws Exception {
        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class)))
                .thenThrow(new RuntimeException("Property not found"));

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to create room type. Error: Property not found"));

        verify(roomTypeRestService, times(1)).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    @Test
    void testCreateRoomType_GeneralException() throws Exception {
        when(roomTypeRestService.createRoomType(any(CreateRoomTypeRequestDTO.class)))
                .thenThrow(new IllegalStateException("Database connection error"));

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to create room type. Error: Database connection error"));

        verify(roomTypeRestService, times(1)).createRoomType(any(CreateRoomTypeRequestDTO.class));
    }

    // ============================================
    // GET ROOM TYPE BY ID TESTS
    // ============================================

    @Test
    void testGetRoomType_Success() throws Exception {
        when(roomTypeRestService.getRoomTypeById(eq(testRoomTypeId)))
                .thenReturn(expectedRoomTypeResponse);

        mockMvc.perform(get("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room type retrieved successfully"))
                .andExpect(jsonPath("$.data.roomTypeID").value(testRoomTypeId.toString()))
                .andExpect(jsonPath("$.data.name").value("Deluxe Suite"));

        verify(roomTypeRestService, times(1)).getRoomTypeById(eq(testRoomTypeId));
    }

    @Test
    void testGetRoomType_NotFound() throws Exception {
        when(roomTypeRestService.getRoomTypeById(eq(testRoomTypeId)))
                .thenThrow(new RuntimeException("Room type not found"));

        mockMvc.perform(get("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Room type not found. Error: Room type not found"));

        verify(roomTypeRestService, times(1)).getRoomTypeById(eq(testRoomTypeId));
    }

    @Test
    void testGetRoomType_GeneralException() throws Exception {
        when(roomTypeRestService.getRoomTypeById(eq(testRoomTypeId)))
                .thenThrow(new IllegalStateException("Database error"));

        mockMvc.perform(get("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Room type not found. Error: Database error"));

        verify(roomTypeRestService, times(1)).getRoomTypeById(eq(testRoomTypeId));
    }

    // ============================================
    // GET ALL ROOM TYPES TESTS
    // ============================================

    @Test
    void testGetAllRoomTypes_Success() throws Exception {
        RoomTypeResponseDTO roomType2 = RoomTypeResponseDTO.builder()
                .roomTypeID(UUID.randomUUID().toString())
                .name("Standard Room")
                .price(500000)
                .capacity(2)
                .build();

        List<RoomTypeResponseDTO> roomTypes = Arrays.asList(expectedRoomTypeResponse, roomType2);

        when(roomTypeRestService.getAllRoomTypes()).thenReturn(roomTypes);

        mockMvc.perform(get("/api/room-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved 2 room type(s)"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(roomTypeRestService, times(1)).getAllRoomTypes();
    }

    @Test
    void testGetAllRoomTypes_EmptyList() throws Exception {
        when(roomTypeRestService.getAllRoomTypes()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/room-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved 0 room type(s)"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(roomTypeRestService, times(1)).getAllRoomTypes();
    }

    @Test
    void testGetAllRoomTypes_Exception() throws Exception {
        when(roomTypeRestService.getAllRoomTypes())
                .thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(get("/api/room-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Failed to retrieve room types. Error: Database connection error"));

        verify(roomTypeRestService, times(1)).getAllRoomTypes();
    }

    // ============================================
    // UPDATE ROOM TYPE TESTS
    // ============================================

    @Test
    void testUpdateRoomType_Success() throws Exception {
        RoomTypeResponseDTO updatedResponse = RoomTypeResponseDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .name("Deluxe Suite")
                .price(800000)
                .capacity(4)
                .facility("WiFi, TV, AC, Mini Bar, Jacuzzi")
                .build();

        when(roomTypeRestService.updateRoomType(eq(testRoomTypeId), any(UpdateRoomTypeRequestDTO.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room type updated successfully"))
                .andExpect(jsonPath("$.data.price").value(800000))
                .andExpect(jsonPath("$.data.capacity").value(4));

        verify(roomTypeRestService, times(1)).updateRoomType(eq(testRoomTypeId), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_ValidationError_MissingRoomTypeID() throws Exception {
        UpdateRoomTypeRequestDTO invalidRequest = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID("")
                .capacity(4)
                .price(800000)
                .facility("WiFi, TV, AC")
                .build();

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Room type ID is required; "));

        verify(roomTypeRestService, never()).updateRoomType(any(UUID.class), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_ValidationError_NullCapacity() throws Exception {
        UpdateRoomTypeRequestDTO invalidRequest = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .capacity(null)
                .price(800000)
                .facility("WiFi, TV, AC")
                .build();

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Capacity is required; "));

        verify(roomTypeRestService, never()).updateRoomType(any(UUID.class), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_ValidationError_ZeroCapacity() throws Exception {
        UpdateRoomTypeRequestDTO invalidRequest = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .capacity(0)
                .price(800000)
                .facility("WiFi, TV, AC")
                .build();

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Capacity must be at least 1; "));

        verify(roomTypeRestService, never()).updateRoomType(any(UUID.class), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_ValidationError_NullPrice() throws Exception {
        UpdateRoomTypeRequestDTO invalidRequest = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .capacity(4)
                .price(null)
                .facility("WiFi, TV, AC")
                .build();

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Price is required; "));

        verify(roomTypeRestService, never()).updateRoomType(any(UUID.class), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_ValidationError_NegativePrice() throws Exception {
        UpdateRoomTypeRequestDTO invalidRequest = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .capacity(4)
                .price(-1000)
                .facility("WiFi, TV, AC")
                .build();

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Price cannot be negative; "));

        verify(roomTypeRestService, never()).updateRoomType(any(UUID.class), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_ValidationError_MissingFacility() throws Exception {
        UpdateRoomTypeRequestDTO invalidRequest = UpdateRoomTypeRequestDTO.builder()
                .roomTypeID(testRoomTypeId.toString())
                .capacity(4)
                .price(800000)
                .facility("")
                .build();

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Facility is required; "));

        verify(roomTypeRestService, never()).updateRoomType(any(UUID.class), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_RuntimeException() throws Exception {
        when(roomTypeRestService.updateRoomType(eq(testRoomTypeId), any(UpdateRoomTypeRequestDTO.class)))
                .thenThrow(new RuntimeException("Room type not found"));

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to update room type. Error: Room type not found"));

        verify(roomTypeRestService, times(1)).updateRoomType(eq(testRoomTypeId), any(UpdateRoomTypeRequestDTO.class));
    }

    @Test
    void testUpdateRoomType_GeneralException() throws Exception {
        when(roomTypeRestService.updateRoomType(eq(testRoomTypeId), any(UpdateRoomTypeRequestDTO.class)))
                .thenThrow(new IllegalStateException("Database error"));

        mockMvc.perform(put("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to update room type. Error: Database error"));

        verify(roomTypeRestService, times(1)).updateRoomType(eq(testRoomTypeId), any(UpdateRoomTypeRequestDTO.class));
    }

    // ============================================
    // DELETE ROOM TYPE TESTS
    // ============================================

    @Test
    void testDeleteRoomType_Success() throws Exception {
        doNothing().when(roomTypeRestService).deleteRoomType(eq(testRoomTypeId));

        mockMvc.perform(delete("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room type deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(roomTypeRestService, times(1)).deleteRoomType(eq(testRoomTypeId));
    }

    @Test
    void testDeleteRoomType_RuntimeException() throws Exception {
        doThrow(new RuntimeException("Room type not found"))
                .when(roomTypeRestService).deleteRoomType(eq(testRoomTypeId));

        mockMvc.perform(delete("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to delete room type. Error: Room type not found"));

        verify(roomTypeRestService, times(1)).deleteRoomType(eq(testRoomTypeId));
    }

    @Test
    void testDeleteRoomType_GeneralException() throws Exception {
        doThrow(new IllegalStateException("Database error"))
                .when(roomTypeRestService).deleteRoomType(eq(testRoomTypeId));

        mockMvc.perform(delete("/api/room-types/{id}", testRoomTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to delete room type. Error: Database error"));

        verify(roomTypeRestService, times(1)).deleteRoomType(eq(testRoomTypeId));
    }
}
