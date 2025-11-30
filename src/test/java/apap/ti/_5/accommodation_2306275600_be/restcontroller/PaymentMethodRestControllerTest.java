package apap.ti._5.accommodation_2306275600_be.restcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.CreatePaymentMethodRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.UpdatePaymentMethodStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod.PaymentMethodResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.PaymentMethodRestServiceRBAC;

@ExtendWith(MockitoExtension.class)
class PaymentMethodRestControllerTest {

    @Mock
    private PaymentMethodRestServiceRBAC paymentMethodRestService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PaymentMethodRestController paymentMethodRestController;

    private PaymentMethodResponseDTO mockPaymentMethod;
    private CreatePaymentMethodRequestDTO createRequest;
    private UpdatePaymentMethodStatusRequestDTO updateStatusRequest;

    @BeforeEach
    void setUp() {
        mockPaymentMethod = new PaymentMethodResponseDTO();
        mockPaymentMethod.setPaymentMethodId("pm-123");
        mockPaymentMethod.setMethodName("Credit Card");
        mockPaymentMethod.setProvider("VISA");
        mockPaymentMethod.setStatus("Active");
        
        createRequest = new CreatePaymentMethodRequestDTO();
        createRequest.setMethodName("Credit Card");
        createRequest.setProvider("VISA");
        
        updateStatusRequest = new UpdatePaymentMethodStatusRequestDTO();
        updateStatusRequest.setPaymentMethodId("pm-123");
        updateStatusRequest.setStatus("Active");
    }

    // ========== CREATE PAYMENT METHOD TESTS ==========
    
    @Test
    void testCreatePaymentMethod_Success() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(paymentMethodRestService.createPaymentMethod(createRequest))
            .thenReturn(mockPaymentMethod);

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.createPaymentMethod(createRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatus());
        assertEquals("Payment method berhasil disimpan", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals("pm-123", response.getBody().getData().getPaymentMethodId());
        verify(paymentMethodRestService, times(1)).createPaymentMethod(createRequest);
    }

    @Test
    void testCreatePaymentMethod_ValidationError() {
        // Arrange
        FieldError fieldError = new FieldError("dto", "name", "Name is required");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.createPaymentMethod(createRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Validation error"));
        assertTrue(response.getBody().getMessage().contains("Name is required"));
        assertNull(response.getBody().getData());
        verify(paymentMethodRestService, never()).createPaymentMethod(any());
    }

    @Test
    void testCreatePaymentMethod_MultipleValidationErrors() {
        // Arrange
        List<FieldError> errors = Arrays.asList(
            new FieldError("dto", "name", "Name is required"),
            new FieldError("dto", "type", "Type is required")
        );
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.createPaymentMethod(createRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Name is required"));
        assertTrue(response.getBody().getMessage().contains("Type is required"));
    }

    @Test
    void testCreatePaymentMethod_ServiceException() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(paymentMethodRestService.createPaymentMethod(createRequest))
            .thenThrow(new RuntimeException("Payment method already exists"));

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.createPaymentMethod(createRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Payment method already exists", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    // ========== GET ALL PAYMENT METHODS TESTS ==========
    
    @Test
    void testGetAllPaymentMethods_Success() {
        // Arrange
        PaymentMethodResponseDTO pm2 = new PaymentMethodResponseDTO();
        pm2.setPaymentMethodId("pm-456");
        pm2.setMethodName("Debit Card");
        pm2.setProvider("Mastercard");
        pm2.setStatus("Active");
        
        List<PaymentMethodResponseDTO> paymentMethods = Arrays.asList(mockPaymentMethod, pm2);
        when(paymentMethodRestService.getAllPaymentMethods()).thenReturn(paymentMethods);

        // Act
        ResponseEntity<BaseResponseDTO<List<PaymentMethodResponseDTO>>> response = 
            paymentMethodRestController.getAllPaymentMethods();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("Payment Method berhasil ditampilkan", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(2, response.getBody().getData().size());
        verify(paymentMethodRestService, times(1)).getAllPaymentMethods();
    }

    @Test
    void testGetAllPaymentMethods_EmptyList() {
        // Arrange
        when(paymentMethodRestService.getAllPaymentMethods()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<BaseResponseDTO<List<PaymentMethodResponseDTO>>> response = 
            paymentMethodRestController.getAllPaymentMethods();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData());
        assertEquals(0, response.getBody().getData().size());
    }

    @Test
    void testGetAllPaymentMethods_Exception() {
        // Arrange
        when(paymentMethodRestService.getAllPaymentMethods())
            .thenThrow(new RuntimeException("Database connection error"));

        // Act
        ResponseEntity<BaseResponseDTO<List<PaymentMethodResponseDTO>>> response = 
            paymentMethodRestController.getAllPaymentMethods();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Database connection error", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    // ========== GET PAYMENT METHOD BY ID TESTS ==========
    
    @Test
    void testGetPaymentMethodById_Success() {
        // Arrange
        String paymentMethodId = "pm-123";
        when(paymentMethodRestService.getPaymentMethodById(paymentMethodId))
            .thenReturn(mockPaymentMethod);

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.getPaymentMethodById(paymentMethodId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("Payment method berhasil ditemukan", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(paymentMethodId, response.getBody().getData().getPaymentMethodId());
        verify(paymentMethodRestService, times(1)).getPaymentMethodById(paymentMethodId);
    }

    @Test
    void testGetPaymentMethodById_NotFound() {
        // Arrange
        String paymentMethodId = "pm-999";
        when(paymentMethodRestService.getPaymentMethodById(paymentMethodId))
            .thenThrow(new RuntimeException("Payment method not found"));

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.getPaymentMethodById(paymentMethodId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Payment method not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    // ========== UPDATE PAYMENT METHOD STATUS TESTS ==========
    
    @Test
    void testUpdatePaymentMethodStatus_Success_Active() {
        // Arrange
        updateStatusRequest.setStatus("Active");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(paymentMethodRestService.updatePaymentMethodStatus(updateStatusRequest))
            .thenReturn(mockPaymentMethod);

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.updatePaymentMethodStatus(updateStatusRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("Status payment method berhasil diperbarui", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(paymentMethodRestService, times(1)).updatePaymentMethodStatus(updateStatusRequest);
    }

    @Test
    void testUpdatePaymentMethodStatus_Success_Inactive() {
        // Arrange
        updateStatusRequest.setStatus("Inactive");
        mockPaymentMethod.setStatus("Inactive");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(paymentMethodRestService.updatePaymentMethodStatus(updateStatusRequest))
            .thenReturn(mockPaymentMethod);

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.updatePaymentMethodStatus(updateStatusRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Status payment method berhasil diperbarui", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals("Inactive", response.getBody().getData().getStatus());
    }

    @Test
    void testUpdatePaymentMethodStatus_ValidationError() {
        // Arrange
        FieldError fieldError = new FieldError("dto", "status", "Status is required");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.updatePaymentMethodStatus(updateStatusRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Validation error"));
        assertTrue(response.getBody().getMessage().contains("Status is required"));
        assertNull(response.getBody().getData());
        verify(paymentMethodRestService, never()).updatePaymentMethodStatus(any());
    }

    @Test
    void testUpdatePaymentMethodStatus_MultipleValidationErrors() {
        // Arrange
        List<FieldError> errors = Arrays.asList(
            new FieldError("dto", "id", "ID is required"),
            new FieldError("dto", "status", "Status is required")
        );
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.updatePaymentMethodStatus(updateStatusRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("ID is required"));
        assertTrue(response.getBody().getMessage().contains("Status is required"));
    }

    @Test
    void testUpdatePaymentMethodStatus_ServiceException() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        when(paymentMethodRestService.updatePaymentMethodStatus(updateStatusRequest))
            .thenThrow(new RuntimeException("Payment method not found"));

        // Act
        ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> response = 
            paymentMethodRestController.updatePaymentMethodStatus(updateStatusRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Payment method not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    // ========== DELETE PAYMENT METHOD TESTS ==========
    
    @Test
    void testDeletePaymentMethod_Success() {
        // Arrange
        String paymentMethodId = "pm-123";
        doNothing().when(paymentMethodRestService).deletePaymentMethod(paymentMethodId);

        // Act
        ResponseEntity<BaseResponseDTO<Void>> response = 
            paymentMethodRestController.deletePaymentMethod(paymentMethodId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Payment method dengan ID :pm-123 berhasil dihapus"));
        assertNull(response.getBody().getData());
        verify(paymentMethodRestService, times(1)).deletePaymentMethod(paymentMethodId);
    }

    @Test
    void testDeletePaymentMethod_NotFound() {
        // Arrange
        String paymentMethodId = "pm-999";
        doThrow(new RuntimeException("Payment method not found"))
            .when(paymentMethodRestService).deletePaymentMethod(paymentMethodId);

        // Act
        ResponseEntity<BaseResponseDTO<Void>> response = 
            paymentMethodRestController.deletePaymentMethod(paymentMethodId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Payment method not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void testDeletePaymentMethod_WithDifferentId() {
        // Arrange
        String paymentMethodId = "pm-456";
        doNothing().when(paymentMethodRestService).deletePaymentMethod(paymentMethodId);

        // Act
        ResponseEntity<BaseResponseDTO<Void>> response = 
            paymentMethodRestController.deletePaymentMethod(paymentMethodId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("pm-456"));
        verify(paymentMethodRestService, times(1)).deletePaymentMethod(paymentMethodId);
    }

    // ========== CONSTRUCTOR TEST ==========
    
    @Test
    void testConstructor() {
        // Act
        PaymentMethodRestController controller = new PaymentMethodRestController(paymentMethodRestService);

        // Assert
        assertNotNull(controller);
    }
}
