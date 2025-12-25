package apap.ti._5.accommodation_2306275600_be.restcontroller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306275600_be.model.AccommodationOwner;
import apap.ti._5.accommodation_2306275600_be.repository.AccommodationOwnerRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserRestController {

    private final AccommodationOwnerRepository accommodationOwnerRepository;

    @GetMapping("/accommodation-owners")
    public ResponseEntity<BaseResponseDTO<List<UserProfileDTO>>> getAccommodationOwners() {
        try {
            // Get all accommodation owners from local database
            List<AccommodationOwner> owners = accommodationOwnerRepository.findAll();
            
            // Convert to UserProfileDTO
            List<UserProfileDTO> ownerDTOs = owners.stream()
                .map(owner -> new UserProfileDTO(
                    owner.getId(),
                    owner.getUsername(),
                    owner.getName(),
                    owner.getEmail(),
                    owner.getGender(),
                    "ACCOMMODATION_OWNER",
                    owner.getCreatedAt(),
                    owner.getUpdatedAt(),
                    false
                ))
                .collect(Collectors.toList());
            
            var response = new BaseResponseDTO<List<UserProfileDTO>>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Successfully retrieved accommodation owners");
            response.setData(ownerDTOs);
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
