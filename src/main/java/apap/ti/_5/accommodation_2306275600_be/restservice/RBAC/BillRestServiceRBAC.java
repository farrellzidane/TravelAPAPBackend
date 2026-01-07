package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.PayBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.UpdateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.BillRestService;

import java.util.List;
import java.util.UUID;


public interface BillRestServiceRBAC extends BillRestService {
    @Override
    BillResponseDTO createBill(CreateBillRequestDTO dto) throws AccessDeniedException;;
    @Override
    List<BillResponseDTO> getAllBills(UUID customerId, String serviceName, Integer status) throws AccessDeniedException;;
    @Override
    List<BillResponseDTO> getCustomerBills(UUID customerId, Integer status, String sortBy, String sortDirection) throws AccessDeniedException;;
    @Override
    List<BillResponseDTO> getServiceBills(String serviceName, Integer status, UUID customerId) throws AccessDeniedException;;
    @Override
    BillDetailResponseDTO getBillDetail(UUID billId) throws AccessDeniedException;;
    @Override
    BillResponseDTO payBill(UUID billId, PayBillRequestDTO dto, UUID authenticatedCustomerId) throws AccessDeniedException;;
    @Override
    BillResponseDTO updateBill(UUID billId, UpdateBillRequestDTO dto) throws AccessDeniedException;;
}
