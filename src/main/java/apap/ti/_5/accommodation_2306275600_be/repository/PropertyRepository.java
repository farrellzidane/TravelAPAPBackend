package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
    List<Property> findByOwnerID(UUID ownerID);
    List<Property> findByActiveStatus(int activeStatus);
    List<Property> findByType(int type);
}