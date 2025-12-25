package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.AccommodationOwner;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.AccommodationOwnerRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.property.RoomTypeInfoDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.roomtype.RoomTypeResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Primary
public class PropertyRestServiceImpl implements PropertyRestService {
    protected final PropertyRepository propertyRepository;
    protected final RoomTypeRestService roomTypeRestService;
    protected final RoomRestService roomRestService;
    protected final BookingRepository bookingRepository;
    protected final AccommodationOwnerRepository accommodationOwnerRepository;
    
    @Autowired
    public PropertyRestServiceImpl(PropertyRepository propertyRepository, 
                                    RoomTypeRestService roomTypeRestService,
                                    RoomRestService roomRestService,
                                    BookingRepository bookingRepository,
                                    AccommodationOwnerRepository accommodationOwnerRepository) {
        this.propertyRepository = propertyRepository;
        this.roomTypeRestService = roomTypeRestService;
        this.roomRestService = roomRestService;
        this.bookingRepository = bookingRepository;
        this.accommodationOwnerRepository = accommodationOwnerRepository;
    }

    @Override
    public List<PropertyResponseDTO> getAllProperties() {
        // Only return active properties (activeStatus = 1)
        List<Property> properties = propertyRepository.findByActiveStatusOrderByCreatedDateDesc(1);
        return properties.stream()
                .map(p -> convertToPropertyResponseDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponseDTO> getFilteredProperties(String name, Integer type, Integer province) {
        List<Property> properties;
        
        // All filters provided
        if (name != null && !name.isEmpty() && type != null && province != null) {
            properties = propertyRepository.findByPropertyNameContainingIgnoreCaseAndTypeAndProvinceAndActiveStatus(name, type, province);
        }
        // Name and type only
        else if (name != null && !name.isEmpty() && type != null) {
            properties = propertyRepository.findByPropertyNameContainingIgnoreCaseAndTypeAndActiveStatus(name, type);
        }
        // Name and province only
        else if (name != null && !name.isEmpty() && province != null) {
            properties = propertyRepository.findByPropertyNameContainingIgnoreCaseAndProvinceAndActiveStatus(name, province);
        }
        // Type and province only
        else if (type != null && province != null) {
            properties = propertyRepository.findByTypeAndProvinceAndActiveStatusOrderByCreatedDateDesc(type, province, 1);
        }
        // Name only
        else if (name != null && !name.isEmpty()) {
            properties = propertyRepository.findByPropertyNameContainingIgnoreCaseAndActiveStatus(name);
        }
        // Type only
        else if (type != null) {
            properties = propertyRepository.findByTypeAndActiveStatusOrderByCreatedDateDesc(type, 1);
        }
        // Province only
        else if (province != null) {
            properties = propertyRepository.findByProvinceAndActiveStatusOrderByCreatedDateDesc(province, 1);
        }
        // No filters - return all active properties
        else {
            properties = propertyRepository.findByActiveStatusOrderByCreatedDateDesc(1);
        }
        
        return properties.stream()
                .map(p -> convertToPropertyResponseDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponseDTO> getPropertiesByOwner(UUID ownerId) {
        List<Property> properties = propertyRepository.findByOwnerID(ownerId);
        return properties.stream()
                .map(p -> convertToPropertyResponseDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public PropertyResponseDTO getPropertyById(UUID id) {
        return getPropertyById(id, null, null);
    }
    
    @Override
    public PropertyResponseDTO getPropertyById(UUID id, LocalDateTime checkIn, LocalDateTime checkOut) {
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isEmpty()) {
            return null;
        }
        
        Property property = propertyOpt.get();
        
        //  Fetch room types untuk property ini
        List<RoomTypeResponseDTO> roomTypes = roomTypeRestService.getRoomTypesByProperty(id);
        
        //  Get booked room IDs if date filter is provided
        List<UUID> bookedRoomIDs = new ArrayList<>();
        if (checkIn != null && checkOut != null) {
            bookedRoomIDs = bookingRepository.findBookedRoomIDsByPropertyAndPeriod(id, checkIn, checkOut);
        }
        
        //  Build room type info dengan FULL room objects (not just IDs)
        List<RoomTypeInfoDTO> roomTypeInfoList = new ArrayList<>();
        
        for (RoomTypeResponseDTO roomType : roomTypes) {
            //  Get FULL room objects untuk room type ini (with maintenance data)
            List<RoomResponseDTO> rooms = roomRestService.getRoomsByRoomType(UUID.fromString(roomType.getRoomTypeID()));
            
            //  Filter out booked rooms if date filter is provided
            if (!bookedRoomIDs.isEmpty()) {
                final List<UUID> finalBookedRoomIDs = bookedRoomIDs;
                rooms = rooms.stream()
                    .filter(room -> !finalBookedRoomIDs.contains(UUID.fromString(room.getRoomID())))
                    .collect(Collectors.toList());
            }
            
            //  Build room type info with FULL room objects
            RoomTypeInfoDTO roomTypeInfo = RoomTypeInfoDTO.builder()
                    .roomTypeID(roomType.getRoomTypeID())
                    .roomTypeName(roomType.getName())
                    .floor(roomType.getFloor())
                    .capacity(roomType.getCapacity())
                    .price(roomType.getPrice())
                    .facility(roomType.getFacility())
                    .description(roomType.getDescription())
                    .listRoom(rooms)  //  Send full room objects instead of roomIDs
                    .createdDate(roomType.getCreatedDate())
                    .updatedDate(roomType.getUpdatedDate())
                    .build();
            
            roomTypeInfoList.add(roomTypeInfo);
        }
        
        return convertToPropertyResponseDTO(property, roomTypeInfoList);
    }
    
   @Override
    public PropertyResponseDTO createProperty(CreatePropertyRequestDTO createPropertyRequestDTO) {
        
        // Validasi duplikasi property-roomtype-floor dalam request
        Set<String> roomTypeCombinations = new HashSet<>();
        for (AddRoomRequestDTO roomType : createPropertyRequestDTO.getRoomTypes()) {
            String combination = roomType.getRoomTypeName() + "-" + roomType.getFloor();
            if (!roomTypeCombinations.add(combination)) {
                throw new RuntimeException("Duplikasi kombinasi tipe kamar–lantai tidak diperbolehkan: " + 
                                         roomType.getRoomTypeName() + " di lantai " + roomType.getFloor());
            }
        }
        
        // Hitung total rooms dari semua room types
        int calculatedTotalRoom = createPropertyRequestDTO.getRoomTypes().stream()
                .mapToInt(AddRoomRequestDTO::getUnitCount)
                .sum();
        
        // Validasi total room match
        if (calculatedTotalRoom != createPropertyRequestDTO.getTotalRoom().intValue()) {
            throw new RuntimeException("Total room tidak sesuai. Expected: " + calculatedTotalRoom + 
                                     ", Got: " + createPropertyRequestDTO.getTotalRoom());
        }
        
        // ❌ COMMENTED: Old formatted ID generation
        // String propertyID = generatePropertyID(createPropertyRequestDTO);
        
        // ✅ NEW: Let @PrePersist auto-generate UUID
        // Create Property (propertyID will be auto-generated by @PrePersist)
        Property property = Property.builder()
                // .propertyID() is not set here - will be auto-generated
                .propertyName(createPropertyRequestDTO.getPropertyName())
                .type(createPropertyRequestDTO.getType())
                .address(createPropertyRequestDTO.getAddress())
                .province(createPropertyRequestDTO.getProvince())
                .description(createPropertyRequestDTO.getDescription())
                .totalRoom(createPropertyRequestDTO.getTotalRoom())
                .activeStatus(1) // Default active
                .income(0) // Default 0
                .ownerName(createPropertyRequestDTO.getOwnerName())
                .owner(accommodationOwnerRepository.findById(UUID.fromString(createPropertyRequestDTO.getOwnerID()))
                    .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + createPropertyRequestDTO.getOwnerID())))
                // createdDate and updatedDate will be set by @PrePersist
                .build();
        
        Property savedProperty = propertyRepository.save(property);
        
        // List untuk menyimpan room type info dengan room IDs
        List<RoomTypeInfoDTO> roomTypeInfoList = new ArrayList<>();
        
        // Create Room Types dan Rooms
        for (AddRoomRequestDTO roomTypeData : createPropertyRequestDTO.getRoomTypes()) {
            
            // Create RoomType
            CreateRoomTypeRequestDTO roomTypeDTO = CreateRoomTypeRequestDTO.builder()
                    .name(roomTypeData.getRoomTypeName())
                    .price(roomTypeData.getPrice())
                    .description(roomTypeData.getRoomTypeDescription())
                    .capacity(roomTypeData.getCapacity())
                    .facility(roomTypeData.getFacility())
                    .floor(roomTypeData.getFloor())
                    // ✅ Use savedProperty's auto-generated UUID
                    .propertyID(savedProperty.getPropertyID().toString())
                    .unitCount(roomTypeData.getUnitCount())
                    .build();
            
            RoomTypeResponseDTO createdRoomType = roomTypeRestService.createRoomType(roomTypeDTO);
            
            // ✅ createRoomType() already auto-generates rooms based on unitCount
            // No need to manually create rooms here - it causes duplication!
            // Just get the room IDs from the created room type
            List<RoomResponseDTO> createdRooms = roomRestService.getRoomsByRoomType(
                UUID.fromString(createdRoomType.getRoomTypeID())
            );
            
            List<String> roomIDs = createdRooms.stream()
                .map(RoomResponseDTO::getRoomID)
                .collect(Collectors.toList());
            
            // Build room type info dengan room IDs (gunakan class terpisah)
            RoomTypeInfoDTO roomTypeInfo = RoomTypeInfoDTO.builder()
                    .roomTypeID(createdRoomType.getRoomTypeID())
                    .roomTypeName(createdRoomType.getName())
                    .floor(createdRoomType.getFloor())
                    .capacity(createdRoomType.getCapacity())
                    .price(createdRoomType.getPrice())
                    .facility(createdRoomType.getFacility())
                    .description(createdRoomType.getDescription())
                    .roomIDs(roomIDs)
                    .build();
            
            roomTypeInfoList.add(roomTypeInfo);
        }
        
        // Convert to response DTO dengan room type info
        return convertToPropertyResponseDTO(savedProperty, roomTypeInfoList);
    }

    @Override
    public PropertyResponseDTO updateProperty(UUID id, UpdatePropertyRequestDTO updatePropertyRequestDTO) {
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        
        if (propertyOpt.isEmpty()) {
            return null;
        }
        
        Property existingProperty = propertyOpt.get();
        
        //  Update property fields
        existingProperty.setPropertyName(updatePropertyRequestDTO.getPropertyName() != null ? 
                                       updatePropertyRequestDTO.getPropertyName() : existingProperty.getPropertyName());
        
        if (updatePropertyRequestDTO.getType() != null) {
            existingProperty.setType(updatePropertyRequestDTO.getType());
        }
        
        existingProperty.setAddress(updatePropertyRequestDTO.getAddress() != null ? 
                                  updatePropertyRequestDTO.getAddress() : existingProperty.getAddress());
        existingProperty.setProvince(updatePropertyRequestDTO.getProvince() != null ? 
                                   updatePropertyRequestDTO.getProvince() : existingProperty.getProvince());
        existingProperty.setDescription(updatePropertyRequestDTO.getDescription() != null ? 
                                      updatePropertyRequestDTO.getDescription() : existingProperty.getDescription());
        existingProperty.setTotalRoom(updatePropertyRequestDTO.getTotalRoom() != null ? 
                                    updatePropertyRequestDTO.getTotalRoom() : existingProperty.getTotalRoom());
        existingProperty.setActiveStatus(updatePropertyRequestDTO.getActiveStatus() != null ? 
                                       updatePropertyRequestDTO.getActiveStatus() : existingProperty.getActiveStatus());
        existingProperty.setUpdatedDate(LocalDateTime.now());
        
        Property updatedProperty = propertyRepository.save(existingProperty);
        
        //  Update room types jika ada di request
        if (updatePropertyRequestDTO.getRoomTypes() != null && !updatePropertyRequestDTO.getRoomTypes().isEmpty()) {
            for (UpdateRoomTypeRequestDTO roomTypeDTO : updatePropertyRequestDTO.getRoomTypes()) {
                roomTypeRestService.updateRoomType(UUID.fromString(roomTypeDTO.getRoomTypeID()), roomTypeDTO);
            }
        }
        
        //  Fetch updated room types
        List<RoomTypeResponseDTO> roomTypes = roomTypeRestService.getRoomTypesByProperty(id);
        List<RoomTypeInfoDTO> roomTypeInfoList = new ArrayList<>();
        
        for (RoomTypeResponseDTO roomType : roomTypes) {
            List<RoomResponseDTO> rooms = roomRestService.getRoomsByRoomType(UUID.fromString(roomType.getRoomTypeID()));
            List<String> roomIDs = rooms.stream()
                    .map(RoomResponseDTO::getRoomID)
                    .collect(Collectors.toList());
            
            RoomTypeInfoDTO roomTypeInfo = RoomTypeInfoDTO.builder()
                    .roomTypeID(roomType.getRoomTypeID())
                    .roomTypeName(roomType.getName())
                    .floor(roomType.getFloor())
                    .capacity(roomType.getCapacity())
                    .price(roomType.getPrice())
                    .facility(roomType.getFacility())
                    .description(roomType.getDescription())
                    .roomIDs(roomIDs)
                    .build();
            
            roomTypeInfoList.add(roomTypeInfo);
        }
        
        return convertToPropertyResponseDTO(updatedProperty, roomTypeInfoList);
    }

    @Override
    public PropertyResponseDTO deleteProperty(UUID id) {
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        
        if (propertyOpt.isEmpty()) {
            return null;
        }
        
        // Check if property has any active bookings (not cancelled or completed)
        boolean hasActiveBookings = bookingRepository.existsActiveBookingsByPropertyID(id);
        if (hasActiveBookings) {
            throw new RuntimeException("Cannot delete property: There are active bookings for this property. Please wait until all bookings are completed or cancelled.");
        }
        
        Property existingProperty = propertyOpt.get();
        existingProperty.setActiveStatus(0);
        existingProperty.setUpdatedDate(LocalDateTime.now());
        
        Property deletedProperty = propertyRepository.save(existingProperty);
        return convertToPropertyResponseDTO(deletedProperty, null);
    }

    // =========== Helper Methods ===========

    // ❌ COMMENTED: Old formatted ID generation - now using UUID auto-generation via @PrePersist
    // private String generatePropertyID(CreatePropertyRequestDTO dto) {
    //     // Get type prefix (HOT, VIL, APT)
    //     String typePrefix = getTypePrefix(dto.getType());
    //     
    //     // 4 karakter terakhir dari UUID Owner
    //     String ownerSuffix = dto.getOwnerID() != null && dto.getOwnerID().length() >= 4 
    //         ? dto.getOwnerID().substring(dto.getOwnerID().length() - 4)
    //         : "0000";
    //     
    //     // 3 digit counter properti ke-N (hitung dari database)
    //     long propertyCount = propertyRepository.count() + 1;
    //     String counter = String.format("%03d", propertyCount);
    //     return typePrefix + "-" + ownerSuffix + "-" + counter;
    // }

    //  Update convertToPropertyResponseDTO dengan room type info
    private PropertyResponseDTO convertToPropertyResponseDTO(Property property, List<RoomTypeInfoDTO> roomTypeInfoList) {
        String typeName = getTypeName(property.getType());
        String activeStatusName = property.getActiveStatus() == 1 ? "Active" : "Non-Active";
        
        return PropertyResponseDTO.builder()
                .propertyID(property.getPropertyID().toString())
                .propertyName(property.getPropertyName())
                .type(property.getType())
                .typeName(typeName)
                .address(property.getAddress())
                .province(property.getProvince())
                .description(property.getDescription())
                .totalRoom(property.getTotalRoom())
                .activeStatus(property.getActiveStatus())
                .activeStatusName(activeStatusName)
                .income(property.getIncome())
                .ownerName(property.getOwnerName())
                .ownerID(property.getOwnerID().toString())
                .createdDate(property.getCreatedDate())
                .updatedDate(property.getUpdatedDate())
                .roomTypes(roomTypeInfoList)
                .build();
    }

    private String getTypeName(int type) {
        return switch (type) {
            case 1 -> "Hotel";
            case 2 -> "Villa";
            case 3 -> "Apartemen";
            default -> "Unknown";
        };
    }

    // ❌ COMMENTED: Old helper for formatted ID generation
    // private String getTypePrefix(Integer type) {
    //     return switch (type) {
    //         case 1 -> "HOT";
    //         case 2 -> "VIL";
    //         case 3 -> "APT";
    //         default -> "UNK";
    //     };
    // }

}