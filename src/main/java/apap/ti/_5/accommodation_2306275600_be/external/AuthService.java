package apap.ti._5.accommodation_2306275600_be.external;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;

public interface AuthService {
    public UserProfileDTO getAuthenticatedUser() throws AccessDeniedException;

    public boolean isSuperAdmin(UserProfileDTO userProfile);

    public boolean isAccommodationOwner(UserProfileDTO userProfile);

    public boolean isCustomer(UserProfileDTO userProfile);

    public boolean isCustomer(CustomerProfileDTO customerProfile);

    public boolean isSuperAdmin(UUID userId);

    public boolean isAccommodationOwner(UUID userId);

    public boolean isCustomer(UUID userId);

    public CustomerProfileDTO getCustomerProfile(UUID userId) throws NoSuchElementException;

    public List<UserProfileDTO> getAllAccommodationOwner();
}
