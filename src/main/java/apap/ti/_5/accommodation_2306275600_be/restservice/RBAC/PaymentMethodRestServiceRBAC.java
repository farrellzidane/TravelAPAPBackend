package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.CreatePaymentMethodRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.UpdatePaymentMethodStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod.PaymentMethodResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.PaymentMethodRestService;

import java.util.List;

public interface PaymentMethodRestServiceRBAC extends PaymentMethodRestService {
    @Override
    PaymentMethodResponseDTO createPaymentMethod(CreatePaymentMethodRequestDTO dto) throws AccessDeniedException;

    @Override
    List<PaymentMethodResponseDTO> getAllPaymentMethods() throws AccessDeniedException;

    @Override
    PaymentMethodResponseDTO getPaymentMethodById(String id) throws AccessDeniedException;

    @Override
    PaymentMethodResponseDTO updatePaymentMethodStatus(UpdatePaymentMethodStatusRequestDTO dto) throws AccessDeniedException;

    @Override
    void deletePaymentMethod(String id) throws AccessDeniedException;
}
