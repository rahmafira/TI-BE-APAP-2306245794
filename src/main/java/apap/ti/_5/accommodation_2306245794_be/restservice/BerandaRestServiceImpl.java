package apap.ti._5.accommodation_2306245794_be.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.BerandaResponseDTO;

@Service
public class BerandaRestServiceImpl implements BerandaRestService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AccommodationBookingRepository accommodationBookingRepository;

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