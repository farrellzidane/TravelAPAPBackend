package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class PropertyTest {

    private Property property;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        property = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Grand Hotel")
                .type(1)
                .address("Jl. Sudirman No. 123, Jakarta")
                .province(31) // DKI Jakarta
                .description("A luxurious 5-star hotel in the heart of Jakarta")
                .totalRoom(100)
                .activeStatus(1)
                .income(5000000)
                .ownerName("John Doe")
                .ownerID(ownerId)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .listRoomType(new ArrayList<>())
                .build();
    }

    @Test
    void testPropertyCreation() {
        assertNotNull(property);
        assertEquals("PROP-001", property.getPropertyID());
        assertEquals("Grand Hotel", property.getPropertyName());
        assertEquals(1, property.getType());
        assertEquals("Jl. Sudirman No. 123, Jakarta", property.getAddress());
        assertEquals(31, property.getProvince());
        assertEquals("A luxurious 5-star hotel in the heart of Jakarta", property.getDescription());
        assertEquals(100, property.getTotalRoom());
        assertEquals(1, property.getActiveStatus());
        assertEquals(5000000, property.getIncome());
        assertEquals("John Doe", property.getOwnerName());
        assertEquals(ownerId, property.getOwnerID());
        assertNotNull(property.getListRoomType());
    }

    @Test
    void testPropertyBuilder() {
        UUID newOwnerId = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2025, 1, 2, 10, 0);

        Property newProperty = Property.builder()
                .propertyID("PROP-002")
                .propertyName("Beach Resort")
                .type(2)
                .address("Jl. Pantai Kuta, Bali")
                .province(51) // Bali
                .description("Beautiful beachfront resort")
                .totalRoom(50)
                .activeStatus(1)
                .income(3000000)
                .ownerName("Jane Smith")
                .ownerID(newOwnerId)
                .createdDate(created)
                .updatedDate(updated)
                .build();

        assertEquals("PROP-002", newProperty.getPropertyID());
        assertEquals("Beach Resort", newProperty.getPropertyName());
        assertEquals(2, newProperty.getType());
        assertEquals("Jl. Pantai Kuta, Bali", newProperty.getAddress());
        assertEquals(51, newProperty.getProvince());
        assertEquals("Beautiful beachfront resort", newProperty.getDescription());
        assertEquals(50, newProperty.getTotalRoom());
        assertEquals(1, newProperty.getActiveStatus());
        assertEquals(3000000, newProperty.getIncome());
        assertEquals("Jane Smith", newProperty.getOwnerName());
        assertEquals(newOwnerId, newProperty.getOwnerID());
        assertEquals(created, newProperty.getCreatedDate());
        assertEquals(updated, newProperty.getUpdatedDate());
    }

    @Test
    void testPropertySettersAndGetters() {
        property.setPropertyID("PROP-UPDATE");
        assertEquals("PROP-UPDATE", property.getPropertyID());

        property.setPropertyName("Updated Hotel");
        assertEquals("Updated Hotel", property.getPropertyName());

        property.setType(3);
        assertEquals(3, property.getType());

        property.setAddress("New Address");
        assertEquals("New Address", property.getAddress());

        property.setProvince(32);
        assertEquals(32, property.getProvince());

        property.setDescription("Updated description");
        assertEquals("Updated description", property.getDescription());

        property.setTotalRoom(150);
        assertEquals(150, property.getTotalRoom());

        property.setActiveStatus(0);
        assertEquals(0, property.getActiveStatus());

        property.setIncome(10000000);
        assertEquals(10000000, property.getIncome());

        property.setOwnerName("New Owner");
        assertEquals("New Owner", property.getOwnerName());

        UUID newOwnerId = UUID.randomUUID();
        property.setOwnerID(newOwnerId);
        assertEquals(newOwnerId, property.getOwnerID());

        LocalDateTime newCreated = LocalDateTime.now().minusDays(1);
        property.setCreatedDate(newCreated);
        assertEquals(newCreated, property.getCreatedDate());

        LocalDateTime newUpdated = LocalDateTime.now();
        property.setUpdatedDate(newUpdated);
        assertEquals(newUpdated, property.getUpdatedDate());
    }

    @Test
    void testPropertyNoArgsConstructor() {
        Property emptyProperty = new Property();
        assertNotNull(emptyProperty);
        assertNull(emptyProperty.getPropertyID());
        assertNull(emptyProperty.getPropertyName());
        assertEquals(0, emptyProperty.getIncome());
    }

    @Test
    void testPropertyAllArgsConstructor() {
        UUID newOwnerId = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        List<RoomType> roomTypes = new ArrayList<>();

        Property newProperty = new Property(
            "PROP-003",
            "Test Property",
            1,
            "Test Address",
            31,
            "Test Description",
            75,
            1,
            2000000,
            roomTypes,
            "Test Owner",
            newOwnerId,
            created,
            updated
        );

        assertEquals("PROP-003", newProperty.getPropertyID());
        assertEquals("Test Property", newProperty.getPropertyName());
        assertEquals(1, newProperty.getType());
        assertEquals("Test Address", newProperty.getAddress());
        assertEquals(31, newProperty.getProvince());
        assertEquals("Test Description", newProperty.getDescription());
        assertEquals(75, newProperty.getTotalRoom());
        assertEquals(1, newProperty.getActiveStatus());
        assertEquals(2000000, newProperty.getIncome());
        assertEquals(roomTypes, newProperty.getListRoomType());
        assertEquals("Test Owner", newProperty.getOwnerName());
        assertEquals(newOwnerId, newProperty.getOwnerID());
        assertEquals(created, newProperty.getCreatedDate());
        assertEquals(updated, newProperty.getUpdatedDate());
    }

    @Test
    void testPropertyRoomTypeRelationship() {
        RoomType roomType1 = RoomType.builder()
                .roomTypeID("RT-001")
                .name("Deluxe Room")
                .property(property)
                .build();

        RoomType roomType2 = RoomType.builder()
                .roomTypeID("RT-002")
                .name("Suite")
                .property(property)
                .build();

        List<RoomType> roomTypes = new ArrayList<>();
        roomTypes.add(roomType1);
        roomTypes.add(roomType2);

        property.setListRoomType(roomTypes);

        assertEquals(2, property.getListRoomType().size());
        assertEquals("Deluxe Room", property.getListRoomType().get(0).getName());
        assertEquals("Suite", property.getListRoomType().get(1).getName());
    }

    @Test
    void testPropertyActiveStatus() {
        // Active
        property.setActiveStatus(1);
        assertEquals(1, property.getActiveStatus());

        // Inactive
        property.setActiveStatus(0);
        assertEquals(0, property.getActiveStatus());
    }

    @Test
    void testPropertyIncomeManagement() {
        assertEquals(5000000, property.getIncome());

        // Add income
        property.setIncome(property.getIncome() + 1000000);
        assertEquals(6000000, property.getIncome());

        // Subtract income (refund)
        property.setIncome(property.getIncome() - 500000);
        assertEquals(5500000, property.getIncome());
    }

    @Test
    void testPropertyTypes() {
        // Test different property types
        property.setType(1); // Hotel
        assertEquals(1, property.getType());

        property.setType(2); // Apartment
        assertEquals(2, property.getType());

        property.setType(3); // Villa
        assertEquals(3, property.getType());
    }

    @Test
    void testPropertyToBuilder() {
        Property modifiedProperty = property.toBuilder()
                .propertyName("Modified Hotel")
                .income(7000000)
                .build();

        assertEquals("Modified Hotel", modifiedProperty.getPropertyName());
        assertEquals(7000000, modifiedProperty.getIncome());
        assertEquals(property.getPropertyID(), modifiedProperty.getPropertyID());
        assertEquals(property.getType(), modifiedProperty.getType());
    }

    @Test
    void testPropertyEqualsAndHashCode() {
        Property property1 = Property.builder()
                .propertyID("PROP-SAME")
                .propertyName("Same Hotel")
                .build();

        Property property2 = Property.builder()
                .propertyID("PROP-SAME")
                .propertyName("Same Hotel")
                .build();

        Property property3 = Property.builder()
                .propertyID("PROP-DIFFERENT")
                .propertyName("Different Hotel")
                .build();

        assertEquals(property1, property2);
        assertNotEquals(property1, property3);
        assertEquals(property1.hashCode(), property2.hashCode());
    }

    @Test
    void testPropertyToString() {
        String propertyString = property.toString();
        assertNotNull(propertyString);
        assertTrue(propertyString.contains("PROP-001"));
        assertTrue(propertyString.contains("Grand Hotel"));
    }

    @Test
    void testPropertyWithEmptyRoomTypeList() {
        property.setListRoomType(new ArrayList<>());
        assertNotNull(property.getListRoomType());
        assertEquals(0, property.getListRoomType().size());
    }

    @Test
    void testPropertyWithNullRoomTypeList() {
        property.setListRoomType(null);
        assertNull(property.getListRoomType());
    }

    @Test
    void testPropertyTotalRoomUpdate() {
        property.setTotalRoom(100);
        assertEquals(100, property.getTotalRoom());

        // Increase total rooms
        property.setTotalRoom(120);
        assertEquals(120, property.getTotalRoom());

        // Decrease total rooms
        property.setTotalRoom(80);
        assertEquals(80, property.getTotalRoom());
    }

    @Test
    void testPropertyDescriptionUpdate() {
        String longDescription = "This is a very long description that contains multiple lines " +
                "and provides detailed information about the property, its amenities, " +
                "location, and services offered to guests.";
        
        property.setDescription(longDescription);
        assertEquals(longDescription, property.getDescription());
    }

    @Test
    void testPropertyAddressUpdate() {
        String newAddress = "Jl. Gatot Subroto No. 456, Jakarta Selatan, DKI Jakarta 12930";
        property.setAddress(newAddress);
        assertEquals(newAddress, property.getAddress());
    }

    @Test
    void testPropertyProvinceCode() {
        // Test various province codes
        property.setProvince(11); // Aceh
        assertEquals(11, property.getProvince());

        property.setProvince(31); // DKI Jakarta
        assertEquals(31, property.getProvince());

        property.setProvince(51); // Bali
        assertEquals(51, property.getProvince());
    }
}
