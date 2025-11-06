package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingSelectionDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.PrefilledBookingDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.UpdateBookingFormDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.BookingRestService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

     @GetMapping("/create/{idRoom}")
    public ResponseEntity<BaseResponseDTO<PrefilledBookingDTO>> getPrefilledBookingData(
        @PathVariable("idRoom") String idRoom
    ) {
        try {
            PrefilledBookingDTO data = bookingRestService.getPrefilledBookingData(idRoom);
            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Success", new Date(), data));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new BaseResponseDTO<>(e.getStatusCode().value(), e.getReason(), new Date(), null));
        }
    }

    @GetMapping("/create")
    public ResponseEntity<BaseResponseDTO<BookingSelectionDTO>> getBookingSelectionData() {
        try {
            BookingSelectionDTO data = bookingRestService.getBookingSelectionData();
            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Success", new Date(), data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), new Date(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<BookingDetailDTO>> createBooking(@Valid @RequestBody CreateBookingRequestDTO dto) {
        try {
            BookingDetailDTO booking = bookingRestService.createBooking(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponseDTO<>(HttpStatus.CREATED.value(), "Booking created successfully.", new Date(), booking));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new BaseResponseDTO<>(e.getStatusCode().value(), e.getReason(), new Date(), null));
        }
    }

    @GetMapping("/update/{id}")
    public ResponseEntity<BaseResponseDTO<UpdateBookingFormDTO>> getBookingForUpdate(@PathVariable("id") String id) {
        try {
            UpdateBookingFormDTO data = bookingRestService.getBookingDataForUpdate(id);
            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Success", new Date(), data));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new BaseResponseDTO<>(e.getStatusCode().value(), e.getReason(), new Date(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO<BookingDetailDTO>> updateBooking(@Valid @RequestBody UpdateBookingRequestDTO dto) {
        try {
            BookingDetailDTO updatedBooking = bookingRestService.updateBooking(dto);

            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Booking updated successfully", new Date(), updatedBooking));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new BaseResponseDTO<>(e.getStatusCode().value(), e.getReason(), new Date(), null));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponseDTO<Object> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        var response = new BaseResponseDTO<Object>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(new Date());
        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        response.setMessage(errorMessages);
        response.setData(null);
        return response;
    }
}