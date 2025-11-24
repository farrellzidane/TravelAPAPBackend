package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.CreatePaymentMethodRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.UpdatePaymentMethodStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod.PaymentMethodResponseDTO;

import java.util.List;

public interface PaymentMethodRestService {
    PaymentMethodResponseDTO createPaymentMethod(CreatePaymentMethodRequestDTO dto);
    List<PaymentMethodResponseDTO> getAllPaymentMethods();
    PaymentMethodResponseDTO getPaymentMethodById(String id);
    PaymentMethodResponseDTO updatePaymentMethodStatus(UpdatePaymentMethodStatusRequestDTO dto);
    void deletePaymentMethod(String id);
}
