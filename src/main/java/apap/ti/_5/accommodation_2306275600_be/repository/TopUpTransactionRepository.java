package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.TopUpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopUpTransactionRepository extends JpaRepository<TopUpTransaction, String> {
    
    // Find all transactions by end user ID (not deleted)
    @Query("SELECT t FROM TopUpTransaction t WHERE (t.customer.id = :endUserId OR t.superadmin.id = :endUserId) AND t.deletedAt IS NULL")
    List<TopUpTransaction> findByEndUserIdAndNotDeleted(@Param("endUserId") UUID endUserId);
    
    // Find all transactions by status (not deleted)
    @Query("SELECT t FROM TopUpTransaction t WHERE t.status = :status AND t.deletedAt IS NULL")
    List<TopUpTransaction> findByStatusAndNotDeleted(String status);
    
    // Find all not deleted transactions
    @Query("SELECT t FROM TopUpTransaction t WHERE t.deletedAt IS NULL")
    List<TopUpTransaction> findAllNotDeleted();
    
    // Find by ID and not deleted
    @Query("SELECT t FROM TopUpTransaction t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<TopUpTransaction> findByIdAndNotDeleted(String id);
}
