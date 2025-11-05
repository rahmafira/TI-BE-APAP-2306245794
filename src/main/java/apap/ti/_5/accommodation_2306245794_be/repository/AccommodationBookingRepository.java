package apap.ti._5.accommodation_2306245794_be.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import apap.ti._5.accommodation_2306245794_be.model.AccommodationBooking;

@Repository
public interface AccommodationBookingRepository extends JpaRepository<AccommodationBooking, String> {
    @Query("SELECT COUNT(b) FROM AccommodationBooking b " +
           "WHERE b.room.roomType.property.propertyId = :propertyId " +
           "AND b.checkInDate >= :currentDate")
    long countFutureBookingsByPropertyId(String propertyId, LocalDateTime currentDate);

    @Query("SELECT COUNT(b) FROM AccommodationBooking b " +
           "WHERE b.room.roomId = :roomId " +
           "AND b.checkInDate < :maintenanceEnd " +
           "AND b.checkOutDate > :maintenanceStart")
    long countOverlappingBookings(String roomId, LocalDateTime maintenanceStart, LocalDateTime maintenanceEnd);
}