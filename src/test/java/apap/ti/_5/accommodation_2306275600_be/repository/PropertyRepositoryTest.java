package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PropertyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PropertyRepository propertyRepository;

    private UUID ownerID1;
    private UUID ownerID2;
    private Property property1;
    private Property property2;
    private Property property3;

    @BeforeEach
    void setUp() {
        ownerID1 = UUID.randomUUID();
        ownerID2 = UUID.randomUUID();

        property1 = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Hotel A")
                .type(1) // Hotel
                .activeStatus(1) // Active
                .income(0)
                .ownerName("Owner One")
                .ownerID(ownerID1)
                .build();
        entityManager.persist(property1);

        property2 = Property.builder()
                .propertyID("PROP-002")
                .propertyName("Hotel B")
                .type(1) // Hotel
                .activeStatus(0) // Inactive
                .income(0)
                .ownerName("Owner One")
                .ownerID(ownerID1)
                .build();
        entityManager.persist(property2);

        property3 = Property.builder()
                .propertyID("PROP-003")
                .propertyName("Villa C")
                .type(2) // Villa
                .activeStatus(1) // Active
                .income(0)
                .ownerName("Owner Two")
                .ownerID(ownerID2)
                .build();
        entityManager.persist(property3);

        entityManager.flush();
    }

    @Test
    void testFindByOwnerID() {
        List<Property> properties = propertyRepository.findByOwnerID(ownerID1);
        
        assertNotNull(properties);
        assertEquals(2, properties.size());
        assertTrue(properties.stream().allMatch(p -> p.getOwnerID().equals(ownerID1)));
    }

    @Test
    void testFindByOwnerID_SingleProperty() {
        List<Property> properties = propertyRepository.findByOwnerID(ownerID2);
        
        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertEquals("Villa C", properties.get(0).getPropertyName());
    }

    @Test
    void testFindByOwnerID_NoResults() {
        UUID nonExistentOwner = UUID.randomUUID();
        List<Property> properties = propertyRepository.findByOwnerID(nonExistentOwner);
        
        assertNotNull(properties);
        assertEquals(0, properties.size());
    }

    @Test
    void testFindByActiveStatus() {
        List<Property> activeProperties = propertyRepository.findByActiveStatus(1);
        
        assertNotNull(activeProperties);
        assertEquals(2, activeProperties.size());
        assertTrue(activeProperties.stream().allMatch(p -> p.getActiveStatus() == 1));
    }

    @Test
    void testFindByActiveStatus_Inactive() {
        List<Property> inactiveProperties = propertyRepository.findByActiveStatus(0);
        
        assertNotNull(inactiveProperties);
        assertEquals(1, inactiveProperties.size());
        assertEquals("Hotel B", inactiveProperties.get(0).getPropertyName());
    }

    @Test
    void testFindByType() {
        List<Property> hotels = propertyRepository.findByType(1);
        
        assertNotNull(hotels);
        assertEquals(2, hotels.size());
        assertTrue(hotels.stream().allMatch(p -> p.getType() == 1));
    }

    @Test
    void testFindByType_Villa() {
        List<Property> villas = propertyRepository.findByType(2);
        
        assertNotNull(villas);
        assertEquals(1, villas.size());
        assertEquals("Villa C", villas.get(0).getPropertyName());
    }

    @Test
    void testSaveProperty() {
        Property newProperty = Property.builder()
                .propertyID("PROP-004")
                .propertyName("Resort D")
                .type(3)
                .activeStatus(1)
                .income(0)
                .ownerName("Owner Three")
                .ownerID(UUID.randomUUID())
                .build();

        Property saved = propertyRepository.save(newProperty);

        assertNotNull(saved);
        assertEquals("PROP-004", saved.getPropertyID());
        assertEquals("Resort D", saved.getPropertyName());
    }

    @Test
    void testUpdateProperty() {
        Property toUpdate = propertyRepository.findById("PROP-001").orElse(null);
        assertNotNull(toUpdate);

        toUpdate.setIncome(5000000);
        toUpdate.setActiveStatus(0);

        Property updated = propertyRepository.save(toUpdate);

        assertEquals(5000000, updated.getIncome());
        assertEquals(0, updated.getActiveStatus());
    }

    @Test
    void testDeleteProperty() {
        propertyRepository.deleteById("PROP-001");

        assertFalse(propertyRepository.findById("PROP-001").isPresent());
    }

    @Test
    void testFindById() {
        Property found = propertyRepository.findById("PROP-002").orElse(null);

        assertNotNull(found);
        assertEquals("Hotel B", found.getPropertyName());
        assertEquals(0, found.getActiveStatus());
    }

    @Test
    void testFindById_NotFound() {
        assertFalse(propertyRepository.findById("PROP-NONEXISTENT").isPresent());
    }

    @Test
    void testFindAll() {
        List<Property> allProperties = propertyRepository.findAll();

        assertNotNull(allProperties);
        assertTrue(allProperties.size() >= 3);
    }
}

