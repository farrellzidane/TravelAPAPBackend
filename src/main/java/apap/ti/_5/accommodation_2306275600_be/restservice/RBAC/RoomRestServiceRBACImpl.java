package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.UpdateRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomRestServiceImpl;

@Service
public class RoomRestServiceRBACImpl extends RoomRestServiceImpl implements RoomRestServiceRBAC {

    private final AuthService authService;

    public RoomRestServiceRBACImpl(
            RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository,
            AuthService authService
        ) {
        super(roomRepository, roomTypeRepository);
        this.authService = authService;
    }

    // [POST] Create Room - Superadmin, Accommodation Owner
    @Override
    public RoomResponseDTO createRoom(AddRoomRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.createRoom(dto);
    }

    // [GET] Get Room by ID - Superadmin, Accommodation Owner, Customer
    @Override
    public RoomResponseDTO getRoomById(String roomID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomById(roomID);
    }

    // [GET] Get All Rooms - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomResponseDTO> getAllRooms() throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAllRooms();
    }

    // [GET] Get Rooms by Room Type - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomResponseDTO> getRoomsByRoomType(String roomTypeID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomsByRoomType(roomTypeID);
    }

    // [GET] Get Available Rooms - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomResponseDTO> getAvailableRooms() throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAvailableRooms();
    }

    // [PUT] Update Room - Superadmin, Accommodation Owner
    @Override
    public RoomResponseDTO updateRoom(String roomID, UpdateRoomRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updateRoom(roomID, dto);
    }

    // [DELETE] Delete Room - Superadmin, Accommodation Owner
    @Override
    public void deleteRoom(String roomID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        super.deleteRoom(roomID);
    }

    // [GET] Get Rooms by Property and Floor - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomResponseDTO> getRoomsByPropertyAndFloor(String propertyID, Integer floor) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomsByPropertyAndFloor(propertyID, floor);
    }

    // [GET] Get Room Entity by ID - Superadmin, Accommodation Owner, Customer
    @Override
    public Room getRoomEntityById(String roomID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomEntityById(roomID);
    }

    // [PUT] Create Room Maintenance - Superadmin, Accommodation Owner
    @Override
    public RoomResponseDTO createMaintenance(CreateMaintenanceRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.createMaintenance(dto);
    }
}
