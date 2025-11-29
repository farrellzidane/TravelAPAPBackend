package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.repository.AccommodationReviewRepository;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.review.CreateReviewRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.review.ReviewResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.ReviewRestServiceImpl;

@Service
@Primary
public class ReviewRestServiceRBACImpl extends ReviewRestServiceImpl implements ReviewRestServiceRBAC {
    
    private final AuthService authService;
    private final PropertyRepository propertyRepository;
    private final AccommodationReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewRestServiceRBACImpl(
            AuthService authService,
            PropertyRepository propertyRepository,
            AccommodationReviewRepository reviewRepository,
            BookingRepository bookingRepository) {
        this.authService = authService;
        this.propertyRepository = propertyRepository;
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    // [GET] Get All Accommodation Review by Property ID
    // Role: Superadmin, Accommodation Owner, Customer
    // - Accommodation Owner: Only can see reviews for their properties
    @Override
    public List<ReviewResponseDTO> getReviewsByPropertyID(UUID propertyID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || 
                           authService.isAccommodationOwner(user) || 
                           authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Accommodation Owner: Validate they own the property
        if (authService.isAccommodationOwner(user)) {
            Property property = propertyRepository.findById(propertyID)
                    .orElseThrow(() -> new RuntimeException("Property not found"));
            
            if (!property.getOwnerID().equals(user.userId())) {
                throw new AccessDeniedException("Anda tidak memiliki akses untuk melihat review property ini");
            }
        }
        
        return super.getReviewsByPropertyID(propertyID);
    }

    // [GET] Get All Accommodation Review by Customer ID
    // Role: Customer
    // - Only can see their own reviews
    @Override
    public List<ReviewResponseDTO> getReviewsByCustomerID(UUID customerID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Validate customer can only see their own reviews
        if (!user.userId().equals(customerID)) {
            throw new AccessDeniedException("Anda hanya dapat melihat review Anda sendiri");
        }
        
        return super.getReviewsByCustomerID(customerID);
    }

    // [GET] Get Accommodation Review Detail
    // Role: Superadmin, Accommodation Owner, Customer
    // - Accommodation Owner: Only can see review details for their properties
    @Override
    public ReviewResponseDTO getReviewByID(UUID reviewID) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isSuperAdmin(user) || 
                           authService.isAccommodationOwner(user) || 
                           authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        ReviewResponseDTO review = super.getReviewByID(reviewID);
        
        // Accommodation Owner: Validate they own the property
        if (authService.isAccommodationOwner(user)) {
            var reviewEntity = reviewRepository.findByReviewID(reviewID)
                    .orElseThrow(() -> new RuntimeException("Review not found"));
            
            UUID propertyOwnerID = reviewEntity.getProperty().getOwnerID();
            
            if (!propertyOwnerID.equals(user.userId())) {
                throw new AccessDeniedException("Anda tidak memiliki akses untuk melihat detail review ini");
            }
        }
        
        return review;
    }

    // [POST] Create Accommodation Review
    // Role: Customer
    // - Only can create review for their own completed bookings
    @Override
    public ReviewResponseDTO createReview(CreateReviewRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authService.getAuthenticatedUser();
        
        boolean hasAccess = authService.isCustomer(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        // Validate customer owns the booking
        UUID bookingID = UUID.fromString(dto.getBookingID());
        var booking = bookingRepository.findById(bookingID)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getCustomerID().equals(user.userId())) {
            throw new AccessDeniedException("Anda hanya dapat membuat review untuk booking Anda sendiri");
        }
        
        return super.createReview(dto);
    }
}
