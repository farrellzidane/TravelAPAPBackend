package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.PaymentMethod;
import apap.ti._5.accommodation_2306275600_be.repository.PaymentMethodRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.CreatePaymentMethodRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.UpdatePaymentMethodStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod.PaymentMethodResponseDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class PaymentMethodRestServiceImpl implements PaymentMethodRestService {
    
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public PaymentMethodResponseDTO createPaymentMethod(CreatePaymentMethodRequestDTO dto) {
        // Validasi: Cek duplikasi method name dan provider
        if (paymentMethodRepository.existsByMethodNameAndProvider(dto.getMethodName(), dto.getProvider())) {
            throw new RuntimeException(
                String.format("Payment method '%s' dengan provider '%s' sudah ada", 
                    dto.getMethodName(), dto.getProvider())
            );
        }
        
        // Generate UUID untuk payment method
        String paymentMethodId = UUID.randomUUID().toString();
        
        // Buat entity PaymentMethod
        PaymentMethod paymentMethod = PaymentMethod.builder()
            .id(paymentMethodId)
            .methodName(dto.getMethodName())
            .provider(dto.getProvider())
            .status("Active") // Default status
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        // Simpan ke database
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        // Convert ke Response DTO
        return convertToResponseDTO(savedPaymentMethod);
    }

    @Override
    public List<PaymentMethodResponseDTO> getAllPaymentMethods() {
        return paymentMethodRepository.findAll().stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public PaymentMethodResponseDTO getPaymentMethodById(String id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + id));
        
        return convertToResponseDTO(paymentMethod);
    }

    @Override
    public PaymentMethodResponseDTO updatePaymentMethodStatus(UpdatePaymentMethodStatusRequestDTO dto) {
        // Validasi payment method exists
        PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
            .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + dto.getPaymentMethodId()));
        
        // Validasi status value
        if (!dto.getStatus().equals("Active") && !dto.getStatus().equals("Inactive")) {
            throw new RuntimeException("Invalid status. Must be 'Active' or 'Inactive'");
        }
        
        // Update status
        paymentMethod.setStatus(dto.getStatus());
        paymentMethod.setUpdatedAt(LocalDateTime.now());
        
        // Simpan perubahan
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        return convertToResponseDTO(updatedPaymentMethod);
    }

    @Override
    public void deletePaymentMethod(String id) {
        // Validasi payment method exists
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + id));
        
        // Soft delete: ubah status menjadi Inactive
        paymentMethod.setStatus("Inactive");
        paymentMethod.setUpdatedAt(LocalDateTime.now());
        paymentMethodRepository.save(paymentMethod);
    }

    private PaymentMethodResponseDTO convertToResponseDTO(PaymentMethod paymentMethod) {
        return PaymentMethodResponseDTO.builder()
            .paymentMethodId(paymentMethod.getId())
            .methodName(paymentMethod.getMethodName())
            .provider(paymentMethod.getProvider())
            .status(paymentMethod.getStatus())
            .createdAt(paymentMethod.getCreatedAt())
            .updatedAt(paymentMethod.getUpdatedAt())
            .build();
    }
}
