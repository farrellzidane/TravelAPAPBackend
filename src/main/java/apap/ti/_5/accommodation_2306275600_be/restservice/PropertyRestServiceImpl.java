package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyListResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyRestServiceImpl implements PropertyRestService {
    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public List<PropertyListResponseDTO> getAllProperties(String name, Integer type, Integer status) {
        List<Property> properties = propertyRepository.findAll();

        // Filter by name (contains, ignore case)
        if (name != null && !name.isEmpty()) {
            properties = properties.stream()
                    .filter(p -> p.getPropertyName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by type
        if (type != null) {
            properties = properties.stream()
                    .filter(p -> p.getType() == type)
                    .collect(Collectors.toList());
        }

        // Filter by status
        if (status != null) {
            properties = properties.stream()
                    .filter(p -> p.getActiveStatus() == status)
                    .collect(Collectors.toList());
        }

        // Sort by updatedDate descending
        properties.sort(Comparator.comparing(Property::getUpdatedDate).reversed());

        // Map to DTO
        return properties.stream()
                .map(p -> new PropertyListResponseDTO(
                        p.getPropertyID(),
                        p.getPropertyName(),
                        p.getType(),
                        p.getActiveStatus(),
                        p.getTotalRoom(),
                        p.getUpdatedDate()
                ))
                .collect(Collectors.toList());
    }
}