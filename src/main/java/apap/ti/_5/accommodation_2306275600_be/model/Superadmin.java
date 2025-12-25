package apap.ti._5.accommodation_2306275600_be.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "superadmin")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "enduser_id")
public class Superadmin extends EndUser {
    
    @OneToMany(mappedBy = "superadmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TopUpTransaction> listTopUpTransaction = new ArrayList<>();
    
    // Superadmin doesn't need additional fields beyond EndUser
    // Just inherits: id, username, name, email, password, role, gender
}

