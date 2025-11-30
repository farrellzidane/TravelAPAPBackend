package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    // Check if there's any booking conflict for a room
    @Query("SELECT b FROM Booking b WHERE b.room.roomID = :roomID " +
           "AND b.status NOT IN (3, 4) " + // Exclude cancelled and completed bookings
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findConflictingBookings(
        @Param("roomID") UUID roomID,
        @Param("checkIn") LocalDateTime checkIn,
        @Param("checkOut") LocalDateTime checkOut
    );
    
    // Find all bookings ordered by booking ID
    @Query("SELECT b FROM Booking b ORDER BY b.bookingID DESC")
    List<Booking> findAllOrderedByBookingID();
    
    // Find bookings by status
    @Query("SELECT b FROM Booking b WHERE b.status = :status ORDER BY b.bookingID DESC")
    List<Booking> findByStatusOrderedByBookingID(@Param("status") int status);
    
    // Search bookings by property name or room number
    @Query("SELECT b FROM Booking b WHERE " +
           "LOWER(b.room.roomType.property.propertyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(CAST(b.room.roomID AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY b.bookingID DESC")
    List<Booking> searchByPropertyOrRoom(@Param("keyword") String keyword);
    
    // Perbaiki juga query ini
    @Query("SELECT b FROM Booking b WHERE " +
           "(LOWER(b.room.roomType.property.propertyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(CAST(b.room.roomID AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND b.status = :status " +
           "ORDER BY b.bookingID DESC")
    List<Booking> searchByPropertyOrRoomAndStatus(
        @Param("keyword") String keyword,
        @Param("status") int status
    );
    
    // Find bookings that need status update (auto check-in)
    @Query("SELECT b FROM Booking b WHERE b.status = 1 " +
           "AND b.checkInDate <= :currentDate")
    List<Booking> findBookingsToAutoCheckIn(@Param("currentDate") LocalDateTime currentDate);
    
    // Find bookings that should be completed (status 2 past check-out date)
    @Query("SELECT b FROM Booking b WHERE b.status = 2 " +
           "AND b.checkOutDate <= :currentDate")
    List<Booking> findBookingsToAutoComplete(@Param("currentDate") LocalDateTime currentDate);
    
    // Find bookings that should be cancelled (only status 0 past check-in date)
    @Query("SELECT b FROM Booking b WHERE b.status = 0 " +
           "AND b.checkInDate < :currentDate")
    List<Booking> findBookingsToAutoCancel(@Param("currentDate") LocalDateTime currentDate);
    
    List<Booking> findByRoom_RoomID(UUID roomID);

    /**
     * Find all bookings with status DONE (4) for a specific month and year
     * Used for statistics/chart
     * Only includes completed bookings (status = 4)
     */
    @Query("SELECT b FROM Booking b " +
       "WHERE b.status = 4 " +  // Only Completed/Done bookings
       "AND MONTH(b.checkOutDate) = :month " +
       "AND YEAR(b.checkOutDate) = :year")
    List<Booking> findDoneBookingsByMonthAndYear(
        @Param("month") int month, 
        @Param("year") int year
    );
    
    /**
     * Count done bookings by property for a specific period
     * Only includes completed bookings (status = 4)
     */
    @Query("SELECT COUNT(b) FROM Booking b " +
       "WHERE b.status = 4 " +  // Only Completed/Done bookings
       "AND b.room.roomType.property.propertyID = :propertyID " +
       "AND MONTH(b.checkOutDate) = :month " +
       "AND YEAR(b.checkOutDate) = :year")
    long countDoneBookingsByPropertyAndPeriod(
        @Param("propertyID") String propertyID,
        @Param("month") int month,
        @Param("year") int year
    );
    
    /**
     * Check if property has any active bookings (not cancelled or completed)
     * Used to prevent property deletion if there are active bookings
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.room.roomType.property.propertyID = :propertyID " +
           "AND b.status NOT IN (3, 4) ") // Exclude cancelled (3) and completed (4)
    boolean existsActiveBookingsByPropertyID(@Param("propertyID") UUID propertyID);
    
    /**
     * Check if room has any bookings during specific period
     * Used to prevent maintenance scheduling if there are bookings
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.room.roomID = :roomID " +
           "AND b.status NOT IN (3, 4) " + // Exclude cancelled and completed
           "AND ((b.checkInDate < :endDate AND b.checkOutDate > :startDate))")
    boolean existsBookingsDuringPeriod(
        @Param("roomID") UUID roomID,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get list of room IDs that have bookings during specific period for a property
     * Used to filter out booked rooms in detail property view
     */
    @Query("SELECT DISTINCT b.room.roomID FROM Booking b " +
           "WHERE b.room.roomType.property.propertyID = :propertyID " +
           "AND b.status NOT IN (3, 4) " + // Exclude cancelled and completed
           "AND ((b.checkInDate < :endDate AND b.checkOutDate > :startDate))")
    List<UUID> findBookedRoomIDsByPropertyAndPeriod(
        @Param("propertyID") UUID propertyID,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}