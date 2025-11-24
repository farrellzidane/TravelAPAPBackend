package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.CreateTopUpRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.topup.UpdateTopUpStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.topup.TopUpTransactionResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.TopUpTransactionRestServiceRBAC;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/top-up")
public class TopUpTransactionRestController {
    
    private final TopUpTransactionRestServiceRBAC topUpTransactionRestService;

    public TopUpTransactionRestController(TopUpTransactionRestServiceRBAC topUpTransactionRestService) {
        this.topUpTransactionRestService = topUpTransactionRestService;
    }

    // [POST] Create Top-Up Transaction - PBI-BE-TU3
    @PostMapping
    public ResponseEntity<BaseResponseDTO<TopUpTransactionResponseDTO>> createTopUpTransaction(
            @Valid @RequestBody CreateTopUpRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<TopUpTransactionResponseDTO>();
        
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
            TopUpTransactionResponseDTO result = topUpTransactionRestService.createTopUpTransaction(dto);
            
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setMessage("Membuat transaksi baru dengan status 'Pending'");
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

    // [GET] Get All Top-Up Transactions - PBI-BE-TU1
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<TopUpTransactionResponseDTO>>> getAllTopUpTransactions() {
        var baseResponseDTO = new BaseResponseDTO<List<TopUpTransactionResponseDTO>>();
        
        try {
            List<TopUpTransactionResponseDTO> result = topUpTransactionRestService.getAllTopUpTransactions();
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Menampilkan seluruh daftar transaksi top up yang terdaftar pada sistem");
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

    // [GET] Get Top-Up Transactions by Customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<BaseResponseDTO<List<TopUpTransactionResponseDTO>>> getTopUpTransactionsByCustomerId(
            @PathVariable String customerId) {
        
        var baseResponseDTO = new BaseResponseDTO<List<TopUpTransactionResponseDTO>>();
        
        try {
            List<TopUpTransactionResponseDTO> result = topUpTransactionRestService.getTopUpTransactionsByCustomerId(customerId);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Terdapat validasi hanya transaction customer yang dapat dilihat pada jwt token yang dapat melihat transactions");
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

    // [GET] Get Top-Up Transaction by ID - PBI-BE-TU2
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<TopUpTransactionResponseDTO>> getTopUpTransactionById(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<TopUpTransactionResponseDTO>();
        
        try {
            TopUpTransactionResponseDTO result = topUpTransactionRestService.getTopUpTransactionById(id);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Menampilkan detail transaksi berdasarkan ID");
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

    // [PUT] Update Top-Up Status - PBI-BE-TU4
    @PutMapping("/status")
    public ResponseEntity<BaseResponseDTO<TopUpTransactionResponseDTO>> updateTopUpStatus(
            @Valid @RequestBody UpdateTopUpStatusRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<TopUpTransactionResponseDTO>();
        
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
            TopUpTransactionResponseDTO result = topUpTransactionRestService.updateTopUpStatus(dto);
            
            String statusMessage = dto.getStatus().equals("Success") 
                ? "Jika 'Success', sistem menambah saldo di Profile Service"
                : "Jika 'Failed', tidak ada perubahan saldo";
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Mengubah status menjadi 'Success' atau 'Failed'. " + statusMessage);
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

    // [DELETE] Delete Top-Up Transaction (Soft Delete) - PBI-BE-TU5
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteTopUpTransaction(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<Void>();
        
        try {
            topUpTransactionRestService.deleteTopUpTransaction(id);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Top Up Transaction dengan ID tertentu berhasil dihapus. Status top up transaction berhasil diperbarui");
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
