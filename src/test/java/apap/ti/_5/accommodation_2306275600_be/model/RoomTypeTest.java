package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class RoomTypeTest {

    private RoomType roomType;
    private Property property;

    @BeforeEach
    void setUp() {
        // Setup Property
        property = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Luxury Hotel")
                .type(1)
                .activeStatus(1)
                .income(0)
                .ownerName("Owner Name")
                .ownerID(UUID.randomUUID())
                .build();

        // Setup RoomType
        roomType = RoomType.builder()
                .roomTypeID("RT-001")
                .name("Deluxe Room")
                .price(750000)
                .description("Spacious room with king-size bed")
                .capacity(2)
                .facility("AC, TV, WiFi, Mini Bar, Safe")
                .floor(5)
                .property(property)
                .listRoom(new ArrayList<>())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testRoomTypeCreation() {
        assertNotNull(roomType);
        assertEquals("RT-001", roomType.getRoomTypeID());
        assertEquals("Deluxe Room", roomType.getName());
        assertEquals(750000, roomType.getPrice());
        assertEquals("Spacious room with king-size bed", roomType.getDescription());
        assertEquals(2, roomType.getCapacity());
        assertEquals("AC, TV, WiFi, Mini Bar, Safe", roomType.getFacility());
        assertEquals(5, roomType.getFloor());
        assertNotNull(roomType.getProperty());
        assertNotNull(roomType.getListRoom());
    }

    @Test
    void testRoomTypeBuilder() {
        LocalDateTime created = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2025, 1, 2, 10, 0);

        RoomType newRoomType = RoomType.builder()
                .roomTypeID("RT-002")
                .name("Presidential Suite")
                .price(2500000)
                .description("Luxurious presidential suite")
                .capacity(4)
                .facility("AC, TV, WiFi, Jacuzzi, Kitchen")
                .floor(10)
                .property(property)
                .createdDate(created)
                .updatedDate(updated)
                .build();

        assertEquals("RT-002", newRoomType.getRoomTypeID());
        assertEquals("Presidential Suite", newRoomType.getName());
        assertEquals(2500000, newRoomType.getPrice());
        assertEquals("Luxurious presidential suite", newRoomType.getDescription());
        assertEquals(4, newRoomType.getCapacity());
        assertEquals("AC, TV, WiFi, Jacuzzi, Kitchen", newRoomType.getFacility());
        assertEquals(10, newRoomType.getFloor());
        assertEquals(property, newRoomType.getProperty());
        assertEquals(created, newRoomType.getCreatedDate());
        assertEquals(updated, newRoomType.getUpdatedDate());
    }

    @Test
    void testRoomTypeSettersAndGetters() {
        roomType.setRoomTypeID("RT-UPDATE");
        assertEquals("RT-UPDATE", roomType.getRoomTypeID());

        roomType.setName("Updated Room Type");
        assertEquals("Updated Room Type", roomType.getName());

        roomType.setPrice(1000000);
        assertEquals(1000000, roomType.getPrice());

        roomType.setDescription("Updated description");
        assertEquals("Updated description", roomType.getDescription());

        roomType.setCapacity(3);
        assertEquals(3, roomType.getCapacity());

        roomType.setFacility("AC, TV, WiFi");
        assertEquals("AC, TV, WiFi", roomType.getFacility());

        roomType.setFloor(7);
        assertEquals(7, roomType.getFloor());

        Property newProperty = Property.builder()
                .propertyID("PROP-002")
                .build();
        roomType.setProperty(newProperty);
        assertEquals("PROP-002", roomType.getProperty().getPropertyID());

        List<Room> rooms = new ArrayList<>();
        roomType.setListRoom(rooms);
        assertEquals(rooms, roomType.getListRoom());

        LocalDateTime newCreated = LocalDateTime.now().minusDays(1);
        roomType.setCreatedDate(newCreated);
        assertEquals(newCreated, roomType.getCreatedDate());

        LocalDateTime newUpdated = LocalDateTime.now();
        roomType.setUpdatedDate(newUpdated);
        assertEquals(newUpdated, roomType.getUpdatedDate());
    }

    @Test
    void testRoomTypeNoArgsConstructor() {
        RoomType emptyRoomType = new RoomType();
        assertNotNull(emptyRoomType);
        assertNull(emptyRoomType.getRoomTypeID());
        assertNull(emptyRoomType.getName());
        assertEquals(0, emptyRoomType.getPrice());
    }

    @Test
    void testRoomTypeAllArgsConstructor() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        List<Room> rooms = new ArrayList<>();

        RoomType newRoomType = new RoomType(
            "RT-003",
            "Standard Room",
            350000,
            "Comfortable standard room",
            2,
            "AC, TV",
            3,
            property,
            rooms,
            created,
            updated
        );

        assertEquals("RT-003", newRoomType.getRoomTypeID());
        assertEquals("Standard Room", newRoomType.getName());
        assertEquals(350000, newRoomType.getPrice());
        assertEquals("Comfortable standard room", newRoomType.getDescription());
        assertEquals(2, newRoomType.getCapacity());
        assertEquals("AC, TV", newRoomType.getFacility());
        assertEquals(3, newRoomType.getFloor());
        assertEquals(property, newRoomType.getProperty());
        assertEquals(rooms, newRoomType.getListRoom());
        assertEquals(created, newRoomType.getCreatedDate());
        assertEquals(updated, newRoomType.getUpdatedDate());
    }

    @Test
    void testRoomTypePrePersist() {
        RoomType newRoomType = new RoomType();
        newRoomType.onCreate();

        assertNotNull(newRoomType.getRoomTypeID());
        assertNotNull(newRoomType.getCreatedDate());
        assertNotNull(newRoomType.getUpdatedDate());
        assertEquals(newRoomType.getCreatedDate(), newRoomType.getUpdatedDate());
    }

    @Test
    void testRoomTypePrePersistWithExistingId() {
        RoomType newRoomType = new RoomType();
        newRoomType.setRoomTypeID("EXISTING-RT-ID");
        newRoomType.onCreate();

        assertEquals("EXISTING-RT-ID", newRoomType.getRoomTypeID());
        assertNotNull(newRoomType.getCreatedDate());
        assertNotNull(newRoomType.getUpdatedDate());
    }

    @Test
    void testRoomTypePreUpdate() throws InterruptedException {
        LocalDateTime originalUpdated = roomType.getUpdatedDate();
        Thread.sleep(10);
        
        roomType.onUpdate();
        
        assertNotNull(roomType.getUpdatedDate());
        assertTrue(roomType.getUpdatedDate().isAfter(originalUpdated) || 
                   roomType.getUpdatedDate().isEqual(originalUpdated));
    }

    @Test
    void testRoomTypePropertyRelationship() {
        assertNotNull(roomType.getProperty());
        assertEquals("PROP-001", roomType.getProperty().getPropertyID());
        assertEquals("Luxury Hotel", roomType.getProperty().getPropertyName());
    }

    @Test
    void testRoomTypeRoomListRelationship() {
        Room room1 = Room.builder()
                .roomID("ROOM-001")
                .name("Deluxe 501")
                .roomType(roomType)
                .build();

        Room room2 = Room.builder()
                .roomID("ROOM-002")
                .name("Deluxe 502")
                .roomType(roomType)
                .build();

        List<Room> rooms = new ArrayList<>();
        rooms.add(room1);
        rooms.add(room2);

        roomType.setListRoom(rooms);

        assertEquals(2, roomType.getListRoom().size());
        assertEquals("Deluxe 501", roomType.getListRoom().get(0).getName());
        assertEquals("Deluxe 502", roomType.getListRoom().get(1).getName());
    }

    @Test
    void testRoomTypeToBuilder() {
        RoomType modifiedRoomType = roomType.toBuilder()
                .name("Modified Deluxe")
                .price(850000)
                .build();

        assertEquals("Modified Deluxe", modifiedRoomType.getName());
        assertEquals(850000, modifiedRoomType.getPrice());
        assertEquals(roomType.getRoomTypeID(), modifiedRoomType.getRoomTypeID());
        assertEquals(roomType.getCapacity(), modifiedRoomType.getCapacity());
    }

    @Test
    void testRoomTypeEqualsAndHashCode() {
        RoomType roomType1 = RoomType.builder()
                .roomTypeID("RT-SAME")
                .name("Same Type")
                .build();

        RoomType roomType2 = RoomType.builder()
                .roomTypeID("RT-SAME")
                .name("Same Type")
                .build();

        RoomType roomType3 = RoomType.builder()
                .roomTypeID("RT-DIFFERENT")
                .name("Different Type")
                .build();

        assertEquals(roomType1, roomType2);
        assertNotEquals(roomType1, roomType3);
        assertEquals(roomType1.hashCode(), roomType2.hashCode());
    }

    @Test
    void testRoomTypeToString() {
        String roomTypeString = roomType.toString();
        assertNotNull(roomTypeString);
        assertTrue(roomTypeString.contains("RT-001"));
        assertTrue(roomTypeString.contains("Deluxe Room"));
    }

    @Test
    void testRoomTypePriceUpdate() {
        roomType.setPrice(800000);
        assertEquals(800000, roomType.getPrice());

        // Price increase
        roomType.setPrice(900000);
        assertEquals(900000, roomType.getPrice());

        // Price decrease (discount)
        roomType.setPrice(700000);
        assertEquals(700000, roomType.getPrice());
    }

    @Test
    void testRoomTypeCapacityUpdate() {
        roomType.setCapacity(2);
        assertEquals(2, roomType.getCapacity());

        roomType.setCapacity(4);
        assertEquals(4, roomType.getCapacity());
    }

    @Test
    void testRoomTypeFloorUpdate() {
        roomType.setFloor(5);
        assertEquals(5, roomType.getFloor());

        roomType.setFloor(10);
        assertEquals(10, roomType.getFloor());
    }

    @Test
    void testRoomTypeFacilityUpdate() {
        String facilities = "AC, TV, WiFi, Mini Bar, Safe, Balcony";
        roomType.setFacility(facilities);
        assertEquals(facilities, roomType.getFacility());
    }

    @Test
    void testRoomTypeDescriptionUpdate() {
        String longDescription = "This is a luxurious deluxe room featuring " +
                "modern amenities, elegant design, and stunning city views. " +
                "Perfect for business travelers and tourists alike.";
        
        roomType.setDescription(longDescription);
        assertEquals(longDescription, roomType.getDescription());
    }

    @Test
    void testRoomTypeWithEmptyRoomList() {
        roomType.setListRoom(new ArrayList<>());
        assertNotNull(roomType.getListRoom());
        assertEquals(0, roomType.getListRoom().size());
    }

    @Test
    void testRoomTypeWithNullRoomList() {
        roomType.setListRoom(null);
        assertNull(roomType.getListRoom());
    }

    @Test
    void testRoomTypeDefaultListRoomInitialization() {
        RoomType newRoomType = RoomType.builder()
                .roomTypeID("RT-DEFAULT")
                .name("Test Type")
                .build();

        assertNotNull(newRoomType.getListRoom());
        assertEquals(0, newRoomType.getListRoom().size());
    }

    @Test
    void testRoomTypeNullRoomTypeId() {
        RoomType newRoomType = new RoomType();
        newRoomType.setRoomTypeID(null);
        newRoomType.onCreate();
        
        assertNotNull(newRoomType.getRoomTypeID());
        assertFalse(newRoomType.getRoomTypeID().isBlank());
    }

    @Test
    void testRoomTypeBlankRoomTypeId() {
        RoomType newRoomType = new RoomType();
        newRoomType.setRoomTypeID("");
        newRoomType.onCreate();
        
        assertNotNull(newRoomType.getRoomTypeID());
        assertFalse(newRoomType.getRoomTypeID().isBlank());
    }

    @Test
    void testRoomTypeMultipleFloors() {
        // Test rooms on different floors
        roomType.setFloor(1); // Ground floor
        assertEquals(1, roomType.getFloor());

        roomType.setFloor(15); // High floor
        assertEquals(15, roomType.getFloor());

        roomType.setFloor(20); // Top floor
        assertEquals(20, roomType.getFloor());
    }

    @Test
    void testRoomTypeVariousCapacities() {
        roomType.setCapacity(1); // Single
        assertEquals(1, roomType.getCapacity());

        roomType.setCapacity(2); // Double
        assertEquals(2, roomType.getCapacity());

        roomType.setCapacity(4); // Family
        assertEquals(4, roomType.getCapacity());

        roomType.setCapacity(6); // Large suite
        assertEquals(6, roomType.getCapacity());
    }

    @Test
    void testRoomTypePriceRange() {
        // Budget room
        roomType.setPrice(200000);
        assertEquals(200000, roomType.getPrice());

        // Mid-range room
        roomType.setPrice(750000);
        assertEquals(750000, roomType.getPrice());

        // Luxury room
        roomType.setPrice(2000000);
        assertEquals(2000000, roomType.getPrice());
    }
}
