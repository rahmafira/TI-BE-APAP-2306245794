package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.BerandaResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.BerandaRestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BerandaRestControllerTest {

    @Mock
    private BerandaRestService berandaRestService;

    @InjectMocks
    private BerandaRestController berandaRestController;

    @Test
    void whenGetBerandaStatistics_thenSuccess() {
        BerandaResponseDTO stats = BerandaResponseDTO.builder()
                .totalProperties(10L)
                .totalBookings(120L)
                .build();

        when(berandaRestService.getBerandaStatistics()).thenReturn(stats);

        ResponseEntity<BaseResponseDTO<BerandaResponseDTO>> response = berandaRestController.getBerandaStatistics();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertNotNull(response.getBody().getData());
        Assertions.assertEquals(10L, response.getBody().getData().getTotalProperties());
        Assertions.assertEquals(120L, response.getBody().getData().getTotalBookings());
        
        verify(berandaRestService, times(1)).getBerandaStatistics();
    }

    @Test
    void whenGetBerandaStatistics_andServiceThrowsException_thenReturnInternalServerError() {
        String errorMessage = "Database connection failed";
        when(berandaRestService.getBerandaStatistics()).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<BaseResponseDTO<BerandaResponseDTO>> response = berandaRestController.getBerandaStatistics();

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        Assertions.assertEquals("An error occurred: " + errorMessage, response.getBody().getMessage());
        Assertions.assertNull(response.getBody().getData());

        verify(berandaRestService, times(1)).getBerandaStatistics();
    }
}