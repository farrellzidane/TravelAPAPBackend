package apap.ti._5.accommodation_2306275600_be.restservice;

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
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyRevenueDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingRestServiceImpl implements BookingRestService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PropertyRepository propertyRepository;
    
    private static final int BREAKFAST_PRICE = 50000;
    
    @Override
    public BookingResponseDTO createBooking(CreateBookingRequestDTO dto) {
        // 1. Validasi Room exists
        Room room = roomRepository.findById(dto.getRoomID())
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
            dto.getRoomID(), 
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
        
        // 7. Generate Booking ID
        String bookingID = generateBookingID(room.getRoomID());
        
        // 8. Create Booking entity
        Booking booking = Booking.builder()
            .bookingID(bookingID)
            .checkInDate(dto.getCheckInDate())
            .checkOutDate(dto.getCheckOutDate())
            .totalDays((int) totalDays)
            .totalPrice(totalPrice)
            .status(0) // 0 = pending/new booking
            .customerID(dto.getCustomerID())
            .customerName(dto.getCustomerName())
            .customerEmail(dto.getCustomerEmail())
            .customerPhone(dto.getCustomerPhone())
            .isBreakfast(dto.getIsBreakfast())
            .capacity(dto.getCapacity())
            .refund(0)
            .extraPay(0)
            .room(room)
            .build();
        
        // 9. Save booking
        Booking savedBooking = bookingRepository.save(booking);
        
        // 10. Log success
        System.out.println("✅ Booking Created Successfully:");
        System.out.println("   Booking ID: " + savedBooking.getBookingID());
        System.out.println("   Room ID: " + room.getRoomID());
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
        // Auto update statuses before fetching
        updateBookingStatuses();
        
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
    public BookingDetailResponseDTO getBookingDetail(String bookingID) {
        // Auto update statuses first
        updateBookingStatuses();
        
        // Find booking
        Booking booking = bookingRepository.findById(bookingID)
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingID));
        
        // Convert to detail DTO
        return convertToDetailResponseDTO(booking);
    }
    
    @Override
    public void updateBookingStatuses() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Auto Check-In: Status 1 (Confirmed) -> Status 2 (Checked-In) when check-in date passed
        List<Booking> bookingsToCheckIn = bookingRepository.findBookingsToAutoCheckIn(now);
        for (Booking booking : bookingsToCheckIn) {
            booking.setStatus(2); // Checked-In
            bookingRepository.save(booking);
            System.out.println("🔄 Auto Check-In: " + booking.getBookingID() + " -> Status 2 (Checked-In)");
        }
        
        // 2. Auto Cancel: Status 0 (Pending) or Status 2 (Checked-In without confirmation) -> Status 3 (Cancelled)
        List<Booking> bookingsToCancel = bookingRepository.findBookingsToAutoCancel(now);
        for (Booking booking : bookingsToCancel) {
            // Calculate refund based on cancellation policy
            int refundAmount = calculateCancellationRefund(booking);
            booking.setStatus(3); // Cancelled
            booking.setRefund(refundAmount);
            bookingRepository.save(booking);
            System.out.println("🔄 Auto Cancel: " + booking.getBookingID() + " -> Status 3 (Cancelled) | Refund: Rp " + refundAmount);
        }
    }
    
    private int calculateCancellationRefund(Booking booking) {
        // Implement cancellation policy
        // For now, return 0 (you can customize based on your business rules)
        return 0;
    }
    
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
    
    private BookingListItemDTO convertToListItemDTO(Booking booking) {
        String roomNumber = extractRoomNumber(booking.getRoom().getRoomID());
        
        return BookingListItemDTO.builder()
            .bookingID(booking.getBookingID())
            .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
            .roomNumber(roomNumber)
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .totalPrice(booking.getTotalPrice())
            .status(booking.getStatus())
            .statusText(getStatusText(booking.getStatus()))
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
    
    private BookingResponseDTO convertToResponseDTO(Booking booking) {
        String roomNumberDisplay = extractRoomNumber(booking.getRoom().getRoomID());
        
        return BookingResponseDTO.builder()
            .bookingID(booking.getBookingID())
            .roomID(booking.getRoom().getRoomID())
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
            .extraPay(booking.getExtraPay())
            .refund(booking.getRefund())
            .createdDate(booking.getCreatedDate())
            .updatedDate(booking.getUpdatedDate())
            .build();
    }
    
    private BookingDetailResponseDTO convertToDetailResponseDTO(Booking booking) {
        // Get room type name
        String roomName = booking.getRoom().getRoomType().getName();
        String roomNumber = extractRoomNumber(booking.getRoom().getRoomID());
        
        // Determine status text and color
        StatusInfo statusInfo = getStatusInfo(booking.getStatus());
        
        // Determine action buttons availability
        boolean canPay = canPay(booking);
        boolean canUpdate = canUpdate(booking);
        boolean canRefund = canRefund(booking);
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
            .extraPay(booking.getExtraPay())
            .refund(booking.getRefund())
            
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
            .canRefund(canRefund)
            .canCancel(canCancel)
            .build();
    }
    
    // Helper method to determine status info
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
            default:
                info.text = "Unknown";
                info.color = "default";
        }
        return info;
    }
    
    // Action button logic
    private boolean canPay(Booking booking) {
        // Can pay if status = 0 (Waiting for Payment) OR has extra payment
        return booking.getStatus() == 0 || booking.getExtraPay() > 0;
    }
    
    private boolean canUpdate(Booking booking) {
        // Can update if status = 0 (without extra pay) OR status = 1
        if (booking.getStatus() == 0 && booking.getExtraPay() == 0) {
            return true;
        }
        return booking.getStatus() == 1;
    }
    
    private boolean canRefund(Booking booking) {
        // Can request refund if refund amount > 0
        return booking.getRefund() > 0;
    }
    
    private boolean canCancel(Booking booking) {
        // Can cancel if status = 0, 1, or 3
        return booking.getStatus() == 0 || 
               booking.getStatus() == 1 || 
               booking.getStatus() == 3;
    }
    
    // Inner class for status info
    private static class StatusInfo {
        String text;
        String color;
    }

    @Override
    public BookingUpdateFormDTO getBookingForUpdate(String bookingID) {
        // Find booking
        Booking booking = bookingRepository.findById(bookingID)
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingID));
        
        // ✅ Updated validation logic
        // Can only update if:
        // 1. Status = 0 (Pending) without extra pay, OR
        // 2. Status = 1 (Confirmed)
        if (booking.getStatus() != 0 && booking.getStatus() != 1) {
            throw new RuntimeException("Can only update bookings with status 'Pending' (0) or 'Confirmed' (1)");
        }
        
        // ✅ Only block if status = 0 AND has extra pay
        if (booking.getStatus() == 0 && booking.getExtraPay() > 0) {
            throw new RuntimeException("Cannot update booking with extra payment pending. Please pay first.");
        }
        
        Room room = booking.getRoom();
        String roomNumber = extractRoomNumber(room.getRoomID());
        
        return BookingUpdateFormDTO.builder()
            .bookingID(booking.getBookingID())
            .propertyID(room.getRoomType().getProperty().getPropertyID())
            .propertyName(room.getRoomType().getProperty().getPropertyName())
            .roomTypeID(room.getRoomType().getRoomTypeID())
            .roomTypeName(room.getRoomType().getName())
            .roomID(room.getRoomID())
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
        
        // 2. ✅ Updated validation logic
        if (booking.getStatus() != 0 && booking.getStatus() != 1) {
            throw new RuntimeException("Can only update bookings with status 'Pending' (0) or 'Confirmed' (1). Current status: " + booking.getStatus());
        }
        
        // ✅ Only block if status = 0 AND has extra pay
        if (booking.getStatus() == 0 && booking.getExtraPay() > 0) {
            throw new RuntimeException("Cannot update booking with extra payment pending. Please pay extra payment first.");
        }
        
        // 3. Validate new room exists
        Room newRoom = roomRepository.findById(dto.getRoomID())
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
            dto.getRoomID(), 
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
        
        // 9. Calculate price difference
        int oldTotalPrice = booking.getTotalPrice();
        int priceDifference = newTotalPrice - oldTotalPrice;
        
        // 10. Update booking fields
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
        
        // 11. ✅ Handle price difference - RESET previous extra pay/refund first
        if (priceDifference > 0) {
            // Price increased → set extraPay, clear refund
            booking.setExtraPay(priceDifference);
            booking.setRefund(0);
            System.out.println("⚠️ Price increased by Rp " + String.format("%,d", priceDifference));
            System.out.println("   Extra payment required: Rp " + String.format("%,d", priceDifference));
        } else if (priceDifference < 0) {
            // Price decreased → set refund, clear extraPay
            booking.setExtraPay(0);
            booking.setRefund(Math.abs(priceDifference));
            System.out.println("✅ Price decreased by Rp " + String.format("%,d", Math.abs(priceDifference)));
            System.out.println("   Refund available: Rp " + String.format("%,d", Math.abs(priceDifference)));
        } else {
            // No price change → clear both
            booking.setExtraPay(0);
            booking.setRefund(0);
            System.out.println("➖ No price change");
        }
        
        // 12. Save updated booking
        Booking updatedBooking = bookingRepository.save(booking);
        
        // 13. Log success
        System.out.println("✅ Booking Updated Successfully:");
        System.out.println("   Booking ID: " + updatedBooking.getBookingID());
        System.out.println("   Old Room: " + booking.getRoom().getRoomID() + " → New Room: " + newRoom.getRoomID());
        System.out.println("   Old Price: Rp " + String.format("%,d", oldTotalPrice) + 
                           " → New Price: Rp " + String.format("%,d", newTotalPrice));
        System.out.println("   Check-in: " + updatedBooking.getCheckInDate());
        System.out.println("   Check-out: " + updatedBooking.getCheckOutDate());
        System.out.println("   Total Days: " + updatedBooking.getTotalDays());
        
        // 14. Convert to DTO and return
        return convertToResponseDTO(updatedBooking);
    }
    
    @Override
    public BookingResponseDTO payBooking(ChangeBookingStatusRequestDTO dto) {
        // Find booking
        Booking booking = bookingRepository.findById(dto.getBookingID())
            .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + dto.getBookingID()));
        
        // Validate: Can only pay if status = 0 OR has extra pay
        if (booking.getStatus() != 0 && booking.getExtraPay() == 0) {
            throw new RuntimeException("This booking does not require payment");
        }
        
        Property property = booking.getRoom().getRoomType().getProperty();
        
        // Case 1: Status = 0 (First payment)
        if (booking.getStatus() == 0) {
            if (booking.getExtraPay() > 0) {
                // Has extra pay → Add extra pay to income
                int paymentAmount = booking.getExtraPay();
                property.setIncome(property.getIncome() + paymentAmount);
                booking.setExtraPay(0);
                
                System.out.println("💰 Extra Payment Processed:");
                System.out.println("   Amount: Rp " + String.format("%,d", paymentAmount));
                System.out.println("   New Property Income: Rp " + String.format("%,d", property.getIncome()));
            } else {
                // No extra pay → Add total price to income
                property.setIncome(property.getIncome() + booking.getTotalPrice());
                
                System.out.println("💰 Initial Payment Processed:");
                System.out.println("   Amount: Rp " + String.format("%,d", booking.getTotalPrice()));
                System.out.println("   New Property Income: Rp " + String.format("%,d", property.getIncome()));
            }
            
            // Change status to Payment Confirmed
            booking.setStatus(1);
            
        } 
        // Case 2: Has extra pay (regardless of status)
        else if (booking.getExtraPay() > 0) {
            int paymentAmount = booking.getExtraPay();
            property.setIncome(property.getIncome() + paymentAmount);
            booking.setExtraPay(0);
            
            System.out.println("💰 Extra Payment Processed:");
            System.out.println("   Amount: Rp " + String.format("%,d", paymentAmount));
            System.out.println("   New Property Income: Rp " + String.format("%,d", property.getIncome()));
        }
        
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
        
        // Validate: Can only cancel if status = 0, 1, or 3
        if (booking.getStatus() != 0 && booking.getStatus() != 1 && booking.getStatus() != 3) {
            throw new RuntimeException("Cannot cancel booking with current status");
        }
        
        Property property = booking.getRoom().getRoomType().getProperty();
        int oldIncome = property.getIncome();
        
        // Case 1: Status = 0 (Pending Payment)
        if (booking.getStatus() == 0) {
            if (booking.getExtraPay() > 0) {
                // Has extra pay → Refund = (totalPrice - extraPay)
                // Income -= totalPrice, Income += extraPay
                int refundAmount = booking.getTotalPrice() - booking.getExtraPay();
                property.setIncome(property.getIncome() - refundAmount);
                
                System.out.println("❌ Cancelled (Status 0 with Extra Pay):");
                System.out.println("   Total Price: Rp " + String.format("%,d", booking.getTotalPrice()));
                System.out.println("   Extra Pay: Rp " + String.format("%,d", booking.getExtraPay()));
                System.out.println("   Refund to Customer: Rp " + String.format("%,d", refundAmount));
                System.out.println("   Income Change: Rp " + String.format("%,d", oldIncome) + 
                                " → Rp " + String.format("%,d", property.getIncome()));
            } else {
                // No payment made yet → Just change status
                System.out.println("❌ Cancelled (Status 0, No Payment Made)");
            }
        }
        // Case 2: Status = 1 (Payment Confirmed)
        else if (booking.getStatus() == 1) {
            // Full refund of total price
            property.setIncome(property.getIncome() - booking.getTotalPrice());
            
            System.out.println("❌ Cancelled (Status 1 - Payment Confirmed):");
            System.out.println("   Refund Amount: Rp " + String.format("%,d", booking.getTotalPrice()));
            System.out.println("   Income Change: Rp " + String.format("%,d", oldIncome) + 
                            " → Rp " + String.format("%,d", property.getIncome()));
        }
        // Case 3: Status = 3 (Request Refund)
        else if (booking.getStatus() == 3) {
            // Deduct (totalPrice - refund) from income
            int deductAmount = booking.getTotalPrice() - booking.getRefund();
            property.setIncome(property.getIncome() - deductAmount);
            
            System.out.println("❌ Cancelled (Status 3 - Request Refund):");
            System.out.println("   Total Price: Rp " + String.format("%,d", booking.getTotalPrice()));
            System.out.println("   Refund Amount: Rp " + String.format("%,d", booking.getRefund()));
            System.out.println("   Income Deduction: Rp " + String.format("%,d", deductAmount));
            System.out.println("   Income Change: Rp " + String.format("%,d", oldIncome) + 
                            " → Rp " + String.format("%,d", property.getIncome()));
        }
        
        // Change status to Cancelled (3)
        booking.setStatus(3);
        
        // Save changes
        propertyRepository.save(property);
        Booking updatedBooking = bookingRepository.save(booking);
        
        System.out.println("✅ Booking Cancelled: " + updatedBooking.getBookingID());
        
        return convertToResponseDTO(updatedBooking);
    }

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
                booking -> booking.getRoom().getRoomType().getProperty().getPropertyID()
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