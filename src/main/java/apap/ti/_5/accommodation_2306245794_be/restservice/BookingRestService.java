package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingResponseDTO;
import java.util.List;

public interface BookingRestService {
    List<BookingResponseDTO> getAllBookings();
}