package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PropertyRestService {
    PropertyResponseDTO createProperty(CreatePropertyRequestDTO dto);
    List<PropertyResponseDTO> getAllProperties();
    List<PropertyResponseDTO> getFilteredProperties(String name, Integer type, Integer province);
    PropertyResponseDTO getPropertyById(UUID propertyID);
    List<PropertyResponseDTO> getPropertiesByOwner(UUID ownerID);
    PropertyResponseDTO updateProperty(UUID propertyID, UpdatePropertyRequestDTO dto);
    PropertyResponseDTO deleteProperty(UUID propertyID);
}