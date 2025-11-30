package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TopUpTransactionTest {

    private TopUpTransaction transaction;
    private String testId;
    private String testEndUserId;
    private PaymentMethod testPaymentMethod;

    @BeforeEach
    void setUp() {
        testId = "txn-12345";
        testEndUserId = "user-67890";
        
        testPaymentMethod = new PaymentMethod();
        testPaymentMethod.setId("pm-12345");
        testPaymentMethod.setMethodName("Credit Card");
        testPaymentMethod.setProvider("Visa");
        testPaymentMethod.setStatus("Active");
        
        transaction = new TopUpTransaction();
        transaction.setId(testId);
        transaction.setEndUserId(testEndUserId);
        transaction.setAmount(100000L);
        transaction.setStatus("Success");
        transaction.setPaymentMethod(testPaymentMethod);
        transaction.setDate(LocalDateTime.now());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setDeletedAt(null);
    }

    @Test
    void testTransactionConstruction() {
        assertNotNull(transaction);
        assertEquals(testId, transaction.getId());
        assertEquals(testEndUserId, transaction.getEndUserId());
        assertEquals(100000L, transaction.getAmount());
        assertEquals("Success", transaction.getStatus());
        assertEquals(testPaymentMethod, transaction.getPaymentMethod());
        assertNotNull(transaction.getDate());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getUpdatedAt());
        assertNull(transaction.getDeletedAt());
    }

    @Test
    void testNoArgsConstructor() {
        TopUpTransaction emptyTransaction = new TopUpTransaction();
        assertNotNull(emptyTransaction);
        assertNull(emptyTransaction.getId());
        assertNull(emptyTransaction.getEndUserId());
        assertNull(emptyTransaction.getAmount());
        assertNull(emptyTransaction.getStatus());
        assertNull(emptyTransaction.getPaymentMethod());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime deletedAt = null;
        
        TopUpTransaction fullTransaction = new TopUpTransaction(
                "txn-67890",
                "user-12345",
                250000L,
                testPaymentMethod,
                date,
                "Pending",
                createdAt,
                updatedAt,
                deletedAt
        );
        
        assertEquals("txn-67890", fullTransaction.getId());
        assertEquals("user-12345", fullTransaction.getEndUserId());
        assertEquals(250000L, fullTransaction.getAmount());
        assertEquals("Pending", fullTransaction.getStatus());
        assertEquals(testPaymentMethod, fullTransaction.getPaymentMethod());
        assertEquals(date, fullTransaction.getDate());
        assertEquals(createdAt, fullTransaction.getCreatedAt());
        assertEquals(updatedAt, fullTransaction.getUpdatedAt());
        assertNull(fullTransaction.getDeletedAt());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        
        TopUpTransaction builtTransaction = TopUpTransaction.builder()
                .id("txn-builder")
                .endUserId("user-builder")
                .amount(500000L)
                .status("Failed")
                .paymentMethod(testPaymentMethod)
                .date(now)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();
        
        assertEquals("txn-builder", builtTransaction.getId());
        assertEquals("user-builder", builtTransaction.getEndUserId());
        assertEquals(500000L, builtTransaction.getAmount());
        assertEquals("Failed", builtTransaction.getStatus());
        assertEquals(testPaymentMethod, builtTransaction.getPaymentMethod());
        assertEquals(now, builtTransaction.getDate());
        assertEquals(now, builtTransaction.getCreatedAt());
        assertEquals(now, builtTransaction.getUpdatedAt());
        assertNull(builtTransaction.getDeletedAt());
    }

    @Test
    void testSettersAndGetters() {
        String newId = "txn-new";
        transaction.setId(newId);
        assertEquals(newId, transaction.getId());
        
        String newEndUserId = "user-new";
        transaction.setEndUserId(newEndUserId);
        assertEquals(newEndUserId, transaction.getEndUserId());
        
        transaction.setAmount(200000L);
        assertEquals(200000L, transaction.getAmount());
        
        transaction.setStatus("Pending");
        assertEquals("Pending", transaction.getStatus());
        
        LocalDateTime newDate = LocalDateTime.of(2024, 1, 1, 10, 0);
        transaction.setDate(newDate);
        assertEquals(newDate, transaction.getDate());
        
        LocalDateTime newCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        transaction.setCreatedAt(newCreatedAt);
        assertEquals(newCreatedAt, transaction.getCreatedAt());
    }

    @Test
    void testStatusPending() {
        transaction.setStatus("Pending");
        assertEquals("Pending", transaction.getStatus());
    }

    @Test
    void testStatusSuccess() {
        transaction.setStatus("Success");
        assertEquals("Success", transaction.getStatus());
    }

    @Test
    void testStatusFailed() {
        transaction.setStatus("Failed");
        assertEquals("Failed", transaction.getStatus());
    }

    @Test
    void testVariousAmounts() {
        Long[] amounts = {
            10000L,
            50000L,
            100000L,
            250000L,
            500000L,
            1000000L,
            5000000L
        };
        
        for (Long amount : amounts) {
            transaction.setAmount(amount);
            assertEquals(amount, transaction.getAmount());
        }
    }

    @Test
    void testZeroAmount() {
        transaction.setAmount(0L);
        assertEquals(0L, transaction.getAmount());
    }

    @Test
    void testLargeAmount() {
        transaction.setAmount(999999999L);
        assertEquals(999999999L, transaction.getAmount());
    }

    @Test
    void testPaymentMethodRelationship() {
        PaymentMethod newPaymentMethod = new PaymentMethod();
        newPaymentMethod.setId("pm-new");
        newPaymentMethod.setMethodName("Bank Transfer");
        newPaymentMethod.setProvider("BCA");
        
        transaction.setPaymentMethod(newPaymentMethod);
        
        assertNotNull(transaction.getPaymentMethod());
        assertEquals("Bank Transfer", transaction.getPaymentMethod().getMethodName());
    }

    @Test
    void testNullPaymentMethod() {
        transaction.setPaymentMethod(null);
        assertNull(transaction.getPaymentMethod());
    }

    @Test
    void testSoftDelete() {
        assertNull(transaction.getDeletedAt());
        
        LocalDateTime deletedTime = LocalDateTime.now();
        transaction.setDeletedAt(deletedTime);
        
        assertNotNull(transaction.getDeletedAt());
        assertEquals(deletedTime, transaction.getDeletedAt());
    }

    @Test
    void testNotDeleted() {
        transaction.setDeletedAt(null);
        assertNull(transaction.getDeletedAt());
    }

    @Test
    void testDeletedAtTimestamp() {
        LocalDateTime deleteTime = LocalDateTime.of(2024, 1, 15, 14, 30);
        transaction.setDeletedAt(deleteTime);
        
        assertEquals(deleteTime, transaction.getDeletedAt());
    }

    @Test
    void testNullEndUserId() {
        transaction.setEndUserId(null);
        assertNull(transaction.getEndUserId());
    }

    @Test
    void testNullStatus() {
        transaction.setStatus(null);
        assertNull(transaction.getStatus());
    }

    @Test
    void testEmptyStatus() {
        transaction.setStatus("");
        assertEquals("", transaction.getStatus());
    }

    @Test
    void testNullAmount() {
        transaction.setAmount(null);
        assertNull(transaction.getAmount());
    }

    @Test
    void testDateTimestamp() {
        LocalDateTime specificDate = LocalDateTime.of(2024, 1, 1, 10, 30);
        transaction.setDate(specificDate);
        
        assertEquals(specificDate, transaction.getDate());
    }

    @Test
    void testCreatedAtTimestamp() {
        LocalDateTime specificDate = LocalDateTime.of(2024, 1, 1, 10, 30);
        transaction.setCreatedAt(specificDate);
        
        assertEquals(specificDate, transaction.getCreatedAt());
    }

    @Test
    void testNullCreatedAt() {
        transaction.setCreatedAt(null);
        assertNull(transaction.getCreatedAt());
    }

    @Test
    void testNullDate() {
        transaction.setDate(null);
        assertNull(transaction.getDate());
    }

    @Test
    void testBidirectionalRelationship() {
        PaymentMethod method = new PaymentMethod();
        method.setId("pm-relation");
        method.setMethodName("E-Wallet");
        method.setProvider("GoPay");
        
        transaction.setPaymentMethod(method);
        
        assertEquals(method, transaction.getPaymentMethod());
        assertEquals("E-Wallet", transaction.getPaymentMethod().getMethodName());
    }

    @Test
    void testMultipleTransactionsSamePaymentMethod() {
        TopUpTransaction transaction2 = new TopUpTransaction();
        transaction2.setId("txn-2");
        transaction2.setAmount(50000L);
        transaction2.setPaymentMethod(testPaymentMethod);
        
        assertEquals(testPaymentMethod, transaction.getPaymentMethod());
        assertEquals(testPaymentMethod, transaction2.getPaymentMethod());
    }

    @Test
    void testTransactionWithDifferentUsers() {
        String userId1 = "user-1";
        String userId2 = "user-2";
        
        transaction.setEndUserId(userId1);
        TopUpTransaction transaction2 = new TopUpTransaction();
        transaction2.setEndUserId(userId2);
        
        assertNotEquals(transaction.getEndUserId(), transaction2.getEndUserId());
    }

    @Test
    void testStatusCaseSensitivity() {
        transaction.setStatus("PENDING");
        assertEquals("PENDING", transaction.getStatus());
        
        transaction.setStatus("pending");
        assertEquals("pending", transaction.getStatus());
        
        transaction.setStatus("Pending");
        assertEquals("Pending", transaction.getStatus());
    }

    @Test
    void testToStringContainsKeyFields() {
        String transactionString = transaction.toString();
        assertNotNull(transactionString);
        assertTrue(transactionString.contains(testId) || 
                   transactionString.contains("id") ||
                   transactionString.contains("100000"));
    }

    @Test
    void testEqualsAndHashCode() {
        TopUpTransaction sameTransaction = new TopUpTransaction();
        sameTransaction.setId(testId);
        sameTransaction.setEndUserId(testEndUserId);
        sameTransaction.setAmount(100000L);
        sameTransaction.setStatus("Success");
        sameTransaction.setPaymentMethod(testPaymentMethod);
        sameTransaction.setDate(transaction.getDate());
        sameTransaction.setCreatedAt(transaction.getCreatedAt());
        sameTransaction.setUpdatedAt(transaction.getUpdatedAt());
        sameTransaction.setDeletedAt(null);
        
        assertEquals(transaction, sameTransaction);
        assertEquals(transaction.hashCode(), sameTransaction.hashCode());
    }

    @Test
    void testNotEquals() {
        TopUpTransaction differentTransaction = new TopUpTransaction();
        differentTransaction.setId("txn-different");
        differentTransaction.setAmount(200000L);
        
        assertNotEquals(transaction, differentTransaction);
    }

    @Test
    void testDeleteAndRestore() {
        assertNull(transaction.getDeletedAt());
        
        LocalDateTime deleteTime = LocalDateTime.now();
        transaction.setDeletedAt(deleteTime);
        assertNotNull(transaction.getDeletedAt());
        
        transaction.setDeletedAt(null);
        assertNull(transaction.getDeletedAt());
    }

    @Test
    void testTransactionLifecycle() {
        transaction.setStatus("Pending");
        assertEquals("Pending", transaction.getStatus());
        
        transaction.setStatus("Success");
        assertEquals("Success", transaction.getStatus());
        
        transaction.setStatus("Failed");
        assertEquals("Failed", transaction.getStatus());
    }

    @Test
    void testMultipleStatusChanges() {
        transaction.setStatus("Pending");
        assertEquals("Pending", transaction.getStatus());
        
        transaction.setStatus("Processing");
        assertEquals("Processing", transaction.getStatus());
        
        transaction.setStatus("Success");
        assertEquals("Success", transaction.getStatus());
    }

    @Test
    void testToBuilder() {
        TopUpTransaction cloned = transaction.toBuilder().build();
        
        assertEquals(transaction.getId(), cloned.getId());
        assertEquals(transaction.getEndUserId(), cloned.getEndUserId());
        assertEquals(transaction.getAmount(), cloned.getAmount());
        assertEquals(transaction.getStatus(), cloned.getStatus());
    }

    @Test
    void testStringIdFormat() {
        String[] testIds = {"txn-1", "txn-12345", "transaction-abc", "TXN-XYZ-123"};
        
        for (String id : testIds) {
            transaction.setId(id);
            assertEquals(id, transaction.getId());
        }
    }

    @Test
    void testNullUpdatedAt() {
        transaction.setUpdatedAt(null);
        assertNull(transaction.getUpdatedAt());
    }
}
