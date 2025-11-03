package apap.ti._5.accommodation_2306275600_be.repository;

import java.util.List;

import apap.ti._5.accommodation_2306275600_be.model.Room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    List<Room> findByRoomType_RoomTypeID(String roomTypeID);
    List<Room> findByAvailabilityStatus(int status);
    List<Room> findByActiveRoom(int activeRoom);
    @Query("SELECT r FROM Room r WHERE r.roomType.floor = :floor AND r.roomType.roomTypeID = :roomTypeID")
    List<Room> findByFloorAndRoomTypeID(@Param("floor") Integer floor, @Param("roomTypeID") String roomTypeID);
    @Query("SELECT r FROM Room r WHERE r.roomType.property.propertyID = :propertyID AND r.roomType.floor = :floor")
    List<Room> findByPropertyIDAndFloor(@Param("propertyID") String propertyID, @Param("floor") Integer floor);
}