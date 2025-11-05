package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Override
    @Transactional(readOnly = true)
    public BookingDetailDTO getBookingDetailById(String id) {
        AccommodationBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking with ID " + id + " not found."));
        return mapBookingToDetailDTO(booking);
    }

    private BookingDetailDTO mapBookingToDetailDTO(AccommodationBooking booking) {
        return BookingDetailDTO.builder()
                .bookingId(booking.getBookingID())
                .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
                .roomName(booking.getRoom().getName())
                .customerId(booking.getCustomerID())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalDays(booking.getTotalDays())
                .status(booking.getStatus())
                .isBreakfast(booking.isBreakfast())
                .totalPrice(booking.getTotalPrice())
                .extraPay(booking.getExtraPay())
                .refund(booking.getRefund())
                .createdDate(booking.getCreatedDate())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}