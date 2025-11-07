package apap.ti._5.accommodation_2306245794_be.restservice;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.BerandaResponseDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BerandaRestServiceImpl implements BerandaRestService {

    private final PropertyRepository propertyRepository;

    private final AccommodationBookingRepository accommodationBookingRepository;

    @Override
    public BerandaResponseDTO getBerandaStatistics() {
        long totalProperties = propertyRepository.count();
        long totalBookings = accommodationBookingRepository.count();

        return BerandaResponseDTO.builder()
                .totalProperties(totalProperties)
                .totalBookings(totalBookings)
                .build();
    }
}