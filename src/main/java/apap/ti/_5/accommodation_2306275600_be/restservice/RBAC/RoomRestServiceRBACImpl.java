package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;

import java.util.Optional;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.UpdateRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomRestServiceImpl;

@Service
public class RoomRestServiceRBACImpl extends RoomRestServiceImpl implements RoomRestServiceRBAC {

    private final AuthService authService;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomRestServiceRBACImpl(
            RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository,
            AuthService authService
        ) {
        super(roomRepository, roomTypeRepository);
        this.authService = authService;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    // Helper method to check if owner has access to room (via room type -> property)
    private boolean isOwnerOfRoom(UUID roomID, UUID ownerID) {
        Optional<Room> roomOpt = roomRepository.findById(roomID);
        if (roomOpt.isEmpty()) {
            return false;
        }
        RoomType roomType = roomOpt.get().getRoomType();
        if (roomType == null || roomType.getProperty() == null) {
            return false;
        }
        Property property = roomType.getProperty();
        return property.getOwnerID().equals(ownerID);
    }

    // Helper method to check if owner has access to room type
    private boolean isOwnerOfRoomType(UUID roomTypeID, UUID ownerID) {
        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(roomTypeID);
        if (roomTypeOpt.isEmpty()) {
            return false;
        }
        Property property = roomTypeOpt.get().getProperty();
        return property != null && property.getOwnerID().equals(ownerID);
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
    public RoomResponseDTO getRoomById(UUID roomID) throws AccessDeniedException {
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

    // [GET] Get Rooms by Room Type
    // - Superadmin: Dapat melihat rooms dari semua room types
    // - Accommodation Owner: Hanya dapat melihat rooms dari room types pada property yang dimilikinya
    // - Customer: Dapat melihat rooms dari semua room types
    @Override
    public List<RoomResponseDTO> getRoomsByRoomType(UUID roomTypeID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Accommodation Owner hanya dapat melihat rooms dari room type pada property miliknya
        if (authService.isAccommodationOwner(user)) {
            if (!isOwnerOfRoomType(roomTypeID, user.userId())) {
                throw new AccessDeniedException("Anda tidak memiliki akses untuk melihat rooms dari room type ini");
            }
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
    public RoomResponseDTO updateRoom(UUID roomID, UpdateRoomRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updateRoom(roomID, dto);
    }

    // [DELETE] Delete Room - Superadmin, Accommodation Owner
    @Override
    public void deleteRoom(UUID roomID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        super.deleteRoom(roomID);
    }

    // [GET] Get Rooms by Property and Floor - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomResponseDTO> getRoomsByPropertyAndFloor(UUID propertyID, Integer floor) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomsByPropertyAndFloor(propertyID, floor);
    }

    // [GET] Get Room Entity by ID - Superadmin, Accommodation Owner, Customer
    @Override
    public Room getRoomEntityById(UUID roomID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomEntityById(roomID);
    }

    // [PUT] Create Room Maintenance
    // - Superadmin: Dapat create maintenance pada semua rooms
    // - Accommodation Owner: Hanya dapat create maintenance pada rooms di property yang dimilikinya
    @Override
    public RoomResponseDTO createMaintenance(CreateMaintenanceRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Accommodation Owner hanya dapat create maintenance pada room di property miliknya
        if (authService.isAccommodationOwner(user)) {
            UUID roomID = UUID.fromString(dto.getRoomID());
            if (!isOwnerOfRoom(roomID, user.userId())) {
                throw new AccessDeniedException("Anda tidak memiliki akses untuk melakukan maintenance pada room ini");
            }
        }
        
        return super.createMaintenance(dto);
    }
}
