package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
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

    private static BookingRepository bookingRepository;
    private final AuthService authService;

    public PropertyRestServiceRBACImpl(
            PropertyRepository propertyRepository, 
            RoomTypeRestService roomTypeRestService,
            RoomRestService roomRestService,
            AuthService authService
        ) {
        super(propertyRepository, roomTypeRestService, roomRestService, bookingRepository);
        this.authService = authService;
    }


    // Bikin function untuk masing2 role
    // override semua function dari PropertyRestService
    // Superadmin, Accomodation Owner, Customer
    // Superadmin, Accomodation Owner
    // Customer
    // API KEY

    // [GET] Get All Property
    // - Superadmin: Dapat melihat semua property
    // - Accommodation Owner: Hanya melihat property yang dimilikinya
    // - Customer: Dapat melihat semua property
    @Override
    public List<PropertyResponseDTO> getAllProperties() throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Accommodation Owner hanya dapat melihat property miliknya
        if (authService.isAccommodationOwner(user)) {
            return super.getPropertiesByOwner(user.userId());
        }
        
        // Superadmin dan Customer dapat melihat semua property
        return super.getAllProperties();
    }

    // [GET] Get Filtered Properties
    // - Superadmin: Dapat filter semua property
    // - Accommodation Owner: Hanya filter property yang dimilikinya
    // - Customer: Dapat filter semua property
    @Override
    public List<PropertyResponseDTO> getFilteredProperties(String name, Integer type, Integer province) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Get filtered properties
        List<PropertyResponseDTO> filteredProperties = super.getFilteredProperties(name, type, province);
        
        // Accommodation Owner hanya dapat melihat property miliknya
        if (authService.isAccommodationOwner(user)) {
            return filteredProperties.stream()
                    .filter(p -> UUID.fromString(p.getOwnerID()).equals(user.userId()))
                    .collect(Collectors.toList());
        }
        
        return filteredProperties;
    }

    // [GET] Get Property Details by Property ID
    // - Superadmin: Dapat melihat detail semua property
    // - Accommodation Owner: Hanya dapat melihat detail property yang dimilikinya
    // - Customer: Dapat melihat detail semua property
    @Override
    public PropertyResponseDTO getPropertyById(UUID propertyId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        PropertyResponseDTO property = super.getPropertyById(propertyId);
        
        if (property == null) {
            return null;
        }
        
        // Accommodation Owner hanya dapat melihat property miliknya
        if (authService.isAccommodationOwner(user) && !UUID.fromString(property.getOwnerID()).equals(user.userId())) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke property ini");
        }
        
        return property;
    }

    // [POST] Create Property
    // - Superadmin: Dapat create property dan assign ownerID via DTO
    // - Accommodation Owner: Dapat create property dan otomatis menjadi pemilik (gunakan user.id dari token)
    // - Customer: TIDAK BISA create property
    @Override
    public PropertyResponseDTO createProperty(CreatePropertyRequestDTO propertyDTO) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Customer tidak memiliki akses untuk membuat property");
        }
        
        // Jika Accommodation Owner, set ownerID otomatis dari user yang login
        if (authService.isAccommodationOwner(user)) {
            propertyDTO.setOwnerID(user.userId().toString());
            // TODO: Fetch owner name from user service and set it
            // For now, keep the ownerName from DTO or set a default
        }
        // Jika Superadmin, gunakan ownerID dari DTO (yang dipilih dari dropdown)
        
        return super.createProperty(propertyDTO);
    }

    // [PUT] Edit Property Details
    // - Superadmin: Dapat update semua property dan dapat mengubah ownerID
    // - Accommodation Owner: Hanya dapat update property yang dimilikinya
    @Override
    public PropertyResponseDTO updateProperty(UUID propertyId, UpdatePropertyRequestDTO propertyDTO) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Get existing property to check ownership
        PropertyResponseDTO existingProperty = super.getPropertyById(propertyId);
        
        if (existingProperty == null) {
            throw new AccessDeniedException("Property tidak ditemukan");
        }
        
        // Accommodation Owner hanya dapat update property miliknya
        if (authService.isAccommodationOwner(user) && !UUID.fromString(existingProperty.getOwnerID()).equals(user.userId())) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk update property ini");
        }
        
        return super.updateProperty(propertyId, propertyDTO);
    }

    // [DELETE] Delete Property
    // - Superadmin: Dapat delete semua property
    // - Accommodation Owner: Hanya dapat delete property yang dimilikinya
    @Override
    public PropertyResponseDTO deleteProperty(UUID propertyId) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Get existing property to check ownership
        PropertyResponseDTO existingProperty = super.getPropertyById(propertyId);
        
        if (existingProperty == null) {
            throw new AccessDeniedException("Property tidak ditemukan");
        }
        
        // Accommodation Owner hanya dapat delete property miliknya
        if (authService.isAccommodationOwner(user) && !UUID.fromString(existingProperty.getOwnerID()).equals(user.userId())) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk delete property ini");
        }
        
        return super.deleteProperty(propertyId);
    }
}
