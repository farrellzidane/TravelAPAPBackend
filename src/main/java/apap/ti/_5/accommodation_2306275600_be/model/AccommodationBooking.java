package apap.ti._5.accommodation_2306275600_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "accommodation_booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationBooking {
    @Id
    private String bookingID;

    @Column(nullable = false)
    private LocalDateTime checkInDate;

    @Column(nullable = false)
    private LocalDateTime checkOutDate;

    @Column(nullable = false)
    private int totalDays;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private int status; // 0: Waiting for Payment, 1: Payment Confirmed, 2: Cancelled, 3: Request Refund, 4: Done

    @Column(nullable = false)
    private String customerID;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private boolean isBreakfast;

    @Column(nullable = false)
    private int refund;

    @Column(nullable = false)
    private int extraPay;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "roomID")
    private Room room;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}