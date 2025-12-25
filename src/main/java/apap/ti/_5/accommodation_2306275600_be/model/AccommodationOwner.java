package apap.ti._5.accommodation_2306275600_be.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accommodation_owner")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "enduser_id")
public class AccommodationOwner extends EndUser {
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Property> listProperty = new ArrayList<>();
    
    // Constructor for easier object creation
    public AccommodationOwner(String username, String name, String email, String password, String gender) {
        this.setUsername(username);
        this.setName(name);
        this.setEmail(email);
        this.setPassword(password);
        this.setRole("ACCOMMODATION_OWNER");
        this.setGender(gender);
    }
}
