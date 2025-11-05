package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomRestService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomRestController {
    
    @Autowired
    private RoomRestService roomRestService;

    public static final String BASE_URL = "/property";
    public static final String MAINTENANCE_ROOM = BASE_URL + "/maintenance";

    @PostMapping(MAINTENANCE_ROOM + "/add")
    public ResponseEntity<BaseResponseDTO<RoomResponseDTO>> createMaintenance(
            @Valid @RequestBody CreateMaintenanceRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<RoomResponseDTO>();
        
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
            RoomResponseDTO room = roomRestService.createMaintenance(dto);
            
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(room);
            baseResponseDTO.setMessage("Konfirmasi: Jadwal perbaikan berhasil ditambahkan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Konfirmasi: Jadwal perbaikan gagal ditambahkan. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}