package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomRestController.class)
class RoomRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomRestService roomRestService;

    @Test
    void testCreateMaintenance_Success() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-001");
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(3));

        RoomResponseDTO response = new RoomResponseDTO();
        response.setRoomID("ROOM-001");
        response.setName("Room 101");
        response.setAvailabilityStatus(0); // Under maintenance
        response.setMaintenanceStart(request.getMaintenanceStart());
        response.setMaintenanceEnd(request.getMaintenanceEnd());

        when(roomRestService.createMaintenance(any())).thenReturn(response);

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Konfirmasi: Jadwal perbaikan berhasil ditambahkan"))
                .andExpect(jsonPath("$.data.roomID").value("ROOM-001"))
                .andExpect(jsonPath("$.data.name").value("Room 101"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateMaintenance_ValidationError_MissingRoomID() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        // Missing RoomID
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(3));

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateMaintenance_ValidationError_MissingMaintenanceStart() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-001");
        // Missing MaintenanceStart
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(3));

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testCreateMaintenance_ValidationError_MissingMaintenanceEnd() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-001");
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        // Missing MaintenanceEnd

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testCreateMaintenance_ValidationError_AllFieldsMissing() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        // All fields missing

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testCreateMaintenance_ServiceError() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-001");
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(3));

        when(roomRestService.createMaintenance(any())).thenThrow(new RuntimeException("Room not found"));

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Konfirmasi: Jadwal perbaikan gagal ditambahkan. Error: Room not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateMaintenance_ServiceError_DatabaseError() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-002");
        request.setMaintenanceStart(LocalDateTime.now().plusDays(2));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(5));

        when(roomRestService.createMaintenance(any())).thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testCreateMaintenance_Success_LongDuration() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-003");
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(30)); // 30 days maintenance

        RoomResponseDTO response = new RoomResponseDTO();
        response.setRoomID("ROOM-003");
        response.setName("Room 303");
        response.setAvailabilityStatus(0);

        when(roomRestService.createMaintenance(any())).thenReturn(response);

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.roomID").value("ROOM-003"));
    }

    @Test
    void testCreateMaintenance_Success_ImmediateStart() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-004");
        request.setMaintenanceStart(LocalDateTime.now()); // Start immediately
        request.setMaintenanceEnd(LocalDateTime.now().plusHours(6));

        RoomResponseDTO response = new RoomResponseDTO();
        response.setRoomID("ROOM-004");
        response.setName("Room 404");

        when(roomRestService.createMaintenance(any())).thenReturn(response);

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Konfirmasi: Jadwal perbaikan berhasil ditambahkan"));
    }

    @Test
    void testCreateMaintenance_Success_WithCompleteRoomInfo() throws Exception {
        CreateMaintenanceRequestDTO request = new CreateMaintenanceRequestDTO();
        request.setRoomID("ROOM-005");
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(3));

        RoomResponseDTO response = new RoomResponseDTO();
        response.setRoomID("ROOM-005");
        response.setName("Deluxe Room 505");
        response.setAvailabilityStatus(0);
        response.setActiveRoom(1);
        response.setMaintenanceStart(request.getMaintenanceStart());
        response.setMaintenanceEnd(request.getMaintenanceEnd());

        when(roomRestService.createMaintenance(any())).thenReturn(response);

        mockMvc.perform(post("/api/property/maintenance/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value("Deluxe Room 505"));
    }
}

