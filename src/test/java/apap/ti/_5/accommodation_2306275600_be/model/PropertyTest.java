package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PropertyTest {

    private Property property;
    private UUID testPropertyId;
    private UUID testOwnerId;

    @BeforeEach
    void setUp() {
        testPropertyId = UUID.randomUUID();
        testOwnerId = UUID.randomUUID();
        
        property = Property.builder()
                .propertyID(testPropertyId)
                .propertyName("Test Hotel")
                .type(1)
                .address("Jl. Test No. 123")
                .province(31)
                .description("A test hotel property")
                .totalRoom(50)
                .activeStatus(1)
                .income(10000000)
                .ownerName("John Doe")
                .ownerID(testOwnerId)
                .build();
    }

    @Test
    void testPropertyBuilder() {
        assertNotNull(property);
        assertEquals(testPropertyId, property.getPropertyID());
        assertEquals("Test Hotel", property.getPropertyName());
        assertEquals(1, property.getType());
        assertEquals("Jl. Test No. 123", property.getAddress());
        assertEquals(31, property.getProvince());
        assertEquals("A test hotel property", property.getDescription());
        assertEquals(50, property.getTotalRoom());
        assertEquals(1, property.getActiveStatus());
        assertEquals(10000000, property.getIncome());
        assertEquals("John Doe", property.getOwnerName());
        assertEquals(testOwnerId, property.getOwnerID());
    }

    @Test
    void testNoArgsConstructor() {
        Property emptyProperty = new Property();
        assertNotNull(emptyProperty);
        assertNull(emptyProperty.getPropertyID());
        assertNull(emptyProperty.getPropertyName());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<RoomType> roomTypes = new ArrayList<>();
        
        Property fullProperty = new Property(
                testPropertyId,
                "Full Hotel",
                2,
                "Jl. Full No. 456",
                32,
                "Full description",
                100,
                1,
                20000000,
                roomTypes,
                "Jane Doe",
                testOwnerId,
                now,
                now
        );
        
        assertEquals(testPropertyId, fullProperty.getPropertyID());
        assertEquals("Full Hotel", fullProperty.getPropertyName());
        assertEquals(2, fullProperty.getType());
        assertEquals("Jl. Full No. 456", fullProperty.getAddress());
        assertEquals(32, fullProperty.getProvince());
        assertEquals("Full description", fullProperty.getDescription());
        assertEquals(100, fullProperty.getTotalRoom());
        assertEquals(1, fullProperty.getActiveStatus());
        assertEquals(20000000, fullProperty.getIncome());
        assertEquals("Jane Doe", fullProperty.getOwnerName());
        assertEquals(testOwnerId, fullProperty.getOwnerID());
        assertEquals(now, fullProperty.getCreatedDate());
        assertEquals(now, fullProperty.getUpdatedDate());
    }

    @Test
    void testSettersAndGetters() {
        property.setPropertyName("Updated Hotel");
        assertEquals("Updated Hotel", property.getPropertyName());
        
        property.setType(3);
        assertEquals(3, property.getType());
        
        property.setAddress("New Address");
        assertEquals("New Address", property.getAddress());
        
        property.setProvince(33);
        assertEquals(33, property.getProvince());
        
        property.setDescription("Updated description");
        assertEquals("Updated description", property.getDescription());
        
        property.setTotalRoom(75);
        assertEquals(75, property.getTotalRoom());
        
        property.setActiveStatus(0);
        assertEquals(0, property.getActiveStatus());
        
        property.setIncome(15000000);
        assertEquals(15000000, property.getIncome());
        
        property.setOwnerName("New Owner");
        assertEquals("New Owner", property.getOwnerName());
        
        UUID newOwnerId = UUID.randomUUID();
        property.setOwnerID(newOwnerId);
        assertEquals(newOwnerId, property.getOwnerID());
    }

    @Test
    void testRoomTypeRelationship() {
        List<RoomType> roomTypes = new ArrayList<>();
        RoomType roomType = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Deluxe Room")
                .price(500000)
                .build();
        roomTypes.add(roomType);
        
        property.setListRoomType(roomTypes);
        
        assertNotNull(property.getListRoomType());
        assertEquals(1, property.getListRoomType().size());
        assertEquals("Deluxe Room", property.getListRoomType().get(0).getName());
    }

    @Test
    void testOnCreate() {
        Property newProperty = new Property();
        assertNull(newProperty.getPropertyID());
        assertNull(newProperty.getCreatedDate());
        assertNull(newProperty.getUpdatedDate());
        
        // Simulate @PrePersist
        newProperty.onCreate();
        
        assertNotNull(newProperty.getPropertyID());
        assertNotNull(newProperty.getCreatedDate());
        assertNotNull(newProperty.getUpdatedDate());
        assertEquals(newProperty.getCreatedDate(), newProperty.getUpdatedDate());
    }

    @Test
    void testOnCreateWithExistingId() {
        Property newProperty = new Property();
        UUID existingId = UUID.randomUUID();
        newProperty.setPropertyID(existingId);
        
        newProperty.onCreate();
        
        // Should not change existing ID
        assertEquals(existingId, newProperty.getPropertyID());
        assertNotNull(newProperty.getCreatedDate());
    }

    @Test
    void testOnUpdate() {
        property.onCreate();
        LocalDateTime originalCreatedDate = property.getCreatedDate();
        LocalDateTime originalUpdatedDate = property.getUpdatedDate();
        
        try {
            Thread.sleep(10); // Small delay to ensure different timestamp
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        property.onUpdate();
        
        assertEquals(originalCreatedDate, property.getCreatedDate());
        assertNotEquals(originalUpdatedDate, property.getUpdatedDate());
        assertTrue(property.getUpdatedDate().isAfter(originalUpdatedDate));
    }

    @Test
    void testToBuilder() {
        Property clonedProperty = property.toBuilder()
                .propertyName("Cloned Hotel")
                .build();
        
        assertEquals(testPropertyId, clonedProperty.getPropertyID());
        assertEquals("Cloned Hotel", clonedProperty.getPropertyName());
        assertEquals(property.getType(), clonedProperty.getType());
        assertEquals(property.getAddress(), clonedProperty.getAddress());
    }

    @Test
    void testEqualsAndHashCode() {
        Property sameProperty = Property.builder()
                .propertyID(testPropertyId)
                .propertyName("Test Hotel")
                .type(1)
                .address("Jl. Test No. 123")
                .province(31)
                .description("A test hotel property")
                .totalRoom(50)
                .activeStatus(1)
                .income(10000000)
                .ownerName("John Doe")
                .ownerID(testOwnerId)
                .build();
        
        assertEquals(property, sameProperty);
        assertEquals(property.hashCode(), sameProperty.hashCode());
    }

    @Test
    void testNotEquals() {
        Property differentProperty = Property.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("Different Hotel")
                .build();
        
        assertNotEquals(property, differentProperty);
    }

    @Test
    void testToString() {
        String propertyString = property.toString();
        assertNotNull(propertyString);
        assertTrue(propertyString.contains("Test Hotel"));
        assertTrue(propertyString.contains(testPropertyId.toString()));
    }

    @Test
    void testNullValues() {
        Property nullProperty = Property.builder().build();
        
        assertNull(nullProperty.getPropertyID());
        assertNull(nullProperty.getPropertyName());
        assertNull(nullProperty.getAddress());
        assertNull(nullProperty.getDescription());
        assertNull(nullProperty.getOwnerName());
        assertNull(nullProperty.getOwnerID());
    }

    @Test
    void testDateTimeFields() {
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedDate = LocalDateTime.now();
        
        property.setCreatedDate(createdDate);
        property.setUpdatedDate(updatedDate);
        
        assertEquals(createdDate, property.getCreatedDate());
        assertEquals(updatedDate, property.getUpdatedDate());
        assertTrue(property.getUpdatedDate().isAfter(property.getCreatedDate()));
    }

    @Test
    void testEmptyRoomTypeList() {
        property.setListRoomType(new ArrayList<>());
        assertNotNull(property.getListRoomType());
        assertTrue(property.getListRoomType().isEmpty());
    }

    @Test
    void testMultipleRoomTypes() {
        List<RoomType> roomTypes = new ArrayList<>();
        roomTypes.add(RoomType.builder().name("Standard").price(300000).build());
        roomTypes.add(RoomType.builder().name("Deluxe").price(500000).build());
        roomTypes.add(RoomType.builder().name("Suite").price(1000000).build());
        
        property.setListRoomType(roomTypes);
        
        assertEquals(3, property.getListRoomType().size());
    }
}
