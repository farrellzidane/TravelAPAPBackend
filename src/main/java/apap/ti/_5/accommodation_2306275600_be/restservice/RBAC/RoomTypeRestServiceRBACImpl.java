package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.external.AuthServiceMock;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomTypeRestServiceImpl;

@Service
public class RoomTypeRestServiceRBACImpl extends RoomTypeRestServiceImpl implements RoomTypeRestServiceRBAC {

    private final AuthService authService;
    private final AuthServiceMock authServiceMock;

    public RoomTypeRestServiceRBACImpl(
            RoomTypeRepository roomTypeRepository,
            PropertyRepository propertyRepository,
            RoomRepository roomRepository,
            AuthService authService,
            AuthServiceMock authServiceMock
        ) {
        super(roomTypeRepository, propertyRepository, roomRepository);
        this.authService = authService;
        this.authServiceMock = authServiceMock;
    }

    // [POST] Create Room Type - Superadmin, Accommodation Owner
    @Override
    public RoomTypeResponseDTO createRoomType(CreateRoomTypeRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.createRoomType(dto);
    }

    // [GET] Get Room Type Details by Room Type ID - Superadmin, Accommodation Owner, Customer
    @Override
    public RoomTypeResponseDTO getRoomTypeById(String roomTypeID) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomTypeById(roomTypeID);
    }

    // [GET] Get All Room Types - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomTypeResponseDTO> getAllRoomTypes() throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAllRoomTypes();
    }

    // [GET] Get Room Types by Property ID - Superadmin, Accommodation Owner, Customer
    @Override
    public List<RoomTypeResponseDTO> getRoomTypesByProperty(String propertyID) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getRoomTypesByProperty(propertyID);
    }

    // [PUT] Update Room Type - Superadmin, Accommodation Owner
    @Override
    public RoomTypeResponseDTO updateRoomType(String roomTypeID, UpdateRoomTypeRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updateRoomType(roomTypeID, dto);
    }

    // [DELETE] Delete Room Type - Superadmin, Accommodation Owner
    @Override
    public void deleteRoomType(String roomTypeID) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        super.deleteRoomType(roomTypeID);
    }

    // [UTIL] Check Duplicate Room Type Floor - Superadmin, Accommodation Owner
    @Override
    public boolean isDuplicateRoomTypeFloor(String propertyID, String roomTypeName, Integer floor) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.isDuplicateRoomTypeFloor(propertyID, roomTypeName, floor);
    }
}
