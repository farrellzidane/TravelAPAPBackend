package apap.ti._5.accommodation_2306275600_be.restservice;

import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;

public interface RoomTypeRestService {
    RoomTypeResponseDTO createRoomType(CreateRoomTypeRequestDTO dto);
    RoomTypeResponseDTO getRoomTypeById(UUID roomTypeID);
    List<RoomTypeResponseDTO> getAllRoomTypes();
    List<RoomTypeResponseDTO> getRoomTypesByProperty(UUID propertyID);
    RoomTypeResponseDTO updateRoomType(UUID roomTypeID, UpdateRoomTypeRequestDTO dto);
    void deleteRoomType(UUID roomTypeID);
    boolean isDuplicateRoomTypeFloor(UUID propertyID, String roomTypeName, Integer floor);
}
