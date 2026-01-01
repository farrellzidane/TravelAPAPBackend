package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.PayBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.bill.UpdateBillRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.BillRestServiceRBAC;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BillRestController {

    private final BillRestServiceRBAC billRestService;

    public BillRestController(BillRestServiceRBAC billRestService) {
        this.billRestService = billRestService;
    }

    public static final String BASE_URL = "/bill";
    public static final String CREATE_BILL = BASE_URL + "/create";
    public static final String GET_ALL_BILL = BASE_URL;
    public static final String GET_CUSTOMER_BILL = BASE_URL + "/customer";
    public static final String GET_SERVICE_BILL = BASE_URL + "/{serviceName}";
    public static final String GET_DETAIL_BILL = BASE_URL + "/detail/{billId}";
    public static final String PAY_BILL = BASE_URL + "/{billId}/pay";
    public static final String UPDATE_BILL = BASE_URL + "/update/{billId}";

    // 1. Create Bill - API Key
    @PostMapping(CREATE_BILL)
    public ResponseEntity<BaseResponseDTO<BillResponseDTO>> createBill(
            @Valid @RequestBody CreateBillRequestDTO dto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<BillResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            BillResponseDTO bill = billRestService.createBill(dto);

            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(bill);
            baseResponseDTO.setMessage("Bill created successfully with ID " + bill.getBillId());
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);

        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to create bill. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Get All Bill - Superadmin
    @GetMapping(GET_ALL_BILL)
    public ResponseEntity<BaseResponseDTO<List<BillResponseDTO>>> getAllBills(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) Integer status) {

        var baseResponseDTO = new BaseResponseDTO<List<BillResponseDTO>>();

        try {
            List<BillResponseDTO> bills = billRestService.getAllBills(customerId, serviceName, status);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bills);
            baseResponseDTO.setMessage("Successfully retrieved " + bills.size() + " bill(s)");
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("No Bill Found")) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage("No Bill Found");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve bills. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Get Customer Bill - Customer
    @GetMapping(GET_CUSTOMER_BILL)
    public ResponseEntity<BaseResponseDTO<List<BillResponseDTO>>> getCustomerBills(
            @RequestParam UUID customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {

        var baseResponseDTO = new BaseResponseDTO<List<BillResponseDTO>>();

        try {
            List<BillResponseDTO> bills = billRestService.getCustomerBills(customerId, status, sortBy, sortDirection);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bills);
            baseResponseDTO.setMessage("Successfully retrieved " + bills.size() + " bill(s)");
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("No Bill Found")) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage("No Bill Found");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve customer bills. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Get Service Bill - Accommodation Owner
    @GetMapping(GET_SERVICE_BILL)
    public ResponseEntity<BaseResponseDTO<List<BillResponseDTO>>> getServiceBills(
            @PathVariable String serviceName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) UUID customerId) {

        var baseResponseDTO = new BaseResponseDTO<List<BillResponseDTO>>();

        try {
            List<BillResponseDTO> bills = billRestService.getServiceBills(serviceName, status, customerId);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bills);
            baseResponseDTO.setMessage("Successfully retrieved " + bills.size() + " bill(s) for service: " + serviceName);
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve service bills. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Get Detail Bill - Superadmin, Customer, Accommodation Owner
    @GetMapping(GET_DETAIL_BILL)
    public ResponseEntity<BaseResponseDTO<BillDetailResponseDTO>> getBillDetail(
            @PathVariable UUID billId) {

        var baseResponseDTO = new BaseResponseDTO<BillDetailResponseDTO>();

        try {
            BillDetailResponseDTO bill = billRestService.getBillDetail(billId);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bill);
            baseResponseDTO.setMessage("Successfully retrieved bill detail");
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("No Bill Found")) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage("No Bill Found");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve bill detail. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 6. Pay Bill - Customer
    @PostMapping(PAY_BILL)
    public ResponseEntity<BaseResponseDTO<BillResponseDTO>> payBill(
            @PathVariable UUID billId,
            @RequestBody(required = false) PayBillRequestDTO dto) {

        var baseResponseDTO = new BaseResponseDTO<BillResponseDTO>();

        // Create empty DTO if null
        if (dto == null) {
            dto = new PayBillRequestDTO();
        }

        try {
            BillResponseDTO bill = billRestService.payBill(billId, dto, null);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bill);
            baseResponseDTO.setMessage("Bill paid successfully");
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("No Bill Found")) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage("No Bill Found");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            } else if (ex.getMessage().contains("insufficient")) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setMessage(ex.getMessage());
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Payment Failed. An unexpected error occurred. Please try again later.");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 7. Update Bill - API Key
    @PutMapping(UPDATE_BILL)
    public ResponseEntity<BaseResponseDTO<BillResponseDTO>> updateBill(
            @PathVariable UUID billId,
            @Valid @RequestBody UpdateBillRequestDTO dto,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<BillResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            BillResponseDTO bill = billRestService.updateBill(billId, dto);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bill);
            baseResponseDTO.setMessage("Bill updated successfully");
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("No Bill Found")) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage("No Bill Found");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to update bill. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
