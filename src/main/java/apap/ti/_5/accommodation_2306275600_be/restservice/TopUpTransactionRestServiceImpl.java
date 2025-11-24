package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.PaymentMethod;
import apap.ti._5.accommodation_2306275600_be.model.TopUpTransaction;
import apap.ti._5.accommodation_2306275600_be.repository.PaymentMethodRepository;
import apap.ti._5.accommodation_2306275600_be.repository.TopUpTransactionRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.CreateTopUpRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.UpdateTopUpStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.topup.TopUpTransactionResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TopUpTransactionRestServiceImpl implements TopUpTransactionRestService {
    
    private final TopUpTransactionRepository topUpTransactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public TopUpTransactionResponseDTO createTopUpTransaction(CreateTopUpRequestDTO dto) {
        // Validasi: Amount harus positif
        if (dto.getAmount() <= 0) {
            throw new RuntimeException("Amount harus positif");
        }
        
        // Validasi: Payment method exists dan aktif
        PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
            .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + dto.getPaymentMethodId()));
        
        if (!paymentMethod.getStatus().equals("Active")) {
            throw new RuntimeException("Payment method tidak aktif. Silakan pilih payment method yang aktif.");
        }
        
        // Generate UUID untuk transaction
        String transactionId = UUID.randomUUID().toString();
        
        // Buat entity TopUpTransaction dengan status "Pending"
        TopUpTransaction transaction = TopUpTransaction.builder()
            .id(transactionId)
            .endUserId(dto.getCustomerId())
            .amount(dto.getAmount())
            .paymentMethod(paymentMethod)
            .date(LocalDateTime.now())
            .status("Pending")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        // Simpan ke database
        TopUpTransaction savedTransaction = topUpTransactionRepository.save(transaction);
        
        // Convert ke Response DTO
        return convertToResponseDTO(savedTransaction);
    }

    @Override
    public List<TopUpTransactionResponseDTO> getAllTopUpTransactions() {
        return topUpTransactionRepository.findAllNotDeleted().stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<TopUpTransactionResponseDTO> getTopUpTransactionsByCustomerId(String customerId) {
        return topUpTransactionRepository.findByEndUserIdAndNotDeleted(customerId).stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TopUpTransactionResponseDTO getTopUpTransactionById(String id) {
        TopUpTransaction transaction = topUpTransactionRepository.findByIdAndNotDeleted(id)
            .orElseThrow(() -> new RuntimeException("Top-up transaction not found with ID: " + id));
        
        return convertToResponseDTO(transaction);
    }

    @Override
    public TopUpTransactionResponseDTO updateTopUpStatus(UpdateTopUpStatusRequestDTO dto) {
        // Validasi transaction exists
        TopUpTransaction transaction = topUpTransactionRepository.findByIdAndNotDeleted(dto.getTransactionId())
            .orElseThrow(() -> new RuntimeException("Top-up transaction not found with ID: " + dto.getTransactionId()));
        
        // Validasi: Transaction harus dalam status "Pending"
        if (!transaction.getStatus().equals("Pending")) {
            throw new RuntimeException("Hanya transaksi dengan status 'Pending' yang dapat diubah");
        }
        
        // Validasi status value
        if (!dto.getStatus().equals("Success") && !dto.getStatus().equals("Failed")) {
            throw new RuntimeException("Invalid status. Must be 'Success' or 'Failed'");
        }
        
        // Update status
        transaction.setStatus(dto.getStatus());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        // Jika status Success, maka:
        // TODO: Tambahkan saldo ke Profile Service (integrasi dengan service eksternal)
        // Untuk sementara hanya update status
        if (dto.getStatus().equals("Success")) {
            // Logic untuk menambah saldo di Profile Service
            // Contoh: profileService.addBalance(transaction.getEndUserId(), transaction.getAmount());
            System.out.println("Status Success: Menambahkan saldo Rp" + transaction.getAmount() + 
                " ke user " + transaction.getEndUserId());
        }
        
        // Simpan perubahan
        TopUpTransaction updatedTransaction = topUpTransactionRepository.save(transaction);
        
        return convertToResponseDTO(updatedTransaction);
    }

    @Override
    public void deleteTopUpTransaction(String id) {
        // Validasi transaction exists
        TopUpTransaction transaction = topUpTransactionRepository.findByIdAndNotDeleted(id)
            .orElseThrow(() -> new RuntimeException("Top-up transaction not found with ID: " + id));
        
        // Soft delete: set deletedAt timestamp
        transaction.setDeletedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        topUpTransactionRepository.save(transaction);
    }

    private TopUpTransactionResponseDTO convertToResponseDTO(TopUpTransaction transaction) {
        return TopUpTransactionResponseDTO.builder()
            .transactionId(transaction.getId())
            .endUserId(transaction.getEndUserId())
            .amount(transaction.getAmount())
            .paymentMethodId(transaction.getPaymentMethod().getId())
            .paymentMethodName(transaction.getPaymentMethod().getMethodName())
            .provider(transaction.getPaymentMethod().getProvider())
            .date(transaction.getDate())
            .status(transaction.getStatus())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .build();
    }
}
