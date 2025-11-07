package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.RoomRestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomRestControllerTest {

    @Mock
    private RoomRestService roomRestService;

    @InjectMocks
    private RoomRestController roomRestController;

    @Test
    void whenAddMaintenanceSchedule_thenSuccess() {
        CreateMaintenanceRequestDTO requestDTO = new CreateMaintenanceRequestDTO();
        requestDTO.setRoomId("ROOM-1");
        requestDTO.setMaintenanceStart(LocalDateTime.now());
        requestDTO.setMaintenanceEnd(LocalDateTime.now().plusDays(1));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        doNothing().when(roomRestService).createMaintenanceSchedule(any(CreateMaintenanceRequestDTO.class));

        ResponseEntity<BaseResponseDTO<Object>> response = roomRestController.addMaintenanceSchedule(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Maintenance schedule added successfully.", response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());
        verify(roomRestService, times(1)).createMaintenanceSchedule(requestDTO);
    }

    @Test
    void whenAddMaintenanceScheduleWithValidationErrors_thenReturnBadRequest() {
        CreateMaintenanceRequestDTO requestDTO = new CreateMaintenanceRequestDTO();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("dto", "roomId", "Room ID is required")
        ));

        ResponseEntity<BaseResponseDTO<Object>> response = roomRestController.addMaintenanceSchedule(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getMessage().contains("Room ID is required"));
        verify(roomRestService, never()).createMaintenanceSchedule(any(CreateMaintenanceRequestDTO.class));
    }

    @Test
    void whenAddMaintenanceScheduleThrowsResponseStatusException_thenReturnCorrectStatus() {
        CreateMaintenanceRequestDTO requestDTO = new CreateMaintenanceRequestDTO();
        requestDTO.setRoomId("ROOM-NOT-FOUND");
        requestDTO.setMaintenanceStart(LocalDateTime.now());
        requestDTO.setMaintenanceEnd(LocalDateTime.now().plusDays(1));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"))
                .when(roomRestService).createMaintenanceSchedule(any(CreateMaintenanceRequestDTO.class));

        ResponseEntity<BaseResponseDTO<Object>> response = roomRestController.addMaintenanceSchedule(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Room not found", response.getBody().getMessage());
    }

    @Test
    void whenAddMaintenanceScheduleThrowsGenericException_thenReturnInternalServerError() {
        CreateMaintenanceRequestDTO requestDTO = new CreateMaintenanceRequestDTO();
        requestDTO.setRoomId("ROOM-ERROR");
        requestDTO.setMaintenanceStart(LocalDateTime.now());
        requestDTO.setMaintenanceEnd(LocalDateTime.now().plusDays(1));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new RuntimeException("Database connection failed"))
                .when(roomRestService).createMaintenanceSchedule(any(CreateMaintenanceRequestDTO.class));

        ResponseEntity<BaseResponseDTO<Object>> response = roomRestController.addMaintenanceSchedule(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("An error occurred: Database connection failed", response.getBody().getMessage());
    }
}