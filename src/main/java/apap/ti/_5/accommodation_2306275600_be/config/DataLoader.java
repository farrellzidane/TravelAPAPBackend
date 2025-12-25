package apap.ti._5.accommodation_2306275600_be.config;

import apap.ti._5.accommodation_2306275600_be.model.Superadmin;
import apap.ti._5.accommodation_2306275600_be.repository.SuperadminRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataLoader to initialize default superadmin account
 * Runs once when application starts
 */
@Component
@RequiredArgsConstructor
public class DataLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;
    
    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        createDefaultSuperadmin();
    }
    
    private void createDefaultSuperadmin() {
        String defaultUsername = "superadmin";
        String defaultEmail = "superadmin@travelapap.com";
        
        // Check if superadmin already exists
        if (superadminRepository.findByUsername(defaultUsername).isPresent()) {
            logger.info("Default superadmin already exists, skipping creation");
            return;
        }
        
        // Create default superadmin
        Superadmin superadmin = new Superadmin();
        superadmin.setUsername(defaultUsername);
        superadmin.setName("Super Administrator");
        superadmin.setEmail(defaultEmail);
        superadmin.setPassword(passwordEncoder.encode("Admin123!"));
        superadmin.setRole("SUPERADMIN");
        superadmin.setGender("Other");
        
        superadminRepository.save(superadmin);
        
        logger.info("========================================");
        logger.info("Default superadmin created successfully!");
        logger.info("Username: {}", defaultUsername);
        logger.info("Email: {}", defaultEmail);
        logger.info("Password: Admin123!");
        logger.info("========================================");
        logger.warn("IMPORTANT: Please change the default password after first login!");
    }
}
