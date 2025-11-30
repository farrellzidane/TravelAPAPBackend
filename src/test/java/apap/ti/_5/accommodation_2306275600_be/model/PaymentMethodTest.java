package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    private PaymentMethod paymentMethod;
    private String testId;

    @BeforeEach
    void setUp() {
        testId = "pm-12345";
        
        paymentMethod = new PaymentMethod();
        paymentMethod.setId(testId);
        paymentMethod.setMethodName("Credit Card");
        paymentMethod.setProvider("Visa");
        paymentMethod.setStatus("Active");
        paymentMethod.setCreatedAt(LocalDateTime.now());
        paymentMethod.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testPaymentMethodConstruction() {
        assertNotNull(paymentMethod);
        assertEquals(testId, paymentMethod.getId());
        assertEquals("Credit Card", paymentMethod.getMethodName());
        assertEquals("Visa", paymentMethod.getProvider());
        assertEquals("Active", paymentMethod.getStatus());
        assertNotNull(paymentMethod.getCreatedAt());
        assertNotNull(paymentMethod.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        PaymentMethod emptyPaymentMethod = new PaymentMethod();
        assertNotNull(emptyPaymentMethod);
        assertNull(emptyPaymentMethod.getId());
        assertNull(emptyPaymentMethod.getMethodName());
        assertNull(emptyPaymentMethod.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<TopUpTransaction> transactions = new ArrayList<>();
        
        PaymentMethod fullPaymentMethod = new PaymentMethod(
                "pm-67890",
                "Bank Transfer",
                "BCA",
                "Active",
                createdAt,
                updatedAt,
                transactions
        );
        
        assertEquals("pm-67890", fullPaymentMethod.getId());
        assertEquals("Bank Transfer", fullPaymentMethod.getMethodName());
        assertEquals("BCA", fullPaymentMethod.getProvider());
        assertEquals("Active", fullPaymentMethod.getStatus());
        assertEquals(createdAt, fullPaymentMethod.getCreatedAt());
        assertEquals(updatedAt, fullPaymentMethod.getUpdatedAt());
        assertEquals(transactions, fullPaymentMethod.getTopUpTransactions());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        
        PaymentMethod builtPaymentMethod = PaymentMethod.builder()
                .id("pm-builder")
                .methodName("E-Wallet")
                .provider("GoPay")
                .status("Active")
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        assertEquals("pm-builder", builtPaymentMethod.getId());
        assertEquals("E-Wallet", builtPaymentMethod.getMethodName());
        assertEquals("GoPay", builtPaymentMethod.getProvider());
        assertEquals("Active", builtPaymentMethod.getStatus());
        assertEquals(now, builtPaymentMethod.getCreatedAt());
        assertEquals(now, builtPaymentMethod.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        String newId = "pm-new";
        paymentMethod.setId(newId);
        assertEquals(newId, paymentMethod.getId());
        
        paymentMethod.setMethodName("Debit Card");
        assertEquals("Debit Card", paymentMethod.getMethodName());
        
        paymentMethod.setProvider("Mastercard");
        assertEquals("Mastercard", paymentMethod.getProvider());
        
        paymentMethod.setStatus("Inactive");
        assertEquals("Inactive", paymentMethod.getStatus());
        
        LocalDateTime newCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        paymentMethod.setCreatedAt(newCreatedAt);
        assertEquals(newCreatedAt, paymentMethod.getCreatedAt());
        
        LocalDateTime newUpdatedAt = LocalDateTime.of(2024, 1, 2, 10, 0);
        paymentMethod.setUpdatedAt(newUpdatedAt);
        assertEquals(newUpdatedAt, paymentMethod.getUpdatedAt());
    }

    @Test
    void testDefaultTransactionList() {
        PaymentMethod newPaymentMethod = new PaymentMethod();
        assertNull(newPaymentMethod.getTopUpTransactions());
    }

    @Test
    void testSetTransactionList() {
        List<TopUpTransaction> transactions = new ArrayList<>();
        
        TopUpTransaction transaction1 = new TopUpTransaction();
        transaction1.setId("txn-1");
        transaction1.setAmount(100000L);
        
        TopUpTransaction transaction2 = new TopUpTransaction();
        transaction2.setId("txn-2");
        transaction2.setAmount(50000L);
        
        transactions.add(transaction1);
        transactions.add(transaction2);
        
        paymentMethod.setTopUpTransactions(transactions);
        
        assertNotNull(paymentMethod.getTopUpTransactions());
        assertEquals(2, paymentMethod.getTopUpTransactions().size());
        assertEquals(100000L, paymentMethod.getTopUpTransactions().get(0).getAmount());
        assertEquals(50000L, paymentMethod.getTopUpTransactions().get(1).getAmount());
    }

    @Test
    void testAddTransactionToList() {
        List<TopUpTransaction> transactions = new ArrayList<>();
        paymentMethod.setTopUpTransactions(transactions);
        
        TopUpTransaction transaction = new TopUpTransaction();
        transaction.setId("txn-3");
        transaction.setAmount(75000L);
        transaction.setPaymentMethod(paymentMethod);
        
        paymentMethod.getTopUpTransactions().add(transaction);
        
        assertEquals(1, paymentMethod.getTopUpTransactions().size());
        assertEquals(paymentMethod, paymentMethod.getTopUpTransactions().get(0).getPaymentMethod());
    }

    @Test
    void testEmptyTransactionList() {
        paymentMethod.setTopUpTransactions(new ArrayList<>());
        
        assertNotNull(paymentMethod.getTopUpTransactions());
        assertTrue(paymentMethod.getTopUpTransactions().isEmpty());
    }

    @Test
    void testMultipleTransactions() {
        List<TopUpTransaction> transactions = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            TopUpTransaction transaction = new TopUpTransaction();
            transaction.setId("txn-" + i);
            transaction.setAmount(100000L * (i + 1));
            transaction.setPaymentMethod(paymentMethod);
            transactions.add(transaction);
        }
        
        paymentMethod.setTopUpTransactions(transactions);
        
        assertEquals(5, paymentMethod.getTopUpTransactions().size());
    }

    @Test
    void testStatusActive() {
        paymentMethod.setStatus("Active");
        assertEquals("Active", paymentMethod.getStatus());
    }

    @Test
    void testStatusInactive() {
        paymentMethod.setStatus("Inactive");
        assertEquals("Inactive", paymentMethod.getStatus());
    }

    @Test
    void testVariousPaymentMethods() {
        String[][] methods = {
            {"Credit Card", "Visa"},
            {"Debit Card", "Mastercard"},
            {"Bank Transfer", "BCA"},
            {"E-Wallet", "GoPay"},
            {"E-Wallet", "OVO"},
            {"Virtual Account", "Mandiri"},
            {"QRIS", "QRIS"}
        };
        
        for (String[] method : methods) {
            paymentMethod.setMethodName(method[0]);
            paymentMethod.setProvider(method[1]);
            assertEquals(method[0], paymentMethod.getMethodName());
            assertEquals(method[1], paymentMethod.getProvider());
        }
    }

    @Test
    void testNullMethodName() {
        paymentMethod.setMethodName(null);
        assertNull(paymentMethod.getMethodName());
    }

    @Test
    void testEmptyMethodName() {
        paymentMethod.setMethodName("");
        assertEquals("", paymentMethod.getMethodName());
    }

    @Test
    void testNullProvider() {
        paymentMethod.setProvider(null);
        assertNull(paymentMethod.getProvider());
    }

    @Test
    void testNullStatus() {
        paymentMethod.setStatus(null);
        assertNull(paymentMethod.getStatus());
    }

    @Test
    void testTimestampUpdate() {
        LocalDateTime originalCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime originalUpdatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        paymentMethod.setCreatedAt(originalCreatedAt);
        paymentMethod.setUpdatedAt(originalUpdatedAt);
        
        assertEquals(originalCreatedAt, paymentMethod.getCreatedAt());
        assertEquals(originalUpdatedAt, paymentMethod.getUpdatedAt());
        
        LocalDateTime newUpdatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);
        paymentMethod.setUpdatedAt(newUpdatedAt);
        
        assertEquals(originalCreatedAt, paymentMethod.getCreatedAt());
        assertEquals(newUpdatedAt, paymentMethod.getUpdatedAt());
    }

    @Test
    void testRemoveTransactionFromList() {
        List<TopUpTransaction> transactions = new ArrayList<>();
        
        TopUpTransaction transaction1 = new TopUpTransaction();
        transaction1.setId("txn-remove-1");
        
        TopUpTransaction transaction2 = new TopUpTransaction();
        transaction2.setId("txn-remove-2");
        
        transactions.add(transaction1);
        transactions.add(transaction2);
        
        paymentMethod.setTopUpTransactions(transactions);
        assertEquals(2, paymentMethod.getTopUpTransactions().size());
        
        paymentMethod.getTopUpTransactions().remove(transaction1);
        assertEquals(1, paymentMethod.getTopUpTransactions().size());
        assertEquals(transaction2, paymentMethod.getTopUpTransactions().get(0));
    }

    @Test
    void testClearTransactionList() {
        List<TopUpTransaction> transactions = new ArrayList<>();
        transactions.add(new TopUpTransaction());
        transactions.add(new TopUpTransaction());
        
        paymentMethod.setTopUpTransactions(transactions);
        assertEquals(2, paymentMethod.getTopUpTransactions().size());
        
        paymentMethod.getTopUpTransactions().clear();
        assertTrue(paymentMethod.getTopUpTransactions().isEmpty());
    }

    @Test
    void testNullTransactionList() {
        paymentMethod.setTopUpTransactions(null);
        assertNull(paymentMethod.getTopUpTransactions());
    }

    @Test
    void testToStringContainsKeyFields() {
        String paymentMethodString = paymentMethod.toString();
        assertNotNull(paymentMethodString);
        assertTrue(paymentMethodString.contains(testId) || 
                   paymentMethodString.contains("id") ||
                   paymentMethodString.contains("Credit Card"));
    }

    @Test
    void testEqualsAndHashCode() {
        PaymentMethod samePaymentMethod = new PaymentMethod();
        samePaymentMethod.setId(testId);
        samePaymentMethod.setMethodName("Credit Card");
        samePaymentMethod.setProvider("Visa");
        samePaymentMethod.setStatus("Active");
        samePaymentMethod.setCreatedAt(paymentMethod.getCreatedAt());
        samePaymentMethod.setUpdatedAt(paymentMethod.getUpdatedAt());
        
        assertEquals(paymentMethod, samePaymentMethod);
        assertEquals(paymentMethod.hashCode(), samePaymentMethod.hashCode());
    }

    @Test
    void testNotEquals() {
        PaymentMethod differentPaymentMethod = new PaymentMethod();
        differentPaymentMethod.setId("pm-different");
        differentPaymentMethod.setMethodName("Different Method");
        
        assertNotEquals(paymentMethod, differentPaymentMethod);
    }

    @Test
    void testBidirectionalRelationship() {
        List<TopUpTransaction> transactions = new ArrayList<>();
        
        TopUpTransaction transaction = new TopUpTransaction();
        transaction.setId("txn-relation");
        transaction.setPaymentMethod(paymentMethod);
        
        transactions.add(transaction);
        paymentMethod.setTopUpTransactions(transactions);
        
        assertEquals(paymentMethod, transaction.getPaymentMethod());
        assertEquals(1, paymentMethod.getTopUpTransactions().size());
        assertEquals(transaction, paymentMethod.getTopUpTransactions().get(0));
    }

    @Test
    void testNullCreatedAt() {
        paymentMethod.setCreatedAt(null);
        assertNull(paymentMethod.getCreatedAt());
    }

    @Test
    void testNullUpdatedAt() {
        paymentMethod.setUpdatedAt(null);
        assertNull(paymentMethod.getUpdatedAt());
    }

    @Test
    void testToBuilder() {
        PaymentMethod cloned = paymentMethod.toBuilder().build();
        
        assertEquals(paymentMethod.getId(), cloned.getId());
        assertEquals(paymentMethod.getMethodName(), cloned.getMethodName());
        assertEquals(paymentMethod.getProvider(), cloned.getProvider());
        assertEquals(paymentMethod.getStatus(), cloned.getStatus());
    }

    @Test
    void testStringIdFormat() {
        String[] testIds = {"pm-1", "pm-12345", "payment-method-abc", "PM-XYZ-123"};
        
        for (String id : testIds) {
            paymentMethod.setId(id);
            assertEquals(id, paymentMethod.getId());
        }
    }
}
