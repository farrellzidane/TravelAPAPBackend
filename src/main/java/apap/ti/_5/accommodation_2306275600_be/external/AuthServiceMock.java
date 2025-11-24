package apap.ti._5.accommodation_2306275600_be.external;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;


public interface AuthServiceMock {
    public UserProfileDTO getSuperAdminUser() throws AccessDeniedException;

    public UserProfileDTO getAccommodationOwnerUser() throws AccessDeniedException;
    
    public CustomerProfileDTO getCustomerUser() throws AccessDeniedException;
}
