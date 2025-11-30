package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;
import apap.ti._5.accommodation_2306275600_be.service.BillIntegrationService;

@ExtendWith(MockitoExtension.class)
class BookingRestServiceRBACImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private BillIntegrationService billIntegrationService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BookingRestServiceRBACImpl bookingRestServiceRBAC;

    private UserProfileDTO superAdminUser;
    private UserProfileDTO ownerUser;
    private UserProfileDTO customerUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        superAdminUser = new UserProfileDTO(userId, "superadmin", "Super Admin", "superadmin@test.com", "M", "SUPERADMIN", LocalDateTime.now(), LocalDateTime.now(), false);
        ownerUser = new UserProfileDTO(userId, "owner", "Owner", "owner@test.com", "M", "ACCOMMODATION_OWNER", LocalDateTime.now(), LocalDateTime.now(), false);
        customerUser = new UserProfileDTO(userId, "customer", "Customer", "customer@test.com", "M", "CUSTOMER", LocalDateTime.now(), LocalDateTime.now(), false);
    }

    // ============================================
    // CREATE BOOKING TESTS
    // ============================================

    @Test
    void testCreateBooking_AsSuperAdmin_ThrowsAccessDeniedException() {
        CreateBookingRequestDTO requestDTO = new CreateBookingRequestDTO();

        when(authService.getAuthenticatedUser()).thenReturn(superAdminUser);
        when(authService.isCustomer(superAdminUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingRestServiceRBAC.createBooking(requestDTO));

        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    @Test
    void testCreateBooking_AsOwner_ThrowsAccessDeniedException() {
        CreateBookingRequestDTO requestDTO = new CreateBookingRequestDTO();

        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isCustomer(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingRestServiceRBAC.createBooking(requestDTO));

        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // GET ALL BOOKINGS TESTS
    // ============================================

    @Test
    void testGetAllBookings_AsSuperAdmin_Success() throws AccessDeniedException {
        when(authService.getAuthenticatedUser()).thenReturn(superAdminUser);
        when(authService.isSuperAdmin(superAdminUser)).thenReturn(true);

        List<BookingListItemDTO> result = bookingRestServiceRBAC.getAllBookings(null, null);

        assertNotNull(result);
        verify(authService).getAuthenticatedUser();
    }

    @Test
    void testGetAllBookings_AsOwner_Success() throws AccessDeniedException {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(ownerUser)).thenReturn(true);

        List<BookingListItemDTO> result = bookingRestServiceRBAC.getAllBookings(null, null);

        assertNotNull(result);
        verify(authService).getAuthenticatedUser();
    }

    @Test
    void testGetAllBookings_AsCustomer_Success() throws AccessDeniedException {
        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(customerUser)).thenReturn(false);
        when(authService.isCustomer(customerUser)).thenReturn(true);

        List<BookingListItemDTO> result = bookingRestServiceRBAC.getAllBookings(null, null);

        assertNotNull(result);
        verify(authService).getAuthenticatedUser();
    }

    // ============================================
    // GET BOOKING DETAIL TESTS
    // ============================================

    @Test
    void testGetBookingForUpdate_AsSuperAdmin_ThrowsAccessDeniedException() {
        UUID bookingId = UUID.randomUUID();

        when(authService.getAuthenticatedUser()).thenReturn(superAdminUser);
        when(authService.isCustomer(superAdminUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingRestServiceRBAC.getBookingForUpdate(bookingId));

        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // UPDATE BOOKING TESTS
    // ============================================

    @Test
    void testUpdateBooking_AsOwner_ThrowsAccessDeniedException() {
        UpdateBookingRequestDTO requestDTO = new UpdateBookingRequestDTO();

        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isCustomer(ownerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingRestServiceRBAC.updateBooking(requestDTO));

        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // CANCEL BOOKING TESTS
    // ============================================

    @Test
    void testCancelBooking_AsSuperAdmin_ThrowsAccessDeniedException() {
        ChangeBookingStatusRequestDTO requestDTO = new ChangeBookingStatusRequestDTO();

        when(authService.getAuthenticatedUser()).thenReturn(superAdminUser);
        when(authService.isCustomer(superAdminUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingRestServiceRBAC.cancelBooking(requestDTO));

        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // REFUND BOOKING TESTS
    // ============================================

    @Test
    void testRefundBooking_AsCustomer_ThrowsAccessDeniedException() {
        ChangeBookingStatusRequestDTO requestDTO = new ChangeBookingStatusRequestDTO();

        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(customerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingRestServiceRBAC.refundBooking(requestDTO));

        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }

    // ============================================
    // GET BOOKING STATISTICS TESTS
    // ============================================

    @Test
    void testGetBookingStatistics_AsSuperAdmin_Success() throws AccessDeniedException {
        when(authService.getAuthenticatedUser()).thenReturn(superAdminUser);
        when(authService.isSuperAdmin(superAdminUser)).thenReturn(true);

        BookingChartResponseDTO result = bookingRestServiceRBAC.getBookingStatistics(1, 2024);

        assertNotNull(result);
        verify(authService).getAuthenticatedUser();
    }

    @Test
    void testGetBookingStatistics_AsOwner_Success() throws AccessDeniedException {
        when(authService.getAuthenticatedUser()).thenReturn(ownerUser);
        when(authService.isSuperAdmin(ownerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(ownerUser)).thenReturn(true);

        BookingChartResponseDTO result = bookingRestServiceRBAC.getBookingStatistics(1, 2024);

        assertNotNull(result);
        verify(authService).getAuthenticatedUser();
    }

    @Test
    void testGetBookingStatistics_AsCustomer_ThrowsAccessDeniedException() {
        when(authService.getAuthenticatedUser()).thenReturn(customerUser);
        when(authService.isSuperAdmin(customerUser)).thenReturn(false);
        when(authService.isAccommodationOwner(customerUser)).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingRestServiceRBAC.getBookingStatistics(1, 2024));

        assertTrue(exception.getMessage().contains("tidak memiliki akses"));
    }
}
