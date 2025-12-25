package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.EndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EndUserRepository extends JpaRepository<EndUser, UUID> {
    
    Optional<EndUser> findByEmail(String email);
    
    Optional<EndUser> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM EndUser u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<EndUser> findByEmailOrUsername(@Param("identifier") String identifier);
}
