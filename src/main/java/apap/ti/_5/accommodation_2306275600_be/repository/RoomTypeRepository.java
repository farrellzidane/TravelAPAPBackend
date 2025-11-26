package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.RoomType;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {
    List<RoomType> findByProperty_PropertyID(UUID propertyID);
}