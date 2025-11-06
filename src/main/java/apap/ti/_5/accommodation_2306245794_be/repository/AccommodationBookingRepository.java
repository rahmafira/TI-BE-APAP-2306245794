package apap.ti._5.accommodation_2306245794_be.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import apap.ti._5.accommodation_2306245794_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306245794_be.restdto.response.chart.ChartDataDTO;

@Repository
public interface AccommodationBookingRepository extends JpaRepository<AccommodationBooking, String> {
    long countByRoomRoomTypePropertyPropertyIdAndCheckInDateGreaterThanEqual(
        String propertyId, 
        LocalDateTime currentDate
    );
    long countByRoomRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
        String roomId, 
        LocalDateTime maintenanceEnd, 
        LocalDateTime maintenanceStart
    );
    List<AccommodationBooking> findAllByOrderByBookingIDDesc();
    long countByRoomRoomIdAndBookingIDNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
        String roomId, 
        String bookingIdToExclude, 
        LocalDateTime maintenanceEnd, 
        LocalDateTime maintenanceStart
    );
    @Query("SELECT new apap.ti._5.accommodation_2306245794_be.restdto.response.chart.ChartDataDTO(b.room.roomType.property.propertyName, SUM(b.totalPrice)) " +
           "FROM AccommodationBooking b " +
           "WHERE b.status = 4 " +
           "AND EXTRACT(YEAR FROM b.checkInDate) = :year " +    // Ganti FUNCTION('YEAR', ...)
           "AND EXTRACT(MONTH FROM b.checkInDate) = :month " +  // Ganti FUNCTION('MONTH', ...)
           "GROUP BY b.room.roomType.property.propertyName " +
           "ORDER BY SUM(b.totalPrice) DESC")
    List<ChartDataDTO> findMonthlyIncomeByProperty(
        @Param("year") int year,
        @Param("month") int month
    );
}