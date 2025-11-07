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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingRestControllerTest {

    @Mock
    private BookingRestService bookingRestService;

    @InjectMocks
    private BookingRestController bookingRestController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void whenGetAllBookings_thenReturnListOfBookings() {
        BookingResponseDTO booking1 = new BookingResponseDTO();
        booking1.setBookingId("BOOK1");
        List<BookingResponseDTO> expectedBookings = List.of(booking1);
        when(bookingRestService.getAllBookings()).thenReturn(expectedBookings);

        ResponseEntity<BaseResponseDTO<List<BookingResponseDTO>>> response = bookingRestController.getAllBookings();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertEquals(1, response.getBody().getData().size());
        verify(bookingRestService, Mockito.times(1)).getAllBookings();
    }

    @Test
    void whenGetAllBookingsThrowsException_thenReturnInternalServerError() {
        when(bookingRestService.getAllBookings()).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<BaseResponseDTO<List<BookingResponseDTO>>> response = bookingRestController.getAllBookings();

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Service error", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenGetBookingDetail_thenReturnBookingDetailDTO() {
        String bookingId = "BOOK1";
        BookingDetailDTO expectedDetail = new BookingDetailDTO();
        expectedDetail.setBookingId(bookingId);
        when(bookingRestService.getBookingDetailById(bookingId)).thenReturn(expectedDetail);

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.getBookingDetail(bookingId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertEquals(bookingId, response.getBody().getData().getBookingId());
        verify(bookingRestService, Mockito.times(1)).getBookingDetailById(bookingId);
    }

    @Test
    void whenGetBookingDetailNotFound_thenReturnNotFound() {
        String bookingId = "NONEXISTENT";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found")).when(bookingRestService).getBookingDetailById(bookingId);

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.getBookingDetail(bookingId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Booking not found", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenGetBookingDetailThrowsGenericException_thenReturnInternalServerError() {
        String bookingId = "ERROR_ID";
        doThrow(new RuntimeException("Database error")).when(bookingRestService).getBookingDetailById(bookingId);

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.getBookingDetail(bookingId);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Database error", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenGetPrefilledBookingData_thenReturnPrefilledBookingDTO() {
        String idRoom = "ROOM1";
        PrefilledBookingDTO expectedData = new PrefilledBookingDTO();
        expectedData.setRoomId(idRoom);
        when(bookingRestService.getPrefilledBookingData(idRoom)).thenReturn(expectedData);

        ResponseEntity<BaseResponseDTO<PrefilledBookingDTO>> response = bookingRestController.getPrefilledBookingData(idRoom);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertEquals(idRoom, response.getBody().getData().getRoomId());
        verify(bookingRestService, Mockito.times(1)).getPrefilledBookingData(idRoom);
    }

    @Test
    void whenGetPrefilledBookingDataNotFound_thenReturnNotFound() {
        String idRoom = "NONEXISTENT_ROOM";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found")).when(bookingRestService).getPrefilledBookingData(idRoom);

        ResponseEntity<BaseResponseDTO<PrefilledBookingDTO>> response = bookingRestController.getPrefilledBookingData(idRoom);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Room not found", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenGetPrefilledBookingDataThrowsGenericException_thenReturnInternalServerError() {
        String idRoom = "ERROR_ROOM";
        doThrow(new RuntimeException("Service unavailable")).when(bookingRestService).getPrefilledBookingData(idRoom);

        ResponseEntity<BaseResponseDTO<PrefilledBookingDTO>> response = bookingRestController.getPrefilledBookingData(idRoom);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Service unavailable", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenGetBookingSelectionData_thenReturnBookingSelectionDTO() {
        BookingSelectionDTO.PropertyOption propertyOption = new BookingSelectionDTO.PropertyOption("PROP1", "Hotel A", null);
        BookingSelectionDTO expectedData = new BookingSelectionDTO();
        expectedData.setProperties(List.of(propertyOption));
        when(bookingRestService.getBookingSelectionData()).thenReturn(expectedData);

        ResponseEntity<BaseResponseDTO<BookingSelectionDTO>> response = bookingRestController.getBookingSelectionData();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertFalse(response.getBody().getData().getProperties().isEmpty());
        verify(bookingRestService, Mockito.times(1)).getBookingSelectionData();
    }

    @Test
    void whenGetBookingSelectionDataThrowsException_thenReturnInternalServerError() {
        when(bookingRestService.getBookingSelectionData()).thenThrow(new RuntimeException("Failed to retrieve selection data"));

        ResponseEntity<BaseResponseDTO<BookingSelectionDTO>> response = bookingRestController.getBookingSelectionData();

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Failed to retrieve selection data", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenCreateBooking_thenReturnBookingDetailDTO() {
        CreateBookingRequestDTO requestDTO = new CreateBookingRequestDTO();
        requestDTO.setRoomId("ROOM1");
        requestDTO.setCustomerName("Test User");

        BookingDetailDTO createdBooking = new BookingDetailDTO();
        createdBooking.setBookingId("NEWBOOKING1");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookingRestService.createBooking(any(CreateBookingRequestDTO.class))).thenReturn(createdBooking);

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.createBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Booking created successfully.", response.getBody().getMessage());
        Assertions.assertEquals("NEWBOOKING1", response.getBody().getData().getBookingId());
        verify(bookingRestService, Mockito.times(1)).createBooking(any(CreateBookingRequestDTO.class));
    }

    @Test
    void whenCreateBookingWithValidationErrors_thenReturnBadRequest() {
        CreateBookingRequestDTO requestDTO = new CreateBookingRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "customerName", "Customer name cannot be empty"),
                new FieldError("dto", "roomId", "Room ID is required")
        ));

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.createBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getMessage().contains("Customer name cannot be empty"));
        Assertions.assertTrue(response.getBody().getMessage().contains("Room ID is required"));
        Assertions.assertNull(response.getBody().getData());
        verify(bookingRestService, Mockito.times(0)).createBooking(any(CreateBookingRequestDTO.class));
    }

    @Test
    void whenCreateBookingThrowsResponseStatusException_thenReturnCorrectStatus() {
        CreateBookingRequestDTO requestDTO = new CreateBookingRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available")).when(bookingRestService).createBooking(any(CreateBookingRequestDTO.class));

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.createBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Room is not available", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenCreateBookingThrowsGenericException_thenReturnInternalServerError() {
        CreateBookingRequestDTO requestDTO = new CreateBookingRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Unexpected error during booking creation")).when(bookingRestService).createBooking(any(CreateBookingRequestDTO.class));

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.createBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Failed to create booking: Unexpected error during booking creation", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenGetBookingForUpdate_thenReturnUpdateBookingFormDTO() {
        String bookingId = "BOOK1";
        BookingDetailDTO currentBooking = new BookingDetailDTO();
        currentBooking.setBookingId(bookingId);

        UpdateBookingFormDTO expectedData = new UpdateBookingFormDTO();
        expectedData.setCurrentBooking(currentBooking);
        when(bookingRestService.getBookingDataForUpdate(bookingId)).thenReturn(expectedData);

        ResponseEntity<BaseResponseDTO<UpdateBookingFormDTO>> response = bookingRestController.getBookingForUpdate(bookingId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertEquals(bookingId, response.getBody().getData().getCurrentBooking().getBookingId());
        verify(bookingRestService, Mockito.times(1)).getBookingDataForUpdate(bookingId);
    }

    @Test
    void whenGetBookingForUpdateNotFound_thenReturnNotFound() {
        String bookingId = "NONEXISTENT";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found for update")).when(bookingRestService).getBookingDataForUpdate(bookingId);

        ResponseEntity<BaseResponseDTO<UpdateBookingFormDTO>> response = bookingRestController.getBookingForUpdate(bookingId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Booking not found for update", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenGetBookingForUpdateThrowsGenericException_thenReturnInternalServerError() {
        String bookingId = "ERROR_ID";
        doThrow(new RuntimeException("Service error during update data retrieval")).when(bookingRestService).getBookingDataForUpdate(bookingId);

        ResponseEntity<BaseResponseDTO<UpdateBookingFormDTO>> response = bookingRestController.getBookingForUpdate(bookingId);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Service error during update data retrieval", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenUpdateBooking_thenReturnBookingDetailDTO() {
        UpdateBookingRequestDTO requestDTO = new UpdateBookingRequestDTO();
        requestDTO.setBookingId("BOOK1");
        requestDTO.setCustomerName("Updated Name");

        BookingDetailDTO updatedBooking = new BookingDetailDTO();
        updatedBooking.setBookingId("BOOK1");
        updatedBooking.setCustomerName("Updated Name");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookingRestService.updateBooking(any(UpdateBookingRequestDTO.class))).thenReturn(updatedBooking);

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.updateBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Booking updated successfully", response.getBody().getMessage());
        Assertions.assertEquals("BOOK1", response.getBody().getData().getBookingId());
        Assertions.assertEquals("Updated Name", response.getBody().getData().getCustomerName());
        verify(bookingRestService, Mockito.times(1)).updateBooking(any(UpdateBookingRequestDTO.class));
    }

    @Test
    void whenUpdateBookingWithValidationErrors_thenReturnBadRequest() {
        UpdateBookingRequestDTO requestDTO = new UpdateBookingRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "customerName", "Customer name cannot be empty for update")
        ));

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.updateBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getMessage().contains("Customer name cannot be empty for update"));
        Assertions.assertNull(response.getBody().getData());
        verify(bookingRestService, Mockito.times(0)).updateBooking(any(UpdateBookingRequestDTO.class));
    }

    @Test
    void whenUpdateBookingThrowsResponseStatusException_thenReturnCorrectStatus() {
        UpdateBookingRequestDTO requestDTO = new UpdateBookingRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found to update")).when(bookingRestService).updateBooking(any(UpdateBookingRequestDTO.class));

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.updateBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Booking not found to update", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenUpdateBookingThrowsGenericException_thenReturnInternalServerError() {
        UpdateBookingRequestDTO requestDTO = new UpdateBookingRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Unexpected error during booking update")).when(bookingRestService).updateBooking(any(UpdateBookingRequestDTO.class));

        ResponseEntity<BaseResponseDTO<BookingDetailDTO>> response = bookingRestController.updateBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Failed to update booking: Unexpected error during booking update", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }

    @Test
    void whenConfirmPayment_thenSuccess() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK1");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(bookingRestService).confirmPayment("BOOK1");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.confirmPayment(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Payment confirmed successfully.", response.getBody().getMessage());
        verify(bookingRestService, Mockito.times(1)).confirmPayment("BOOK1");
    }

    @Test
    void whenConfirmPaymentWithValidationErrors_thenReturnBadRequest() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "bookingId", "Booking ID is required")
        ));

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.confirmPayment(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getMessage().contains("Booking ID is required"));
        verify(bookingRestService, Mockito.times(0)).confirmPayment(any(String.class));
    }

    @Test
    void whenConfirmPaymentThrowsResponseStatusException_thenReturnCorrectStatus() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK_INVALID");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status for payment")).when(bookingRestService).confirmPayment("BOOK_INVALID");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.confirmPayment(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Invalid status for payment", response.getBody().getMessage());
    }

    @Test
    void whenConfirmPaymentThrowsGenericException_thenReturnInternalServerError() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK_ERROR");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Database connection failed")).when(bookingRestService).confirmPayment("BOOK_ERROR");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.confirmPayment(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Failed to confirm payment: Database connection failed", response.getBody().getMessage());
    }

    @Test
    void whenCancelBooking_thenSuccess() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK1");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(bookingRestService).cancelBooking("BOOK1");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.cancelBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Booking cancelled successfully.", response.getBody().getMessage());
        verify(bookingRestService, Mockito.times(1)).cancelBooking("BOOK1");
    }

    @Test
    void whenCancelBookingWithValidationErrors_thenReturnBadRequest() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "bookingId", "Booking ID is required")
        ));

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.cancelBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getMessage().contains("Booking ID is required"));
        verify(bookingRestService, Mockito.times(0)).cancelBooking(any(String.class));
    }

    @Test
    void whenCancelBookingThrowsResponseStatusException_thenReturnCorrectStatus() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK_INVALID");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking cannot be cancelled")).when(bookingRestService).cancelBooking("BOOK_INVALID");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.cancelBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Booking cannot be cancelled", response.getBody().getMessage());
    }

    @Test
    void whenCancelBookingThrowsGenericException_thenReturnInternalServerError() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK_ERROR");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("System error during cancellation")).when(bookingRestService).cancelBooking("BOOK_ERROR");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.cancelBooking(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Failed to cancel booking: System error during cancellation", response.getBody().getMessage());
    }

    @Test
    void whenProcessRefund_thenSuccess() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK1");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(bookingRestService).processRefund("BOOK1");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.processRefund(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Refund processed successfully.", response.getBody().getMessage());
        verify(bookingRestService, Mockito.times(1)).processRefund("BOOK1");
    }

    @Test
    void whenProcessRefundWithValidationErrors_thenReturnBadRequest() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "bookingId", "Booking ID is required for refund")
        ));

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.processRefund(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getMessage().contains("Booking ID is required for refund"));
        verify(bookingRestService, Mockito.times(0)).processRefund(any(String.class));
    }

    @Test
    void whenProcessRefundThrowsResponseStatusException_thenReturnCorrectStatus() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK_INVALID");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Refund not possible for this booking status")).when(bookingRestService).processRefund("BOOK_INVALID");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.processRefund(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Refund not possible for this booking status", response.getBody().getMessage());
    }

    @Test
    void whenProcessRefundThrowsGenericException_thenReturnInternalServerError() {
        UpdateStatusRequestDTO requestDTO = new UpdateStatusRequestDTO();
        requestDTO.setBookingId("BOOK_ERROR");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Payment gateway error during refund")).when(bookingRestService).processRefund("BOOK_ERROR");

        ResponseEntity<BaseResponseDTO<Object>> response = bookingRestController.processRefund(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Failed to process refund: Payment gateway error during refund", response.getBody().getMessage());
    }

    @Test
    void whenGetChartData_thenReturnListOfChartDataDTO() {
        int month = 10;
        int year = 2023;
        ChartDataDTO chartData1 = new ChartDataDTO("Hotel A", 5000L);
        List<ChartDataDTO> expectedChartData = List.of(chartData1);
        when(bookingRestService.getChartData(month, year)).thenReturn(expectedChartData);

        ResponseEntity<BaseResponseDTO<List<ChartDataDTO>>> response = bookingRestController.getChartData(month, year);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertEquals(1, response.getBody().getData().size());
        verify(bookingRestService, Mockito.times(1)).getChartData(month, year);
    }

    @Test
    void whenGetChartDataThrowsException_thenReturnInternalServerError() {
        int month = 1;
        int year = 2024;
        when(bookingRestService.getChartData(month, year)).thenThrow(new RuntimeException("Chart data retrieval failed"));

        ResponseEntity<BaseResponseDTO<List<ChartDataDTO>>> response = bookingRestController.getChartData(month, year);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Chart data retrieval failed", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
    }
}