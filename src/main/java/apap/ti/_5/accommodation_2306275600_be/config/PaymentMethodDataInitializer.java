package apap.ti._5.accommodation_2306275600_be.config;

import apap.ti._5.accommodation_2306275600_be.model.PaymentMethod;
import apap.ti._5.accommodation_2306275600_be.repository.PaymentMethodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class PaymentMethodDataInitializer {

    @Bean
    @Profile("dev") // Only runs in dev profile
    CommandLineRunner initPaymentMethods(PaymentMethodRepository paymentMethodRepository) {
        return args -> {
            // Check if data already exists
            if (paymentMethodRepository.count() > 0) {
                System.out.println("Payment methods already exist. Skipping initialization.");
                return;
            }

            System.out.println("Initializing payment methods dummy data...");

            LocalDateTime now = LocalDateTime.now();

            List<PaymentMethod> paymentMethods = Arrays.asList(
                    PaymentMethod.builder()
                            .id("pm-va-bca-001")
                            .methodName("Virtual Account")
                            .provider("BCA")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-va-mandiri-002")
                            .methodName("Virtual Account")
                            .provider("Mandiri")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-va-bni-003")
                            .methodName("Virtual Account")
                            .provider("BNI")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-va-bri-004")
                            .methodName("Virtual Account")
                            .provider("BRI")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-ew-gopay-005")
                            .methodName("E-Wallet")
                            .provider("GoPay")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-ew-ovo-006")
                            .methodName("E-Wallet")
                            .provider("OVO")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-ew-dana-007")
                            .methodName("E-Wallet")
                            .provider("DANA")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-ew-shopeepay-008")
                            .methodName("E-Wallet")
                            .provider("ShopeePay")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-cc-visa-009")
                            .methodName("Credit Card")
                            .provider("Visa")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-cc-mastercard-010")
                            .methodName("Credit Card")
                            .provider("Mastercard")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-qr-qris-011")
                            .methodName("QRIS")
                            .provider("QRIS")
                            .status("Active")
                            .createdAt(now)
                            .updatedAt(now)
                            .build(),

                    PaymentMethod.builder()
                            .id("pm-retail-alfamart-012")
                            .methodName("Retail Payment")
                            .provider("Alfamart")
                            .status("Inactive")
                            .createdAt(now)
                            .updatedAt(now)
                            .build()
            );

            paymentMethodRepository.saveAll(paymentMethods);
            System.out.println("Successfully initialized " + paymentMethods.size() + " payment methods.");
        };
    }
}
