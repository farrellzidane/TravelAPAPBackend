package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Property;
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
class RoomTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private Property property1;
    private Property property2;
    private RoomType roomType1;
    private RoomType roomType2;
    private RoomType roomType3;

    @BeforeEach
    void setUp() {
        property1 = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Hotel A")
                .type(1)
                .activeStatus(1)
                .income(0)
                .ownerName("Owner One")
                .ownerID(UUID.randomUUID())
                .build();
        entityManager.persist(property1);

        property2 = Property.builder()
                .propertyID("PROP-002")
                .propertyName("Hotel B")
                .type(1)
                .activeStatus(1)
                .income(0)
                .ownerName("Owner Two")
                .ownerID(UUID.randomUUID())
                .build();
        entityManager.persist(property2);

        roomType1 = RoomType.builder()
                .roomTypeID("RT-001")
                .name("Deluxe")
                .price(500000)
                .description("Comfortable deluxe room")
                .capacity(2)
                .facility("AC, TV, WiFi")
                .floor(5)
                .property(property1)
                .build();
        entityManager.persist(roomType1);

        roomType2 = RoomType.builder()
                .roomTypeID("RT-002")
                .name("Suite")
                .price(1000000)
                .description("Luxurious suite")
                .capacity(4)
                .facility("AC, TV, WiFi, Jacuzzi, Kitchen")
                .floor(10)
                .property(property1)
                .build();
        entityManager.persist(roomType2);

        roomType3 = RoomType.builder()
                .roomTypeID("RT-003")
                .name("Standard")
                .price(300000)
                .description("Basic standard room")
                .capacity(2)
                .facility("AC, TV")
                .floor(3)
                .property(property2)
                .build();
        entityManager.persist(roomType3);

        entityManager.flush();
    }

    @Test
    void testFindByProperty_PropertyID() {
        List<RoomType> roomTypes = roomTypeRepository.findByProperty_PropertyID("PROP-001");

        assertNotNull(roomTypes);
        assertEquals(2, roomTypes.size());
        assertTrue(roomTypes.stream().allMatch(rt -> rt.getProperty().getPropertyID().equals("PROP-001")));
    }

    @Test
    void testFindByProperty_PropertyID_SingleRoomType() {
        List<RoomType> roomTypes = roomTypeRepository.findByProperty_PropertyID("PROP-002");

        assertNotNull(roomTypes);
        assertEquals(1, roomTypes.size());
        assertEquals("Standard", roomTypes.get(0).getName());
    }

    @Test
    void testFindByProperty_PropertyID_NoResults() {
        List<RoomType> roomTypes = roomTypeRepository.findByProperty_PropertyID("PROP-NONEXISTENT");

        assertNotNull(roomTypes);
        assertEquals(0, roomTypes.size());
    }

    @Test
    void testSaveRoomType() {
        RoomType newRoomType = RoomType.builder()
                .roomTypeID("RT-004")
                .name("Presidential Suite")
                .price(2500000)
                .description("The most luxurious suite")
                .capacity(6)
                .facility("AC, TV, WiFi, Jacuzzi, Kitchen, Private Pool")
                .floor(15)
                .property(property1)
                .build();

        RoomType saved = roomTypeRepository.save(newRoomType);

        assertNotNull(saved);
        assertEquals("RT-004", saved.getRoomTypeID());
        assertEquals("Presidential Suite", saved.getName());
        assertEquals(2500000, saved.getPrice());
    }

    @Test
    void testUpdateRoomType() {
        RoomType toUpdate = roomTypeRepository.findById("RT-001").orElse(null);
        assertNotNull(toUpdate);

        toUpdate.setPrice(550000);
        toUpdate.setFacility("AC, TV, WiFi, Mini Bar");

        RoomType updated = roomTypeRepository.save(toUpdate);

        assertEquals(550000, updated.getPrice());
        assertEquals("AC, TV, WiFi, Mini Bar", updated.getFacility());
    }

    @Test
    void testDeleteRoomType() {
        roomTypeRepository.deleteById("RT-001");

        assertFalse(roomTypeRepository.findById("RT-001").isPresent());
    }

    @Test
    void testFindById() {
        RoomType found = roomTypeRepository.findById("RT-002").orElse(null);

        assertNotNull(found);
        assertEquals("Suite", found.getName());
        assertEquals(1000000, found.getPrice());
        assertEquals(4, found.getCapacity());
    }

    @Test
    void testFindById_NotFound() {
        assertFalse(roomTypeRepository.findById("RT-NONEXISTENT").isPresent());
    }

    @Test
    void testFindAll() {
        List<RoomType> allRoomTypes = roomTypeRepository.findAll();

        assertNotNull(allRoomTypes);
        assertTrue(allRoomTypes.size() >= 3);
    }

    @Test
    void testRoomTypeProperties() {
        RoomType roomType = roomTypeRepository.findById("RT-001").orElse(null);

        assertNotNull(roomType);
        assertEquals("Deluxe", roomType.getName());
        assertEquals(500000, roomType.getPrice());
        assertEquals("Comfortable deluxe room", roomType.getDescription());
        assertEquals(2, roomType.getCapacity());
        assertEquals("AC, TV, WiFi", roomType.getFacility());
        assertEquals(5, roomType.getFloor());
        assertNotNull(roomType.getProperty());
        assertEquals("PROP-001", roomType.getProperty().getPropertyID());
    }

    @Test
    void testMultipleRoomTypesForSameProperty() {
        List<RoomType> roomTypes = roomTypeRepository.findByProperty_PropertyID("PROP-001");

        assertEquals(2, roomTypes.size());
        assertTrue(roomTypes.stream().anyMatch(rt -> rt.getName().equals("Deluxe")));
        assertTrue(roomTypes.stream().anyMatch(rt -> rt.getName().equals("Suite")));
    }
}

