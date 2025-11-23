package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.UpdateStatusRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.BookingSelectionDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.PrefilledBookingDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.UpdateBookingFormDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.chart.ChartDataDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.BookingRestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
// @CrossOrigin(origins = {"http://2306245794-fe.hafizmuh.site", "http://localhost:5173"})
@RequiredArgsConstructor
public class BookingRestController {

    private final BookingRestService bookingRestService;

    @GetMapping("")
    public ResponseEntity<BaseResponseDTO<List<BookingResponseDTO>>> getAllBookings() {
        var baseResponseDTO = new BaseResponseDTO<List<BookingResponseDTO>>();
        try {
            List<BookingResponseDTO> bookings = bookingRestService.getAllBookings();
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Success");
            baseResponseDTO.setData(bookings);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id:.+}")
    public ResponseEntity<BaseResponseDTO<BookingDetailDTO>> getBookingDetail(@PathVariable("id") String id) {
        var baseResponseDTO = new BaseResponseDTO<BookingDetailDTO>();
        try {
            BookingDetailDTO bookingDetail = bookingRestService.getBookingDetailById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Success");
            baseResponseDTO.setData(bookingDetail);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value()); 
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode()); 

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     @GetMapping("/create/{idRoom}")
    public ResponseEntity<BaseResponseDTO<PrefilledBookingDTO>> getPrefilledBookingData(
        @PathVariable("idRoom") String idRoom
    ) {
        var baseResponseDTO = new BaseResponseDTO<PrefilledBookingDTO>();
        try {
            PrefilledBookingDTO data = bookingRestService.getPrefilledBookingData(idRoom);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Success");
            baseResponseDTO.setData(data);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value());
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode()); 

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/create")
    public ResponseEntity<BaseResponseDTO<BookingSelectionDTO>> getBookingSelectionData() {
        var baseResponseDTO = new BaseResponseDTO<BookingSelectionDTO>();
        try {
            BookingSelectionDTO data = bookingRestService.getBookingSelectionData();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Success");
            baseResponseDTO.setData(data);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<BookingDetailDTO>> createBooking(
        @Valid @RequestBody CreateBookingRequestDTO dto, BindingResult bindingResult
    ) {
        var baseResponseDTO = new BaseResponseDTO<BookingDetailDTO>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            BookingDetailDTO booking = bookingRestService.createBooking(dto);

            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setMessage("Booking created successfully.");
            baseResponseDTO.setData(booking);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value()); 
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode()); 

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to create booking: " + e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update/{id:.+}")
    public ResponseEntity<BaseResponseDTO<UpdateBookingFormDTO>> getBookingForUpdate(@PathVariable("id") String id) {
        var baseResponseDTO = new BaseResponseDTO<UpdateBookingFormDTO>();
        try {
            UpdateBookingFormDTO data = bookingRestService.getBookingDataForUpdate(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Success");
            baseResponseDTO.setData(data);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value()); 
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode()); 
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO<BookingDetailDTO>> updateBooking(
        @Valid @RequestBody UpdateBookingRequestDTO dto, BindingResult bindingResult
    ) {
        var baseResponseDTO = new BaseResponseDTO<BookingDetailDTO>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            BookingDetailDTO updatedBooking = bookingRestService.updateBooking(dto);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Booking updated successfully");
            baseResponseDTO.setData(updatedBooking);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value()); 
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode()); 

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to update booking: " + e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/status/pay")
    public ResponseEntity<BaseResponseDTO<Object>> confirmPayment(
        @Valid @RequestBody UpdateStatusRequestDTO dto, BindingResult bindingResult
    ) {
        var baseResponseDTO = new BaseResponseDTO<Object>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            bookingRestService.confirmPayment(dto.getBookingId());

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Payment confirmed successfully.");
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value());
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode()); 

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to confirm payment: " + e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/status/cancel")
    public ResponseEntity<BaseResponseDTO<Object>> cancelBooking(
        @Valid @RequestBody UpdateStatusRequestDTO dto, BindingResult bindingResult
    ) {
        var baseResponseDTO = new BaseResponseDTO<Object>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            bookingRestService.cancelBooking(dto.getBookingId());
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Booking cancelled successfully.");
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value());
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode());

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to cancel booking: " + e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/status/refund")
    public ResponseEntity<BaseResponseDTO<Object>> processRefund(
        @Valid @RequestBody UpdateStatusRequestDTO dto, BindingResult bindingResult
    ) {
        var baseResponseDTO = new BaseResponseDTO<Object>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            bookingRestService.processRefund(dto.getBookingId());

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Refund processed successfully.");
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            baseResponseDTO.setStatus(e.getStatusCode().value()); 
            baseResponseDTO.setMessage(e.getReason());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, e.getStatusCode()); 

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to process refund: " + e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chart")
    public ResponseEntity<BaseResponseDTO<List<ChartDataDTO>>> getChartData(
        @RequestParam("month") int month,
        @RequestParam("year") int year
    ) {
        try {
            List<ChartDataDTO> chartData = bookingRestService.getChartData(month, year);
            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Success", new Date(), chartData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), new Date(), null));
        }
    }
}