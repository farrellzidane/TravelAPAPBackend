package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomTypeRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomTypeRestController.class)
class RoomTypeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomTypeRestService roomTypeRestService;

    // ==================== CREATE ROOM TYPE TESTS ====================
    
    @Test
    void testCreateRoomType_Success() throws Exception {
        CreateRoomTypeRequestDTO request = new CreateRoomTypeRequestDTO();
        request.setName("DELUXE");
        request.setPrice(1000000);
        request.setCapacity(2);
        request.setFloor(3);
        request.setUnitCount(5);
        request.setPropertyID("PROP-001");

        RoomTypeResponseDTO response = new RoomTypeResponseDTO();
        response.setRoomTypeID("RT-001");
        response.setName("DELUXE");
        response.setPrice(1000000);
        response.setCapacity(2);
        response.setFloor(3);

        when(roomTypeRestService.createRoomType(any())).thenReturn(response);

        mockMvc.perform(post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Room type created successfully"))
                .andExpect(jsonPath("$.data.roomTypeID").value("RT-001"))
                .andExpect(jsonPath("$.data.name").value("DELUXE"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateRoomType_ValidationError() throws Exception {
        CreateRoomTypeRequestDTO request = new CreateRoomTypeRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateRoomType_RuntimeException() throws Exception {
        CreateRoomTypeRequestDTO request = new CreateRoomTypeRequestDTO();
        request.setName("DELUXE");
        request.setPrice(1000000);
        request.setCapacity(2);
        request.setFloor(3);
        request.setUnitCount(5);
        request.setPropertyID("PROP-001");

        when(roomTypeRestService.createRoomType(any())).thenThrow(new RuntimeException("Property not found"));

        mockMvc.perform(post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to create room type. Error: Property not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ==================== GET ROOM TYPE BY ID TESTS ====================
    
    @Test
    void testGetRoomType_Success() throws Exception {
        RoomTypeResponseDTO response = new RoomTypeResponseDTO();
        response.setRoomTypeID("RT-001");
        response.setName("DELUXE");
        response.setPrice(1000000);
        response.setCapacity(2);
        response.setFloor(3);

        when(roomTypeRestService.getRoomTypeById("RT-001")).thenReturn(response);

        mockMvc.perform(get("/api/room-types/RT-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room type retrieved successfully"))
                .andExpect(jsonPath("$.data.roomTypeID").value("RT-001"))
                .andExpect(jsonPath("$.data.name").value("DELUXE"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetRoomType_NotFound() throws Exception {
        when(roomTypeRestService.getRoomTypeById("RT-999")).thenThrow(new RuntimeException("Room type not found"));

        mockMvc.perform(get("/api/room-types/RT-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Room type not found. Error: Room type not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    // ==================== GET ALL ROOM TYPES TESTS ====================
    
    @Test
    void testGetAllRoomTypes_Success() throws Exception {
        RoomTypeResponseDTO response1 = new RoomTypeResponseDTO();
        response1.setRoomTypeID("RT-001");
        response1.setName("DELUXE");
        response1.setPrice(1000000);

        RoomTypeResponseDTO response2 = new RoomTypeResponseDTO();
        response2.setRoomTypeID("RT-002");
        response2.setName("SUITE");
        response2.setPrice(2000000);

        List<RoomTypeResponseDTO> responses = Arrays.asList(response1, response2);

        when(roomTypeRestService.getAllRoomTypes()).thenReturn(responses);

        mockMvc.perform(get("/api/room-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved 2 room type(s)"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].roomTypeID").value("RT-001"))
                .andExpect(jsonPath("$.data[1].roomTypeID").value("RT-002"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetAllRoomTypes_EmptyList() throws Exception {
        when(roomTypeRestService.getAllRoomTypes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/room-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved 0 room type(s)"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetAllRoomTypes_InternalServerError() throws Exception {
        when(roomTypeRestService.getAllRoomTypes()).thenThrow(new IllegalStateException("Database error"));

        mockMvc.perform(get("/api/room-types"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Failed to retrieve room types. Error: Database error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ==================== UPDATE ROOM TYPE TESTS ====================
    
    @Test
    void testUpdateRoomType_Success() throws Exception {
        UpdateRoomTypeRequestDTO request = new UpdateRoomTypeRequestDTO();
        request.setRoomTypeID("RT-001");
        request.setPrice(1200000);
        request.setCapacity(3);
        request.setFacility("AC, TV, Wifi, Mini Bar");
        request.setDescription("Updated deluxe room");

        RoomTypeResponseDTO response = new RoomTypeResponseDTO();
        response.setRoomTypeID("RT-001");
        response.setName("DELUXE");
        response.setPrice(1200000);
        response.setCapacity(3);

        when(roomTypeRestService.updateRoomType(eq("RT-001"), any())).thenReturn(response);

        mockMvc.perform(put("/api/room-types/RT-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room type updated successfully"))
                .andExpect(jsonPath("$.data.roomTypeID").value("RT-001"))
                .andExpect(jsonPath("$.data.price").value(1200000))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testUpdateRoomType_ValidationError() throws Exception {
        UpdateRoomTypeRequestDTO request = new UpdateRoomTypeRequestDTO();
        // Missing required fields

        mockMvc.perform(put("/api/room-types/RT-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testUpdateRoomType_RuntimeException() throws Exception {
        UpdateRoomTypeRequestDTO request = new UpdateRoomTypeRequestDTO();
        request.setRoomTypeID("RT-999");
        request.setPrice(1200000);
        request.setCapacity(3);
        request.setFacility("AC, TV");

        when(roomTypeRestService.updateRoomType(eq("RT-999"), any())).thenThrow(new RuntimeException("Room type not found"));

        mockMvc.perform(put("/api/room-types/RT-999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to update room type. Error: Room type not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ==================== DELETE ROOM TYPE TESTS ====================
    
    @Test
    void testDeleteRoomType_Success() throws Exception {
        doNothing().when(roomTypeRestService).deleteRoomType("RT-001");

        mockMvc.perform(delete("/api/room-types/RT-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room type deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteRoomType_RuntimeException() throws Exception {
        doThrow(new RuntimeException("Room type not found or has associated rooms"))
                .when(roomTypeRestService).deleteRoomType("RT-999");

        mockMvc.perform(delete("/api/room-types/RT-999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to delete room type. Error: Room type not found or has associated rooms"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ==================== ADDITIONAL EDGE CASE TESTS ====================
    
    @Test
    void testCreateRoomType_Success_WithMaxValues() throws Exception {
        CreateRoomTypeRequestDTO request = new CreateRoomTypeRequestDTO();
        request.setName("PRESIDENTIAL_SUITE");
        request.setPrice(Integer.MAX_VALUE);
        request.setCapacity(10);
        request.setFloor(50);
        request.setUnitCount(100);
        request.setPropertyID("PROP-001");

        RoomTypeResponseDTO response = new RoomTypeResponseDTO();
        response.setRoomTypeID("RT-999");
        response.setName("PRESIDENTIAL_SUITE");
        response.setPrice(Integer.MAX_VALUE);

        when(roomTypeRestService.createRoomType(any())).thenReturn(response);

        mockMvc.perform(post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.roomTypeID").value("RT-999"));
    }

    @Test
    void testGetAllRoomTypes_Success_SingleItem() throws Exception {
        RoomTypeResponseDTO response = new RoomTypeResponseDTO();
        response.setRoomTypeID("RT-001");
        response.setName("STANDARD");
        response.setPrice(500000);

        when(roomTypeRestService.getAllRoomTypes()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/room-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved 1 room type(s)"))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void testUpdateRoomType_Success_PartialUpdate() throws Exception {
        UpdateRoomTypeRequestDTO request = new UpdateRoomTypeRequestDTO();
        request.setRoomTypeID("RT-001");
        request.setPrice(1500000);
        request.setCapacity(2);
        request.setFacility("AC, TV");
        // description is optional, not setting it

        RoomTypeResponseDTO response = new RoomTypeResponseDTO();
        response.setRoomTypeID("RT-001");
        response.setName("DELUXE");
        response.setPrice(1500000);

        when(roomTypeRestService.updateRoomType(eq("RT-001"), any())).thenReturn(response);

        mockMvc.perform(put("/api/room-types/RT-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.price").value(1500000));
    }
}
