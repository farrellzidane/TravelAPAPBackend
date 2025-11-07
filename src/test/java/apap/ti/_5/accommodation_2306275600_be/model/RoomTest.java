package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class RoomTest {

    private Room room;
    private RoomType roomType;
    private Property property;

    @BeforeEach
    void setUp() {
        // Setup Property
        property = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Grand Hotel")
                .type(1)
                .activeStatus(1)
                .build();

        // Setup RoomType
        roomType = RoomType.builder()
                .roomTypeID("RT-001")
                .name("Deluxe Room")
                .price(500000)
                .description("Spacious deluxe room")
                .capacity(2)
                .facility("AC, TV, WiFi, Mini Bar")
                .floor(5)
                .property(property)
                .build();

        // Setup Room
        room = Room.builder()
                .roomID("ROOM-001")
                .name("Deluxe 501")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(roomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testRoomCreation() {
        assertNotNull(room);
        assertEquals("ROOM-001", room.getRoomID());
        assertEquals("Deluxe 501", room.getName());
        assertEquals(1, room.getAvailabilityStatus());
        assertEquals(1, room.getActiveRoom());
        assertNotNull(room.getRoomType());
        assertNull(room.getMaintenanceStart());
        assertNull(room.getMaintenanceEnd());
    }

    @Test
    void testRoomBuilder() {
        LocalDateTime maintenanceStart = LocalDateTime.of(2025, 12, 1, 8, 0);
        LocalDateTime maintenanceEnd = LocalDateTime.of(2025, 12, 3, 18, 0);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        Room newRoom = Room.builder()
                .roomID("ROOM-002")
                .name("Suite 301")
                .availabilityStatus(0)
                .activeRoom(1)
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .roomType(roomType)
                .createdDate(created)
                .updatedDate(updated)
                .build();

        assertEquals("ROOM-002", newRoom.getRoomID());
        assertEquals("Suite 301", newRoom.getName());
        assertEquals(0, newRoom.getAvailabilityStatus());
        assertEquals(1, newRoom.getActiveRoom());
        assertEquals(maintenanceStart, newRoom.getMaintenanceStart());
        assertEquals(maintenanceEnd, newRoom.getMaintenanceEnd());
        assertEquals(roomType, newRoom.getRoomType());
        assertEquals(created, newRoom.getCreatedDate());
        assertEquals(updated, newRoom.getUpdatedDate());
    }

    @Test
    void testRoomSettersAndGetters() {
        room.setRoomID("ROOM-UPDATE");
        assertEquals("ROOM-UPDATE", room.getRoomID());

        room.setName("Updated Room Name");
        assertEquals("Updated Room Name", room.getName());

        room.setAvailabilityStatus(0);
        assertEquals(0, room.getAvailabilityStatus());

        room.setActiveRoom(0);
        assertEquals(0, room.getActiveRoom());

        LocalDateTime maintenanceStart = LocalDateTime.now();
        room.setMaintenanceStart(maintenanceStart);
        assertEquals(maintenanceStart, room.getMaintenanceStart());

        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(2);
        room.setMaintenanceEnd(maintenanceEnd);
        assertEquals(maintenanceEnd, room.getMaintenanceEnd());

        RoomType newRoomType = RoomType.builder()
                .roomTypeID("RT-002")
                .name("Suite")
                .build();
        room.setRoomType(newRoomType);
        assertEquals("RT-002", room.getRoomType().getRoomTypeID());

        LocalDateTime newCreated = LocalDateTime.now().minusDays(1);
        room.setCreatedDate(newCreated);
        assertEquals(newCreated, room.getCreatedDate());

        LocalDateTime newUpdated = LocalDateTime.now();
        room.setUpdatedDate(newUpdated);
        assertEquals(newUpdated, room.getUpdatedDate());
    }

    @Test
    void testRoomNoArgsConstructor() {
        Room emptyRoom = new Room();
        assertNotNull(emptyRoom);
        assertNull(emptyRoom.getRoomID());
        assertNull(emptyRoom.getName());
        assertEquals(0, emptyRoom.getAvailabilityStatus());
    }

    @Test
    void testRoomAllArgsConstructor() {
        LocalDateTime maintenanceStart = LocalDateTime.now();
        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(1);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        Room newRoom = new Room(
            "ROOM-003",
            "Test Room",
            1,
            1,
            maintenanceStart,
            maintenanceEnd,
            roomType,
            created,
            updated
        );

        assertEquals("ROOM-003", newRoom.getRoomID());
        assertEquals("Test Room", newRoom.getName());
        assertEquals(1, newRoom.getAvailabilityStatus());
        assertEquals(1, newRoom.getActiveRoom());
        assertEquals(maintenanceStart, newRoom.getMaintenanceStart());
        assertEquals(maintenanceEnd, newRoom.getMaintenanceEnd());
        assertEquals(roomType, newRoom.getRoomType());
        assertEquals(created, newRoom.getCreatedDate());
        assertEquals(updated, newRoom.getUpdatedDate());
    }

    @Test
    void testRoomPrePersist() {
        Room newRoom = new Room();
        newRoom.onCreate();

        assertNotNull(newRoom.getRoomID());
        assertNotNull(newRoom.getCreatedDate());
        assertNotNull(newRoom.getUpdatedDate());
        assertEquals(newRoom.getCreatedDate(), newRoom.getUpdatedDate());
    }

    @Test
    void testRoomPrePersistWithExistingId() {
        Room newRoom = new Room();
        newRoom.setRoomID("EXISTING-ROOM-ID");
        newRoom.onCreate();

        assertEquals("EXISTING-ROOM-ID", newRoom.getRoomID());
        assertNotNull(newRoom.getCreatedDate());
        assertNotNull(newRoom.getUpdatedDate());
    }

    @Test
    void testRoomPreUpdate() throws InterruptedException {
        LocalDateTime originalUpdated = room.getUpdatedDate();
        Thread.sleep(10);
        
        room.onUpdate();
        
        assertNotNull(room.getUpdatedDate());
        assertTrue(room.getUpdatedDate().isAfter(originalUpdated) || 
                   room.getUpdatedDate().isEqual(originalUpdated));
    }

    @Test
    void testRoomAvailabilityStatus() {
        // Available
        room.setAvailabilityStatus(1);
        assertEquals(1, room.getAvailabilityStatus());

        // Not Available
        room.setAvailabilityStatus(0);
        assertEquals(0, room.getAvailabilityStatus());
    }

    @Test
    void testRoomActiveStatus() {
        // Active
        room.setActiveRoom(1);
        assertEquals(1, room.getActiveRoom());

        // Inactive
        room.setActiveRoom(0);
        assertEquals(0, room.getActiveRoom());
    }

    @Test
    void testRoomMaintenanceSchedule() {
        assertNull(room.getMaintenanceStart());
        assertNull(room.getMaintenanceEnd());

        LocalDateTime start = LocalDateTime.of(2025, 12, 10, 8, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 12, 18, 0);

        room.setMaintenanceStart(start);
        room.setMaintenanceEnd(end);

        assertEquals(start, room.getMaintenanceStart());
        assertEquals(end, room.getMaintenanceEnd());
        assertTrue(room.getMaintenanceEnd().isAfter(room.getMaintenanceStart()));
    }

    @Test
    void testRoomTypeRelationship() {
        assertNotNull(room.getRoomType());
        assertEquals("RT-001", room.getRoomType().getRoomTypeID());
        assertEquals("Deluxe Room", room.getRoomType().getName());
        assertEquals(500000, room.getRoomType().getPrice());
        assertEquals(2, room.getRoomType().getCapacity());
    }

    @Test
    void testRoomToBuilder() {
        Room modifiedRoom = room.toBuilder()
                .name("Modified Room")
                .availabilityStatus(0)
                .build();

        assertEquals("Modified Room", modifiedRoom.getName());
        assertEquals(0, modifiedRoom.getAvailabilityStatus());
        assertEquals(room.getRoomID(), modifiedRoom.getRoomID());
        assertEquals(room.getActiveRoom(), modifiedRoom.getActiveRoom());
    }

    @Test
    void testRoomEqualsAndHashCode() {
        Room room1 = Room.builder()
                .roomID("ROOM-SAME")
                .name("Same Room")
                .build();

        Room room2 = Room.builder()
                .roomID("ROOM-SAME")
                .name("Same Room")
                .build();

        Room room3 = Room.builder()
                .roomID("ROOM-DIFFERENT")
                .name("Different Room")
                .build();

        assertEquals(room1, room2);
        assertNotEquals(room1, room3);
        assertEquals(room1.hashCode(), room2.hashCode());
    }

    @Test
    void testRoomToString() {
        String roomString = room.toString();
        assertNotNull(roomString);
        assertTrue(roomString.contains("ROOM-001"));
        assertTrue(roomString.contains("Deluxe 501"));
    }

    @Test
    void testRoomWithoutMaintenance() {
        assertNull(room.getMaintenanceStart());
        assertNull(room.getMaintenanceEnd());

        // Room should be available for booking
        room.setAvailabilityStatus(1);
        assertEquals(1, room.getAvailabilityStatus());
    }

    @Test
    void testRoomUnderMaintenance() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(3);

        room.setMaintenanceStart(start);
        room.setMaintenanceEnd(end);
        room.setAvailabilityStatus(0); // Not available during maintenance

        assertEquals(0, room.getAvailabilityStatus());
        assertNotNull(room.getMaintenanceStart());
        assertNotNull(room.getMaintenanceEnd());
    }

    @Test
    void testRoomNullRoomId() {
        Room newRoom = new Room();
        newRoom.setRoomID(null);
        newRoom.onCreate();
        
        assertNotNull(newRoom.getRoomID());
        assertFalse(newRoom.getRoomID().isBlank());
    }

    @Test
    void testRoomBlankRoomId() {
        Room newRoom = new Room();
        newRoom.setRoomID("");
        newRoom.onCreate();
        
        assertNotNull(newRoom.getRoomID());
        assertFalse(newRoom.getRoomID().isBlank());
    }

    @Test
    void testRoomNameUpdate() {
        room.setName("Presidential Suite 1001");
        assertEquals("Presidential Suite 1001", room.getName());
    }

    @Test
    void testRoomMultipleStatusChanges() {
        // Available -> Under Maintenance -> Available
        room.setAvailabilityStatus(1);
        assertEquals(1, room.getAvailabilityStatus());

        room.setAvailabilityStatus(0);
        room.setMaintenanceStart(LocalDateTime.now());
        room.setMaintenanceEnd(LocalDateTime.now().plusDays(1));
        assertEquals(0, room.getAvailabilityStatus());

        room.setAvailabilityStatus(1);
        room.setMaintenanceStart(null);
        room.setMaintenanceEnd(null);
        assertEquals(1, room.getAvailabilityStatus());
        assertNull(room.getMaintenanceStart());
        assertNull(room.getMaintenanceEnd());
    }

    @Test
    void testRoomDeactivation() {
        assertEquals(1, room.getActiveRoom());
        
        // Deactivate room
        room.setActiveRoom(0);
        assertEquals(0, room.getActiveRoom());
        
        // Reactivate room
        room.setActiveRoom(1);
        assertEquals(1, room.getActiveRoom());
    }
}
