package apap.ti._5.accommodation_2306275600_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    private String roomID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int availabilityStatus; // 0: Unavailable, 1: Available

    @Column(nullable = false)
    private int activeRoom; // 0: NonActive, 1: Active

    private LocalDateTime maintenanceStart;

    private LocalDateTime maintenanceEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", referencedColumnName = "roomTypeID")
    private RoomType roomType;

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