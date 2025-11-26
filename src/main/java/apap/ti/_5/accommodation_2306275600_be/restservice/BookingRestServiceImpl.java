package apap.ti._5.accommodation_2306275600_be.restservice;

import java.util.UUID;

import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;
// import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyRevenueDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class BookingRestServiceImpl implements BookingRestService {
    
    protected final BookingRepository bookingRepository;
    protected final RoomRepository roomRepository;
    protected final PropertyRepository propertyRepository;
    
    private static final int BREAKFAST_PRICE = 50000;
    
    @Override
    public BookingResponseDTO createBooking(CreateBookingRequestDTO dto) {
        // 1. Validasi Room exists
        Room room = roomRepository.findById(UUID.fromString(dto.getRoomID()))
            .orElseThrow(() -> new RuntimeException("Room not found with ID: " + dto.getRoomID()));
        
        // 2. Validasi tanggal
        validateBookingDates(dto.getCheckInDate(), dto.getCheckOutDate());
        
        // 3. Validasi capacity
        if (dto.getCapacity() > room.getRoomType().getCapacity()) {
            throw new RuntimeException(
                String.format("Capacity (%d) exceeds room type capacity (%d)", 
                    dto.getCapacity(), room.getRoomType().getCapacity())
            );
        }
        
        // 4. Check maintenance schedule
        if (room.getMaintenanceStart() != null && room.getMaintenanceEnd() != null) {
            if (isDateRangeOverlapping(
                dto.getCheckInDate(), dto.getCheckOutDate(),
                room.getMaintenanceStart(), room.getMaintenanceEnd())) {
                throw new RuntimeException(
                    String.format("Room has maintenance scheduled from %s to %s",
                        room.getMaintenanceStart(), room.getMaintenanceEnd())
                );
            }
        }
        
        // 5. Check booking conflicts
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            UUID.fromString(dto.getRoomID()), 
            dto.getCheckInDate(), 
            dto.getCheckOutDate()
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException(
                "Room is already booked for the selected dates"
            );
        }
        
        // 6. Calculate total days and price
        long totalDays = ChronoUnit.DAYS.between(
            dto.getCheckInDate().toLocalDate(), 
            dto.getCheckOutDate().toLocalDate()
        );
        
        if (totalDays < 1) {
            totalDays = 1; // Minimum 1 day
        }
        
        int basePrice = room.getRoomType().getPrice() * (int) totalDays;
        int totalPrice = basePrice;
        
        if (dto.getIsBreakfast()) {
            totalPrice += BREAKFAST_PRICE * (int) totalDays;
        }
        
        // 7. Create Booking entity with UUID
        // Note: bookingID will be auto-generated as UUID in @PrePersist
        Booking booking = Booking.builder()
            .checkInDate(dto.getCheckInDate())
            .checkOutDate(dto.getCheckOutDate())
            .totalDays((int) totalDays)
            .totalPrice(totalPrice)
            .status(0) // 0 = Waiting for Payment
            .customerID(dto.getCustomerID())
            .customerName(dto.getCustomerName())
            .customerEmail(dto.getCustomerEmail())
            .customerPhone(dto.getCustomerPhone())
            .isBreakfast(dto.getIsBreakfast())
            .capacity(dto.getCapacity())
            // Removed: refund and extraPay as per updated requirements
            // .refund(0)
            // .extraPay(0)
            .room(room)
            .build();
        
        // 8. Save booking
        Booking savedBooking = bookingRepository.save(booking);
        
        // 10. Log success
        System.out.println("✅ Booking Created Successfully:");
        System.out.println("   Booking ID: " + savedBooking.getBookingID().toString());
        System.out.println("   Room ID: " + room.getRoomID().toString());
        System.out.println("   Customer: " + savedBooking.getCustomerName());
        System.out.println("   Check-in: " + savedBooking.getCheckInDate());
        System.out.println("   Check-out: " + savedBooking.getCheckOutDate());
        System.out.println("   Total Days: " + savedBooking.getTotalDays());
        System.out.println("   Total Price: Rp " + String.format("%,d", savedBooking.getTotalPrice()));
        
        // 11. Convert to DTO and return
        return convertToResponseDTO(savedBooking);
    }
    
    @Override
    public List<BookingListItemDTO> getAllBookings(Integer status, String search) {
        // Commented out: Auto update statuses removed with simplified 3-status model
        // updateBookingStatuses();
        
        List<Booking> bookings;
        
        // Apply filters
        if (status != null && search != null && !search.trim().isEmpty()) {
            // Filter by both status and search keyword
            bookings = bookingRepository.searchByPropertyOrRoomAndStatus(search.trim(), status);
        } else if (status != null) {
            // Filter by status only
            bookings = bookingRepository.findByStatusOrderedByBookingID(status);
        } else if (search != null && !search.trim().isEmpty()) {
            // Filter by search keyword only
            bookings = bookingRepository.searchByPropertyOrRoom(search.trim());
        } else {
            // No filter, get all
            bookings = bookingRepository.findAllOrderedByBookingID();
        }
        
        // Convert to DTO
        return bookings.stream()
            .map(this::convertToListItemDTO)
            .collect(Collectors.toList());
    }

    @Override
    public BookingDetailResponseDTO getBookingDetail(UUID bookingID) {
        // Commented out: Auto update statuses removed with simplified 3-status model
        // updateBookingStatuses();
        
        // Find booking
        Booking booking = bookingRepository.findById(bookingID)
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingID));
        
        // Convert to detail DTO
        return convertToDetailResponseDTO(booking);
    }
    
    // Commented out: updateBookingStatuses no longer needed with simplified 3-status model
    /*
    @Override
    public void updateBookingStatuses() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Auto Check-In: Status 1 (Confirmed) -> Status 2 (Checked-In) when check-in date passed
        List<Booking> bookingsToCheckIn = bookingRepository.findBookingsToAutoCheckIn(now);
        for (Booking booking : bookingsToCheckIn) {
            booking.setStatus(2); // Checked-In
            bookingRepository.save(booking);
            System.out.println("🔄 Auto Check-In: " + booking.getBookingID().toString() + " -> Status 2 (Checked-In)");
        }
        
        // 2. Auto Complete: Status 2 (Checked-In) -> Status 4 (Completed/Done) when check-out date passed
        List<Booking> bookingsToComplete = bookingRepository.findBookingsToAutoComplete(now);
        for (Booking booking : bookingsToComplete) {
            booking.setStatus(4); // Completed/Done
            bookingRepository.save(booking);
            System.out.println("🔄 Auto Complete: " + booking.getBookingID().toString() + " -> Status 4 (Completed/Done)");
        }
        
        // 3. Auto Cancel: Status 0 (Pending) -> Status 3 (Cancelled) when check-in date passed
        List<Booking> bookingsToCancel = bookingRepository.findBookingsToAutoCancel(now);
        for (Booking booking : bookingsToCancel) {
            // Calculate refund based on cancellation policy
            int refundAmount = calculateCancellationRefund(booking);
            booking.setStatus(3); // Cancelled
            booking.setRefund(refundAmount);
            bookingRepository.save(booking);
            System.out.println("🔄 Auto Cancel: " + booking.getBookingID().toString() + " -> Status 3 (Cancelled) | Refund: Rp " + refundAmount);
        }
    }
    
    private int calculateCancellationRefund(Booking booking) {
        // Implement cancellation policy
        // For now, return 0 (you can customize based on your business rules)
        return 0;
    }
    */
    
    // Commented out: getStatusText using old 5-status model, replaced by getStatusInfo
    /*
    private String getStatusText(int status) {
        switch (status) {
            case 0: return "Pending";
            case 1: return "Confirmed";
            case 2: return "Checked-In";
            case 3: return "Cancelled";
            case 4: return "Completed";
            default: return "Unknown";
        }
    }
    */
    
    private BookingListItemDTO convertToListItemDTO(Booking booking) {
        String roomNumber = extractRoomNumber(booking.getRoom().getRoomID().toString());
        StatusInfo statusInfo = getStatusInfo(booking.getStatus());
        
        return BookingListItemDTO.builder()
            .bookingID(booking.getBookingID())
            .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
            .roomNumber(roomNumber)
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .totalPrice(booking.getTotalPrice())
            .status(booking.getStatus())
            .statusText(statusInfo.text)
            // Removed: refund field as per updated requirements
            // .refund(booking.getRefund())
            .build();
    }
    
    private String extractRoomNumber(String roomID) {
        if (roomID != null && !roomID.isEmpty()) {
            String[] parts = roomID.split("-");
            if (parts.length >= 1) {
                return parts[parts.length - 1];
            }
        }
        return roomID;
    }

    // ...existing code...
    
    private void validateBookingDates(LocalDateTime checkIn, LocalDateTime checkOut) {
        LocalDateTime now = LocalDateTime.now();
        
        if (checkIn.isBefore(now)) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }
        
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }
        
        long daysBetween = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
        if (daysBetween < 1) {
            throw new RuntimeException("Minimum booking duration is 1 day");
        }
    }
    
    private boolean isDateRangeOverlapping(
        LocalDateTime start1, LocalDateTime end1,
        LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    // Commented out: generateBookingID no longer used, replaced with UUID generation in entity @PrePersist
    /*
    private String generateBookingID(String roomID) {
        if (roomID == null || roomID.isEmpty()) {
            throw new RuntimeException("Room ID is required for booking ID generation");
        }
        
        String[] parts = roomID.split("-");
        
        String propertyCode = "";
        String roomNumber = "";
        
        if (parts.length >= 4) {
            propertyCode = parts[parts.length - 2];
            roomNumber = parts[parts.length - 1];
        } else if (parts.length >= 2) {
            propertyCode = parts[parts.length - 2];
            roomNumber = parts[parts.length - 1];
        } else {
            throw new RuntimeException("Invalid Room ID format: " + roomID);
        }
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
        String datetime = now.format(formatter);
        
        return String.format("BOOK-%s-%s-%s", propertyCode, roomNumber, datetime);
    }
    */
    
    private BookingResponseDTO convertToResponseDTO(Booking booking) {
        String roomNumberDisplay = extractRoomNumber(booking.getRoom().getRoomID().toString());
        
        return BookingResponseDTO.builder()
            .bookingID(booking.getBookingID())
            .roomID(booking.getRoom().getRoomID().toString())
            .roomNumber(roomNumberDisplay)
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .totalDays(booking.getTotalDays())
            .totalPrice(booking.getTotalPrice())
            .status(booking.getStatus())
            .customerID(booking.getCustomerID())
            .customerName(booking.getCustomerName())
            .customerEmail(booking.getCustomerEmail())
            .customerPhone(booking.getCustomerPhone())
            .isBreakfast(booking.isBreakfast())
            .capacity(booking.getCapacity())
            // Removed: extraPay and refund fields as per updated requirements
            // .extraPay(booking.getExtraPay())
            // .refund(booking.getRefund())
            .createdDate(booking.getCreatedDate())
            .updatedDate(booking.getUpdatedDate())
            .build();
    }
    
    private BookingDetailResponseDTO convertToDetailResponseDTO(Booking booking) {
        // Get room type name
        String roomName = booking.getRoom().getRoomType().getName();
        String roomNumber = extractRoomNumber(booking.getRoom().getRoomID().toString());
        
        // Determine status text and color
        StatusInfo statusInfo = getStatusInfo(booking.getStatus());
        
        // Determine action buttons availability
        boolean canPay = canPay(booking);
        boolean canUpdate = canUpdate(booking);
        // Removed: canRefund as refund feature removed
        // boolean canRefund = canRefund(booking);
        boolean canCancel = canCancel(booking);
        
        return BookingDetailResponseDTO.builder()
            // Basic Info
            .bookingID(booking.getBookingID())
            .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
            .roomName(roomName)
            .roomNumber(roomNumber)
            
            // Customer Info
            .customerID(booking.getCustomerID())
            .customerName(booking.getCustomerName())
            .customerEmail(booking.getCustomerEmail())
            .customerPhone(booking.getCustomerPhone())
            
            // Booking Details
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .totalDays(booking.getTotalDays())
            .isBreakfast(booking.isBreakfast())
            .capacity(booking.getCapacity())
            
            // Pricing
            .totalPrice(booking.getTotalPrice())
            // Removed: extraPay and refund fields as per updated requirements
            // .extraPay(booking.getExtraPay())
            // .refund(booking.getRefund())
            
            // Status
            .status(booking.getStatus())
            .statusText(statusInfo.text)
            .statusColor(statusInfo.color)
            
            // Timestamps
            .createdDate(booking.getCreatedDate())
            .updatedDate(booking.getUpdatedDate())
            
            // Action Buttons
            .canPay(canPay)
            .canUpdate(canUpdate)
            // Removed: canRefund as refund feature removed
            // .canRefund(canRefund)
            .canCancel(canCancel)
            .build();
    }
    
    // Helper method to determine status info (Updated to 3 statuses only)
    private StatusInfo getStatusInfo(int status) {
        StatusInfo info = new StatusInfo();
        switch (status) {
            case 0:
                info.text = "Waiting for Payment";
                info.color = "warning"; // Yellow/Orange
                break;
            case 1:
                info.text = "Payment Confirmed";
                info.color = "success"; // Green
                break;
            case 2:
                info.text = "Cancelled";
                info.color = "danger"; // Red
                break;
            // Commented out: Old status codes removed as per updated requirements
            /*
            case 2:
                info.text = "Checked-In";
                info.color = "info"; // Blue
                break;
            case 3:
                info.text = "Cancelled";
                info.color = "danger"; // Red
                break;
            case 4:
                info.text = "Done";
                info.color = "secondary"; // Gray
                break;
            */
            default:
                info.text = "Unknown";
                info.color = "default";
        }
        return info;
    }
    
    // Action button logic (Updated for 3 statuses)
    private boolean canPay(Booking booking) {
        // Can pay only if status = 0 (Waiting for Payment)
        return booking.getStatus() == 0;
    }
    
    private boolean canUpdate(Booking booking) {
        // Can update only if status = 0 (Waiting for Payment)
        // After payment confirmation (status = 1), no updates allowed
        return booking.getStatus() == 0;
    }
    
    // Commented out: canRefund no longer needed with refund feature removed
    /*
    private boolean canRefund(Booking booking) {
        // Can request refund if refund amount > 0
        return booking.getRefund() > 0;
    }
    */
    
    private boolean canCancel(Booking booking) {
        // Can cancel only if status = 0 (Waiting for Payment)
        return booking.getStatus() == 0;
    }
    
    // Inner class for status info
    private static class StatusInfo {
        String text;
        String color;
    }

    @Override
    public BookingUpdateFormDTO getBookingForUpdate(UUID bookingID) {
        // Find booking
        Booking booking = bookingRepository.findById(bookingID)
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingID));
        
        // Validation: Can only update if status = 0 (Waiting for Payment)
        if (booking.getStatus() != 0) {
            throw new RuntimeException("Can only update bookings with status 'Waiting for Payment' (0)");
        }
        
        Room room = booking.getRoom();
        String roomNumber = extractRoomNumber(room.getRoomID().toString());
        
        return BookingUpdateFormDTO.builder()
            .bookingID(booking.getBookingID())
            .propertyID(room.getRoomType().getProperty().getPropertyID().toString())
            .propertyName(room.getRoomType().getProperty().getPropertyName())
            .roomTypeID(room.getRoomType().getRoomTypeID().toString())
            .roomTypeName(room.getRoomType().getName())
            .roomID(room.getRoomID().toString())
            .roomNumber(roomNumber)
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .customerID(booking.getCustomerID())
            .customerName(booking.getCustomerName())
            .customerEmail(booking.getCustomerEmail())
            .customerPhone(booking.getCustomerPhone())
            .isBreakfast(booking.isBreakfast())
            .capacity(booking.getCapacity())
            .currentTotalPrice(booking.getTotalPrice())
            .currentTotalDays(booking.getTotalDays())
            .build();
    }

    @Override
    public BookingResponseDTO updateBooking(UpdateBookingRequestDTO dto) {
        // 1. Find existing booking
        Booking booking = bookingRepository.findById(dto.getBookingID())
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + dto.getBookingID()));
        
        // 2. Validation: Prevent updates after payment confirmation (status = 1)
        if (booking.getStatus() == 1) {
            throw new RuntimeException("Cannot update booking after payment has been confirmed.");
        }
        
        // Only allow updates for status = 0 (Waiting for Payment)
        if (booking.getStatus() != 0) {
            throw new RuntimeException("Can only update bookings with status 'Waiting for Payment' (0). Current status: " + booking.getStatus());
        }
        
        // 3. Validate new room exists
        Room newRoom = roomRepository.findById(UUID.fromString(dto.getRoomID()))
            .orElseThrow(() -> new RuntimeException("Room not found with ID: " + dto.getRoomID()));
        
        // 4. Validate dates
        validateBookingDates(dto.getCheckInDate(), dto.getCheckOutDate());
        
        // 5. Validate capacity
        if (dto.getCapacity() > newRoom.getRoomType().getCapacity()) {
            throw new RuntimeException(
                String.format("Capacity (%d) exceeds room type capacity (%d)", 
                    dto.getCapacity(), newRoom.getRoomType().getCapacity())
            );
        }
        
        // 6. Check maintenance schedule
        if (newRoom.getMaintenanceStart() != null && newRoom.getMaintenanceEnd() != null) {
            if (isDateRangeOverlapping(
                dto.getCheckInDate(), dto.getCheckOutDate(),
                newRoom.getMaintenanceStart(), newRoom.getMaintenanceEnd())) {
                throw new RuntimeException(
                    String.format("Room has maintenance scheduled from %s to %s",
                        newRoom.getMaintenanceStart(), newRoom.getMaintenanceEnd())
                );
            }
        }
        
        // 7. Check booking conflicts (exclude current booking)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            UUID.fromString(dto.getRoomID()), 
            dto.getCheckInDate(), 
            dto.getCheckOutDate()
        );
        
        // Remove current booking from conflicts
        conflicts.removeIf(b -> b.getBookingID().equals(booking.getBookingID()));
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }
        
        // 8. Calculate new total days and price
        long newTotalDays = ChronoUnit.DAYS.between(
            dto.getCheckInDate().toLocalDate(), 
            dto.getCheckOutDate().toLocalDate()
        );
        
        if (newTotalDays < 1) {
            newTotalDays = 1;
        }
        
        int newBasePrice = newRoom.getRoomType().getPrice() * (int) newTotalDays;
        int newTotalPrice = newBasePrice;
        
        if (dto.getIsBreakfast()) {
            newTotalPrice += BREAKFAST_PRICE * (int) newTotalDays;
        }
        
        // 9. Update booking fields (no price difference calculation needed - refund/extraPay removed)
        booking.setRoom(newRoom);
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setTotalDays((int) newTotalDays);
        booking.setTotalPrice(newTotalPrice);
        booking.setCustomerID(dto.getCustomerID());
        booking.setCustomerName(dto.getCustomerName());
        booking.setCustomerEmail(dto.getCustomerEmail());
        booking.setCustomerPhone(dto.getCustomerPhone());
        booking.setBreakfast(dto.getIsBreakfast());
        booking.setCapacity(dto.getCapacity());
        
        // Commented out: Price difference handling removed as refund/extraPay features removed
        /*
        // 11. ✅ Handle price difference based on current status
        if (priceDifference > 0) {
            // Price increased → set extraPay, clear refund
            booking.setExtraPay(priceDifference);
            booking.setRefund(0);
            System.out.println("⚠️ Price increased by Rp " + String.format("%,d", priceDifference));
            System.out.println("   Extra payment required: Rp " + String.format("%,d", priceDifference));
        } else if (priceDifference < 0) {
            // Price decreased
            int refundAmount = Math.abs(priceDifference);
            
            if (booking.getStatus() == 1) {
                // Status 1 (Payment Confirmed) → Customer already paid oldTotalPrice
                // Set refund and update totalPrice to reflect net amount after refund
                booking.setExtraPay(0);
                booking.setRefund(refundAmount);
                booking.setTotalPrice(newTotalPrice); // Update totalPrice to new amount
                
                System.out.println("✅ Price decreased by Rp " + String.format("%,d", refundAmount));
                System.out.println("   Status: Payment Confirmed - Customer already paid");
                System.out.println("   Total Price updated: Rp " + String.format("%,d", oldTotalPrice) + 
                                 " → Rp " + String.format("%,d", newTotalPrice));
                System.out.println("   Refund to customer: Rp " + String.format("%,d", refundAmount));
            } else {
                // Status 0 (Pending) → Customer hasn't paid yet
                // Just set refund, totalPrice already updated above
                booking.setExtraPay(0);
                booking.setRefund(refundAmount);
                
                System.out.println("✅ Price decreased by Rp " + String.format("%,d", refundAmount));
                System.out.println("   Status: Pending - No payment made yet");
                System.out.println("   Refund available: Rp " + String.format("%,d", refundAmount));
            }
        } else {
            // No price change → clear both
            booking.setExtraPay(0);
            booking.setRefund(0);
            System.out.println("➖ No price change");
        }
        */
        
        // 10. Save updated booking
        Booking updatedBooking = bookingRepository.save(booking);
        
        // 11. Log success
        System.out.println("✅ Booking Updated Successfully:");
        System.out.println("   Booking ID: " + updatedBooking.getBookingID());
        System.out.println("   New Room: " + newRoom.getRoomID());
        System.out.println("   New Price: Rp " + String.format("%,d", newTotalPrice));
        System.out.println("   Check-in: " + updatedBooking.getCheckInDate());
        System.out.println("   Check-out: " + updatedBooking.getCheckOutDate());
        System.out.println("   Total Days: " + updatedBooking.getTotalDays());
        
        // 12. Convert to DTO and return
        return convertToResponseDTO(updatedBooking);
    }
    
    @Override
    public BookingResponseDTO payBooking(ChangeBookingStatusRequestDTO dto) {
        // Find booking
        Booking booking = bookingRepository.findById(dto.getBookingID())
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + dto.getBookingID()));
        
        // Validate: Can only pay if status = 0 (Waiting for Payment)
        if (booking.getStatus() != 0) {
            throw new RuntimeException("This booking does not require payment or has already been paid");
        }
        
        Property property = booking.getRoom().getRoomType().getProperty();
        
        // Add total price to property income
        property.setIncome(property.getIncome() + booking.getTotalPrice());
        
        System.out.println("💰 Payment Processed:");
        System.out.println("   Amount: Rp " + String.format("%,d", booking.getTotalPrice()));
        System.out.println("   New Property Income: Rp " + String.format("%,d", property.getIncome()));
        
        // Change status to Payment Confirmed (1)
        booking.setStatus(1);
        
        // Save changes
        propertyRepository.save(property);
        Booking updatedBooking = bookingRepository.save(booking);
        
        System.out.println("✅ Payment Successful for Booking: " + updatedBooking.getBookingID());
        
        return convertToResponseDTO(updatedBooking);
    }

    @Override
    public BookingResponseDTO cancelBooking(ChangeBookingStatusRequestDTO dto) {
        // Find booking
        Booking booking = bookingRepository.findById(dto.getBookingID())
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + dto.getBookingID()));
        
        // Validate: Can only cancel if status = 0 (Waiting for Payment)
        if (booking.getStatus() != 0) {
            throw new RuntimeException("Cannot cancel booking that has been paid or already cancelled");
        }
        
        // Change status to Cancelled (2)
        booking.setStatus(2);
        
        // Save changes
        Booking updatedBooking = bookingRepository.save(booking);
        
        System.out.println("❌ Booking Cancelled: " + updatedBooking.getBookingID());
        
        return convertToResponseDTO(updatedBooking);
    }

    // Commented out: refundBooking no longer needed with refund feature removed
    /*
    @Override
    public BookingResponseDTO refundBooking(ChangeBookingStatusRequestDTO dto) {
        // Find booking
        Booking booking = bookingRepository.findById(dto.getBookingID())
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + dto.getBookingID()));
        
        // Validate: Can only refund if refund > 0
        if (booking.getRefund() <= 0) {
            throw new RuntimeException("No refund available for this booking");
        }
        
        Property property = booking.getRoom().getRoomType().getProperty();
        int refundAmount = booking.getRefund();
        int oldIncome = property.getIncome();
        
        // Deduct refund amount from property income
        property.setIncome(property.getIncome() - refundAmount);
        
        // Clear refund after processing
        booking.setRefund(0);
        
        System.out.println("💸 Refund Processed:");
        System.out.println("   Booking ID: " + booking.getBookingID());
        System.out.println("   Refund Amount: Rp " + String.format("%,d", refundAmount));
        System.out.println("   Income Change: Rp " + String.format("%,d", oldIncome) + 
                        " → Rp " + String.format("%,d", property.getIncome()));
        
        // Save changes
        propertyRepository.save(property);
        Booking updatedBooking = bookingRepository.save(booking);
        
        System.out.println("✅ Refund Successful for Booking: " + updatedBooking.getBookingID());
        
        return convertToResponseDTO(updatedBooking);
    }
    */
    
    @Override
    public BookingResponseDTO refundBooking(ChangeBookingStatusRequestDTO dto) {
        throw new RuntimeException("Refund feature has been removed as per updated requirements");
    }

    @Override
    public BookingChartResponseDTO getBookingStatistics(Integer month, Integer year) {
        // Use current month/year if not provided
        LocalDateTime now = LocalDateTime.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();
        
        // Validate month
        if (targetMonth < 1 || targetMonth > 12) {
            throw new RuntimeException("Invalid month. Must be between 1 and 12");
        }
        
        // Validate year
        if (targetYear < 2000 || targetYear > 2100) {
            throw new RuntimeException("Invalid year");
        }
        
        System.out.println("📊 Generating Booking Statistics for: " + 
                        YearMonth.of(targetYear, targetMonth));
        
        // Get all done bookings for the period
        List<Booking> doneBookings = bookingRepository.findDoneBookingsByMonthAndYear(
            targetMonth, targetYear
        );
        
        System.out.println("   Total Done Bookings: " + doneBookings.size());
        
        // Group bookings by property and calculate revenue
        Map<String, List<Booking>> bookingsByProperty = doneBookings.stream()
            .collect(Collectors.groupingBy(
                booking -> booking.getRoom().getRoomType().getProperty().getPropertyID().toString()
            ));
        
        // Calculate revenue per property
        List<PropertyRevenueDTO> propertyRevenues = new ArrayList<>();
        int totalRevenue = 0;
        
        for (Map.Entry<String, List<Booking>> entry : bookingsByProperty.entrySet()) {
            String propertyID = entry.getKey();
            List<Booking> propertyBookings = entry.getValue();
            
            // Get property info from first booking
            Property property = propertyBookings.get(0).getRoom().getRoomType().getProperty();
            
            // Calculate total revenue for this property
            int propertyRevenue = propertyBookings.stream()
                .mapToInt(Booking::getTotalPrice)
                .sum();
            
            totalRevenue += propertyRevenue;
            
            PropertyRevenueDTO revenueDTO = PropertyRevenueDTO.builder()
                .propertyID(propertyID)
                .propertyName(property.getPropertyName())
                .propertyType(String.valueOf(property.getType()))
                .totalBookings(propertyBookings.size())
                .totalRevenue(propertyRevenue)
                .percentage(0.0) // Will calculate later
                .build();
            
            propertyRevenues.add(revenueDTO);
            
            System.out.println("   - " + property.getPropertyName() + ": Rp " + 
                            String.format("%,d", propertyRevenue) + 
                            " (" + propertyBookings.size() + " bookings)");
        }
        
        // Calculate percentages and sort by revenue (descending)
        final int finalTotalRevenue = totalRevenue;
        propertyRevenues.forEach(dto -> {
            if (finalTotalRevenue > 0) {
                dto.setPercentage((dto.getTotalRevenue() * 100.0) / finalTotalRevenue);
            }
        });
        
        // Sort by revenue descending (highest first)
        propertyRevenues.sort((a, b) -> Integer.compare(b.getTotalRevenue(), a.getTotalRevenue()));
        
        // Format period string
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        String periodString = monthNames[targetMonth - 1] + " " + targetYear;
        
        BookingChartResponseDTO response = BookingChartResponseDTO.builder()
            .period(periodString)
            .month(targetMonth)
            .year(targetYear)
            .totalProperties(propertyRevenues.size())
            .totalRevenue(totalRevenue)
            .propertyRevenues(propertyRevenues)
            .build();
        
        System.out.println("✅ Statistics Generated:");
        System.out.println("   Total Properties: " + propertyRevenues.size());
        System.out.println("   Total Revenue: Rp " + String.format("%,d", totalRevenue));
        
        return response;
    }
    
}