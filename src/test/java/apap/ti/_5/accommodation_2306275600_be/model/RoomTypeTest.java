package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoomTypeTest {

    private RoomType roomType;
    private UUID testRoomTypeId;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        testRoomTypeId = UUID.randomUUID();
        
        testProperty = Property.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("Test Hotel")
                .type(1)
                .build();
        
        roomType = RoomType.builder()
                .roomTypeID(testRoomTypeId)
                .name("Deluxe Room")
                .price(500000)
                .description("Spacious deluxe room with city view")
                .capacity(2)
                .facility("WiFi, AC, TV, Mini Bar")
                .floor(3)
                .property(testProperty)
                .listRoom(new ArrayList<>())
                .build();
    }

    @Test
    void testRoomTypeBuilder() {
        assertNotNull(roomType);
        assertEquals(testRoomTypeId, roomType.getRoomTypeID());
        assertEquals("Deluxe Room", roomType.getName());
        assertEquals(500000, roomType.getPrice());
        assertEquals("Spacious deluxe room with city view", roomType.getDescription());
        assertEquals(2, roomType.getCapacity());
        assertEquals("WiFi, AC, TV, Mini Bar", roomType.getFacility());
        assertEquals(3, roomType.getFloor());
        assertEquals(testProperty, roomType.getProperty());
        assertNotNull(roomType.getListRoom());
    }

    @Test
    void testNoArgsConstructor() {
        RoomType emptyRoomType = new RoomType();
        assertNotNull(emptyRoomType);
        assertNull(emptyRoomType.getRoomTypeID());
        assertNull(emptyRoomType.getName());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<Room> rooms = new ArrayList<>();
        
        RoomType fullRoomType = new RoomType(
                testRoomTypeId,
                "Suite Room",
                1000000,
                "Luxurious suite room",
                4,
                "WiFi, AC, TV, Mini Bar, Jacuzzi",
                5,
                testProperty,
                rooms,
                now,
                now
        );
        
        assertEquals(testRoomTypeId, fullRoomType.getRoomTypeID());
        assertEquals("Suite Room", fullRoomType.getName());
        assertEquals(1000000, fullRoomType.getPrice());
        assertEquals("Luxurious suite room", fullRoomType.getDescription());
        assertEquals(4, fullRoomType.getCapacity());
        assertEquals("WiFi, AC, TV, Mini Bar, Jacuzzi", fullRoomType.getFacility());
        assertEquals(5, fullRoomType.getFloor());
        assertEquals(testProperty, fullRoomType.getProperty());
        assertEquals(now, fullRoomType.getCreatedDate());
        assertEquals(now, fullRoomType.getUpdatedDate());
    }

    @Test
    void testSettersAndGetters() {
        roomType.setName("Superior Room");
        assertEquals("Superior Room", roomType.getName());
        
        roomType.setPrice(750000);
        assertEquals(750000, roomType.getPrice());
        
        roomType.setDescription("Updated description");
        assertEquals("Updated description", roomType.getDescription());
        
        roomType.setCapacity(3);
        assertEquals(3, roomType.getCapacity());
        
        roomType.setFacility("WiFi, AC");
        assertEquals("WiFi, AC", roomType.getFacility());
        
        roomType.setFloor(4);
        assertEquals(4, roomType.getFloor());
    }

    @Test
    void testPropertyRelationship() {
        Property newProperty = Property.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("New Hotel")
                .build();
        
        roomType.setProperty(newProperty);
        
        assertNotNull(roomType.getProperty());
        assertEquals("New Hotel", roomType.getProperty().getPropertyName());
    }

    @Test
    void testRoomListRelationship() {
        List<Room> rooms = new ArrayList<>();
        Room room1 = Room.builder()
                .roomID(UUID.randomUUID())
                .name("Room-101")
                .availabilityStatus(1)
                .build();
        Room room2 = Room.builder()
                .roomID(UUID.randomUUID())
                .name("Room-102")
                .availabilityStatus(1)
                .build();
        
        rooms.add(room1);
        rooms.add(room2);
        
        roomType.setListRoom(rooms);
        
        assertNotNull(roomType.getListRoom());
        assertEquals(2, roomType.getListRoom().size());
        assertEquals("Room-101", roomType.getListRoom().get(0).getName());
        assertEquals("Room-102", roomType.getListRoom().get(1).getName());
    }

    @Test
    void testOnCreate() {
        RoomType newRoomType = new RoomType();
        assertNull(newRoomType.getRoomTypeID());
        assertNull(newRoomType.getCreatedDate());
        assertNull(newRoomType.getUpdatedDate());
        
        // Simulate @PrePersist
        newRoomType.onCreate();
        
        assertNotNull(newRoomType.getRoomTypeID());
        assertNotNull(newRoomType.getCreatedDate());
        assertNotNull(newRoomType.getUpdatedDate());
        assertEquals(newRoomType.getCreatedDate(), newRoomType.getUpdatedDate());
    }

    @Test
    void testOnCreateWithExistingId() {
        RoomType newRoomType = new RoomType();
        UUID existingId = UUID.randomUUID();
        newRoomType.setRoomTypeID(existingId);
        
        newRoomType.onCreate();
        
        // Should not change existing ID
        assertEquals(existingId, newRoomType.getRoomTypeID());
        assertNotNull(newRoomType.getCreatedDate());
    }

    @Test
    void testOnUpdate() {
        roomType.onCreate();
        LocalDateTime originalCreatedDate = roomType.getCreatedDate();
        LocalDateTime originalUpdatedDate = roomType.getUpdatedDate();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        roomType.onUpdate();
        
        assertEquals(originalCreatedDate, roomType.getCreatedDate());
        assertNotEquals(originalUpdatedDate, roomType.getUpdatedDate());
        assertTrue(roomType.getUpdatedDate().isAfter(originalUpdatedDate));
    }

    @Test
    void testToBuilder() {
        RoomType clonedRoomType = roomType.toBuilder()
                .name("Premium Room")
                .price(800000)
                .build();
        
        assertEquals(testRoomTypeId, clonedRoomType.getRoomTypeID());
        assertEquals("Premium Room", clonedRoomType.getName());
        assertEquals(800000, clonedRoomType.getPrice());
        assertEquals(roomType.getCapacity(), clonedRoomType.getCapacity());
    }

    @Test
    void testEqualsAndHashCode() {
        RoomType sameRoomType = RoomType.builder()
                .roomTypeID(testRoomTypeId)
                .name("Deluxe Room")
                .price(500000)
                .description("Spacious deluxe room with city view")
                .capacity(2)
                .facility("WiFi, AC, TV, Mini Bar")
                .floor(3)
                .property(testProperty)
                .listRoom(new ArrayList<>())
                .build();
        
        assertEquals(roomType, sameRoomType);
        assertEquals(roomType.hashCode(), sameRoomType.hashCode());
    }

    @Test
    void testNotEquals() {
        RoomType differentRoomType = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Different Room")
                .build();
        
        assertNotEquals(roomType, differentRoomType);
    }

    @Test
    void testToString() {
        String roomTypeString = roomType.toString();
        assertNotNull(roomTypeString);
        assertTrue(roomTypeString.contains("Deluxe Room"));
        assertTrue(roomTypeString.contains(testRoomTypeId.toString()));
    }

    @Test
    void testNullValues() {
        RoomType nullRoomType = RoomType.builder().build();
        
        assertNull(nullRoomType.getRoomTypeID());
        assertNull(nullRoomType.getName());
        assertNull(nullRoomType.getDescription());
        assertNull(nullRoomType.getFacility());
        assertNull(nullRoomType.getProperty());
    }

    @Test
    void testDefaultListRoom() {
        RoomType defaultRoomType = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Standard Room")
                .build();
        
        // Should have default empty ArrayList
        assertNotNull(defaultRoomType.getListRoom());
        assertTrue(defaultRoomType.getListRoom().isEmpty());
    }

    @Test
    void testPriceRange() {
        int[] prices = {100000, 500000, 1000000, 2000000, 5000000};
        for (int price : prices) {
            roomType.setPrice(price);
            assertEquals(price, roomType.getPrice());
        }
    }

    @Test
    void testCapacityRange() {
        int[] capacities = {1, 2, 3, 4, 6, 8};
        for (int capacity : capacities) {
            roomType.setCapacity(capacity);
            assertEquals(capacity, roomType.getCapacity());
        }
    }

    @Test
    void testFloorRange() {
        int[] floors = {1, 2, 3, 5, 10, 15};
        for (int floor : floors) {
            roomType.setFloor(floor);
            assertEquals(floor, roomType.getFloor());
        }
    }

    @Test
    void testDateTimeFields() {
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedDate = LocalDateTime.now();
        
        roomType.setCreatedDate(createdDate);
        roomType.setUpdatedDate(updatedDate);
        
        assertEquals(createdDate, roomType.getCreatedDate());
        assertEquals(updatedDate, roomType.getUpdatedDate());
        assertTrue(roomType.getUpdatedDate().isAfter(roomType.getCreatedDate()));
    }

    @Test
    void testLongDescription() {
        String longDescription = "This is a very long description for the room type. ".repeat(10);
        roomType.setDescription(longDescription);
        assertEquals(longDescription, roomType.getDescription());
    }

    @Test
    void testMultipleFacilities() {
        String facilities = "WiFi, AC, TV, Mini Bar, Safe Box, Hair Dryer, Bathtub, Shower, Balcony, City View";
        roomType.setFacility(facilities);
        assertEquals(facilities, roomType.getFacility());
        assertTrue(roomType.getFacility().contains("WiFi"));
        assertTrue(roomType.getFacility().contains("City View"));
    }

    @Test
    void testEmptyRoomList() {
        roomType.setListRoom(new ArrayList<>());
        assertNotNull(roomType.getListRoom());
        assertTrue(roomType.getListRoom().isEmpty());
    }

    @Test
    void testAddRoomToList() {
        Room newRoom = Room.builder()
                .roomID(UUID.randomUUID())
                .name("Room-201")
                .roomType(roomType)
                .build();
        
        roomType.getListRoom().add(newRoom);
        
        assertEquals(1, roomType.getListRoom().size());
        assertEquals("Room-201", roomType.getListRoom().get(0).getName());
    }

    @Test
    void testRoomTypeWithNoProperty() {
        RoomType orphanRoomType = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Orphan Room Type")
                .price(300000)
                .build();
        
        assertNull(orphanRoomType.getProperty());
    }
}
