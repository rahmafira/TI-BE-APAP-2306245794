package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingRestServiceImpl implements BookingRestService {

    @Autowired
    private AccommodationBookingRepository bookingRepository;

    @Override
    @Transactional
    public List<BookingResponseDTO> getAllBookings() {
        List<AccommodationBooking> allBookings = bookingRepository.findAllByOrderByBookingIDDesc();
        LocalDateTime now = LocalDateTime.now();

        for (AccommodationBooking booking : allBookings) {
            if (booking.getCheckInDate().isBefore(now)) {
                if (booking.getStatus() == 1) {
                    booking.setStatus(4); 
                }
                else if (booking.getStatus() == 0 || booking.getStatus() == 3) {
                    booking.setStatus(2); 
                }
                bookingRepository.save(booking);
            }
        }

        return allBookings.stream()
                .map(this::mapBookingToResponseDTO)
                .collect(Collectors.toList());
    }

    private BookingResponseDTO mapBookingToResponseDTO(AccommodationBooking booking) {
        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingID())
                .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
                .roomName(booking.getRoom().getName())
                .checkIn(booking.getCheckInDate())
                .checkOut(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .build();
    }
}