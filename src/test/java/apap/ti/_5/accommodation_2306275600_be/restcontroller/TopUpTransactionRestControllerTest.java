package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.CreateTopUpRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.UpdateTopUpStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.topup.TopUpTransactionResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.TopUpTransactionRestServiceRBAC;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopUpTransactionRestController.class)
class TopUpTransactionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TopUpTransactionRestServiceRBAC topUpTransactionRestService;

    private CreateTopUpRequestDTO validCreateRequest;
    private UpdateTopUpStatusRequestDTO validUpdateRequest;
    private TopUpTransactionResponseDTO expectedTopUpResponse;
    private String testTransactionId;

    @BeforeEach
    void setUp() {
        testTransactionId = "topup-123";
        LocalDateTime now = LocalDateTime.now();

        validCreateRequest = CreateTopUpRequestDTO.builder()
                .customerId("customer-456")
                .amount(100000L)
                .paymentMethodId("payment-789")
                .build();

        validUpdateRequest = UpdateTopUpStatusRequestDTO.builder()
                .transactionId(testTransactionId)
                .status("Success")
                .build();

        expectedTopUpResponse = TopUpTransactionResponseDTO.builder()
                .transactionId(testTransactionId)
                .endUserId("customer-456")
                .amount(100000L)
                .paymentMethodId("payment-789")
                .paymentMethodName("Bank Transfer")
                .provider("BCA")
                .date(now)
                .status("Pending")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // ============================================
    // CREATE TOP-UP TRANSACTION TESTS
    // ============================================

    @Test
    void testCreateTopUpTransaction_Success() throws Exception {
        when(topUpTransactionRestService.createTopUpTransaction(any(CreateTopUpRequestDTO.class)))
                .thenReturn(expectedTopUpResponse);

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Membuat transaksi baru dengan status 'Pending'"))
                .andExpect(jsonPath("$.data.transactionId").value(testTransactionId))
                .andExpect(jsonPath("$.data.amount").value(100000))
                .andExpect(jsonPath("$.data.status").value("Pending"));

        verify(topUpTransactionRestService, times(1)).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    @Test
    void testCreateTopUpTransaction_ValidationError_MissingCustomerId() throws Exception {
        CreateTopUpRequestDTO invalidRequest = CreateTopUpRequestDTO.builder()
                .customerId("")
                .amount(100000L)
                .paymentMethodId("payment-789")
                .build();

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error: Customer ID is required; "));

        verify(topUpTransactionRestService, never()).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    @Test
    void testCreateTopUpTransaction_ValidationError_NullAmount() throws Exception {
        CreateTopUpRequestDTO invalidRequest = CreateTopUpRequestDTO.builder()
                .customerId("customer-456")
                .amount(null)
                .paymentMethodId("payment-789")
                .build();

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error: Amount is required; "));

        verify(topUpTransactionRestService, never()).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    @Test
    void testCreateTopUpTransaction_ValidationError_ZeroAmount() throws Exception {
        CreateTopUpRequestDTO invalidRequest = CreateTopUpRequestDTO.builder()
                .customerId("customer-456")
                .amount(0L)
                .paymentMethodId("payment-789")
                .build();

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error: Amount must be positive; "));

        verify(topUpTransactionRestService, never()).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    @Test
    void testCreateTopUpTransaction_ValidationError_MissingPaymentMethodId() throws Exception {
        CreateTopUpRequestDTO invalidRequest = CreateTopUpRequestDTO.builder()
                .customerId("customer-456")
                .amount(100000L)
                .paymentMethodId("")
                .build();

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error: Payment method ID is required; "));

        verify(topUpTransactionRestService, never()).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    @Test
    void testCreateTopUpTransaction_ValidationError_MultipleFields() throws Exception {
        CreateTopUpRequestDTO invalidRequest = CreateTopUpRequestDTO.builder()
                .customerId("")
                .amount(null)
                .paymentMethodId("")
                .build();

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(topUpTransactionRestService, never()).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    @Test
    void testCreateTopUpTransaction_ServiceException() throws Exception {
        when(topUpTransactionRestService.createTopUpTransaction(any(CreateTopUpRequestDTO.class)))
                .thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(topUpTransactionRestService, times(1)).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    @Test
    void testCreateTopUpTransaction_NegativeAmount() throws Exception {
        CreateTopUpRequestDTO invalidRequest = CreateTopUpRequestDTO.builder()
                .customerId("customer-456")
                .amount(-1000L)
                .paymentMethodId("payment-789")
                .build();

        mockMvc.perform(post("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error: Amount must be positive; "));

        verify(topUpTransactionRestService, never()).createTopUpTransaction(any(CreateTopUpRequestDTO.class));
    }

    // ============================================
    // GET ALL TOP-UP TRANSACTIONS TESTS
    // ============================================

    @Test
    void testGetAllTopUpTransactions_Success() throws Exception {
        TopUpTransactionResponseDTO topUp2 = TopUpTransactionResponseDTO.builder()
                .transactionId("topup-999")
                .endUserId("customer-999")
                .amount(200000L)
                .status("Success")
                .build();

        List<TopUpTransactionResponseDTO> transactions = Arrays.asList(expectedTopUpResponse, topUp2);

        when(topUpTransactionRestService.getAllTopUpTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Menampilkan seluruh daftar transaksi top up yang terdaftar pada sistem"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(topUpTransactionRestService, times(1)).getAllTopUpTransactions();
    }

    @Test
    void testGetAllTopUpTransactions_EmptyList() throws Exception {
        when(topUpTransactionRestService.getAllTopUpTransactions()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(topUpTransactionRestService, times(1)).getAllTopUpTransactions();
    }

    @Test
    void testGetAllTopUpTransactions_Exception() throws Exception {
        when(topUpTransactionRestService.getAllTopUpTransactions())
                .thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(get("/api/top-up")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Database connection error"));

        verify(topUpTransactionRestService, times(1)).getAllTopUpTransactions();
    }

    // ============================================
    // GET TOP-UP TRANSACTIONS BY CUSTOMER ID TESTS
    // ============================================

    @Test
    void testGetTopUpTransactionsByCustomerId_Success() throws Exception {
        List<TopUpTransactionResponseDTO> transactions = Arrays.asList(expectedTopUpResponse);

        when(topUpTransactionRestService.getTopUpTransactionsByCustomerId(eq("customer-456")))
                .thenReturn(transactions);

        mockMvc.perform(get("/api/top-up/customer/{customerId}", "customer-456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Terdapat validasi hanya transaction customer yang dapat dilihat pada jwt token yang dapat melihat transactions"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(topUpTransactionRestService, times(1)).getTopUpTransactionsByCustomerId(eq("customer-456"));
    }

    @Test
    void testGetTopUpTransactionsByCustomerId_EmptyList() throws Exception {
        when(topUpTransactionRestService.getTopUpTransactionsByCustomerId(eq("customer-456")))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/top-up/customer/{customerId}", "customer-456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(topUpTransactionRestService, times(1)).getTopUpTransactionsByCustomerId(eq("customer-456"));
    }

    @Test
    void testGetTopUpTransactionsByCustomerId_Exception() throws Exception {
        when(topUpTransactionRestService.getTopUpTransactionsByCustomerId(eq("customer-456")))
                .thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(get("/api/top-up/customer/{customerId}", "customer-456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(topUpTransactionRestService, times(1)).getTopUpTransactionsByCustomerId(eq("customer-456"));
    }

    // ============================================
    // GET TOP-UP TRANSACTION BY ID TESTS
    // ============================================

    @Test
    void testGetTopUpTransactionById_Success() throws Exception {
        when(topUpTransactionRestService.getTopUpTransactionById(eq(testTransactionId)))
                .thenReturn(expectedTopUpResponse);

        mockMvc.perform(get("/api/top-up/{id}", testTransactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Menampilkan detail transaksi berdasarkan ID"))
                .andExpect(jsonPath("$.data.transactionId").value(testTransactionId))
                .andExpect(jsonPath("$.data.amount").value(100000));

        verify(topUpTransactionRestService, times(1)).getTopUpTransactionById(eq(testTransactionId));
    }

    @Test
    void testGetTopUpTransactionById_NotFound() throws Exception {
        when(topUpTransactionRestService.getTopUpTransactionById(eq(testTransactionId)))
                .thenThrow(new RuntimeException("Transaction not found"));

        mockMvc.perform(get("/api/top-up/{id}", testTransactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Transaction not found"));

        verify(topUpTransactionRestService, times(1)).getTopUpTransactionById(eq(testTransactionId));
    }

    // ============================================
    // UPDATE TOP-UP STATUS TESTS
    // ============================================

    @Test
    void testUpdateTopUpStatus_Success() throws Exception {
        TopUpTransactionResponseDTO updatedResponse = TopUpTransactionResponseDTO.builder()
                .transactionId(testTransactionId)
                .endUserId("customer-456")
                .amount(100000L)
                .status("Success")
                .build();

        when(topUpTransactionRestService.updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/top-up/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Mengubah status menjadi 'Success' atau 'Failed'. Jika 'Success', sistem menambah saldo di Profile Service"))
                .andExpect(jsonPath("$.data.status").value("Success"));

        verify(topUpTransactionRestService, times(1)).updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class));
    }

    @Test
    void testUpdateTopUpStatus_Failed() throws Exception {
        UpdateTopUpStatusRequestDTO failedRequest = UpdateTopUpStatusRequestDTO.builder()
                .transactionId(testTransactionId)
                .status("Failed")
                .build();

        TopUpTransactionResponseDTO updatedResponse = TopUpTransactionResponseDTO.builder()
                .transactionId(testTransactionId)
                .endUserId("customer-456")
                .amount(100000L)
                .status("Failed")
                .build();

        when(topUpTransactionRestService.updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/top-up/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Mengubah status menjadi 'Success' atau 'Failed'. Jika 'Failed', tidak ada perubahan saldo"))
                .andExpect(jsonPath("$.data.status").value("Failed"));

        verify(topUpTransactionRestService, times(1)).updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class));
    }

    @Test
    void testUpdateTopUpStatus_ValidationError_MissingTransactionId() throws Exception {
        UpdateTopUpStatusRequestDTO invalidRequest = UpdateTopUpStatusRequestDTO.builder()
                .transactionId("")
                .status("Success")
                .build();

        mockMvc.perform(put("/api/top-up/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error: Transaction ID is required; "));

        verify(topUpTransactionRestService, never()).updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class));
    }

    @Test
    void testUpdateTopUpStatus_ValidationError_MissingStatus() throws Exception {
        UpdateTopUpStatusRequestDTO invalidRequest = UpdateTopUpStatusRequestDTO.builder()
                .transactionId(testTransactionId)
                .status("")
                .build();

        mockMvc.perform(put("/api/top-up/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error: Status is required; "));

        verify(topUpTransactionRestService, never()).updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class));
    }

    @Test
    void testUpdateTopUpStatus_ValidationError_MultipleFields() throws Exception {
        UpdateTopUpStatusRequestDTO invalidRequest = UpdateTopUpStatusRequestDTO.builder()
                .transactionId("")
                .status("")
                .build();

        mockMvc.perform(put("/api/top-up/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(topUpTransactionRestService, never()).updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class));
    }

    @Test
    void testUpdateTopUpStatus_ServiceException() throws Exception {
        when(topUpTransactionRestService.updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class)))
                .thenThrow(new RuntimeException("Transaction not found"));

        mockMvc.perform(put("/api/top-up/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Transaction not found"));

        verify(topUpTransactionRestService, times(1)).updateTopUpStatus(any(UpdateTopUpStatusRequestDTO.class));
    }

    // ============================================
    // DELETE TOP-UP TRANSACTION TESTS
    // ============================================

    @Test
    void testDeleteTopUpTransaction_Success() throws Exception {
        doNothing().when(topUpTransactionRestService).deleteTopUpTransaction(eq(testTransactionId));

        mockMvc.perform(delete("/api/top-up/{id}", testTransactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Top Up Transaction dengan ID tertentu berhasil dihapus. Status top up transaction berhasil diperbarui"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(topUpTransactionRestService, times(1)).deleteTopUpTransaction(eq(testTransactionId));
    }

    @Test
    void testDeleteTopUpTransaction_NotFound() throws Exception {
        doThrow(new RuntimeException("Transaction not found"))
                .when(topUpTransactionRestService).deleteTopUpTransaction(eq(testTransactionId));

        mockMvc.perform(delete("/api/top-up/{id}", testTransactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Transaction not found"));

        verify(topUpTransactionRestService, times(1)).deleteTopUpTransaction(eq(testTransactionId));
    }

    @Test
    void testDeleteTopUpTransaction_ServiceException() throws Exception {
        doThrow(new RuntimeException("Cannot delete transaction with status Success"))
                .when(topUpTransactionRestService).deleteTopUpTransaction(eq(testTransactionId));

        mockMvc.perform(delete("/api/top-up/{id}", testTransactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cannot delete transaction with status Success"));

        verify(topUpTransactionRestService, times(1)).deleteTopUpTransaction(eq(testTransactionId));
    }
}
