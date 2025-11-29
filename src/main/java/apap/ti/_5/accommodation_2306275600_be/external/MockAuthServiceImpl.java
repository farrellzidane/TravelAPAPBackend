package apap.ti._5.accommodation_2306275600_be.external;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Mock implementation of AuthService for development.
 * Decodes JWT tokens locally without calling external auth service.
 * Active only when spring profile is "dev".
 */
@Service
@Profile("dev")
public class MockAuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(MockAuthServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String SUPERADMIN = "Superadmin";
    private static final String ACCOMMODATION_OWNER = "Accommodation Owner";
    private static final String ACCOMMODATION_OWNER_TYPO = "Accomodation Owner"; // Typo variant from SSO
    private static final String CUSTOMER = "Customer";

    @Override
    public UserProfileDTO getAuthenticatedUser() throws AccessDeniedException {
        try {
            String token = extractBearerToken();
            if (token == null) {
                throw new AccessDeniedException("No authorization token found");
            }

            // Decode JWT token (format: header.payload.signature)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new AccessDeniedException("Invalid JWT token format");
            }

            // Decode payload (base64)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            logger.info("Mock Auth - Decoded JWT payload: {}", payload);

            // Parse JSON payload
            JsonNode payloadJson = objectMapper.readTree(payload);
            
            // Extract userId and role from payload
            String userIdStr = payloadJson.get("id").asText();
            String role = payloadJson.get("role").asText();
            UUID userId = UUID.fromString(userIdStr);

            logger.info("Mock Auth - User ID: {}, Role: {}", userId, role);

            // Create mock user profile
            UserProfileDTO user = new UserProfileDTO(
                userId,
                "mock_user_" + userId.toString().substring(0, 8),
                getMockNameByRole(role),
                "mock@example.com",
                "M",
                role,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                false
            );

            return user;

        } catch (Exception e) {
            logger.error("Mock Auth - Error decoding JWT token: {}", e.getMessage(), e);
            throw new AccessDeniedException("Failed to decode JWT token: " + e.getMessage());
        }
    }

    @Override
    public boolean isSuperAdmin(UserProfileDTO userProfile) {
        return SUPERADMIN.equals(userProfile.role());
    }

    @Override
    public boolean isAccommodationOwner(UserProfileDTO userProfile) {
        return ACCOMMODATION_OWNER.equals(userProfile.role()) || 
               ACCOMMODATION_OWNER_TYPO.equals(userProfile.role());
    }

    @Override
    public boolean isCustomer(UserProfileDTO userProfile) {
        return CUSTOMER.equals(userProfile.role());
    }

    @Override
    public boolean isCustomer(CustomerProfileDTO customerProfile) {
        return CUSTOMER.equals(customerProfile.role());
    }

    @Override
    public boolean isSuperAdmin(UUID userId) {
        try {
            UserProfileDTO user = getAuthenticatedUser();
            return user.userId().equals(userId) && isSuperAdmin(user);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isAccommodationOwner(UUID userId) {
        try {
            UserProfileDTO user = getAuthenticatedUser();
            return user.userId().equals(userId) && isAccommodationOwner(user);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isCustomer(UUID userId) {
        try {
            UserProfileDTO user = getAuthenticatedUser();
            return user.userId().equals(userId) && isCustomer(user);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CustomerProfileDTO getCustomerProfile(UUID customerId) throws NoSuchElementException {
        return new CustomerProfileDTO(
            customerId,
            "mock_customer_" + customerId.toString().substring(0, 8),
            "Mock Customer",
            "customer@example.com",
            "M",
            "Customer",
            LocalDateTime.now().minusDays(30),
            LocalDateTime.now(),
            false,
            0L
        );
    }

    private String extractBearerToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        if (Objects.isNull(request)) {
            return null;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7); // Remove "Bearer " prefix
    }

    private String getMockNameByRole(String role) {
        return switch (role) {
            case "Superadmin" -> "Mock Superadmin";
            case "Accommodation Owner" -> "Mock Accommodation Owner";
            case "Customer" -> "Mock Customer";
            default -> "Mock User";
        };
    }
}
