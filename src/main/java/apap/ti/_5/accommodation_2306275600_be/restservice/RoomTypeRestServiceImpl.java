package apap.ti._5.accommodation_2306275600_be.restservice;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;


@Service
@Primary
@Transactional
@RequiredArgsConstructor
public class RoomTypeRestServiceImpl implements RoomTypeRestService{
    
    protected final RoomTypeRepository roomTypeRepository;
    protected final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;

    @Override
    public RoomTypeResponseDTO createRoomType(CreateRoomTypeRequestDTO dto) {
        // Find property
        Property property = propertyRepository.findById(UUID.fromString(dto.getPropertyID()))
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + dto.getPropertyID()));
        
        // ❌ COMMENTED: Old formatted ID generation
        // String roomTypeID = generateRoomTypeID(dto);
        
        // ✅ NEW: Let @PrePersist auto-generate UUID
        // Manual conversion DTO to Entity
        RoomType roomType = RoomType.builder()
                // .roomTypeID() is not set here - will be auto-generated
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .capacity(dto.getCapacity())
                .facility(dto.getFacility())
                .floor(dto.getFloor())
                .property(property)
                .build();
        
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return convertToResponseDTO(savedRoomType);
    }

    @Override
    public RoomTypeResponseDTO getRoomTypeById(UUID roomTypeID) {
        RoomType roomType = roomTypeRepository.findById(roomTypeID)
                .orElseThrow(() -> new RuntimeException("Room type not found with id: " + roomTypeID));
        return convertToResponseDTO(roomType);
    }

    @Override
    public List<RoomTypeResponseDTO> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomTypeResponseDTO> getRoomTypesByProperty(UUID propertyID) {
        return roomTypeRepository.findByProperty_PropertyID(propertyID).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomTypeResponseDTO updateRoomType(UUID roomTypeID, UpdateRoomTypeRequestDTO dto) {
        RoomType roomType = roomTypeRepository.findById(roomTypeID)
            .orElseThrow(() -> new RuntimeException("Room type not found with id: " + roomTypeID));
    
    
        if (dto.getCapacity() != null) {
            roomType.setCapacity(dto.getCapacity());
        }
        if (dto.getPrice() != null) {
            roomType.setPrice(dto.getPrice());
        }
        if (dto.getFacility() != null) {
            roomType.setFacility(dto.getFacility());
        }
        if (dto.getDescription() != null) {
            roomType.setDescription(dto.getDescription());
        }
        
        RoomType updatedRoomType = roomTypeRepository.save(roomType);
        
        return RoomTypeResponseDTO.builder()
                .roomTypeID(updatedRoomType.getRoomTypeID().toString())
                .name(updatedRoomType.getName())
                .floor(updatedRoomType.getFloor())
                .capacity(updatedRoomType.getCapacity())
                .price(updatedRoomType.getPrice())
                .facility(updatedRoomType.getFacility())
                .description(updatedRoomType.getDescription())
                .propertyID(updatedRoomType.getProperty() != null ? updatedRoomType.getProperty().getPropertyID().toString() : null)
                .build();
        }


    @Override
    public void deleteRoomType(UUID roomTypeID) {
        if (!roomTypeRepository.existsById(roomTypeID)) {
            throw new RuntimeException("Room type not found with id: " + roomTypeID);
        }
        roomTypeRepository.deleteById(roomTypeID);
    }
    
    @Override
    public boolean isDuplicateRoomTypeFloor(UUID propertyID, String roomTypeName, Integer floor) {
        if (floor == null) {
            return false;
        }
        List<RoomType> existingRoomTypes = roomTypeRepository.findByProperty_PropertyID(propertyID);
        
        return existingRoomTypes.stream()
                .anyMatch(rt -> rt.getName().equals(roomTypeName) && rt.getFloor() == floor);
    }

    // ❌ COMMENTED: Old formatted ID generation - now using UUID auto-generation via @PrePersist
    // private String generateRoomTypeID(CreateRoomTypeRequestDTO dto) {
    //     // PropertyID format: PREFIX-4chars-3digits (contoh: APT-0000-004)
    //     // Split by dash dan ambil bagian terakhir (counter)
    //     String propertyID = dto.getPropertyID();
    //     String[] parts = propertyID.split("-");
    //     String propertyCounter = parts.length >= 3 ? parts[2] : "001";
    //     
    //     // Nama tipe kamar (replace space dengan underscore)
    //     String roomTypeName = dto.getName().replace(" ", "_");
    //     
    //     // Nomor lantai kamar
    //     String floor = String.format("%d", dto.getFloor());
    //     
    //     // Format: 003–Single_Room–2 (menggunakan em dash –)
    //     return propertyCounter + "–" + roomTypeName + "–" + floor;
    // }
    


    // Helper method untuk konversi Entity -> DTO
    private RoomTypeResponseDTO convertToResponseDTO(RoomType roomType) {
        return RoomTypeResponseDTO.builder()
                .roomTypeID(roomType.getRoomTypeID().toString())
                .name(roomType.getName())
                .price(roomType.getPrice())
                .description(roomType.getDescription())
                .capacity(roomType.getCapacity())
                .facility(roomType.getFacility())
                .floor(roomType.getFloor())
                .propertyID(roomType.getProperty() != null ? roomType.getProperty().getPropertyID().toString() : null)
                .propertyName(roomType.getProperty() != null ? roomType.getProperty().getPropertyName() : null)
                .createdDate(roomType.getCreatedDate())
                .updatedDate(roomType.getUpdatedDate())
                .build();
    }
}
