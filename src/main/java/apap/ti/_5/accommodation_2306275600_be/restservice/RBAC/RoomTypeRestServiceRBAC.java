package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomTypeRestService;

public interface RoomTypeRestServiceRBAC extends RoomTypeRestService {
    @Override
    RoomTypeResponseDTO createRoomType(CreateRoomTypeRequestDTO dto) throws AccessDeniedException;

    @Override
    RoomTypeResponseDTO getRoomTypeById(String roomTypeID) throws AccessDeniedException;

    @Override
    List<RoomTypeResponseDTO> getAllRoomTypes() throws AccessDeniedException;

    @Override
    List<RoomTypeResponseDTO> getRoomTypesByProperty(String propertyID) throws AccessDeniedException;

    @Override
    RoomTypeResponseDTO updateRoomType(String roomTypeID, UpdateRoomTypeRequestDTO dto) throws AccessDeniedException;

    @Override
    void deleteRoomType(String roomTypeID) throws AccessDeniedException;

    @Override
    boolean isDuplicateRoomTypeFloor(String propertyID, String roomTypeName, Integer floor) throws AccessDeniedException;
}
