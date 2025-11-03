package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyListResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.PropertyRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/property")
public class PropertyRestController {
    @Autowired
    private PropertyRestService propertyRestService;

    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<PropertyListResponseDTO>>> getAllProperties(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status
    ) {
        var baseResponseDTO = new BaseResponseDTO<List<PropertyListResponseDTO>>();

        List<PropertyListResponseDTO> properties;

        if (name != null && type != null && status != null) {
            properties = propertyRestService.getAllProperties(name, type, status);
        } else if (name != null && type != null) {
            properties = propertyRestService.getAllProperties(name, type, null);
        } else if (name != null && status != null) {
            properties = propertyRestService.getAllProperties(name, null, status);
        } else if (type != null && status != null) {
            properties = propertyRestService.getAllProperties(null, type, status);
        } else if (name != null) {
            properties = propertyRestService.getAllProperties(name, null, null);
        } else if (type != null) {
            properties = propertyRestService.getAllProperties(null, type, null);
        } else if (status != null) {
            properties = propertyRestService.getAllProperties(null, null, status);
        } else {
            properties = propertyRestService.getAllProperties(null, null, null);
        }

        baseResponseDTO.setStatus(org.springframework.http.HttpStatus.OK.value());
        baseResponseDTO.setData(properties);
        baseResponseDTO.setMessage("Properties retrieved successfully");
        baseResponseDTO.setTimestamp(new java.util.Date());
        return new ResponseEntity<>(baseResponseDTO, org.springframework.http.HttpStatus.OK);
    }
}