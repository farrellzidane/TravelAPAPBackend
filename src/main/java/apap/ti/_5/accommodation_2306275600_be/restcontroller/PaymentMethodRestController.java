package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.CreatePaymentMethodRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.UpdatePaymentMethodStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod.PaymentMethodResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.PaymentMethodRestServiceRBAC;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/payment-method")
public class PaymentMethodRestController {
    
    private final PaymentMethodRestServiceRBAC paymentMethodRestService;

    public PaymentMethodRestController(PaymentMethodRestServiceRBAC paymentMethodRestService) {
        this.paymentMethodRestService = paymentMethodRestService;
    }

    // [POST] Create Payment Method - PBI-BE-TU7
    @PostMapping
    public ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> createPaymentMethod(
            @Valid @RequestBody CreatePaymentMethodRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<PaymentMethodResponseDTO>();
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation error: ");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessage.toString());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            PaymentMethodResponseDTO result = paymentMethodRestService.createPaymentMethod(dto);
            
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setMessage("Payment method berhasil disimpan");
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(result);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    // [GET] Get All Payment Methods - PBI-BE-TU6
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<PaymentMethodResponseDTO>>> getAllPaymentMethods() {
        var baseResponseDTO = new BaseResponseDTO<List<PaymentMethodResponseDTO>>();
        
        try {
            List<PaymentMethodResponseDTO> result = paymentMethodRestService.getAllPaymentMethods();
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Payment Method berhasil ditampilkan");
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(result);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // [GET] Get Payment Method by ID
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> getPaymentMethodById(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<PaymentMethodResponseDTO>();
        
        try {
            PaymentMethodResponseDTO result = paymentMethodRestService.getPaymentMethodById(id);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Payment method berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(result);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }

    // [PUT] Update Payment Method Status - PBI-BE-TU8
    @PutMapping("/status")
    public ResponseEntity<BaseResponseDTO<PaymentMethodResponseDTO>> updatePaymentMethodStatus(
            @Valid @RequestBody UpdatePaymentMethodStatusRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<PaymentMethodResponseDTO>();
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation error: ");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessage.toString());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            PaymentMethodResponseDTO result = paymentMethodRestService.updatePaymentMethodStatus(dto);
            
            String statusMessage = dto.getStatus().equals("Active") 
                ? "Status payment method berhasil diperbarui"
                : "Status payment method berhasil diperbarui";
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage(statusMessage);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(result);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    // [DELETE] Delete Payment Method - PBI-BE-TU9
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deletePaymentMethod(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<Void>();
        
        try {
            paymentMethodRestService.deletePaymentMethod(id);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Payment method dengan ID :" + id + " berhasil dihapus");
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(null);
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }
}
