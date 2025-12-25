package apap.ti._5.accommodation_2306275600_be.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserProfileDTO mockUser;
    private CustomerProfileDTO mockCustomer;
    private String authServiceUrl;

    @BeforeEach
    void setUp() {
        authServiceUrl = "https://travel-apap-mock-server.vercel.app";
        ReflectionTestUtils.setField(authService, "authServiceUrl", authServiceUrl);

        UUID userId = UUID.randomUUID();
        mockUser = new UserProfileDTO(
            userId,
            "testuser",
            "Test User",
            "test@example.com",
            "M",
            "Customer",
            LocalDateTime.now().minusDays(30),
            LocalDateTime.now(),
            false
        );

        mockCustomer = new CustomerProfileDTO(
            userId,
            "customer",
            "Customer User",
            "customer@example.com",
            "F",
            "Customer",
            LocalDateTime.now().minusDays(20),
            LocalDateTime.now(),
            false,
            50000L
        );
    }

    @Test
    void testGetAuthenticatedUser_ValidToken() throws Exception {
        // Arrange
        String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImMxYzFjMWMxLWMxYzEtYzFjMS1jMWMxLWMxYzFjMWMxYzFjMSIsInJvbGUiOiJDdXN0b21lciJ9.AIRg51HdixEkiDJP0afDZCCz1Z8EduexjZA8u85yEJs";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act
        UserProfileDTO result = authService.getAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertEquals("Customer", result.role());
        assertTrue(result.username().contains("mock_user_"));
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAuthenticatedUser_NoToken() {
        // Arrange
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authService.getAuthenticatedUser()
        );
        assertTrue(exception.getMessage().contains("No authorization token found"));
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAuthenticatedUser_InvalidTokenFormat() {
        // Arrange
        String invalidToken = "invalid.token";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authService.getAuthenticatedUser()
        );
        assertTrue(exception.getMessage().contains("Invalid JWT token format"));
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAuthenticatedUser_SuperAdmin() throws Exception {
        // Arrange
        String superAdminToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMSIsInJvbGUiOiJTdXBlcmFkbWluIn0.lJEnbqCnBRHd5VQGRpt2bhL6thuJc35qY5dupmg8dwI";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + superAdminToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act
        UserProfileDTO result = authService.getAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertEquals("Superadmin", result.role());
        assertEquals("Mock Superadmin", result.name());
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAuthenticatedUser_AccommodationOwner() throws Exception {
        // Arrange
        String ownerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjFhMmIzYzRkLTVlNmYtNzA4MC05MGEwLWIxYzJkM2U0ZjUwMSIsInJvbGUiOiJBY2NvbW1vZGF0aW9uIE93bmVyIn0.UQV7EuEaokBoLv8yB3Ti-wLAujzitxZmZ6g0fF4VJPI";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + ownerToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act
        UserProfileDTO result = authService.getAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertEquals("Accommodation Owner", result.role());
        assertEquals("Mock Accommodation Owner", result.name());
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAuthenticatedUser_NoRequestContext() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authService.getAuthenticatedUser()
        );
        assertTrue(exception.getMessage().contains("No authorization token found"));
    }

    @Test
    void testGetAuthenticatedUser_InvalidBase64() {
        // Arrange
        String invalidBase64Token = "header.invalid_base64!@#.signature";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + invalidBase64Token);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authService.getAuthenticatedUser()
        );
        assertTrue(exception.getMessage().contains("Failed to decode JWT token"));
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testIsSuperAdmin_UserProfile() {
        // Arrange
        UserProfileDTO superAdmin = new UserProfileDTO(
            UUID.randomUUID(),
            "admin",
            "Admin",
            "admin@example.com",
            "M",
            "Superadmin",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertTrue(authService.isSuperAdmin(superAdmin));
        assertFalse(authService.isSuperAdmin(mockUser));
    }

    @Test
    void testIsAccommodationOwner_UserProfile() {
        // Arrange
        UserProfileDTO owner = new UserProfileDTO(
            UUID.randomUUID(),
            "owner",
            "Owner",
            "owner@example.com",
            "F",
            "Accommodation Owner",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertTrue(authService.isAccommodationOwner(owner));
        assertFalse(authService.isAccommodationOwner(mockUser));
    }

    @Test
    void testIsAccommodationOwner_TypoVariant() {
        // Arrange
        UserProfileDTO ownerTypo = new UserProfileDTO(
            UUID.randomUUID(),
            "owner",
            "Owner",
            "owner@example.com",
            "F",
            "Accomodation Owner",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        // Act & Assert
        assertTrue(authService.isAccommodationOwner(ownerTypo));
    }

    @Test
    void testIsCustomer_UserProfile() {
        // Act & Assert
        assertTrue(authService.isCustomer(mockUser));
    }

    @Test
    void testIsCustomer_CustomerProfile() {
        // Act & Assert
        assertTrue(authService.isCustomer(mockCustomer));
    }

    @Test
    void testIsSuperAdmin_UserId() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        String superAdminToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMSIsInJvbGUiOiJTdXBlcmFkbWluIn0.lJEnbqCnBRHd5VQGRpt2bhL6thuJc35qY5dupmg8dwI";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + superAdminToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act
        boolean result = authService.isSuperAdmin(userId);

        // Assert
        assertTrue(result);
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testIsSuperAdmin_UserId_WrongId() throws Exception {
        // Arrange
        UUID wrongUserId = UUID.randomUUID();
        String superAdminToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMSIsInJvbGUiOiJTdXBlcmFkbWluIn0.lJEnbqCnBRHd5VQGRpt2bhL6thuJc35qY5dupmg8dwI";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + superAdminToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act
        boolean result = authService.isSuperAdmin(wrongUserId);

        // Assert
        assertFalse(result);
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testIsSuperAdmin_UserId_Exception() {
        // Arrange
        UUID userId = UUID.randomUUID();
        RequestContextHolder.resetRequestAttributes();

        // Act
        boolean result = authService.isSuperAdmin(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsAccommodationOwner_UserId() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("1a2b3c4d-5e6f-7080-90a0-b1c2d3e4f501");
        String ownerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjFhMmIzYzRkLTVlNmYtNzA4MC05MGEwLWIxYzJkM2U0ZjUwMSIsInJvbGUiOiJBY2NvbW1vZGF0aW9uIE93bmVyIn0.UQV7EuEaokBoLv8yB3Ti-wLAujzitxZmZ6g0fF4VJPI";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + ownerToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act
        boolean result = authService.isAccommodationOwner(userId);

        // Assert
        assertTrue(result);
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testIsAccommodationOwner_UserId_Exception() {
        // Arrange
        UUID userId = UUID.randomUUID();
        RequestContextHolder.resetRequestAttributes();

        // Act
        boolean result = authService.isAccommodationOwner(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsCustomer_UserId() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1");
        String customerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImMxYzFjMWMxLWMxYzEtYzFjMS1jMWMxLWMxYzFjMWMxYzFjMSIsInJvbGUiOiJDdXN0b21lciJ9.AIRg51HdixEkiDJP0afDZCCz1Z8EduexjZA8u85yEJs";
        
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + customerToken);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act
        boolean result = authService.isCustomer(userId);

        // Assert
        assertTrue(result);
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testIsCustomer_UserId_Exception() {
        // Arrange
        UUID userId = UUID.randomUUID();
        RequestContextHolder.resetRequestAttributes();

        // Act
        boolean result = authService.isCustomer(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetCustomerProfile() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        // Act
        CustomerProfileDTO result = authService.getCustomerProfile(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(customerId, result.userId());
        assertEquals("Customer", result.role());
        assertEquals("Mock Customer", result.name());
        assertEquals(0L, result.saldo());
    }

    @Test
    void testGetAllAccommodationOwner_Success() {
        // Arrange
        UserProfileDTO owner1 = new UserProfileDTO(
            UUID.randomUUID(),
            "owner1",
            "Owner 1",
            "owner1@example.com",
            "M",
            "Accommodation Owner",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
        
        UserProfileDTO owner2 = new UserProfileDTO(
            UUID.randomUUID(),
            "owner2",
            "Owner 2",
            "owner2@example.com",
            "F",
            "Accommodation Owner",
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );

        List<UserProfileDTO> owners = Arrays.asList(owner1, owner2);
        
        BaseResponseDTO<List<UserProfileDTO>> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(owners);
        
        ResponseEntity<BaseResponseDTO<List<UserProfileDTO>>> responseEntity = ResponseEntity.ok(responseDTO);

        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<UserProfileDTO> result = authService.getAllAccommodationOwner();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Accommodation Owner", result.get(0).role());
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAllAccommodationOwner_Exception() {
        // Arrange
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("No owners found"));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.getAllAccommodationOwner());
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAllAccommodationOwner_ResourceAccessException() {
        // Arrange
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Timeout"));

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authService.getAllAccommodationOwner()
        );
        assertTrue(exception.getMessage().contains("Connection to auth server failed or timed out"));
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAllAccommodationOwner_NullPointerException() {
        // Arrange
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        ResponseEntity<BaseResponseDTO<List<UserProfileDTO>>> responseEntity = ResponseEntity.ok(null);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> authService.getAllAccommodationOwner());
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAllAccommodationOwner_GenericException() {
        // Arrange
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authService.getAllAccommodationOwner()
        );
        assertTrue(exception.getMessage().contains("An unexpected error occurred"));
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetAuthenticatedUser_NoBearerPrefix() {
        // Arrange
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("InvalidToken");
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> authService.getAuthenticatedUser()
        );
        assertTrue(exception.getMessage().contains("No authorization token found"));
        
        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }
}
