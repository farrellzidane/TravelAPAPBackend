package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.CreateTopUpRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.UpdateTopUpStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.topup.TopUpTransactionResponseDTO;

import java.util.List;

public interface TopUpTransactionRestService {
    TopUpTransactionResponseDTO createTopUpTransaction(CreateTopUpRequestDTO dto);
    List<TopUpTransactionResponseDTO> getAllTopUpTransactions();
    List<TopUpTransactionResponseDTO> getTopUpTransactionsByCustomerId(String customerId);
    TopUpTransactionResponseDTO getTopUpTransactionById(String id);
    TopUpTransactionResponseDTO updateTopUpStatus(UpdateTopUpStatusRequestDTO dto);
    void deleteTopUpTransaction(String id);
}
