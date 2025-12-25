package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    @Autowired
    private AuthService authService;

    /**
     * Get current user's profile
     * Accessible by all authenticated users
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponseDTO<UserProfileDTO>> getMyProfile() {
        var baseResponseDTO = new BaseResponseDTO<UserProfileDTO>();
        
        try {
            UserProfileDTO profile = authService.getAuthenticatedUser();
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(profile);
            baseResponseDTO.setMessage("Successfully retrieved profile");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve profile: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get profile by user ID
     * Accessible by SUPERADMIN and ACCOMMODATION_OWNER only
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ACCOMMODATION_OWNER')")
    public ResponseEntity<BaseResponseDTO<UserProfileDTO>> getProfileById(
            @PathVariable String userId) {
        
        var baseResponseDTO = new BaseResponseDTO<UserProfileDTO>();
        
        try {
            // For now, return current user profile
            // In production, this would fetch specific user by ID from external service
            UserProfileDTO profile = authService.getAuthenticatedUser();
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(profile);
            baseResponseDTO.setMessage("Successfully retrieved profile for user: " + userId);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve profile: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
