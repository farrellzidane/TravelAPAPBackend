package apap.ti._5.accommodation_2306275600_be.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;

@ExtendWith(MockitoExtension.class)
class BillIntegrationServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BillIntegrationServiceImpl billIntegrationService;

    private Booking testBooking;
    private Room testRoom;
    private RoomType testRoomType;
    private Property testProperty;
    private UUID bookingId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID roomTypeId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        // Set up Property
        testProperty = Property.builder()
            .propertyID(propertyId)
            .propertyName("Test Hotel")
            .type(1)
            .address("Test Address")
            .province(1)
            .description("Test Description")
            .totalRoom(10)
            .activeStatus(1)
            .income(0)
            .ownerID(UUID.randomUUID())
            .ownerName("Test Owner")
            .createdDate(LocalDateTime.now())
            .build();

        // Set up RoomType
        testRoomType = RoomType.builder()
            .roomTypeID(roomTypeId)
            .name("Deluxe Room")
            .capacity(2)
            .price(500000)
            .facility("AC, TV, WiFi")
            .floor(3)
            .property(testProperty)
            .createdDate(LocalDateTime.now())
            .build();

        // Set up Room
        testRoom = Room.builder()
            .roomID(roomId)
            .name("301")
            .availabilityStatus(1)
            .activeRoom(1)
            .roomType(testRoomType)
            .createdDate(LocalDateTime.now())
            .build();

        // Set up Booking
        testBooking = Booking.builder()
            .bookingID(bookingId)
            .customerID(customerId)
            .customerName("Test Customer")
            .customerEmail("customer@test.com")
            .room(testRoom)
            .checkInDate(LocalDateTime.now().plusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(3))
            .totalPrice(1000000)
            .status(0)
            .createdDate(LocalDateTime.now())
            .build();

        // Set configuration values using ReflectionTestUtils
        ReflectionTestUtils.setField(billIntegrationService, "billServiceUrl", "http://test-bill-service.com");
        ReflectionTestUtils.setField(billIntegrationService, "apiKey", "TEST_API_KEY");
    }

    // ============================================
    // CREATE BILL FOR BOOKING TESTS
    // ============================================

    @Test
    void testCreateBillForBooking_Success() {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_WithDifferentAmount() {
        testBooking.setTotalPrice(2500000);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_WithLongerStay() {
        testBooking.setCheckInDate(LocalDateTime.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDateTime.now().plusDays(7));
        testBooking.setTotalPrice(3000000);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_RestClientException_DoesNotThrow() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("Connection failed"));

        // Should NOT throw exception - fire and forget
        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_NetworkError_DoesNotThrow() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RuntimeException("Network error"));

        // Should NOT throw exception - fire and forget
        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_BillServiceTimeout_DoesNotThrow() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("Timeout"));

        // Should NOT throw exception - fire and forget
        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_BillService500Error_DoesNotThrow() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("500 Internal Server Error"));

        // Should NOT throw exception - fire and forget
        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_WithDifferentRoomType() {
        testRoomType.setName("Suite Room");
        testRoomType.setPrice(1500000);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_WithMinimalPrice() {
        testBooking.setTotalPrice(100000);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_WithMaximalPrice() {
        testBooking.setTotalPrice(50000000);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_NullPointerException_DoesNotThrow() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new NullPointerException("Null pointer"));

        // Should NOT throw exception - fire and forget
        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_VerifyHttpEntityContents() {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        billIntegrationService.createBillForBooking(testBooking);

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            argThat(entity -> {
                HttpEntity<?> httpEntity = (HttpEntity<?>) entity;
                return httpEntity.getHeaders().getContentType() != null;
            }),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_WithDifferentCustomer() {
        UUID newCustomerId = UUID.randomUUID();
        testBooking.setCustomerID(newCustomerId);
        testBooking.setCustomerName("Different Customer");
        testBooking.setCustomerEmail("different@test.com");
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_WithSameDayCheckInCheckOut() {
        LocalDateTime today = LocalDateTime.now();
        testBooking.setCheckInDate(today.withHour(14).withMinute(0));
        testBooking.setCheckOutDate(today.withHour(23).withMinute(59));
        testBooking.setTotalPrice(500000);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Bill created", HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void testCreateBillForBooking_IllegalArgumentException_DoesNotThrow() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new IllegalArgumentException("Invalid argument"));

        // Should NOT throw exception - fire and forget
        assertDoesNotThrow(() -> billIntegrationService.createBillForBooking(testBooking));

        verify(restTemplate).postForEntity(
            eq("http://test-bill-service.com/api/bill/create"),
            any(HttpEntity.class),
            eq(String.class)
        );
    }
}
