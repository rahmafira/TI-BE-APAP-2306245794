package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.Room;
import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateMaintenanceRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoomRestServiceImpl implements RoomRestService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AccommodationBookingRepository bookingRepository;

    @Override
    @Transactional
    public void createMaintenanceSchedule(CreateMaintenanceRequestDTO dto) {
        if (dto.getMaintenanceEnd().isBefore(dto.getMaintenanceStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maintenance end date cannot be before the start date.");
        }

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room with ID " + dto.getRoomId() + " not found."));

        long overlappingBookings = bookingRepository.countOverlappingBookings(dto.getRoomId(), dto.getMaintenanceStart(), dto.getMaintenanceEnd());
        if (overlappingBookings > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot schedule maintenance. There are " + overlappingBookings + " bookings in the selected date range.");
        }

        room.setMaintenanceStart(dto.getMaintenanceStart());
        room.setMaintenanceEnd(dto.getMaintenanceEnd());

        roomRepository.save(room);
    }
}