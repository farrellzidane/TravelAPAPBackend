package apap.ti._5.accommodation_2306275600_be.restservice.RBAC; 

import java.util.List; 

import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO; 
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO; 
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.PropertyRestService;
import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException; 

public interface PropertyRestServiceRBAC extends PropertyRestService {
    
    @Override 
    PropertyResponseDTO createProperty(CreatePropertyRequestDTO dto) throws AccessDeniedException; 
    
    @Override 
    List<PropertyResponseDTO> getAllProperties() throws AccessDeniedException; 

    @Override 
    PropertyResponseDTO getPropertyById(String propertyID) throws AccessDeniedException; 

    @Override 
    List<PropertyResponseDTO> getPropertiesByOwner(String ownerID) throws AccessDeniedException; 

    @Override 
    PropertyResponseDTO updateProperty(String propertyID, UpdatePropertyRequestDTO dto) throws AccessDeniedException; 

    @Override 
    PropertyResponseDTO deleteProperty(String propertyID) throws AccessDeniedException; 
}
