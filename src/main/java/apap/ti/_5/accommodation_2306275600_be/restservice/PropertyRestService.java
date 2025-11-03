package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;

import java.util.List;

public interface PropertyRestService {
    PropertyResponseDTO createProperty(CreatePropertyRequestDTO dto);
    List<PropertyResponseDTO> getAllProperties();
    PropertyResponseDTO getPropertyById(String propertyID);
    List<PropertyResponseDTO> getPropertiesByOwner(String ownerID);
    PropertyResponseDTO updateProperty(String propertyID, UpdatePropertyRequestDTO dto);
    PropertyResponseDTO deleteProperty(String propertyID);
}