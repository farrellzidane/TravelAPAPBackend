package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.PropertyRestServiceImpl;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomRestService;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomTypeRestService;

@Service
public class PropertyRestServiceRBACImpl extends PropertyRestServiceImpl implements PropertyRestServiceRBAC {

    private final AuthService authService;

    public PropertyRestServiceRBACImpl(
            PropertyRepository propertyRepository, 
            RoomTypeRestService roomTypeRestService,
            RoomRestService roomRestService,
            AuthService authService
        ) {
        super(propertyRepository, roomTypeRestService, roomRestService);
        this.authService = authService;
    }


    // Bikin function untuk masing2 role
    // override semua function dari PropertyRestService
    // Superadmin, Accomodation Owner, Customer
    // Superadmin, Accomodation Owner
    // Customer
    // API KEY

    // [GET] Get All Property - Superadmin, Accommodation Owner, Customer
    @Override
    public List<PropertyResponseDTO> getAllProperties() throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAllProperties();
    }

    // [GET] Get Property Details by Property ID - Superadmin, Accommodation Owner, Customer
    @Override
    public PropertyResponseDTO getPropertyById(String propertyId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getPropertyById(propertyId);
    }

    // [POST] Create Property - Superadmin, Accommodation Owner
    @Override
    public PropertyResponseDTO createProperty(CreatePropertyRequestDTO propertyDTO) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.createProperty(propertyDTO);
    }

    // [PUT] Edit Property Details - Superadmin, Accommodation Owner
    @Override
    public PropertyResponseDTO updateProperty(String propertyId, UpdatePropertyRequestDTO propertyDTO) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updateProperty(propertyId, propertyDTO);
    }

    // [DELETE] Delete Property - Superadmin, Accommodation Owner
    @Override
    public PropertyResponseDTO deleteProperty(String propertyId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.deleteProperty(propertyId);
    }
}
