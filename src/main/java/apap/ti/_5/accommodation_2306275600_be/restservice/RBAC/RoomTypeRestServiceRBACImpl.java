package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;

import java.util.Optional;
import java.util.stream.Collectors;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomTypeRestServiceImpl;

@Service
public class RoomTypeRestServiceRBACImpl extends RoomTypeRestServiceImpl implements RoomTypeRestServiceRBAC {

    private final AuthService authService;
    private final PropertyRepository propertyRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeRestServiceRBACImpl(
            RoomTypeRepository roomTypeRepository,
            PropertyRepository propertyRepository,
            RoomRepository roomRepository,
            AuthService authService
        ) {
        super(roomTypeRepository, propertyRepository, roomRepository);
        this.authService = authService;
        this.propertyRepository = propertyRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    // Helper method to check if owner has access to property
    private boolean isOwnerOfProperty(UUID propertyID, UUID ownerID) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyID);
        return propertyOpt.isPresent() && propertyOpt.get().getOwnerID().equals(ownerID);
    }

    // Helper method to check if owner has access to room type (via property)
    private boolean isOwnerOfRoomType(UUID roomTypeID, UUID ownerID) {
        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(roomTypeID);
        if (roomTypeOpt.isEmpty()) {
            return false;
        }
        UUID propertyID = roomTypeOpt.get().getProperty().getPropertyID();
        return isOwnerOfProperty(propertyID, ownerID);
    }

    // [POST] Create Room Type
    // - Superadmin: Dapat create room type pada semua property
    // - Accommodation Owner: Hanya dapat create room type pada property yang dimilikinya
    @Override
    public RoomTypeResponseDTO createRoomType(CreateRoomTypeRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Accommodation Owner hanya dapat create room type pada property miliknya
        if (authService.isAccommodationOwner(user)) {
            UUID propertyID = UUID.fromString(dto.getPropertyID());
            if (!isOwnerOfProperty(propertyID, user.userId())) {
                throw new AccessDeniedException("Anda tidak memiliki akses untuk menambahkan room type pada property ini");
            }
        }
        
        return super.createRoomType(dto);
    }

    // [GET] Get Room Type Details by Room Type ID
    // - Superadmin: Dapat melihat semua room type
    // - Accommodation Owner: Hanya dapat melihat room type dari property yang dimilikinya
    // - Customer: Dapat melihat semua room type
    @Override
    public RoomTypeResponseDTO getRoomTypeById(UUID roomTypeID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Accommodation Owner hanya dapat melihat room type dari property miliknya
        if (authService.isAccommodationOwner(user)) {
            if (!isOwnerOfRoomType(roomTypeID, user.userId())) {
                throw new AccessDeniedException("Anda tidak memiliki akses untuk melihat room type ini");
            }
        }
        
        return super.getRoomTypeById(roomTypeID);
    }

    // [GET] Get All Room Types - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomTypeResponseDTO> getAllRoomTypes() throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAllRoomTypes();
    }

    // [GET] Get Room Types by Property ID
    // - Superadmin: Dapat melihat room types dari semua property
    // - Accommodation Owner: Hanya dapat melihat room types dari property yang dimilikinya
    // - Customer: Dapat melihat room types dari semua property
    @Override
    public List<RoomTypeResponseDTO> getRoomTypesByProperty(UUID propertyID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Accommodation Owner hanya dapat melihat room types dari property miliknya
        if (authService.isAccommodationOwner(user)) {
            if (!isOwnerOfProperty(propertyID, user.userId())) {
                throw new AccessDeniedException("Anda tidak memiliki akses untuk melihat room types dari property ini");
            }
        }
        
        return super.getRoomTypesByProperty(propertyID);
    }

    // [PUT] Update Room Type - Superadmin, Accommodation Owner
    @Override
    public RoomTypeResponseDTO updateRoomType(UUID roomTypeID, UpdateRoomTypeRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updateRoomType(roomTypeID, dto);
    }

    // [DELETE] Delete Room Type - Superadmin, Accommodation Owner
    @Override
    public void deleteRoomType(UUID roomTypeID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        super.deleteRoomType(roomTypeID);
    }

    // [UTIL] Check Duplicate Room Type Floor - Superadmin, Accommodation Owner
    @Override
    public boolean isDuplicateRoomTypeFloor(UUID propertyID, String roomTypeName, Integer floor) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.isDuplicateRoomTypeFloor(propertyID, roomTypeName, floor);
    }
}
