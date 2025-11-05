package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.BookingRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingRestController {

    @Autowired
    private BookingRestService bookingRestService;

    @GetMapping("")
    public ResponseEntity<BaseResponseDTO<List<BookingResponseDTO>>> getAllBookings() {
        try {
            List<BookingResponseDTO> bookings = bookingRestService.getAllBookings();
            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Success", new Date(), bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), new Date(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<BookingDetailDTO>> getBookingDetail(@PathVariable("id") String id) {
        try {
            BookingDetailDTO bookingDetail = bookingRestService.getBookingDetailById(id);
            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Success", new Date(), bookingDetail));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new BaseResponseDTO<>(e.getStatusCode().value(), e.getReason(), new Date(), null));
        }
    }
}