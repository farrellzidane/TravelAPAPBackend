package apap.ti._5.accommodation_2306275600_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @Column(name = "bill_id", nullable = false, columnDefinition = "uuid")
    private UUID billId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "enduser_id", nullable = false)
    private Customer customer;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "service_reference_id", nullable = false)
    private String serviceReferenceId;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "status", nullable = false)
    private int status; // 0 = Unpaid, 1 = Paid

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "payment_timestamp")
    private LocalDateTime paymentTimestamp;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (billId == null) {
            billId = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == 0) {
            status = 0; // Default to Unpaid
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
