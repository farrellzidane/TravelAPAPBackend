package apap.ti._5.accommodation_2306275600_be.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.CustomerProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;

@ExtendWith(MockitoExtension.class)
class AuthServiceMockImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceMockImpl authServiceMock;

    private UserProfileDTO mockSuperAdminUser;
    private UserProfileDTO mockAccommodationOwnerUser;
    private CustomerProfileDTO mockCustomerUser;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "https://travel-apap-mock-server.vercel.app/";
        ReflectionTestUtils.setField(authServiceMock, "be1Url", baseUrl);

        UUID superAdminId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        mockSuperAdminUser = new UserProfileDTO(
            superAdminId,
            "superadmin",
            "Super Admin User",
            "superadmin@example.com",
            "M",
            "Superadmin",
            LocalDateTime.now().minusDays(100),
            LocalDateTime.now(),
            false
        );

        UUID accommodationOwnerId = UUID.fromString("1a2b3c4d-5e6f-7080-90a0-b1c2d3e4f501");
        mockAccommodationOwnerUser = new UserProfileDTO(
            accommodationOwnerId,
            "owner",
            "Accommodation Owner",
            "owner@example.com",
            "F",
            "Accommodation Owner",
            LocalDateTime.now().minusDays(50),
            LocalDateTime.now(),
            false
        );

        UUID customerId = UUID.fromString("c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1");
        mockCustomerUser = new CustomerProfileDTO(
            customerId,
            "customer",
            "Customer User",
            "customer@example.com",
            "M",
            "Customer",
            LocalDateTime.now().minusDays(30),
            LocalDateTime.now(),
            false,
            100000L
        );
    }

    @Test
    void testGetSuperAdminUser_Success() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockSuperAdminUser);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        UserProfileDTO result = authServiceMock.getSuperAdminUser();

        // Assert
        assertNotNull(result);
        assertEquals("Superadmin", result.role());
        assertEquals("superadmin", result.username());
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void testGetAccommodationOwnerUser_Success() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockAccommodationOwnerUser);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        UserProfileDTO result = authServiceMock.getAccommodationOwnerUser();

        // Assert
        assertNotNull(result);
        assertEquals("Accommodation Owner", result.role());
        assertEquals("owner", result.username());
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void testGetCustomerUser_Success() {
        // Arrange
        BaseResponseDTO<CustomerProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockCustomerUser);
        
        ResponseEntity<BaseResponseDTO<CustomerProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        CustomerProfileDTO result = authServiceMock.getCustomerUser();

        // Assert
        assertNotNull(result);
        assertEquals("Customer", result.role());
        assertEquals("customer", result.username());
        assertEquals(100000L, result.saldo());
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void testGetSuperAdminUser_Exception() {
        // Arrange  
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new IllegalArgumentException("Invalid request"));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authServiceMock.getSuperAdminUser());
    }

    @Test
    void testGetAccommodationOwnerUser_ResourceAccessException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Connection timeout"));

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class, 
            () -> authServiceMock.getAccommodationOwnerUser()
        );
        assertTrue(exception.getMessage().contains("Connection to auth server timed out"));
    }

    @Test
    void testGetCustomerUser_NullPointerException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new NullPointerException("Response body is null"));

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class, 
            () -> authServiceMock.getCustomerUser()
        );
        assertTrue(exception.getMessage().contains("NPE occurred"));
    }

    @Test
    void testGetSuperAdminUser_GenericException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class, 
            () -> authServiceMock.getSuperAdminUser()
        );
        assertTrue(exception.getMessage().contains("An unexpected error occurred"));
    }

    @Test
    void testGetSuperAdminUser_ResponseBodyNull() {
        // Arrange
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(null);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authServiceMock.getSuperAdminUser());
    }

    @Test
    void testGetAccommodationOwnerUser_ResponseDataNull() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(null);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authServiceMock.getAccommodationOwnerUser());
    }

    @Test
    void testGetCustomerUser_ResponseBodyAndDataNull() {
        // Arrange
        ResponseEntity<BaseResponseDTO<CustomerProfileDTO>> responseEntity = ResponseEntity.ok(null);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authServiceMock.getCustomerUser());
    }

    @Test
    void testConstructor() {
        // Act
        AuthServiceMockImpl service = new AuthServiceMockImpl(restTemplate);

        // Assert
        assertNotNull(service);
    }

    @Test
    void testGetSuperAdminUser_VerifyUrl() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockSuperAdminUser);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            eq(baseUrl + "api/auth/user"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        UserProfileDTO result = authServiceMock.getSuperAdminUser();

        // Assert
        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(
            eq(baseUrl + "api/auth/user"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void testGetSuperAdminUser_MultipleSuccessfulCalls() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockSuperAdminUser);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        UserProfileDTO result1 = authServiceMock.getSuperAdminUser();
        UserProfileDTO result2 = authServiceMock.getSuperAdminUser();

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.userId(), result2.userId());
        verify(restTemplate, times(2)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void testGetAccommodationOwnerUser_VerifyToken() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockAccommodationOwnerUser);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        UserProfileDTO result = authServiceMock.getAccommodationOwnerUser();

        // Assert
        assertNotNull(result);
        assertEquals(mockAccommodationOwnerUser.userId(), result.userId());
    }

    @Test
    void testGetCustomerUser_VerifyToken() {
        // Arrange
        BaseResponseDTO<CustomerProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockCustomerUser);
        
        ResponseEntity<BaseResponseDTO<CustomerProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        CustomerProfileDTO result = authServiceMock.getCustomerUser();

        // Assert
        assertNotNull(result);
        assertEquals(mockCustomerUser.saldo(), result.saldo());
    }

    @Test
    void testGetSuperAdminUser_ThrowsIllegalStateException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new IllegalStateException("Illegal state error"));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authServiceMock.getSuperAdminUser());
        assertTrue(exception.getMessage().contains("An unexpected error occurred"));
    }

    @Test
    void testGetAccommodationOwnerUser_ThrowsIllegalArgumentException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new IllegalArgumentException("Invalid argument"));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authServiceMock.getAccommodationOwnerUser());
        assertTrue(exception.getMessage().contains("An unexpected error occurred"));
    }

    @Test
    void testGetCustomerUser_EmptyResponseBody() {
        // Arrange
        ResponseEntity<BaseResponseDTO<CustomerProfileDTO>> emptyEntity = 
            new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(emptyEntity);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authServiceMock.getCustomerUser());
    }

    @Test
    void testGetSuperAdminUser_EmptyData() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(null);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authServiceMock.getSuperAdminUser());
    }

    @Test
    void testGetAccommodationOwnerUser_VerifyUserData() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockAccommodationOwnerUser);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        UserProfileDTO result = authServiceMock.getAccommodationOwnerUser();

        // Assert
        assertNotNull(result);
        assertEquals(mockAccommodationOwnerUser.email(), result.email());
        assertEquals(mockAccommodationOwnerUser.gender(), result.gender());
        assertEquals(mockAccommodationOwnerUser.role(), result.role());
    }

    @Test
    void testGetCustomerUser_VerifyAllFields() {
        // Arrange
        BaseResponseDTO<CustomerProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockCustomerUser);
        
        ResponseEntity<BaseResponseDTO<CustomerProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        CustomerProfileDTO result = authServiceMock.getCustomerUser();

        // Assert
        assertNotNull(result);
        assertEquals(mockCustomerUser.userId(), result.userId());
        assertEquals(mockCustomerUser.email(), result.email());
        assertEquals(mockCustomerUser.role(), result.role());
        assertEquals(mockCustomerUser.saldo(), result.saldo());
        assertEquals(mockCustomerUser.gender(), result.gender());
    }

    @Test
    void testConstructor_WithCustomBaseUrl() {
        // Arrange
        RestTemplate customRestTemplate = mock(RestTemplate.class);
        
        // Act
        AuthServiceMockImpl customService = new AuthServiceMockImpl(customRestTemplate);
        
        // Assert
        assertNotNull(customService);
    }

    @Test
    void testGetSuperAdminUser_VerifyRestTemplateCalledOnce() {
        // Arrange
        BaseResponseDTO<UserProfileDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setStatus(200);
        responseDTO.setMessage("Success");
        responseDTO.setData(mockSuperAdminUser);
        
        ResponseEntity<BaseResponseDTO<UserProfileDTO>> responseEntity = ResponseEntity.ok(responseDTO);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        authServiceMock.getSuperAdminUser();

        // Assert
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }
}
