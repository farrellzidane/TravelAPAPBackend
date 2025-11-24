package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.CreateTopUpRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.UpdateTopUpStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.topup.TopUpTransactionResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.TopUpTransactionRestService;

import java.util.List;

public interface TopUpTransactionRestServiceRBAC extends TopUpTransactionRestService {
    @Override
    TopUpTransactionResponseDTO createTopUpTransaction(CreateTopUpRequestDTO dto) throws AccessDeniedException;

    @Override
    List<TopUpTransactionResponseDTO> getAllTopUpTransactions() throws AccessDeniedException;

    @Override
    List<TopUpTransactionResponseDTO> getTopUpTransactionsByCustomerId(String customerId) throws AccessDeniedException;

    @Override
    TopUpTransactionResponseDTO getTopUpTransactionById(String id) throws AccessDeniedException;

    @Override
    TopUpTransactionResponseDTO updateTopUpStatus(UpdateTopUpStatusRequestDTO dto) throws AccessDeniedException;

    @Override
    void deleteTopUpTransaction(String id) throws AccessDeniedException;
}
