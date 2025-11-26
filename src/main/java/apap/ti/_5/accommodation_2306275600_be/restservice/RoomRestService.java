package apap.ti._5.accommodation_2306275600_be.restservice;

import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.UpdateRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;

public interface RoomRestService {
    RoomResponseDTO createRoom(AddRoomRequestDTO dto);
    RoomResponseDTO getRoomById(UUID roomID);
    List<RoomResponseDTO> getAllRooms();
    List<RoomResponseDTO> getRoomsByRoomType(UUID roomTypeID);
    List<RoomResponseDTO> getAvailableRooms();
    RoomResponseDTO updateRoom(UUID roomID, UpdateRoomRequestDTO dto);
    void deleteRoom(UUID roomID);
    List<RoomResponseDTO> getRoomsByPropertyAndFloor(UUID propertyID, Integer floor);
    Room getRoomEntityById(UUID roomID);
    RoomResponseDTO createMaintenance(CreateMaintenanceRequestDTO dto);
}
