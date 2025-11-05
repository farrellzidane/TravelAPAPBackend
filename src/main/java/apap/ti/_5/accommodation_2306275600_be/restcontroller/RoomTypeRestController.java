package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RoomTypeRestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomTypeRestController {
    
    private final RoomTypeRestService roomTypeRestService;
    
    @PostMapping()
    public ResponseEntity<BaseResponseDTO<RoomTypeResponseDTO>> createRoomType(
            @Valid @RequestBody CreateRoomTypeRequestDTO request,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<RoomTypeResponseDTO>();
        
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
            RoomTypeResponseDTO response = roomTypeRestService.createRoomType(request);
            
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(response);
            baseResponseDTO.setMessage("Room type created successfully");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to create room type. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to create room type. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/room-types/{id}")
    public ResponseEntity<BaseResponseDTO<RoomTypeResponseDTO>> getRoomType(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<RoomTypeResponseDTO>();
        
        try {
            RoomTypeResponseDTO response = roomTypeRestService.getRoomTypeById(id);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(response);
            baseResponseDTO.setMessage("Room type retrieved successfully");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Room type not found. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve room type. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/room-types")
    public ResponseEntity<BaseResponseDTO<List<RoomTypeResponseDTO>>> getAllRoomTypes() {
        var baseResponseDTO = new BaseResponseDTO<List<RoomTypeResponseDTO>>();
        
        try {
            List<RoomTypeResponseDTO> responses = roomTypeRestService.getAllRoomTypes();
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responses);
            baseResponseDTO.setMessage("Successfully retrieved " + responses.size() + " room type(s)");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve room types. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/room-types/{id}")
    public ResponseEntity<BaseResponseDTO<RoomTypeResponseDTO>> updateRoomType(
            @PathVariable String id,
            @Valid @RequestBody UpdateRoomTypeRequestDTO request,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<RoomTypeResponseDTO>();
        
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
            RoomTypeResponseDTO response = roomTypeRestService.updateRoomType(id, request);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(response);
            baseResponseDTO.setMessage("Room type updated successfully");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to update room type. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to update room type. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/room-types/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteRoomType(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<Void>();
        
        try {
            roomTypeRestService.deleteRoomType(id);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Room type deleted successfully");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to delete room type. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to delete room type. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}