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
import apap.ti._5.accommodation_2306275600_be.model.TopUpTransaction;
import apap.ti._5.accommodation_2306275600_be.repository.PaymentMethodRepository;
import apap.ti._5.accommodation_2306275600_be.repository.TopUpTransactionRepository;
import apap.ti._5.accommodation_2306275600_be.repository.CustomerRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.CreateTopUpRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.UpdateTopUpStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.topup.TopUpTransactionResponseDTO;
import apap.ti._5.accommodation_2306275600_be.external.ProfileService;
import apap.ti._5.accommodation_2306275600_be.service.TopUpBillIntegrationService;

@ExtendWith(MockitoExtension.class)
class TopUpTransactionRestServiceImplTest {

    @Mock
    private TopUpTransactionRepository topUpTransactionRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TopUpBillIntegrationService topUpBillIntegrationService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private TopUpTransactionRestServiceImpl topUpTransactionRestService;

    private TopUpTransaction testTransaction;
    private PaymentMethod testPaymentMethod;
    private String transactionId;
    private String paymentMethodId;
    private String customerId;

    @BeforeEach
    void setUp() {
        transactionId = "txn-test-123";
        paymentMethodId = "pm-test-123";
        customerId = "customer-123";

        testPaymentMethod = PaymentMethod.builder()
            .id(paymentMethodId)
            .methodName("Credit Card")
            .provider("VISA")
            .status("Active")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        testTransaction = TopUpTransaction.builder()
            .id(transactionId)
            .endUserId(customerId)
            .amount(100000L)
            .paymentMethod(testPaymentMethod)
            .date(LocalDateTime.now())
            .status("Pending")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    // ============================================
    // CREATE TOP-UP TRANSACTION TESTS
    // ============================================

    @Test
    void testCreateTopUpTransaction_Success() {
        CreateTopUpRequestDTO requestDTO = CreateTopUpRequestDTO.builder()
            .customerId(customerId)
            .amount(100000L)
            .paymentMethodId(paymentMethodId)
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(topUpTransactionRepository.save(any(TopUpTransaction.class))).thenReturn(testTransaction);

        TopUpTransactionResponseDTO result = topUpTransactionRestService.createTopUpTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(customerId, result.getEndUserId());
        assertEquals(100000L, result.getAmount());
        assertEquals("Pending", result.getStatus());
        assertEquals(paymentMethodId, result.getPaymentMethodId());
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(topUpTransactionRepository).save(any(TopUpTransaction.class));
    }

    @Test
    void testCreateTopUpTransaction_NegativeAmount_ThrowsException() {
        CreateTopUpRequestDTO requestDTO = CreateTopUpRequestDTO.builder()
            .customerId(customerId)
            .amount(-10000L)
            .paymentMethodId(paymentMethodId)
            .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.createTopUpTransaction(requestDTO));
        
        assertTrue(exception.getMessage().contains("Amount harus positif"));
        verify(paymentMethodRepository, never()).findById(anyString());
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    @Test
    void testCreateTopUpTransaction_ZeroAmount_ThrowsException() {
        CreateTopUpRequestDTO requestDTO = CreateTopUpRequestDTO.builder()
            .customerId(customerId)
            .amount(0L)
            .paymentMethodId(paymentMethodId)
            .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.createTopUpTransaction(requestDTO));
        
        assertTrue(exception.getMessage().contains("Amount harus positif"));
        verify(paymentMethodRepository, never()).findById(anyString());
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    @Test
    void testCreateTopUpTransaction_PaymentMethodNotFound_ThrowsException() {
        CreateTopUpRequestDTO requestDTO = CreateTopUpRequestDTO.builder()
            .customerId(customerId)
            .amount(100000L)
            .paymentMethodId(paymentMethodId)
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.createTopUpTransaction(requestDTO));
        
        assertTrue(exception.getMessage().contains("Payment method not found with ID: " + paymentMethodId));
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    @Test
    void testCreateTopUpTransaction_InactivePaymentMethod_ThrowsException() {
        testPaymentMethod.setStatus("Inactive");
        
        CreateTopUpRequestDTO requestDTO = CreateTopUpRequestDTO.builder()
            .customerId(customerId)
            .amount(100000L)
            .paymentMethodId(paymentMethodId)
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.createTopUpTransaction(requestDTO));
        
        assertTrue(exception.getMessage().contains("Payment method tidak aktif"));
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    @Test
    void testCreateTopUpTransaction_LargeAmount_Success() {
        CreateTopUpRequestDTO requestDTO = CreateTopUpRequestDTO.builder()
            .customerId(customerId)
            .amount(10000000L)
            .paymentMethodId(paymentMethodId)
            .build();

        TopUpTransaction largeTransaction = TopUpTransaction.builder()
            .id("txn-large")
            .endUserId(customerId)
            .amount(10000000L)
            .paymentMethod(testPaymentMethod)
            .date(LocalDateTime.now())
            .status("Pending")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(topUpTransactionRepository.save(any(TopUpTransaction.class))).thenReturn(largeTransaction);

        TopUpTransactionResponseDTO result = topUpTransactionRestService.createTopUpTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(10000000L, result.getAmount());
        verify(topUpTransactionRepository).save(any(TopUpTransaction.class));
    }

    // ============================================
    // GET ALL TOP-UP TRANSACTIONS TESTS
    // ============================================

    @Test
    void testGetAllTopUpTransactions_ReturnsMultipleTransactions() {
        TopUpTransaction transaction1 = TopUpTransaction.builder()
            .id("txn-1")
            .endUserId("customer-1")
            .amount(50000L)
            .paymentMethod(testPaymentMethod)
            .date(LocalDateTime.now())
            .status("Pending")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        TopUpTransaction transaction2 = TopUpTransaction.builder()
            .id("txn-2")
            .endUserId("customer-2")
            .amount(75000L)
            .paymentMethod(testPaymentMethod)
            .date(LocalDateTime.now())
            .status("Success")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(topUpTransactionRepository.findAllNotDeleted()).thenReturn(Arrays.asList(transaction1, transaction2));

        List<TopUpTransactionResponseDTO> result = topUpTransactionRestService.getAllTopUpTransactions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50000L, result.get(0).getAmount());
        assertEquals(75000L, result.get(1).getAmount());
        verify(topUpTransactionRepository).findAllNotDeleted();
    }

    @Test
    void testGetAllTopUpTransactions_EmptyList() {
        when(topUpTransactionRepository.findAllNotDeleted()).thenReturn(Arrays.asList());

        List<TopUpTransactionResponseDTO> result = topUpTransactionRestService.getAllTopUpTransactions();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(topUpTransactionRepository).findAllNotDeleted();
    }

    // ============================================
    // GET TRANSACTIONS BY CUSTOMER ID TESTS
    // ============================================

    @Test
    void testGetTopUpTransactionsByCustomerId_ReturnsCustomerTransactions() {
        TopUpTransaction transaction1 = TopUpTransaction.builder()
            .id("txn-1")
            .endUserId(customerId)
            .amount(50000L)
            .paymentMethod(testPaymentMethod)
            .date(LocalDateTime.now())
            .status("Pending")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        TopUpTransaction transaction2 = TopUpTransaction.builder()
            .id("txn-2")
            .endUserId(customerId)
            .amount(75000L)
            .paymentMethod(testPaymentMethod)
            .date(LocalDateTime.now())
            .status("Success")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(topUpTransactionRepository.findByEndUserIdAndNotDeleted(customerId))
            .thenReturn(Arrays.asList(transaction1, transaction2));

        List<TopUpTransactionResponseDTO> result = topUpTransactionRestService.getTopUpTransactionsByCustomerId(customerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(customerId, result.get(0).getEndUserId());
        assertEquals(customerId, result.get(1).getEndUserId());
        verify(topUpTransactionRepository).findByEndUserIdAndNotDeleted(customerId);
    }

    @Test
    void testGetTopUpTransactionsByCustomerId_NoTransactions() {
        when(topUpTransactionRepository.findByEndUserIdAndNotDeleted(customerId)).thenReturn(Arrays.asList());

        List<TopUpTransactionResponseDTO> result = topUpTransactionRestService.getTopUpTransactionsByCustomerId(customerId);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(topUpTransactionRepository).findByEndUserIdAndNotDeleted(customerId);
    }

    // ============================================
    // GET TRANSACTION BY ID TESTS
    // ============================================

    @Test
    void testGetTopUpTransactionById_Success() {
        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));

        TopUpTransactionResponseDTO result = topUpTransactionRestService.getTopUpTransactionById(transactionId);

        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals(customerId, result.getEndUserId());
        assertEquals(100000L, result.getAmount());
        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
    }

    @Test
    void testGetTopUpTransactionById_NotFound_ThrowsException() {
        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.getTopUpTransactionById(transactionId));
        
        assertTrue(exception.getMessage().contains("Top-up transaction not found with ID: " + transactionId));
        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
    }

    // ============================================
    // UPDATE TOP-UP STATUS TESTS
    // ============================================

    @Test
    void testUpdateTopUpStatus_ToSuccess_Success() {
        UpdateTopUpStatusRequestDTO requestDTO = UpdateTopUpStatusRequestDTO.builder()
            .transactionId(transactionId)
            .status("Success")
            .build();

        TopUpTransaction updatedTransaction = TopUpTransaction.builder()
            .id(transactionId)
            .endUserId(customerId)
            .amount(100000L)
            .paymentMethod(testPaymentMethod)
            .date(testTransaction.getDate())
            .status("Success")
            .createdAt(testTransaction.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));
        when(topUpTransactionRepository.save(any(TopUpTransaction.class))).thenReturn(updatedTransaction);

        TopUpTransactionResponseDTO result = topUpTransactionRestService.updateTopUpStatus(requestDTO);

        assertNotNull(result);
        assertEquals("Success", result.getStatus());
        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
        verify(topUpTransactionRepository).save(any(TopUpTransaction.class));
        verify(profileService).addBalance(customerId, 100000L);
    }

    @Test
    void testUpdateTopUpStatus_ToFailed_Success() {
        UpdateTopUpStatusRequestDTO requestDTO = UpdateTopUpStatusRequestDTO.builder()
            .transactionId(transactionId)
            .status("Failed")
            .build();

        TopUpTransaction updatedTransaction = TopUpTransaction.builder()
            .id(transactionId)
            .endUserId(customerId)
            .amount(100000L)
            .paymentMethod(testPaymentMethod)
            .date(testTransaction.getDate())
            .status("Failed")
            .createdAt(testTransaction.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));
        when(topUpTransactionRepository.save(any(TopUpTransaction.class))).thenReturn(updatedTransaction);

        TopUpTransactionResponseDTO result = topUpTransactionRestService.updateTopUpStatus(requestDTO);

        assertNotNull(result);
        assertEquals("Failed", result.getStatus());
        verify(topUpTransactionRepository).save(any(TopUpTransaction.class));
        verify(profileService, never()).addBalance(anyString(), anyLong());
    }

    @Test
    void testUpdateTopUpStatus_TransactionNotFound_ThrowsException() {
        UpdateTopUpStatusRequestDTO requestDTO = UpdateTopUpStatusRequestDTO.builder()
            .transactionId(transactionId)
            .status("Success")
            .build();

        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.updateTopUpStatus(requestDTO));
        
        assertTrue(exception.getMessage().contains("Top-up transaction not found with ID: " + transactionId));
        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    @Test
    void testUpdateTopUpStatus_NotPendingStatus_ThrowsException() {
        testTransaction.setStatus("Success");
        
        UpdateTopUpStatusRequestDTO requestDTO = UpdateTopUpStatusRequestDTO.builder()
            .transactionId(transactionId)
            .status("Failed")
            .build();

        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.updateTopUpStatus(requestDTO));
        
        assertTrue(exception.getMessage().contains("Hanya transaksi dengan status 'Pending' yang dapat diubah"));
        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    @Test
    void testUpdateTopUpStatus_InvalidStatus_ThrowsException() {
        UpdateTopUpStatusRequestDTO requestDTO = UpdateTopUpStatusRequestDTO.builder()
            .transactionId(transactionId)
            .status("InvalidStatus")
            .build();

        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.updateTopUpStatus(requestDTO));
        
        assertTrue(exception.getMessage().contains("Invalid status. Must be 'Success' or 'Failed'"));
        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    // ============================================
    // DELETE TOP-UP TRANSACTION TESTS
    // ============================================

    @Test
    void testDeleteTopUpTransaction_Success() {
        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));
        when(topUpTransactionRepository.save(any(TopUpTransaction.class))).thenReturn(testTransaction);

        topUpTransactionRestService.deleteTopUpTransaction(transactionId);

        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
        verify(topUpTransactionRepository).save(any(TopUpTransaction.class));
        assertNotNull(testTransaction.getDeletedAt());
    }

    @Test
    void testDeleteTopUpTransaction_NotFound_ThrowsException() {
        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> topUpTransactionRestService.deleteTopUpTransaction(transactionId));
        
        assertTrue(exception.getMessage().contains("Top-up transaction not found with ID: " + transactionId));
        verify(topUpTransactionRepository).findByIdAndNotDeleted(transactionId);
        verify(topUpTransactionRepository, never()).save(any(TopUpTransaction.class));
    }

    @Test
    void testDeleteTopUpTransaction_PendingStatus_StillDeletes() {
        testTransaction.setStatus("Pending");
        
        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));
        when(topUpTransactionRepository.save(any(TopUpTransaction.class))).thenReturn(testTransaction);

        topUpTransactionRestService.deleteTopUpTransaction(transactionId);

        verify(topUpTransactionRepository).save(any(TopUpTransaction.class));
        assertNotNull(testTransaction.getDeletedAt());
    }

    @Test
    void testDeleteTopUpTransaction_SuccessStatus_StillDeletes() {
        testTransaction.setStatus("Success");
        
        when(topUpTransactionRepository.findByIdAndNotDeleted(transactionId)).thenReturn(Optional.of(testTransaction));
        when(topUpTransactionRepository.save(any(TopUpTransaction.class))).thenReturn(testTransaction);

        topUpTransactionRestService.deleteTopUpTransaction(transactionId);

        verify(topUpTransactionRepository).save(any(TopUpTransaction.class));
        assertNotNull(testTransaction.getDeletedAt());
    }
}
