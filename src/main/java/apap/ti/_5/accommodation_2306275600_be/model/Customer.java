package apap.ti._5.accommodation_2306275600_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "enduser_id")
public class Customer extends EndUser {
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> listBooking = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccommodationReview> listReview = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TopUpTransaction> listTopUpTransaction = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bill> listBill = new ArrayList<>();
    
    // Constructor for easier object creation
    public Customer(String username, String name, String email, String password, String gender, BigDecimal saldo) {
        this.setUsername(username);
        this.setName(name);
        this.setEmail(email);
        this.setPassword(password);
        this.setRole("CUSTOMER");
        this.setGender(gender);
        this.saldo = saldo != null ? saldo : BigDecimal.ZERO;
    }
}
