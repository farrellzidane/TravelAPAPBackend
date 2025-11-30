package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room room;
    private UUID testRoomId;
    private RoomType testRoomType;

    @BeforeEach
    void setUp() {
        testRoomId = UUID.randomUUID();
        
        testRoomType = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Deluxe Room")
                .price(500000)
                .capacity(2)
                .build();
        
        room = Room.builder()
                .roomID(testRoomId)
                .name("Room-101")
                .availabilityStatus(1)
                .activeRoom(1)
                .maintenanceStart(null)
                .maintenanceEnd(null)
                .roomType(testRoomType)
                .build();
    }

    @Test
    void testRoomBuilder() {
        assertNotNull(room);
        assertEquals(testRoomId, room.getRoomID());
        assertEquals("Room-101", room.getName());
        assertEquals(1, room.getAvailabilityStatus());
        assertEquals(1, room.getActiveRoom());
        assertNull(room.getMaintenanceStart());
        assertNull(room.getMaintenanceEnd());
        assertEquals(testRoomType, room.getRoomType());
    }

    @Test
    void testNoArgsConstructor() {
        Room emptyRoom = new Room();
        assertNotNull(emptyRoom);
        assertNull(emptyRoom.getRoomID());
        assertNull(emptyRoom.getName());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maintenanceStart = now.plusDays(1);
        LocalDateTime maintenanceEnd = now.plusDays(3);
        
        Room fullRoom = new Room(
                testRoomId,
                "Room-202",
                0,
                1,
                maintenanceStart,
                maintenanceEnd,
                testRoomType,
                now,
                now
        );
        
        assertEquals(testRoomId, fullRoom.getRoomID());
        assertEquals("Room-202", fullRoom.getName());
        assertEquals(0, fullRoom.getAvailabilityStatus());
        assertEquals(1, fullRoom.getActiveRoom());
        assertEquals(maintenanceStart, fullRoom.getMaintenanceStart());
        assertEquals(maintenanceEnd, fullRoom.getMaintenanceEnd());
        assertEquals(testRoomType, fullRoom.getRoomType());
        assertEquals(now, fullRoom.getCreatedDate());
        assertEquals(now, fullRoom.getUpdatedDate());
    }

    @Test
    void testSettersAndGetters() {
        room.setName("Room-303");
        assertEquals("Room-303", room.getName());
        
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
    }

    @Test
    void testRoomTypeRelationship() {
        RoomType newRoomType = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Suite Room")
                .price(1000000)
                .capacity(4)
                .build();
        
        room.setRoomType(newRoomType);
        
        assertNotNull(room.getRoomType());
        assertEquals("Suite Room", room.getRoomType().getName());
        assertEquals(1000000, room.getRoomType().getPrice());
    }

    @Test
    void testOnCreate() {
        Room newRoom = new Room();
        assertNull(newRoom.getRoomID());
        assertNull(newRoom.getName());
        assertNull(newRoom.getCreatedDate());
        assertNull(newRoom.getUpdatedDate());
        
        // Simulate @PrePersist
        newRoom.onCreate();
        
        assertNotNull(newRoom.getRoomID());
        assertNotNull(newRoom.getName());
        assertEquals(newRoom.getRoomID().toString(), newRoom.getName());
        assertNotNull(newRoom.getCreatedDate());
        assertNotNull(newRoom.getUpdatedDate());
        assertEquals(newRoom.getCreatedDate(), newRoom.getUpdatedDate());
    }

    @Test
    void testOnCreateWithExistingId() {
        Room newRoom = new Room();
        UUID existingId = UUID.randomUUID();
        newRoom.setRoomID(existingId);
        
        newRoom.onCreate();
        
        // Should not change existing ID
        assertEquals(existingId, newRoom.getRoomID());
        assertEquals(existingId.toString(), newRoom.getName());
        assertNotNull(newRoom.getCreatedDate());
    }

    @Test
    void testOnCreateWithExistingName() {
        Room newRoom = new Room();
        newRoom.setName("Custom-Name");
        
        newRoom.onCreate();
        
        // Should not change existing name
        assertEquals("Custom-Name", newRoom.getName());
        assertNotNull(newRoom.getRoomID());
    }

    @Test
    void testOnUpdate() {
        room.onCreate();
        LocalDateTime originalCreatedDate = room.getCreatedDate();
        LocalDateTime originalUpdatedDate = room.getUpdatedDate();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        room.onUpdate();
        
        assertEquals(originalCreatedDate, room.getCreatedDate());
        assertNotEquals(originalUpdatedDate, room.getUpdatedDate());
        assertTrue(room.getUpdatedDate().isAfter(originalUpdatedDate));
    }

    @Test
    void testAvailabilityStatuses() {
        // Test availability status: 0 = Not Available, 1 = Available
        room.setAvailabilityStatus(0);
        assertEquals(0, room.getAvailabilityStatus());
        
        room.setAvailabilityStatus(1);
        assertEquals(1, room.getAvailabilityStatus());
    }

    @Test
    void testActiveRoomStatuses() {
        // Test active room: 0 = Inactive, 1 = Active
        room.setActiveRoom(0);
        assertEquals(0, room.getActiveRoom());
        
        room.setActiveRoom(1);
        assertEquals(1, room.getActiveRoom());
    }

    @Test
    void testMaintenancePeriod() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        
        room.setMaintenanceStart(start);
        room.setMaintenanceEnd(end);
        
        assertNotNull(room.getMaintenanceStart());
        assertNotNull(room.getMaintenanceEnd());
        assertTrue(room.getMaintenanceEnd().isAfter(room.getMaintenanceStart()));
    }

    @Test
    void testToBuilder() {
        Room clonedRoom = room.toBuilder()
                .name("Room-404")
                .build();
        
        assertEquals(testRoomId, clonedRoom.getRoomID());
        assertEquals("Room-404", clonedRoom.getName());
        assertEquals(room.getAvailabilityStatus(), clonedRoom.getAvailabilityStatus());
        assertEquals(room.getActiveRoom(), clonedRoom.getActiveRoom());
    }

    @Test
    void testEqualsAndHashCode() {
        Room sameRoom = Room.builder()
                .roomID(testRoomId)
                .name("Room-101")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(testRoomType)
                .build();
        
        assertEquals(room, sameRoom);
        assertEquals(room.hashCode(), sameRoom.hashCode());
    }

    @Test
    void testNotEquals() {
        Room differentRoom = Room.builder()
                .roomID(UUID.randomUUID())
                .name("Room-999")
                .build();
        
        assertNotEquals(room, differentRoom);
    }

    @Test
    void testToString() {
        String roomString = room.toString();
        assertNotNull(roomString);
        assertTrue(roomString.contains("Room-101"));
        assertTrue(roomString.contains(testRoomId.toString()));
    }

    @Test
    void testNullValues() {
        Room nullRoom = Room.builder().build();
        
        assertNull(nullRoom.getRoomID());
        assertNull(nullRoom.getName());
        assertNull(nullRoom.getMaintenanceStart());
        assertNull(nullRoom.getMaintenanceEnd());
        assertNull(nullRoom.getRoomType());
    }

    @Test
    void testDateTimeFields() {
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedDate = LocalDateTime.now();
        
        room.setCreatedDate(createdDate);
        room.setUpdatedDate(updatedDate);
        
        assertEquals(createdDate, room.getCreatedDate());
        assertEquals(updatedDate, room.getUpdatedDate());
        assertTrue(room.getUpdatedDate().isAfter(room.getCreatedDate()));
    }

    @Test
    void testRoomInMaintenance() {
        // Room in maintenance
        room.setAvailabilityStatus(0);
        room.setMaintenanceStart(LocalDateTime.now());
        room.setMaintenanceEnd(LocalDateTime.now().plusDays(3));
        
        assertEquals(0, room.getAvailabilityStatus());
        assertNotNull(room.getMaintenanceStart());
        assertNotNull(room.getMaintenanceEnd());
    }

    @Test
    void testRoomAvailable() {
        // Room available (not in maintenance)
        room.setAvailabilityStatus(1);
        room.setMaintenanceStart(null);
        room.setMaintenanceEnd(null);
        
        assertEquals(1, room.getAvailabilityStatus());
        assertNull(room.getMaintenanceStart());
        assertNull(room.getMaintenanceEnd());
    }

    @Test
    void testInactiveRoom() {
        room.setActiveRoom(0);
        room.setAvailabilityStatus(0);
        
        assertEquals(0, room.getActiveRoom());
        assertEquals(0, room.getAvailabilityStatus());
    }

    @Test
    void testRoomNameGeneration() {
        Room autoNamedRoom = new Room();
        autoNamedRoom.onCreate();
        
        assertNotNull(autoNamedRoom.getName());
        assertEquals(autoNamedRoom.getRoomID().toString(), autoNamedRoom.getName());
    }
}
