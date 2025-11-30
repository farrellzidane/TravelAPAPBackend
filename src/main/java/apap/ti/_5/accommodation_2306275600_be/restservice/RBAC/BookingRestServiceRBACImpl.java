package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
import apap.ti._5.accommodation_2306275600_be.restservice.BookingRestServiceImpl;
import apap.ti._5.accommodation_2306275600_be.service.BillIntegrationService;

@Service
public class BookingRestServiceRBACImpl extends BookingRestServiceImpl implements BookingRestServiceRBAC {

    private final AuthService authService;

    public BookingRestServiceRBACImpl(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            PropertyRepository propertyRepository,
            BillIntegrationService billIntegrationService,
            AuthService authService
        ) {
        super(bookingRepository, roomRepository, propertyRepository, billIntegrationService);
        this.authService = authService;
    }

    // [POST] Create Accommodation Booking - Customer
    @Override
    public BookingResponseDTO createBooking(CreateBookingRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.createBooking(dto);
    }

    // [GET] Get All Accommodation Booking - Superadmin, Accommodation Owner, Customer
    @Override
    public List<BookingListItemDTO> getAllBookings(Integer status, String search) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAllBookings(status, search);
    }

    // [GET] Get Accommodation Booking Details - Superadmin, Accommodation Owner, Customer
    @Override
    public BookingDetailResponseDTO getBookingDetail(UUID bookingID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user) || authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getBookingDetail(bookingID);
    }

    // [GET] Get Booking For Update - Customer
    @Override
    public BookingUpdateFormDTO getBookingForUpdate(UUID bookingID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getBookingForUpdate(bookingID);
    }

    // [PUT] Update Accommodation Booking Details - Customer
    @Override
    public BookingResponseDTO updateBooking(UpdateBookingRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updateBooking(dto);
    }

    // [PUT] Update Accommodation Booking Payment Status - API KEY (No RBAC needed, handled by API key authentication)
    @Override
    public BookingResponseDTO payBooking(ChangeBookingStatusRequestDTO dto) throws AccessDeniedException {
        // API KEY authentication is handled at controller level
        // No role-based access control needed here
        return super.payBooking(dto);
    }

    // [PUT] Cancel Booking - Customer
    @Override
    public BookingResponseDTO cancelBooking(ChangeBookingStatusRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.cancelBooking(dto);
    }

    // [PUT] Refund Booking - Superadmin, Accommodation Owner
    @Override
    public BookingResponseDTO refundBooking(ChangeBookingStatusRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.refundBooking(dto);
    }

    // [GET] Get Booking Statistics - Superadmin, Accommodation Owner
    @Override
    public BookingChartResponseDTO getBookingStatistics(Integer month, Integer year) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || authService.isAccommodationOwner(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getBookingStatistics(month, year);
    }

    // Commented out: updateBookingStatuses no longer needed with simplified 3-status model
    /*
    // [UTIL] Update Booking Statuses - System/Internal (No user-based access control)
    @Override
    public void updateBookingStatuses() throws AccessDeniedException {
        // This is a system/scheduled task, no user authentication required
        super.updateBookingStatuses();
    }
    */
}
