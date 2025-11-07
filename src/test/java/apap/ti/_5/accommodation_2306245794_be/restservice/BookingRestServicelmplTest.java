package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.*;
import apap.ti._5.accommodation_2306245794_be.repository.*;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.*;
import apap.ti._5.accommodation_2306245794_be.restdto.response.chart.ChartDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingRestServiceImplTest {

    @Mock
    private AccommodationBookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private BookingRestServiceImpl bookingRestService;

    private AccommodationBooking mockBooking;
    private Room mockRoom;
    private RoomType mockRoomType;
    private Property mockProperty;
    private LocalDateTime now;
    private final UUID mockCustomerId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        mockProperty = new Property();
        mockProperty.setPropertyId("P001");
        mockProperty.setPropertyName("Grand Hyatt Jakarta");
        mockProperty.setIncome(1000000); 
        mockProperty.setActiveStatus(1);

        mockRoomType = new RoomType();
        mockRoomType.setRoomTypeId("RT001");
        mockRoomType.setName("Deluxe King");
        mockRoomType.setCapacity(2);
        mockRoomType.setPrice(1000000);
        mockRoomType.setProperty(mockProperty);

        mockRoom = new Room();
        mockRoom.setRoomId("ROOM-0001"); 
        mockRoom.setName("101");
        mockRoom.setRoomType(mockRoomType);
        mockRoom.setMaintenanceStart(null);
        mockRoom.setMaintenanceEnd(null);

        mockBooking = new AccommodationBooking();
        mockBooking.setBookingID("BOOK-M-00001-2025-11-07-10:00:00"); 
        mockBooking.setRoom(mockRoom);
        mockBooking.setCheckInDate(now.plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0));
        mockBooking.setCheckOutDate(now.plusDays(4).withHour(12).withMinute(0).withSecond(0).withNano(0));
        mockBooking.setTotalDays(2);
        mockBooking.setTotalPrice(2000000);
        mockBooking.setStatus(1); 
        mockBooking.setCustomerID(mockCustomerId); 
        mockBooking.setCustomerName("John Doe");
        mockBooking.setCustomerEmail("john.doe@example.com");
        mockBooking.setCustomerPhone("081234567890");
        mockBooking.setBreakfast(false);
        mockBooking.setCapacity(2);
        mockBooking.setExtraPay(0);
        mockBooking.setRefund(0);
        mockBooking.setCreatedDate(now.minusHours(10));
        mockBooking.setUpdatedAt(now.minusHours(1));

        mockProperty.setListRoomType(Collections.singletonList(mockRoomType));
        mockRoomType.setListRoom(Collections.singletonList(mockRoom));
    }

    @Test
    void getAllBookings_ShouldUpdateStatusAndReturnAllBookings() {
        AccommodationBooking bookingPastConfirmed = new AccommodationBooking();
        bookingPastConfirmed.setBookingID("B1");
        bookingPastConfirmed.setRoom(mockRoom);
        bookingPastConfirmed.setCheckInDate(now.minusDays(5));
        bookingPastConfirmed.setCheckOutDate(now.minusDays(3));
        bookingPastConfirmed.setStatus(1);
        bookingPastConfirmed.setTotalPrice(100);

        AccommodationBooking bookingPastWaiting = new AccommodationBooking();
        bookingPastWaiting.setBookingID("B2");
        bookingPastWaiting.setRoom(mockRoom);
        bookingPastWaiting.setCheckInDate(now.minusDays(5));
        bookingPastWaiting.setCheckOutDate(now.minusDays(3));
        bookingPastWaiting.setStatus(0);
        bookingPastWaiting.setTotalPrice(200);

        AccommodationBooking bookingPastRefund = new AccommodationBooking();
        bookingPastRefund.setBookingID("B3");
        bookingPastRefund.setRoom(mockRoom);
        bookingPastRefund.setCheckInDate(now.minusDays(5));
        bookingPastRefund.setCheckOutDate(now.minusDays(3));
        bookingPastRefund.setStatus(3);
        bookingPastRefund.setTotalPrice(300);

        AccommodationBooking bookingFuture = new AccommodationBooking();
        bookingFuture.setBookingID("B4");
        bookingFuture.setRoom(mockRoom);
        bookingFuture.setCheckInDate(now.plusDays(5));
        bookingFuture.setCheckOutDate(now.plusDays(7));
        bookingFuture.setStatus(1);
        bookingFuture.setTotalPrice(400);

        List<AccommodationBooking> bookings = Arrays.asList(
            bookingPastConfirmed, bookingPastWaiting, bookingPastRefund, bookingFuture
        );
        when(bookingRepository.findAllByOrderByBookingIDDesc()).thenReturn(bookings);
        when(bookingRepository.save(any(AccommodationBooking.class)))
            .thenAnswer(invocation -> invocation.getArgument(0)); 

        List<BookingResponseDTO> result = bookingRestService.getAllBookings();

        assertEquals(4, result.size());
        assertEquals(4, bookingPastConfirmed.getStatus()); 
        assertEquals(2, bookingPastWaiting.getStatus());   
        assertEquals(2, bookingPastRefund.getStatus());    
        assertEquals(1, bookingFuture.getStatus());        

        verify(bookingRepository, times(3)).save(any(AccommodationBooking.class));
    }

    @Test
    void getAllBookings_ShouldReturnEmptyListWhenNoBookings() {
        when(bookingRepository.findAllByOrderByBookingIDDesc()).thenReturn(Collections.emptyList());
        List<BookingResponseDTO> result = bookingRestService.getAllBookings();
        assertTrue(result.isEmpty());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingDetailById_ShouldReturnDetailDTO_WhenBookingExists() {
        when(bookingRepository.findById("BOOK-ID")).thenReturn(Optional.of(mockBooking));
        BookingDetailDTO result = bookingRestService.getBookingDetailById("BOOK-ID");

        assertNotNull(result);
        assertEquals(mockBooking.getBookingID(), result.getBookingId());
        assertEquals(mockCustomerId, result.getCustomerId()); 
    }

    @Test
    void getBookingDetailById_ShouldThrowNotFound_WhenBookingNotExists() {
        when(bookingRepository.findById("BOOK-ID")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookingRestService.getBookingDetailById("BOOK-ID");
        });
        assertEquals("404 NOT_FOUND \"Booking with ID BOOK-ID not found.\"", exception.getMessage());
    }

    @Test
    void getPrefilledBookingData_ShouldReturnDTO_WhenRoomExists() {
        when(roomRepository.findById(mockRoom.getRoomId())).thenReturn(Optional.of(mockRoom));

        PrefilledBookingDTO result = bookingRestService.getPrefilledBookingData(mockRoom.getRoomId());

        assertNotNull(result);
        assertEquals(mockRoom.getRoomId(), result.getRoomId()); 
        assertEquals(mockRoom.getName(), result.getRoomName());
        assertEquals(mockRoom.getRoomType().getCapacity(), result.getCapacity());
    }

    @Test
    void getBookingSelectionData_ShouldReturnSelectionDTO_WithActiveProperties() {
        when(propertyRepository.findByActiveStatus(1)).thenReturn(Collections.singletonList(mockProperty));

        BookingSelectionDTO result = bookingRestService.getBookingSelectionData();

        assertNotNull(result);
        assertEquals(1, result.getProperties().size());
        assertEquals("P001", result.getProperties().get(0).getId());
    }
    
    @Test
    void createBooking_ShouldCreateBooking_WhenValidData() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setRoomId(mockRoom.getRoomId());
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(3)); 
        dto.setCustomerId(mockCustomerId); 
        dto.setCustomerName("Test User");
        dto.setCustomerEmail("test@example.com");
        dto.setCustomerPhone("08111");
        dto.setCapacity(2);
        dto.setIsBreakfast(true); 

        when(roomRepository.findById(mockRoom.getRoomId())).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.countByRoomRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any())).thenReturn(0L);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenAnswer(invocation -> {
            AccommodationBooking savedBooking = invocation.getArgument(0);
            savedBooking.setBookingID("BOOK-TEST-ID");
            return savedBooking;
        });

        BookingDetailDTO result = bookingRestService.createBooking(dto); 

        assertNotNull(result);
        assertEquals(2, result.getTotalDays());
        assertEquals(2100000, result.getTotalPrice()); 
        assertEquals(mockCustomerId, result.getCustomerId()); 
        assertEquals(0, result.getStatus()); 
    }

    @Test
    void createBooking_ShouldThrowBadRequest_WhenCheckOutBeforeCheckInPlusOneDay() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setRoomId(mockRoom.getRoomId());
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(1).plusDays(1)); 
        
        when(roomRepository.findById(mockRoom.getRoomId())).thenReturn(Optional.of(mockRoom));

        assertThrows(ResponseStatusException.class, () -> bookingRestService.createBooking(dto), 
            "Minimum booking is for one day.");
    }
    
    @Test
    void createBooking_ShouldThrowBadRequest_WhenOverlappingWithMaintenance() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setRoomId(mockRoom.getRoomId());
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(3));
        dto.setCapacity(2);
        
        mockRoom.setMaintenanceStart(LocalDateTime.now().plusDays(2).minusHours(1));
        mockRoom.setMaintenanceEnd(LocalDateTime.now().plusDays(2).plusHours(1));

        when(roomRepository.findById(mockRoom.getRoomId())).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.countByRoomRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any())).thenReturn(0L);

        assertThrows(ResponseStatusException.class, () -> bookingRestService.createBooking(dto), 
            "Room is scheduled for maintenance during the selected dates.");
    }

    @Test
    void getBookingDataForUpdate_ShouldReturnDTO_WhenBookingExistsAndInactiveProperty() {
        mockProperty.setActiveStatus(0); 
        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));
        when(propertyRepository.findByActiveStatus(1)).thenReturn(Collections.emptyList()); 
        
        UpdateBookingFormDTO result = bookingRestService.getBookingDataForUpdate(mockBooking.getBookingID());

        assertNotNull(result);
        assertEquals(1, result.getSelectionData().getProperties().size()); 
        assertEquals(mockProperty.getPropertyId(), result.getSelectionData().getProperties().get(0).getId());
    }
    
    @Test
    void getBookingDataForUpdate_ShouldThrowBadRequest_WhenRefundPending() {
        mockBooking.setRefund(100000); 
        mockBooking.setExtraPay(0);
        when(bookingRepository.findById("BOOK-ID")).thenReturn(Optional.of(mockBooking));

        assertThrows(ResponseStatusException.class, () -> bookingRestService.getBookingDataForUpdate("BOOK-ID"), 
            "Booking with pending extra payment or refund cannot be updated.");
    }

    @Test
    void updateBooking_ShouldUpdateAndProcessExtraPay_WhenNewPriceIsHigher() {
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(mockBooking.getBookingID());
        dto.setRoomId(mockRoom.getRoomId());
        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(8)); 
        dto.setCustomerId(mockCustomerId); 
        dto.setCapacity(2);
        dto.setIsBreakfast(false);
        dto.setCustomerName("Test User Update");
        dto.setCustomerEmail("update@example.com");
        dto.setCustomerPhone("08111");
        
        mockBooking.setTotalPrice(2000000); 
        mockBooking.setExtraPay(0);

        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));
        when(roomRepository.findById(mockRoom.getRoomId())).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.countByRoomRoomIdAndBookingIDNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any(), any())).thenReturn(0L);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDetailDTO result = bookingRestService.updateBooking(dto); 

        assertEquals(3000000, result.getTotalPrice()); 
        assertEquals(1000000, result.getExtraPay()); 
        assertEquals(0, result.getStatus()); 
    }
    
    @Test
    void updateBooking_ShouldUpdateAndProcessRefund_WhenNewPriceIsLower() {
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(mockBooking.getBookingID());
        dto.setRoomId(mockRoom.getRoomId());
        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(7)); 
        
        dto.setCustomerId(mockCustomerId); 
        dto.setCapacity(2);
        dto.setIsBreakfast(false);
        dto.setCustomerName("Test User Update");
        dto.setCustomerEmail("update@example.com");
        dto.setCustomerPhone("08111");
        
        mockBooking.setTotalPrice(3000000); 
        mockBooking.setStatus(1); 

        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));
        when(roomRepository.findById(mockRoom.getRoomId())).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.countByRoomRoomIdAndBookingIDNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any(), any())).thenReturn(0L);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDetailDTO result = bookingRestService.updateBooking(dto); 

        assertEquals(2, result.getTotalDays()); 
        assertEquals(2000000, result.getTotalPrice()); 
        assertEquals(1000000, result.getRefund()); 
        assertEquals(3, result.getStatus()); 
    }

    @Test
    void confirmPayment_ShouldConfirmPayment_WhenExtraPayIsPending() {
        mockBooking.setStatus(1); 
        mockBooking.setExtraPay(500000); 
        mockBooking.setTotalPrice(1000000);
        long initialIncome = mockProperty.getIncome();
        
        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(AccommodationBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingRestService.confirmPayment(mockBooking.getBookingID());

        assertEquals(1, mockBooking.getStatus()); 
        assertEquals(0, mockBooking.getExtraPay());
        assertEquals(initialIncome + 1500000, mockProperty.getIncome()); 
    }
    
    @Test
    void confirmPayment_ShouldThrowBadRequest_WhenStatusIsCompleted() {
        mockBooking.setStatus(4); 
        mockBooking.setExtraPay(0);
        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));

        assertThrows(ResponseStatusException.class, () -> bookingRestService.confirmPayment(mockBooking.getBookingID()), 
            "Payment can only be confirmed for bookings with status 'Waiting for Payment'.");
    }

    @Test
    void cancelBooking_ShouldCancel_WhenStatusIsConfirmed() {
        mockBooking.setStatus(1); 
        mockBooking.setTotalPrice(1000000);
        long initialIncome = mockProperty.getIncome();
        
        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(AccommodationBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingRestService.cancelBooking(mockBooking.getBookingID());

        assertEquals(2, mockBooking.getStatus()); 
        assertEquals(initialIncome - 1000000, mockProperty.getIncome()); 
    }

    @Test
    void cancelBooking_ShouldCancel_WhenStatusIsRequestRefund() {
        mockBooking.setStatus(3); 
        mockBooking.setRefund(500000);
        mockBooking.setTotalPrice(1000000);
        long initialIncome = mockProperty.getIncome();
        
        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(AccommodationBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingRestService.cancelBooking(mockBooking.getBookingID());

        assertEquals(2, mockBooking.getStatus()); 
        assertEquals(initialIncome - 1500000, mockProperty.getIncome()); 
    }

    @Test
    void processRefund_ShouldProcessRefund_WhenStatusIsRequestRefund() {
        mockBooking.setStatus(3); 
        mockBooking.setRefund(500000);
        long initialIncome = mockProperty.getIncome();
        
        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(AccommodationBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingRestService.processRefund(mockBooking.getBookingID());

        assertEquals(1, mockBooking.getStatus()); 
        assertEquals(0, mockBooking.getRefund());
        assertEquals(initialIncome - 500000, mockProperty.getIncome()); 
    }
    
    @Test
    void processRefund_ShouldThrowBadRequest_WhenStatusIsNotRequestRefund() {
        mockBooking.setStatus(1); 
        mockBooking.setRefund(0);
        when(bookingRepository.findById(mockBooking.getBookingID())).thenReturn(Optional.of(mockBooking));

        assertThrows(ResponseStatusException.class, () -> bookingRestService.processRefund(mockBooking.getBookingID()), 
            "Refund can only be processed for bookings with status 'Request Refund'.");
    }
    
    @Test
    void getChartData_ShouldReturnChartDataFromRepository() {
        int year = 2025;
        int month = 11;
        List<ChartDataDTO> mockData = Arrays.asList(
            new ChartDataDTO("Property 1", 1000000L),
            new ChartDataDTO("Property 2", 2000000L)
        );
        when(bookingRepository.findMonthlyIncomeByProperty(year, month)).thenReturn(mockData);

        List<ChartDataDTO> result = bookingRestService.getChartData(month, year);

        assertEquals(2, result.size());
        assertEquals("Property 1", result.get(0).getPropertyName());
        assertEquals(1000000L, result.get(0).getTotalIncome()); 
        verify(bookingRepository, times(1)).findMonthlyIncomeByProperty(year, month);
    }
}