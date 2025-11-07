package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    private Property property;
    private RoomType roomType1;
    private RoomType roomType2;
    private Room room1;
    private Room room2;
    private Room room3;

    @BeforeEach
    void setUp() {
        property = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Test Hotel")
                .type(1)
                .activeStatus(1)
                .income(0)
                .ownerName("Owner")
                .ownerID(UUID.randomUUID())
                .build();
        entityManager.persist(property);

        roomType1 = RoomType.builder()
                .roomTypeID("RT-001")
                .name("Deluxe")
                .price(500000)
                .capacity(2)
                .facility("AC, TV, WiFi")
                .floor(5)
                .property(property)
                .build();
        entityManager.persist(roomType1);

        roomType2 = RoomType.builder()
                .roomTypeID("RT-002")
                .name("Suite")
                .price(1000000)
                .capacity(4)
                .facility("AC, TV, WiFi, Jacuzzi")
                .floor(10)
                .property(property)
                .build();
        entityManager.persist(roomType2);

        room1 = Room.builder()
                .roomID("ROOM-001")
                .name("Deluxe 501")
                .availabilityStatus(1) // Available
                .activeRoom(1) // Active
                .roomType(roomType1)
                .build();
        entityManager.persist(room1);

        room2 = Room.builder()
                .roomID("ROOM-002")
                .name("Deluxe 502")
                .availabilityStatus(0) // Not available
                .activeRoom(1) // Active
                .roomType(roomType1)
                .build();
        entityManager.persist(room2);

        room3 = Room.builder()
                .roomID("ROOM-003")
                .name("Suite 1001")
                .availabilityStatus(1) // Available
                .activeRoom(0) // Inactive
                .roomType(roomType2)
                .build();
        entityManager.persist(room3);

        entityManager.flush();
    }

    @Test
    void testFindByRoomType_RoomTypeID() {
        List<Room> rooms = roomRepository.findByRoomType_RoomTypeID("RT-001");

        assertNotNull(rooms);
        assertEquals(2, rooms.size());
        assertTrue(rooms.stream().allMatch(r -> r.getRoomType().getRoomTypeID().equals("RT-001")));
    }

    @Test
    void testFindByRoomType_RoomTypeID_SingleRoom() {
        List<Room> rooms = roomRepository.findByRoomType_RoomTypeID("RT-002");

        assertNotNull(rooms);
        assertEquals(1, rooms.size());
        assertEquals("Suite 1001", rooms.get(0).getName());
    }

    @Test
    void testFindByRoomType_RoomTypeID_NoResults() {
        List<Room> rooms = roomRepository.findByRoomType_RoomTypeID("RT-NONEXISTENT");

        assertNotNull(rooms);
        assertEquals(0, rooms.size());
    }

    @Test
    void testFindByAvailabilityStatus_Available() {
        List<Room> availableRooms = roomRepository.findByAvailabilityStatus(1);

        assertNotNull(availableRooms);
        assertEquals(2, availableRooms.size());
        assertTrue(availableRooms.stream().allMatch(r -> r.getAvailabilityStatus() == 1));
    }

    @Test
    void testFindByAvailabilityStatus_NotAvailable() {
        List<Room> notAvailableRooms = roomRepository.findByAvailabilityStatus(0);

        assertNotNull(notAvailableRooms);
        assertEquals(1, notAvailableRooms.size());
        assertEquals("ROOM-002", notAvailableRooms.get(0).getRoomID());
    }

    @Test
    void testFindByActiveRoom_Active() {
        List<Room> activeRooms = roomRepository.findByActiveRoom(1);

        assertNotNull(activeRooms);
        assertEquals(2, activeRooms.size());
        assertTrue(activeRooms.stream().allMatch(r -> r.getActiveRoom() == 1));
    }

    @Test
    void testFindByActiveRoom_Inactive() {
        List<Room> inactiveRooms = roomRepository.findByActiveRoom(0);

        assertNotNull(inactiveRooms);
        assertEquals(1, inactiveRooms.size());
        assertEquals("ROOM-003", inactiveRooms.get(0).getRoomID());
    }

    @Test
    void testFindByFloorAndRoomTypeID() {
        List<Room> rooms = roomRepository.findByFloorAndRoomTypeID(5, "RT-001");

        assertNotNull(rooms);
        assertEquals(2, rooms.size());
        assertTrue(rooms.stream().allMatch(r -> r.getRoomType().getFloor() == 5));
    }

    @Test
    void testFindByFloorAndRoomTypeID_DifferentFloor() {
        List<Room> rooms = roomRepository.findByFloorAndRoomTypeID(10, "RT-002");

        assertNotNull(rooms);
        assertEquals(1, rooms.size());
        assertEquals("Suite 1001", rooms.get(0).getName());
    }

    @Test
    void testFindByFloorAndRoomTypeID_NoResults() {
        List<Room> rooms = roomRepository.findByFloorAndRoomTypeID(99, "RT-001");

        assertNotNull(rooms);
        assertEquals(0, rooms.size());
    }

    @Test
    void testFindByPropertyIDAndFloor() {
        List<Room> rooms = roomRepository.findByPropertyIDAndFloor("PROP-001", 5);

        assertNotNull(rooms);
        assertEquals(2, rooms.size());
        assertTrue(rooms.stream().allMatch(r -> r.getRoomType().getProperty().getPropertyID().equals("PROP-001")));
    }

    @Test
    void testFindByPropertyIDAndFloor_Floor10() {
        List<Room> rooms = roomRepository.findByPropertyIDAndFloor("PROP-001", 10);

        assertNotNull(rooms);
        assertEquals(1, rooms.size());
        assertEquals("Suite 1001", rooms.get(0).getName());
    }

    @Test
    void testFindByPropertyIDAndFloor_NoResults() {
        List<Room> rooms = roomRepository.findByPropertyIDAndFloor("PROP-NONEXISTENT", 5);

        assertNotNull(rooms);
        assertEquals(0, rooms.size());
    }

    @Test
    void testSaveRoom() {
        Room newRoom = Room.builder()
                .roomID("ROOM-004")
                .name("Deluxe 503")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(roomType1)
                .build();

        Room saved = roomRepository.save(newRoom);

        assertNotNull(saved);
        assertEquals("ROOM-004", saved.getRoomID());
        assertEquals("Deluxe 503", saved.getName());
    }

    @Test
    void testUpdateRoom() {
        Room toUpdate = roomRepository.findById("ROOM-001").orElse(null);
        assertNotNull(toUpdate);

        toUpdate.setAvailabilityStatus(0);
        toUpdate.setName("Updated Room Name");

        Room updated = roomRepository.save(toUpdate);

        assertEquals(0, updated.getAvailabilityStatus());
        assertEquals("Updated Room Name", updated.getName());
    }

    @Test
    void testDeleteRoom() {
        roomRepository.deleteById("ROOM-001");

        assertFalse(roomRepository.findById("ROOM-001").isPresent());
    }

    @Test
    void testFindById() {
        Room found = roomRepository.findById("ROOM-002").orElse(null);

        assertNotNull(found);
        assertEquals("Deluxe 502", found.getName());
        assertEquals(0, found.getAvailabilityStatus());
    }

    @Test
    void testFindById_NotFound() {
        assertFalse(roomRepository.findById("ROOM-NONEXISTENT").isPresent());
    }

    @Test
    void testFindAll() {
        List<Room> allRooms = roomRepository.findAll();

        assertNotNull(allRooms);
        assertTrue(allRooms.size() >= 3);
    }
}

