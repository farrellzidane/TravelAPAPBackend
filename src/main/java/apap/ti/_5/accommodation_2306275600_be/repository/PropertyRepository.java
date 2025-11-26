package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findByOwnerID(UUID ownerID);
    List<Property> findByActiveStatus(int activeStatus);
    List<Property> findByType(int type);
    
    // Filter for active properties only
    List<Property> findByActiveStatusOrderByCreatedDateDesc(int activeStatus);
    
    // Filter by name (case-insensitive) and active status
    @Query("SELECT p FROM Property p WHERE LOWER(p.propertyName) LIKE LOWER(CONCAT('%', :name, '%')) AND p.activeStatus = 1 ORDER BY p.createdDate DESC")
    List<Property> findByPropertyNameContainingIgnoreCaseAndActiveStatus(@Param("name") String name);
    
    // Filter by type and active status
    List<Property> findByTypeAndActiveStatusOrderByCreatedDateDesc(int type, int activeStatus);
    
    // Filter by province and active status
    List<Property> findByProvinceAndActiveStatusOrderByCreatedDateDesc(int province, int activeStatus);
    
    // Filter by name and type and active status
    @Query("SELECT p FROM Property p WHERE LOWER(p.propertyName) LIKE LOWER(CONCAT('%', :name, '%')) AND p.type = :type AND p.activeStatus = 1 ORDER BY p.createdDate DESC")
    List<Property> findByPropertyNameContainingIgnoreCaseAndTypeAndActiveStatus(@Param("name") String name, @Param("type") int type);
    
    // Filter by name and province and active status
    @Query("SELECT p FROM Property p WHERE LOWER(p.propertyName) LIKE LOWER(CONCAT('%', :name, '%')) AND p.province = :province AND p.activeStatus = 1 ORDER BY p.createdDate DESC")
    List<Property> findByPropertyNameContainingIgnoreCaseAndProvinceAndActiveStatus(@Param("name") String name, @Param("province") int province);
    
    // Filter by type and province and active status
    List<Property> findByTypeAndProvinceAndActiveStatusOrderByCreatedDateDesc(int type, int province, int activeStatus);
    
    // Filter by all three and active status
    @Query("SELECT p FROM Property p WHERE LOWER(p.propertyName) LIKE LOWER(CONCAT('%', :name, '%')) AND p.type = :type AND p.province = :province AND p.activeStatus = 1 ORDER BY p.createdDate DESC")
    List<Property> findByPropertyNameContainingIgnoreCaseAndTypeAndProvinceAndActiveStatus(@Param("name") String name, @Param("type") int type, @Param("province") int province);
}