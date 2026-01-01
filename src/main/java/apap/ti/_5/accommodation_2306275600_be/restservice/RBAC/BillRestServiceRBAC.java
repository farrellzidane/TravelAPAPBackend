package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.PayBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.UpdateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.BillRestService;

import java.util.List;
import java.util.UUID;

public interface BillRestServiceRBAC extends BillRestService {
    BillResponseDTO createBill(CreateBillRequestDTO dto);
    List<BillResponseDTO> getAllBills(UUID customerId, String serviceName, Integer status);
    List<BillResponseDTO> getCustomerBills(UUID customerId, Integer status, String sortBy, String sortDirection);
    List<BillResponseDTO> getServiceBills(String serviceName, Integer status, UUID customerId);
    BillDetailResponseDTO getBillDetail(UUID billId);
    BillResponseDTO payBill(UUID billId, PayBillRequestDTO dto, UUID authenticatedCustomerId);
    BillResponseDTO updateBill(UUID billId, UpdateBillRequestDTO dto);
}
