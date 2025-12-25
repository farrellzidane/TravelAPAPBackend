package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.Customer;
import apap.ti._5.accommodation_2306275600_be.model.EndUser;
import apap.ti._5.accommodation_2306275600_be.restdto.request.LoginRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.RegisterRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.UpdateUserRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.CustomerResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.EndUserResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.LoginResponseDTO;

import java.util.List;

public interface AuthRestService {
    
    /**
     * Get all users (SUPERADMIN only)
     */
    List<EndUserResponseDTO> getAllUsers();
    
    /**
     * Get all customers with optional search and gender filter
     * @param search Search term for name, email, or username
     * @param gender Gender filter (MALE, FEMALE, OTHER)
     */
    List<CustomerResponseDTO> getAllCustomers(String search, String gender);
    
    /**
     * Login user with email/username and password
     * Returns JWT token in cookie
     */
    LoginResponseDTO login(LoginRequestDTO request);
    
    /**
     * Register new customer
     */
    EndUserResponseDTO register(RegisterRequestDTO request);
    
    /**
     * Get user details by identifier (email or username)
     */
    EndUserResponseDTO getUserByIdentifier(String identifier);
    
    /**
     * Update user information
     * Business rules:
     * - Users can update their own info (except role)
     * - SUPERADMIN can update anyone's info including role
     * - ACCOMMODATION_OWNER can update customer info (except role)
     */
    EndUserResponseDTO updateUser(String userId, UpdateUserRequestDTO request, String requesterId, String requesterRole);
    
    /**
     * Convert EndUser to DTO
     */
    EndUserResponseDTO convertToDTO(EndUser user);
    
    /**
     * Convert Customer to DTO
     */
    CustomerResponseDTO convertToCustomerDTO(Customer customer);
}
