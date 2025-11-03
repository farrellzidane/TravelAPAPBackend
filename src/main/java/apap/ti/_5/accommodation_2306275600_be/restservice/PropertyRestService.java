package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyListResponseDTO;

import java.util.List;

public interface PropertyRestService {
    List<PropertyListResponseDTO> getAllProperties(String name, Integer type, Integer status);
}