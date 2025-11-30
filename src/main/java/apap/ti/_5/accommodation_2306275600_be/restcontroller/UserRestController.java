package apap.ti._5.accommodation_2306275600_be.restcontroller;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserRestController {

    private final AuthService authService;

    @GetMapping("/accommodation-owners")
    public ResponseEntity<BaseResponseDTO<List<UserProfileDTO>>> getAccommodationOwners() {
        try {
            // Call external API through AuthService to get all accommodation owners
            List<UserProfileDTO> owners = authService.getAllAccommodationOwner();
            
            var response = new BaseResponseDTO<List<UserProfileDTO>>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Successfully retrieved accommodation owners");
            response.setData(owners);
            response.setTimestamp(new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            var response = new BaseResponseDTO<List<UserProfileDTO>>();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Failed to retrieve accommodation owners: " + e.getMessage());
            response.setTimestamp(new Date());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
