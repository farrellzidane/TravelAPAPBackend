package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.LoginRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.RegisterRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.UpdateUserRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.CustomerResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.EndUserResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.LoginResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.AuthRestService;
import apap.ti._5.accommodation_2306275600_be.security.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    
    private final AuthRestService authRestService;
    
    /**
     * Get all users (SUPERADMIN only)
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<EndUserResponseDTO> users = authRestService.getAllUsers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Users retrieved successfully");
            response.put("data", users);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all customers with optional search and gender filter
     * (SUPERADMIN and ACCOMMODATION_OWNER can access)
     */
    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ACCOMMODATION_OWNER')")
    public ResponseEntity<Map<String, Object>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender) {
        try {
            List<CustomerResponseDTO> customers = authRestService.getAllCustomers(search, gender);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Customers retrieved successfully");
            response.put("data", customers);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Login endpoint - returns JWT in httpOnly cookie
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO loginResponse = authRestService.login(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", loginResponse.getMessage());
            response.put("data", loginResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * Register new customer
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            EndUserResponseDTO user = authRestService.register(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Registration successful");
            response.put("data", user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get user details by email or username
     * Authenticated users can access
     */
    @GetMapping("/user/{identifier}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserByIdentifier(@PathVariable String identifier) {
        try {
            EndUserResponseDTO user = authRestService.getUserByIdentifier(identifier);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User retrieved successfully");
            response.put("data", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Update user information
     * Business rules enforced in service layer
     */
    @PutMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequestDTO request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            EndUserResponseDTO updatedUser = authRestService.updateUser(
                    userId, 
                    request, 
                    userPrincipal.getUserId(), 
                    userPrincipal.getRole()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User updated successfully");
            response.put("data", updatedUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Logout endpoint - clears JWT cookie
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        try {
            // Clear the JWT cookie
            Cookie cookie = new Cookie("jwt", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Required for HTTPS in production
            cookie.setPath("/");
            cookie.setMaxAge(0); // Delete cookie
            
            // Add SameSite=None for cross-domain cookie deletion
            String cookieHeader = "jwt=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=None";
            response.setHeader("Set-Cookie", cookieHeader);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("message", "Logout successful");
            
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "error");
            responseBody.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }
}
