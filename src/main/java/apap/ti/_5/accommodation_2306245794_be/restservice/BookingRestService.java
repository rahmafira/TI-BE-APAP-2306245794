package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingSelectionDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.PrefilledBookingDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.UpdateBookingFormDTO;

import java.util.List;

public interface BookingRestService {
    List<BookingResponseDTO> getAllBookings();
    BookingDetailDTO getBookingDetailById(String id);
    PrefilledBookingDTO getPrefilledBookingData(String idRoom);
    BookingSelectionDTO getBookingSelectionData();
    BookingDetailDTO createBooking(CreateBookingRequestDTO dto);
    UpdateBookingFormDTO getBookingDataForUpdate(String id);
    BookingDetailDTO updateBooking(UpdateBookingRequestDTO dto);
    void confirmPayment(String bookingId);
    void cancelBooking(String bookingId);
    void processRefund(String bookingId);
}