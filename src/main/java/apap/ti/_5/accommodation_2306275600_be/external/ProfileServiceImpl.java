package apap.ti._5.accommodation_2306275600_be.external;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apap.ti._5.accommodation_2306275600_be.model.Customer;
import apap.ti._5.accommodation_2306275600_be.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void addBalance(String userId, Long amount) {
        try {
            UUID customerId = UUID.fromString(userId);
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + userId));
            
            BigDecimal currentBalance = customer.getSaldo();
            BigDecimal amountToAdd = BigDecimal.valueOf(amount);
            BigDecimal newBalance = currentBalance.add(amountToAdd);
            
            customer.setSaldo(newBalance);
            customerRepository.save(customer);
            
            logger.info("💰 Balance updated successfully:");
            logger.info("   Customer ID: {}", userId);
            logger.info("   Previous Balance: Rp {}", String.format("%,d", currentBalance.longValue()));
            logger.info("   Amount Added: Rp {}", String.format("%,d", amount));
            logger.info("   New Balance: Rp {}", String.format("%,d", newBalance.longValue()));
            
        } catch (Exception e) {
            logger.error("⚠️ Failed to add balance: {}", e.getMessage());
            throw new RuntimeException("Failed to add balance to customer: " + e.getMessage());
        }
    }
}
