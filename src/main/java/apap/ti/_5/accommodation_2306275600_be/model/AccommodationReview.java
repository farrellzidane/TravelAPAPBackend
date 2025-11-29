package apap.ti._5.accommodation_2306275600_be.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation_review")
@SQLDelete(sql = "UPDATE accommodation_review SET deleted = true WHERE review_id = ?")
@Where(clause = "deleted = false")
public class AccommodationReview {
    
    @Id
    @Column(name = "review_id", nullable = false)
    private UUID reviewID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_review_booking"))
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", referencedColumnName = "property_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_review_property"))
    private Property property;

    @Column(name = "customer_id", nullable = false)
    private UUID customerID;

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Overall rating must be at least 1")
    @Max(value = 5, message = "Overall rating must be at most 5")
    @Column(name = "overall_rating", nullable = false)
    private Double overallRating;

    @NotNull(message = "Cleanliness rating is required")
    @Min(value = 1, message = "Cleanliness rating must be between 1 and 5")
    @Max(value = 5, message = "Cleanliness rating must be between 1 and 5")
    @Column(name = "cleanliness_rating", nullable = false)
    private Integer cleanlinessRating;

    @NotNull(message = "Facility rating is required")
    @Min(value = 1, message = "Facility rating must be between 1 and 5")
    @Max(value = 5, message = "Facility rating must be between 1 and 5")
    @Column(name = "facility_rating", nullable = false)
    private Integer facilityRating;

    @NotNull(message = "Service rating is required")
    @Min(value = 1, message = "Service rating must be between 1 and 5")
    @Max(value = 5, message = "Service rating must be between 1 and 5")
    @Column(name = "service_rating", nullable = false)
    private Integer serviceRating;

    @NotNull(message = "Value rating is required")
    @Min(value = 1, message = "Value rating must be between 1 and 5")
    @Max(value = 5, message = "Value rating must be between 1 and 5")
    @Column(name = "value_rating", nullable = false)
    private Integer valueRating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @PrePersist
    public void prePersist() {
        if (this.reviewID == null) {
            this.reviewID = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = LocalDateTime.now();
        }
        // Calculate overall rating as average
        if (this.cleanlinessRating != null && this.facilityRating != null && 
            this.serviceRating != null && this.valueRating != null) {
            this.overallRating = (this.cleanlinessRating + this.facilityRating + 
                                 this.serviceRating + this.valueRating) / 4.0;
        }
    }
}
