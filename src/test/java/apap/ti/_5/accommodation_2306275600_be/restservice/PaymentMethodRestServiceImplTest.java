package apap.ti._5.accommodation_2306275600_be.restservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import apap.ti._5.accommodation_2306275600_be.model.PaymentMethod;
import apap.ti._5.accommodation_2306275600_be.repository.PaymentMethodRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.CreatePaymentMethodRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.UpdatePaymentMethodStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod.PaymentMethodResponseDTO;

@ExtendWith(MockitoExtension.class)
class PaymentMethodRestServiceImplTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentMethodRestServiceImpl paymentMethodRestService;

    private PaymentMethod testPaymentMethod;
    private String paymentMethodId;

    @BeforeEach
    void setUp() {
        paymentMethodId = "pm-test-123";
        
        testPaymentMethod = PaymentMethod.builder()
            .id(paymentMethodId)
            .methodName("Credit Card")
            .provider("VISA")
            .status("Active")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    // ============================================
    // CREATE PAYMENT METHOD TESTS
    // ============================================

    @Test
    void testCreatePaymentMethod_Success() {
        CreatePaymentMethodRequestDTO requestDTO = CreatePaymentMethodRequestDTO.builder()
            .methodName("Credit Card")
            .provider("VISA")
            .build();

        when(paymentMethodRepository.existsByMethodNameAndProvider("Credit Card", "VISA")).thenReturn(false);
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(testPaymentMethod);

        PaymentMethodResponseDTO result = paymentMethodRestService.createPaymentMethod(requestDTO);

        assertNotNull(result);
        assertEquals("Credit Card", result.getMethodName());
        assertEquals("VISA", result.getProvider());
        assertEquals("Active", result.getStatus());
        verify(paymentMethodRepository).existsByMethodNameAndProvider("Credit Card", "VISA");
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void testCreatePaymentMethod_DuplicateMethodAndProvider_ThrowsException() {
        CreatePaymentMethodRequestDTO requestDTO = CreatePaymentMethodRequestDTO.builder()
            .methodName("Credit Card")
            .provider("VISA")
            .build();

        when(paymentMethodRepository.existsByMethodNameAndProvider("Credit Card", "VISA")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentMethodRestService.createPaymentMethod(requestDTO));
        
        assertTrue(exception.getMessage().contains("Payment method 'Credit Card' dengan provider 'VISA' sudah ada"));
        verify(paymentMethodRepository).existsByMethodNameAndProvider("Credit Card", "VISA");
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void testCreatePaymentMethod_DifferentProviderSameName_Success() {
        CreatePaymentMethodRequestDTO requestDTO = CreatePaymentMethodRequestDTO.builder()
            .methodName("Credit Card")
            .provider("Mastercard")
            .build();

        PaymentMethod savedPaymentMethod = PaymentMethod.builder()
            .id("pm-test-456")
            .methodName("Credit Card")
            .provider("Mastercard")
            .status("Active")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(paymentMethodRepository.existsByMethodNameAndProvider("Credit Card", "Mastercard")).thenReturn(false);
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(savedPaymentMethod);

        PaymentMethodResponseDTO result = paymentMethodRestService.createPaymentMethod(requestDTO);

        assertNotNull(result);
        assertEquals("Credit Card", result.getMethodName());
        assertEquals("Mastercard", result.getProvider());
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    // ============================================
    // GET ALL PAYMENT METHODS TESTS
    // ============================================

    @Test
    void testGetAllPaymentMethods_ReturnsMultiplePaymentMethods() {
        PaymentMethod paymentMethod1 = PaymentMethod.builder()
            .id("pm-1")
            .methodName("Credit Card")
            .provider("VISA")
            .status("Active")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        PaymentMethod paymentMethod2 = PaymentMethod.builder()
            .id("pm-2")
            .methodName("E-Wallet")
            .provider("GoPay")
            .status("Active")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(paymentMethodRepository.findAll()).thenReturn(Arrays.asList(paymentMethod1, paymentMethod2));

        List<PaymentMethodResponseDTO> result = paymentMethodRestService.getAllPaymentMethods();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Credit Card", result.get(0).getMethodName());
        assertEquals("E-Wallet", result.get(1).getMethodName());
        verify(paymentMethodRepository).findAll();
    }

    @Test
    void testGetAllPaymentMethods_EmptyList() {
        when(paymentMethodRepository.findAll()).thenReturn(Arrays.asList());

        List<PaymentMethodResponseDTO> result = paymentMethodRestService.getAllPaymentMethods();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(paymentMethodRepository).findAll();
    }

    // ============================================
    // GET PAYMENT METHOD BY ID TESTS
    // ============================================

    @Test
    void testGetPaymentMethodById_Success() {
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));

        PaymentMethodResponseDTO result = paymentMethodRestService.getPaymentMethodById(paymentMethodId);

        assertNotNull(result);
        assertEquals(paymentMethodId, result.getPaymentMethodId());
        assertEquals("Credit Card", result.getMethodName());
        assertEquals("VISA", result.getProvider());
        verify(paymentMethodRepository).findById(paymentMethodId);
    }

    @Test
    void testGetPaymentMethodById_NotFound_ThrowsException() {
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentMethodRestService.getPaymentMethodById(paymentMethodId));
        
        assertTrue(exception.getMessage().contains("Payment method not found with ID: " + paymentMethodId));
        verify(paymentMethodRepository).findById(paymentMethodId);
    }

    // ============================================
    // UPDATE PAYMENT METHOD STATUS TESTS
    // ============================================

    @Test
    void testUpdatePaymentMethodStatus_ToInactive_Success() {
        UpdatePaymentMethodStatusRequestDTO requestDTO = UpdatePaymentMethodStatusRequestDTO.builder()
            .paymentMethodId(paymentMethodId)
            .status("Inactive")
            .build();

        PaymentMethod updatedPaymentMethod = PaymentMethod.builder()
            .id(paymentMethodId)
            .methodName("Credit Card")
            .provider("VISA")
            .status("Inactive")
            .createdAt(testPaymentMethod.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(updatedPaymentMethod);

        PaymentMethodResponseDTO result = paymentMethodRestService.updatePaymentMethodStatus(requestDTO);

        assertNotNull(result);
        assertEquals("Inactive", result.getStatus());
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void testUpdatePaymentMethodStatus_ToActive_Success() {
        testPaymentMethod.setStatus("Inactive");
        
        UpdatePaymentMethodStatusRequestDTO requestDTO = UpdatePaymentMethodStatusRequestDTO.builder()
            .paymentMethodId(paymentMethodId)
            .status("Active")
            .build();

        PaymentMethod updatedPaymentMethod = PaymentMethod.builder()
            .id(paymentMethodId)
            .methodName("Credit Card")
            .provider("VISA")
            .status("Active")
            .createdAt(testPaymentMethod.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(updatedPaymentMethod);

        PaymentMethodResponseDTO result = paymentMethodRestService.updatePaymentMethodStatus(requestDTO);

        assertNotNull(result);
        assertEquals("Active", result.getStatus());
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void testUpdatePaymentMethodStatus_PaymentMethodNotFound_ThrowsException() {
        UpdatePaymentMethodStatusRequestDTO requestDTO = UpdatePaymentMethodStatusRequestDTO.builder()
            .paymentMethodId(paymentMethodId)
            .status("Inactive")
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentMethodRestService.updatePaymentMethodStatus(requestDTO));
        
        assertTrue(exception.getMessage().contains("Payment method not found with ID: " + paymentMethodId));
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void testUpdatePaymentMethodStatus_InvalidStatus_ThrowsException() {
        UpdatePaymentMethodStatusRequestDTO requestDTO = UpdatePaymentMethodStatusRequestDTO.builder()
            .paymentMethodId(paymentMethodId)
            .status("InvalidStatus")
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentMethodRestService.updatePaymentMethodStatus(requestDTO));
        
        assertTrue(exception.getMessage().contains("Invalid status. Must be 'Active' or 'Inactive'"));
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    // ============================================
    // DELETE PAYMENT METHOD TESTS
    // ============================================

    @Test
    void testDeletePaymentMethod_Success() {
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(testPaymentMethod);

        paymentMethodRestService.deletePaymentMethod(paymentMethodId);

        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
        assertEquals("Inactive", testPaymentMethod.getStatus());
    }

    @Test
    void testDeletePaymentMethod_NotFound_ThrowsException() {
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentMethodRestService.deletePaymentMethod(paymentMethodId));
        
        assertTrue(exception.getMessage().contains("Payment method not found with ID: " + paymentMethodId));
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void testDeletePaymentMethod_AlreadyInactive_StillUpdates() {
        testPaymentMethod.setStatus("Inactive");
        
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(testPaymentMethod);

        paymentMethodRestService.deletePaymentMethod(paymentMethodId);

        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
        assertEquals("Inactive", testPaymentMethod.getStatus());
    }
}
