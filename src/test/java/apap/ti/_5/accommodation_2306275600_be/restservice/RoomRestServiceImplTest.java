package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.AddRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.room.UpdateRoomRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.room.RoomResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomRestServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @InjectMocks
    private RoomRestServiceImpl roomRestService;

    private Room testRoom;
    private RoomType testRoomType;
    private Property testProperty;
    private String testRoomID;
    private String testRoomTypeID;
    private String testPropertyID;

    @BeforeEach
    void setUp() {
        testPropertyID = "HOT-1234-001";
        testRoomTypeID = "RT-001";
        testRoomID = "HOT-1234-001-101";

        testProperty = Property.builder()
                .propertyID(testPropertyID)
                .propertyName("Test Hotel")
                .type(1)
                .address("Test Address")
                .province(1)
                .description("Test Description")
                .totalRoom(10)
                .activeStatus(1)
                .income(0)
                .ownerName("Test Owner")
                .ownerID(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        testRoomType = RoomType.builder()
                .roomTypeID(testRoomTypeID)
                .name("Deluxe")
                .price(500000)
                .description("Deluxe Room")
                .capacity(2)
                .facility("AC, TV")
                .floor(1)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        testRoom = Room.builder()
                .roomID(testRoomID)
                .name(testRoomID)
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(testRoomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void whenCreateRoom_thenReturnCreatedRoom() {
        // Arrange
        AddRoomRequestDTO requestDTO = AddRoomRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomRepository.findByPropertyIDAndFloor(testPropertyID, 1)).thenReturn(new ArrayList<>());
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // Act
        RoomResponseDTO result = roomRestService.createRoom(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testRoomID, result.getRoomID());
        assertEquals(1, result.getAvailabilityStatus());
        assertEquals("Available", result.getAvailabilityStatusName());
        assertEquals(1, result.getActiveRoom());
        assertEquals("Active", result.getActiveRoomName());
        assertEquals(testRoomTypeID, result.getRoomTypeID());
        assertEquals("Deluxe", result.getRoomTypeName());
        verify(roomTypeRepository, times(1)).findById(testRoomTypeID);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void whenCreateRoomWithRoomTypeNotFound_thenThrowException() {
        // Arrange
        AddRoomRequestDTO requestDTO = AddRoomRequestDTO.builder()
                .roomTypeID("INVALID-RT")
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        when(roomTypeRepository.findById("INVALID-RT")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.createRoom(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Room type not found"));
        verify(roomTypeRepository, times(1)).findById("INVALID-RT");
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void whenCreateRoomWithExistingRoomsOnFloor_thenGenerateCorrectRoomNumber() {
        // Arrange
        Room existingRoom = Room.builder()
                .roomID("HOT-1234-001-101")
                .name("HOT-1234-001-101")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(testRoomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        AddRoomRequestDTO requestDTO = AddRoomRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        Room newRoom = Room.builder()
                .roomID("HOT-1234-001-102")
                .name("HOT-1234-001-102")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(testRoomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(testRoomType));
        when(roomRepository.findByPropertyIDAndFloor(testPropertyID, 1))
                .thenReturn(Collections.singletonList(existingRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(newRoom);

        // Act
        RoomResponseDTO result = roomRestService.createRoom(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoomID().endsWith("-102"));
        verify(roomRepository, times(1)).findByPropertyIDAndFloor(testPropertyID, 1);
    }

    @Test
    void whenGetRoomById_thenReturnRoom() {
        // Arrange
        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));

        // Act
        RoomResponseDTO result = roomRestService.getRoomById(testRoomID);

        // Assert
        assertNotNull(result);
        assertEquals(testRoomID, result.getRoomID());
        assertEquals(testRoomID, result.getName());
        assertEquals(1, result.getAvailabilityStatus());
        assertEquals("Available", result.getAvailabilityStatusName());
        assertEquals(1, result.getActiveRoom());
        assertEquals("Active", result.getActiveRoomName());
        assertEquals(2, result.getCapacity());
        assertEquals(500000, result.getPrice());
        assertEquals(1, result.getFloor());
        verify(roomRepository, times(1)).findById(testRoomID);
    }

    @Test
    void whenGetRoomByIdNotFound_thenThrowException() {
        // Arrange
        when(roomRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.getRoomById("INVALID-ID");
        });

        assertTrue(exception.getMessage().contains("Room not found"));
        verify(roomRepository, times(1)).findById("INVALID-ID");
    }

    @Test
    void whenGetRoomEntityById_thenReturnRoomEntity() {
        // Arrange
        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));

        // Act
        Room result = roomRestService.getRoomEntityById(testRoomID);

        // Assert
        assertNotNull(result);
        assertEquals(testRoomID, result.getRoomID());
        assertEquals(testRoom, result);
        verify(roomRepository, times(1)).findById(testRoomID);
    }

    @Test
    void whenGetRoomEntityByIdNotFound_thenThrowException() {
        // Arrange
        when(roomRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.getRoomEntityById("INVALID-ID");
        });

        assertTrue(exception.getMessage().contains("Room not found"));
        verify(roomRepository, times(1)).findById("INVALID-ID");
    }

    @Test
    void whenGetAllRooms_thenReturnRoomList() {
        // Arrange
        Room room2 = Room.builder()
                .roomID("HOT-1234-001-102")
                .name("HOT-1234-001-102")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(testRoomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        List<Room> rooms = Arrays.asList(testRoom, room2);
        when(roomRepository.findAll()).thenReturn(rooms);

        // Act
        List<RoomResponseDTO> result = roomRestService.getAllRooms();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testRoomID, result.get(0).getRoomID());
        assertEquals("HOT-1234-001-102", result.get(1).getRoomID());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllRoomsEmpty_thenReturnEmptyList() {
        // Arrange
        when(roomRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<RoomResponseDTO> result = roomRestService.getAllRooms();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void whenGetRoomsByRoomType_thenReturnFilteredRooms() {
        // Arrange
        List<Room> rooms = Collections.singletonList(testRoom);
        when(roomRepository.findByRoomType_RoomTypeID(testRoomTypeID)).thenReturn(rooms);

        // Act
        List<RoomResponseDTO> result = roomRestService.getRoomsByRoomType(testRoomTypeID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRoomTypeID, result.get(0).getRoomTypeID());
        verify(roomRepository, times(1)).findByRoomType_RoomTypeID(testRoomTypeID);
    }

    @Test
    void whenGetRoomsByRoomTypeEmpty_thenReturnEmptyList() {
        // Arrange
        when(roomRepository.findByRoomType_RoomTypeID("INVALID-RT")).thenReturn(new ArrayList<>());

        // Act
        List<RoomResponseDTO> result = roomRestService.getRoomsByRoomType("INVALID-RT");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomRepository, times(1)).findByRoomType_RoomTypeID("INVALID-RT");
    }

    @Test
    void whenGetRoomsByPropertyAndFloor_thenReturnFilteredRooms() {
        // Arrange
        List<RoomType> roomTypes = Collections.singletonList(testRoomType);
        List<Room> rooms = Collections.singletonList(testRoom);
        
        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID)).thenReturn(roomTypes);
        when(roomRepository.findByRoomType_RoomTypeID(testRoomTypeID)).thenReturn(rooms);

        // Act
        List<RoomResponseDTO> result = roomRestService.getRoomsByPropertyAndFloor(testPropertyID, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRoomID, result.get(0).getRoomID());
        assertEquals(1, result.get(0).getFloor());
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID(testPropertyID);
        verify(roomRepository, times(1)).findByRoomType_RoomTypeID(testRoomTypeID);
    }

    @Test
    void whenGetRoomsByPropertyAndFloorNotMatching_thenReturnEmptyList() {
        // Arrange
        List<RoomType> roomTypes = Collections.singletonList(testRoomType);
        
        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID)).thenReturn(roomTypes);

        // Act - Looking for floor 2, but only floor 1 exists
        List<RoomResponseDTO> result = roomRestService.getRoomsByPropertyAndFloor(testPropertyID, 2);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomTypeRepository, times(1)).findByProperty_PropertyID(testPropertyID);
        verify(roomRepository, never()).findByRoomType_RoomTypeID(anyString());
    }

    @Test
    void whenGetAvailableRooms_thenReturnOnlyAvailableRooms() {
        // Arrange
        Room availableRoom1 = testRoom;
        Room availableRoom2 = Room.builder()
                .roomID("HOT-1234-001-102")
                .name("HOT-1234-001-102")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(testRoomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        List<Room> availableRooms = Arrays.asList(availableRoom1, availableRoom2);
        when(roomRepository.findByAvailabilityStatus(1)).thenReturn(availableRooms);

        // Act
        List<RoomResponseDTO> result = roomRestService.getAvailableRooms();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getAvailabilityStatus());
        assertEquals(1, result.get(1).getAvailabilityStatus());
        verify(roomRepository, times(1)).findByAvailabilityStatus(1);
    }

    @Test
    void whenGetAvailableRoomsEmpty_thenReturnEmptyList() {
        // Arrange
        when(roomRepository.findByAvailabilityStatus(1)).thenReturn(new ArrayList<>());

        // Act
        List<RoomResponseDTO> result = roomRestService.getAvailableRooms();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomRepository, times(1)).findByAvailabilityStatus(1);
    }

    @Test
    void whenUpdateRoom_thenReturnUpdatedRoom() {
        // Arrange
        UpdateRoomRequestDTO updateDTO = UpdateRoomRequestDTO.builder()
                .name("Updated Room Name")
                .availabilityStatus(0)
                .activeRoom(1)
                .build();

        Room updatedRoom = testRoom.toBuilder()
                .name("Updated Room Name")
                .availabilityStatus(0)
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        // Act
        RoomResponseDTO result = roomRestService.updateRoom(testRoomID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Room Name", result.getName());
        assertEquals(0, result.getAvailabilityStatus());
        assertEquals("Unavailable", result.getAvailabilityStatusName());
        verify(roomRepository, times(1)).findById(testRoomID);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void whenUpdateRoomWithPartialData_thenUpdateOnlyProvidedFields() {
        // Arrange
        UpdateRoomRequestDTO updateDTO = UpdateRoomRequestDTO.builder()
                .availabilityStatus(0)
                .build();

        Room updatedRoom = testRoom.toBuilder()
                .availabilityStatus(0)
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        // Act
        RoomResponseDTO result = roomRestService.updateRoom(testRoomID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testRoomID, result.getName()); // Name unchanged
        assertEquals(0, result.getAvailabilityStatus()); // Status changed
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void whenUpdateRoomWithMaintenance_thenSetMaintenanceAndUnavailable() {
        // Arrange
        LocalDateTime maintenanceStart = LocalDateTime.now().plusDays(1);
        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(3);

        UpdateRoomRequestDTO updateDTO = UpdateRoomRequestDTO.builder()
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .build();

        Room updatedRoom = testRoom.toBuilder()
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .availabilityStatus(0)
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        // Act
        RoomResponseDTO result = roomRestService.updateRoom(testRoomID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(maintenanceStart, result.getMaintenanceStart());
        assertEquals(maintenanceEnd, result.getMaintenanceEnd());
        assertEquals(0, result.getAvailabilityStatus());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void whenUpdateRoomNotFound_thenThrowException() {
        // Arrange
        UpdateRoomRequestDTO updateDTO = UpdateRoomRequestDTO.builder()
                .name("New Name")
                .build();

        when(roomRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.updateRoom("INVALID-ID", updateDTO);
        });

        assertTrue(exception.getMessage().contains("Room not found"));
        verify(roomRepository, times(1)).findById("INVALID-ID");
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void whenDeleteRoomExists_thenDeleteSuccessfully() {
        // Arrange
        when(roomRepository.existsById(testRoomID)).thenReturn(true);
        doNothing().when(roomRepository).deleteById(testRoomID);

        // Act
        assertDoesNotThrow(() -> roomRestService.deleteRoom(testRoomID));

        // Assert
        verify(roomRepository, times(1)).existsById(testRoomID);
        verify(roomRepository, times(1)).deleteById(testRoomID);
    }

    @Test
    void whenDeleteRoomNotFound_thenThrowException() {
        // Arrange
        when(roomRepository.existsById("INVALID-ID")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.deleteRoom("INVALID-ID");
        });

        assertTrue(exception.getMessage().contains("Room not found"));
        verify(roomRepository, times(1)).existsById("INVALID-ID");
        verify(roomRepository, never()).deleteById(anyString());
    }

    @Test
    void whenCreateMaintenance_thenReturnRoomWithMaintenance() {
        // Arrange
        LocalDateTime maintenanceStart = LocalDateTime.now().plusDays(1);
        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(3);

        CreateMaintenanceRequestDTO maintenanceDTO = CreateMaintenanceRequestDTO.builder()
                .roomID(testRoomID)
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .build();

        Room roomWithMaintenance = testRoom.toBuilder()
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(roomWithMaintenance);

        // Act
        RoomResponseDTO result = roomRestService.createMaintenance(maintenanceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(maintenanceStart, result.getMaintenanceStart());
        assertEquals(maintenanceEnd, result.getMaintenanceEnd());
        assertEquals(1, result.getAvailabilityStatus()); // Status remains available
        verify(roomRepository, times(1)).findById(testRoomID);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void whenCreateMaintenanceWithEndBeforeStart_thenThrowException() {
        // Arrange
        LocalDateTime maintenanceStart = LocalDateTime.now().plusDays(3);
        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(1); // End before start

        CreateMaintenanceRequestDTO maintenanceDTO = CreateMaintenanceRequestDTO.builder()
                .roomID(testRoomID)
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .build();

        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.createMaintenance(maintenanceDTO);
        });

        assertTrue(exception.getMessage().contains("Tanggal selesai tidak boleh lebih awal"));
        verify(roomRepository, times(1)).findById(testRoomID);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void whenCreateMaintenanceWithStartBeforeToday_thenThrowException() {
        // Arrange
        LocalDateTime maintenanceStart = LocalDateTime.now().minusDays(1); // Before today
        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(2);

        CreateMaintenanceRequestDTO maintenanceDTO = CreateMaintenanceRequestDTO.builder()
                .roomID(testRoomID)
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .build();

        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.createMaintenance(maintenanceDTO);
        });

        assertTrue(exception.getMessage().contains("Tanggal mulai tidak boleh sebelum hari ini"));
        verify(roomRepository, times(1)).findById(testRoomID);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void whenCreateMaintenanceRoomNotFound_thenThrowException() {
        // Arrange
        LocalDateTime maintenanceStart = LocalDateTime.now().plusDays(1);
        LocalDateTime maintenanceEnd = LocalDateTime.now().plusDays(3);

        CreateMaintenanceRequestDTO maintenanceDTO = CreateMaintenanceRequestDTO.builder()
                .roomID("INVALID-ID")
                .maintenanceStart(maintenanceStart)
                .maintenanceEnd(maintenanceEnd)
                .build();

        when(roomRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomRestService.createMaintenance(maintenanceDTO);
        });

        assertTrue(exception.getMessage().contains("Room not found"));
        verify(roomRepository, times(1)).findById("INVALID-ID");
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void whenConvertRoomWithAvailabilityStatus0_thenReturnUnavailable() {
        // Arrange
        testRoom.setAvailabilityStatus(0);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        // Act
        List<RoomResponseDTO> result = roomRestService.getAllRooms();

        // Assert
        assertEquals("Unavailable", result.get(0).getAvailabilityStatusName());
    }

    @Test
    void whenConvertRoomWithActiveRoom0_thenReturnInactive() {
        // Arrange
        testRoom.setActiveRoom(0);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        // Act
        List<RoomResponseDTO> result = roomRestService.getAllRooms();

        // Assert
        assertEquals("Inactive", result.get(0).getActiveRoomName());
    }

    @Test
    void whenCreateRoomOnFloor2_thenGenerateCorrectRoomID() {
        // Arrange
        RoomType floor2RoomType = testRoomType.toBuilder()
                .floor(2)
                .build();

        AddRoomRequestDTO requestDTO = AddRoomRequestDTO.builder()
                .roomTypeID(testRoomTypeID)
                .availabilityStatus(1)
                .activeRoom(1)
                .build();

        Room floor2Room = Room.builder()
                .roomID("HOT-1234-001-201")
                .name("HOT-1234-001-201")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(floor2RoomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomTypeRepository.findById(testRoomTypeID)).thenReturn(Optional.of(floor2RoomType));
        when(roomRepository.findByPropertyIDAndFloor(testPropertyID, 2)).thenReturn(new ArrayList<>());
        when(roomRepository.save(any(Room.class))).thenReturn(floor2Room);

        // Act
        RoomResponseDTO result = roomRestService.createRoom(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRoomID().contains("-201"));
        assertEquals(2, result.getFloor());
    }

    @Test
    void whenUpdateRoomReplaceMaintenance_thenOldMaintenanceReplaced() {
        // Arrange
        LocalDateTime oldStart = LocalDateTime.now().plusDays(1);
        LocalDateTime oldEnd = LocalDateTime.now().plusDays(2);
        testRoom.setMaintenanceStart(oldStart);
        testRoom.setMaintenanceEnd(oldEnd);

        LocalDateTime newStart = LocalDateTime.now().plusDays(5);
        LocalDateTime newEnd = LocalDateTime.now().plusDays(7);

        UpdateRoomRequestDTO updateDTO = UpdateRoomRequestDTO.builder()
                .maintenanceStart(newStart)
                .maintenanceEnd(newEnd)
                .build();

        Room updatedRoom = testRoom.toBuilder()
                .maintenanceStart(newStart)
                .maintenanceEnd(newEnd)
                .availabilityStatus(0)
                .updatedDate(LocalDateTime.now())
                .build();

        when(roomRepository.findById(testRoomID)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        // Act
        RoomResponseDTO result = roomRestService.updateRoom(testRoomID, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(newStart, result.getMaintenanceStart());
        assertEquals(newEnd, result.getMaintenanceEnd());
        assertNotEquals(oldStart, result.getMaintenanceStart());
        assertNotEquals(oldEnd, result.getMaintenanceEnd());
    }

    @Test
    void whenGetRoomsByPropertyAndFloorWithMultipleRoomTypes_thenReturnAllRoomsOnFloor() {
        // Arrange
        RoomType roomType2 = RoomType.builder()
                .roomTypeID("RT-002")
                .name("Suite")
                .price(800000)
                .description("Suite Room")
                .capacity(4)
                .facility("AC, TV, Mini Bar")
                .floor(1)
                .property(testProperty)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        Room room2 = Room.builder()
                .roomID("HOT-1234-001-102")
                .name("HOT-1234-001-102")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(roomType2)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        List<RoomType> roomTypes = Arrays.asList(testRoomType, roomType2);
        when(roomTypeRepository.findByProperty_PropertyID(testPropertyID)).thenReturn(roomTypes);
        when(roomRepository.findByRoomType_RoomTypeID(testRoomTypeID))
                .thenReturn(Collections.singletonList(testRoom));
        when(roomRepository.findByRoomType_RoomTypeID("RT-002"))
                .thenReturn(Collections.singletonList(room2));

        // Act
        List<RoomResponseDTO> result = roomRestService.getRoomsByPropertyAndFloor(testPropertyID, 1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roomRepository, times(2)).findByRoomType_RoomTypeID(anyString());
    }
}
