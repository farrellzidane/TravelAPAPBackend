package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.AccommodationOwner;
import apap.ti._5.accommodation_2306275600_be.model.Customer;
import apap.ti._5.accommodation_2306275600_be.model.EndUser;
import apap.ti._5.accommodation_2306275600_be.repository.AccommodationOwnerRepository;
import apap.ti._5.accommodation_2306275600_be.repository.CustomerRepository;
import apap.ti._5.accommodation_2306275600_be.repository.EndUserRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.LoginRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.RegisterRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.UpdateUserRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.CustomerResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.EndUserResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.LoginResponseDTO;
import apap.ti._5.accommodation_2306275600_be.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthRestServiceImpl implements AuthRestService {
    
    private final EndUserRepository endUserRepository;
    private final CustomerRepository customerRepository;
    private final AccommodationOwnerRepository accommodationOwnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Override
    public List<EndUserResponseDTO> getAllUsers() {
        return endUserRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CustomerResponseDTO> getAllCustomers(String search, String gender) {
        List<Customer> customers;
        
        if (search != null && !search.trim().isEmpty()) {
            customers = customerRepository.searchCustomers(search.trim());
        } else {
            customers = customerRepository.findAllOrderByCreatedAtDesc();
        }
        
        // Apply gender filter if provided
        if (gender != null && !gender.trim().isEmpty()) {
            customers = customers.stream()
                    .filter(c -> gender.equalsIgnoreCase(c.getGender()))
                    .collect(Collectors.toList());
        }
        
        return customers.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Find user by email or username
        EndUser user = endUserRepository.findByEmailOrUsername(request.getIdentifier())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId().toString(), user.getUsername(), user.getRole());
        
        // Set JWT in httpOnly cookie
        setJwtCookie(token);
        
        return new LoginResponseDTO(
                user.getId().toString(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }
    
    @Override
    @Transactional
    public EndUserResponseDTO register(RegisterRequestDTO request) {
        // Check if email already exists
        if (endUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Check if username already exists
        if (endUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        
        // Validate role
        if (!"CUSTOMER".equals(request.getRole()) && !"ACCOMMODATION_OWNER".equals(request.getRole())) {
            throw new RuntimeException("Invalid role. Must be CUSTOMER or ACCOMMODATION_OWNER");
        }
        
        EndUser savedUser;
        
        // Create user based on selected role
        if ("CUSTOMER".equals(request.getRole())) {
            Customer customer = new Customer();
            customer.setUsername(request.getUsername());
            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
            customer.setRole("CUSTOMER");
            customer.setGender(request.getGender());
            customer.setSaldo(BigDecimal.ZERO);
            
            savedUser = customerRepository.save(customer);
        } else {
            // ACCOMMODATION_OWNER
            AccommodationOwner owner = new AccommodationOwner();
            owner.setUsername(request.getUsername());
            owner.setName(request.getName());
            owner.setEmail(request.getEmail());
            owner.setPassword(passwordEncoder.encode(request.getPassword()));
            owner.setRole("ACCOMMODATION_OWNER");
            owner.setGender(request.getGender());
            
            savedUser = accommodationOwnerRepository.save(owner);
        }
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public EndUserResponseDTO getUserByIdentifier(String identifier) {
        EndUser user = endUserRepository.findByEmailOrUsername(identifier)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user instanceof Customer) {
            return convertToCustomerDTO((Customer) user);
        }
        
        return convertToDTO(user);
    }
    
    @Override
    @Transactional
    public EndUserResponseDTO updateUser(String userId, UpdateUserRequestDTO request, String requesterId, String requesterRole) {
        EndUser user = endUserRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Authorization checks
        boolean isSuperAdmin = "SUPERADMIN".equals(requesterRole);
        boolean isOwner = "ACCOMMODATION_OWNER".equals(requesterRole);
        boolean isSelf = userId.equals(requesterId);
        
        // Only SUPERADMIN can update anyone, or user can update self
        if (!isSuperAdmin && !isSelf && !(isOwner && "CUSTOMER".equals(user.getRole()))) {
            throw new RuntimeException("You don't have permission to update this user");
        }
        
        // Update fields if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Check if email is already taken by another user
            if (endUserRepository.existsByEmail(request.getEmail()) && 
                !user.getEmail().equals(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getGender() != null && !request.getGender().isBlank()) {
            user.setGender(request.getGender());
        }
        
        // Only SUPERADMIN can change role
        if (request.getRole() != null && !request.getRole().isBlank()) {
            if (!isSuperAdmin) {
                throw new RuntimeException("Only SUPERADMIN can change user roles");
            }
            user.setRole(request.getRole());
        }
        
        EndUser updatedUser = endUserRepository.save(user);
        
        if (updatedUser instanceof Customer) {
            return convertToCustomerDTO((Customer) updatedUser);
        }
        
        return convertToDTO(updatedUser);
    }
    
    @Override
    public EndUserResponseDTO convertToDTO(EndUser user) {
        EndUserResponseDTO dto = new EndUserResponseDTO();
        dto.setId(user.getId().toString());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setGender(user.getGender());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    
    @Override
    public CustomerResponseDTO convertToCustomerDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId().toString());
        dto.setUsername(customer.getUsername());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setRole(customer.getRole());
        dto.setGender(customer.getGender());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        dto.setSaldo(customer.getSaldo());
        return dto;
    }
    
    private void setJwtCookie(String token) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            if (response != null) {
                // Create JWT cookie
                Cookie cookie = new Cookie("jwt", token);
                cookie.setHttpOnly(true);
                cookie.setSecure(false); // Set to true in production with HTTPS
                cookie.setPath("/");
                cookie.setMaxAge((int) (jwtExpiration / 1000)); // Convert ms to seconds
                
                // Add SameSite attribute manually via Set-Cookie header
                String cookieHeader = String.format(
                    "jwt=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax",
                    token,
                    (int) (jwtExpiration / 1000)
                );
                response.setHeader("Set-Cookie", cookieHeader);
            }
        }
    }
}
