package apap.ti._5.accommodation_2306275600_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_method")
public class PaymentMethod {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "status", nullable = false)
    private String status; // "Active" or "Inactive"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL)
    private List<TopUpTransaction> topUpTransactions;
}
