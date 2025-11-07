package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.BerandaResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BerandaRestServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private AccommodationBookingRepository accommodationBookingRepository;

    @InjectMocks
    private BerandaRestServiceImpl berandaRestService;

    @Test
    void whenGetBerandaStatistics_shouldReturnCorrectCounts() {
        long expectedPropertiesCount = 15L;
        long expectedBookingsCount = 250L;

        when(propertyRepository.count()).thenReturn(expectedPropertiesCount);
        when(accommodationBookingRepository.count()).thenReturn(expectedBookingsCount);

        BerandaResponseDTO result = berandaRestService.getBerandaStatistics();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedPropertiesCount, result.getTotalProperties());
        Assertions.assertEquals(expectedBookingsCount, result.getTotalBookings());

        verify(propertyRepository, times(1)).count();
        verify(accommodationBookingRepository, times(1)).count();
    }
}