package apap.ti._5.accommodation_2306275600_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "property")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    @Id
    private String propertyID;

    @Column(nullable = false)
    private String propertyName;

    @Column(nullable = false)
    private int type; // 1: Hotel, 2: Villa, 3: Apartment

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int province;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int totalRoom;

    @Column(nullable = false)
    private int activeStatus; // 0: NonActive, 1: Active

    @Column(nullable = false)
    private int income;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomType> listRoomType;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String ownerID;

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