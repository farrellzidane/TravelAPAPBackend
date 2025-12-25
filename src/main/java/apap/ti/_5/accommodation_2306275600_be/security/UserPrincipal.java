package apap.ti._5.accommodation_2306275600_be.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents the authenticated user principal stored in SecurityContext
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements Serializable {
    private String userId;
    private String username;
    private String role;
}
