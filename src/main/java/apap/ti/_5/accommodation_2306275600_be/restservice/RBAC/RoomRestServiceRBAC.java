package apap.ti._5.accommodation_2306275600_be.restservice.RBAC; 

import java.util.List;
import java.util.UUID; 


import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.UpdateRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomRestService; 

public interface RoomRestServiceRBAC extends RoomRestService {
    @Override
    RoomResponseDTO createRoom(AddRoomRequestDTO dto) throws AccessDeniedException;

    @Override
    RoomResponseDTO getRoomById(UUID roomID) throws AccessDeniedException;
    
    @Override
    List<RoomResponseDTO> getAllRooms() throws AccessDeniedException;

    @Override
    List<RoomResponseDTO> getRoomsByRoomType(UUID roomTypeID) throws AccessDeniedException;

    @Override
    List<RoomResponseDTO> getAvailableRooms() throws AccessDeniedException;

    @Override
    RoomResponseDTO updateRoom(UUID roomID, UpdateRoomRequestDTO dto) throws AccessDeniedException;

    @Override
    void deleteRoom(UUID roomID) throws AccessDeniedException;

    @Override
    List<RoomResponseDTO> getRoomsByPropertyAndFloor(UUID propertyID, Integer floor) throws AccessDeniedException;

    @Override
    Room getRoomEntityById(UUID roomID) throws AccessDeniedException;

    @Override
    RoomResponseDTO createMaintenance(CreateMaintenanceRequestDTO dto) throws AccessDeniedException;
}
