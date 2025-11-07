package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.Room;
import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateMaintenanceRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomRestServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AccommodationBookingRepository bookingRepository;

    @InjectMocks
    private RoomRestServiceImpl roomRestService;

    private Room room;
    private CreateMaintenanceRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setRoomId("ROOM-1");
        room.setName("101");

        requestDTO = new CreateMaintenanceRequestDTO();
        requestDTO.setRoomId("ROOM-1");
        requestDTO.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        requestDTO.setMaintenanceEnd(LocalDateTime.now().plusDays(3));
    }

    @Test
    void whenCreateMaintenance_withValidData_shouldSucceed() {
        when(roomRepository.findById("ROOM-1")).thenReturn(Optional.of(room));
        when(bookingRepository.countByRoomRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(anyString(), any(), any())).thenReturn(0L);

        roomRestService.createMaintenanceSchedule(requestDTO);

        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository, times(1)).save(roomCaptor.capture());
        
        Room savedRoom = roomCaptor.getValue();
        assertEquals(requestDTO.getMaintenanceStart(), savedRoom.getMaintenanceStart());
        assertEquals(requestDTO.getMaintenanceEnd(), savedRoom.getMaintenanceEnd());
    }

    @Test
    void whenCreateMaintenance_withEndDateBeforeStartDate_shouldThrowBadRequest() {
        requestDTO.setMaintenanceEnd(LocalDateTime.now());
        requestDTO.setMaintenanceStart(LocalDateTime.now().plusDays(1));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            roomRestService.createMaintenanceSchedule(requestDTO);
        });

        assertEquals("Maintenance end date cannot be before the start date.", exception.getReason());
        verify(roomRepository, never()).findById(anyString());
        verify(bookingRepository, never()).countByRoomRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(anyString(), any(), any());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void whenCreateMaintenance_withInvalidRoomId_shouldThrowNotFound() {
        when(roomRepository.findById("INVALID-ID")).thenReturn(Optional.empty());
        requestDTO.setRoomId("INVALID-ID");

        assertThrows(ResponseStatusException.class, () -> {
            roomRestService.createMaintenanceSchedule(requestDTO);
        });
    }

    @Test
    void whenCreateMaintenance_withOverlappingBookings_shouldThrowBadRequest() {
        long overlappingCount = 2L;
        when(roomRepository.findById("ROOM-1")).thenReturn(Optional.of(room));
        when(bookingRepository.countByRoomRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            requestDTO.getRoomId(), requestDTO.getMaintenanceEnd(), requestDTO.getMaintenanceStart()
        )).thenReturn(overlappingCount);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            roomRestService.createMaintenanceSchedule(requestDTO);
        });

        assertTrue(exception.getReason().contains("Cannot schedule maintenance. There are " + overlappingCount + " bookings"));
        verify(roomRepository, never()).save(any(Room.class));
    }
}