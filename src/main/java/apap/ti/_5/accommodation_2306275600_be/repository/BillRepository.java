package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Bill;
import apap.ti._5.accommodation_2306275600_be.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {

    @Query("SELECT b FROM Bill b WHERE b.deletedAt IS NULL")
    List<Bill> findAllNotDeleted();

    @Query("SELECT b FROM Bill b WHERE b.billId = :billId AND b.deletedAt IS NULL")
    Optional<Bill> findByIdNotDeleted(@Param("billId") UUID billId);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.deletedAt IS NULL")
    List<Bill> findByCustomerNotDeleted(@Param("customer") Customer customer);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.status = :status AND b.deletedAt IS NULL")
    List<Bill> findByCustomerAndStatusNotDeleted(@Param("customer") Customer customer, @Param("status") int status);

    @Query("SELECT b FROM Bill b WHERE b.serviceName = :serviceName AND b.deletedAt IS NULL")
    List<Bill> findByServiceNameNotDeleted(@Param("serviceName") String serviceName);

    @Query("SELECT b FROM Bill b WHERE b.serviceName = :serviceName AND b.status = :status AND b.deletedAt IS NULL")
    List<Bill> findByServiceNameAndStatusNotDeleted(@Param("serviceName") String serviceName, @Param("status") int status);

    @Query("SELECT b FROM Bill b WHERE b.serviceName = :serviceName AND b.customer.id = :customerId AND b.deletedAt IS NULL")
    List<Bill> findByServiceNameAndCustomerIdNotDeleted(@Param("serviceName") String serviceName, @Param("customerId") UUID customerId);

    @Query("SELECT b FROM Bill b WHERE b.status = :status AND b.deletedAt IS NULL")
    List<Bill> findByStatusNotDeleted(@Param("status") int status);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.deletedAt IS NULL ORDER BY b.createdAt ASC")
    List<Bill> findByCustomerOrderByCreatedAtAsc(@Param("customer") Customer customer);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    List<Bill> findByCustomerOrderByCreatedAtDesc(@Param("customer") Customer customer);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.deletedAt IS NULL ORDER BY b.serviceName ASC")
    List<Bill> findByCustomerOrderByServiceNameAsc(@Param("customer") Customer customer);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.deletedAt IS NULL ORDER BY b.serviceName DESC")
    List<Bill> findByCustomerOrderByServiceNameDesc(@Param("customer") Customer customer);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.status = :status AND b.deletedAt IS NULL ORDER BY b.createdAt ASC")
    List<Bill> findByCustomerAndStatusOrderByCreatedAtAsc(@Param("customer") Customer customer, @Param("status") int status);

    @Query("SELECT b FROM Bill b WHERE b.customer = :customer AND b.status = :status AND b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    List<Bill> findByCustomerAndStatusOrderByCreatedAtDesc(@Param("customer") Customer customer, @Param("status") int status);
}
