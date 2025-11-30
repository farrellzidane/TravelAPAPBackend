package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.RoomRestServiceRBAC;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomRestController.class)
class RoomRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomRestServiceRBAC roomRestService;

    private CreateMaintenanceRequestDTO validMaintenanceRequest;
    private RoomResponseDTO expectedRoomResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(7);

        validMaintenanceRequest = CreateMaintenanceRequestDTO.builder()
                .roomID("room-123")
                .maintenanceStart(now)
                .maintenanceEnd(future)
                .build();

        expectedRoomResponse = RoomResponseDTO.builder()
                .roomID("room-123")
                .name("Room 101")
                .availabilityStatus(0)
                .availabilityStatusName("Under Maintenance")
                .activeRoom(1)
                .activeRoomName("Active")
                .maintenanceStart(now)
                .maintenanceEnd(future)
                .capacity(2)
                .price(500000)
                .floor(1)
                .roomTypeID("roomtype-456")
                .roomTypeName("Deluxe Room")
                .createdDate(now)
                .updatedDate(now)
                .build();
    }

    // ============================================
    // CREATE MAINTENANCE TESTS
    // ============================================

    @Test
    void testCreateMaintenance_Success() throws Exception {
        when(roomRestService.createMaintenance(any(CreateMaintenanceRequestDTO.class)))
                .thenReturn(expectedRoomResponse);

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMaintenanceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Konfirmasi: Jadwal perbaikan berhasil ditambahkan"))
                .andExpect(jsonPath("$.data.roomID").value("room-123"))
                .andExpect(jsonPath("$.data.name").value("Room 101"))
                .andExpect(jsonPath("$.data.availabilityStatusName").value("Under Maintenance"));

        verify(roomRestService, times(1)).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_ValidationError_MissingRoomID() throws Exception {
        CreateMaintenanceRequestDTO invalidRequest = CreateMaintenanceRequestDTO.builder()
                .roomID("")
                .maintenanceStart(LocalDateTime.now())
                .maintenanceEnd(LocalDateTime.now().plusDays(7))
                .build();

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Room ID is required; "));

        verify(roomRestService, never()).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_ValidationError_MissingMaintenanceStart() throws Exception {
        CreateMaintenanceRequestDTO invalidRequest = CreateMaintenanceRequestDTO.builder()
                .roomID("room-123")
                .maintenanceStart(null)
                .maintenanceEnd(LocalDateTime.now().plusDays(7))
                .build();

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Maintenance start date is required; "));

        verify(roomRestService, never()).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_ValidationError_MissingMaintenanceEnd() throws Exception {
        CreateMaintenanceRequestDTO invalidRequest = CreateMaintenanceRequestDTO.builder()
                .roomID("room-123")
                .maintenanceStart(LocalDateTime.now())
                .maintenanceEnd(null)
                .build();

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Maintenance end date is required; "));

        verify(roomRestService, never()).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_ValidationError_MultipleFields() throws Exception {
        CreateMaintenanceRequestDTO invalidRequest = CreateMaintenanceRequestDTO.builder()
                .roomID("")
                .maintenanceStart(null)
                .maintenanceEnd(null)
                .build();

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(roomRestService, never()).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_ServiceThrowsException() throws Exception {
        when(roomRestService.createMaintenance(any(CreateMaintenanceRequestDTO.class)))
                .thenThrow(new RuntimeException("Room not found"));

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMaintenanceRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Konfirmasi: Jadwal perbaikan gagal ditambahkan. Error: Room not found"));

        verify(roomRestService, times(1)).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_ServiceThrowsIllegalArgumentException() throws Exception {
        when(roomRestService.createMaintenance(any(CreateMaintenanceRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid maintenance dates"));

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMaintenanceRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Konfirmasi: Jadwal perbaikan gagal ditambahkan. Error: Invalid maintenance dates"));

        verify(roomRestService, times(1)).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_NullPointerException() throws Exception {
        when(roomRestService.createMaintenance(any(CreateMaintenanceRequestDTO.class)))
                .thenThrow(new NullPointerException("Null pointer exception occurred"));

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMaintenanceRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Konfirmasi: Jadwal perbaikan gagal ditambahkan. Error: Null pointer exception occurred"));

        verify(roomRestService, times(1)).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_WithWhitespaceRoomID() throws Exception {
        CreateMaintenanceRequestDTO invalidRequest = CreateMaintenanceRequestDTO.builder()
                .roomID("   ")
                .maintenanceStart(LocalDateTime.now())
                .maintenanceEnd(LocalDateTime.now().plusDays(7))
                .build();

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Room ID is required; "));

        verify(roomRestService, never()).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_VerifyResponseTimestamp() throws Exception {
        when(roomRestService.createMaintenance(any(CreateMaintenanceRequestDTO.class)))
                .thenReturn(expectedRoomResponse);

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMaintenanceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(roomRestService, times(1)).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void testCreateMaintenance_VerifyAllResponseFields() throws Exception {
        when(roomRestService.createMaintenance(any(CreateMaintenanceRequestDTO.class)))
                .thenReturn(expectedRoomResponse);

        mockMvc.perform(post("/api/property/maintenance/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMaintenanceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.roomID").value("room-123"))
                .andExpect(jsonPath("$.data.capacity").value(2))
                .andExpect(jsonPath("$.data.price").value(500000))
                .andExpect(jsonPath("$.data.floor").value(1));

        verify(roomRestService, times(1)).createMaintenance(any(CreateMaintenanceRequestDTO.class));
    }
}
