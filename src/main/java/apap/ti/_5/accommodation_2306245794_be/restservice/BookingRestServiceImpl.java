package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.*;
import apap.ti._5.accommodation_2306245794_be.repository.*;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingRestServiceImpl implements BookingRestService {
    
    @Autowired 
    private AccommodationBookingRepository bookingRepository;

    @Autowired 
    private RoomRepository roomRepository;

    @Autowired 
    private PropertyRepository propertyRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getAllBookings() {
        List<AccommodationBooking> allBookings = bookingRepository.findAllByOrderByBookingIDDesc();
        LocalDateTime now = LocalDateTime.now();

        for (AccommodationBooking booking : allBookings) {
            if (booking.getCheckInDate().isBefore(now)) {
                if (booking.getStatus() == 1) { 
                    booking.setStatus(4); 
                } else if (booking.getStatus() == 0 || booking.getStatus() == 3) { 
                    booking.setStatus(2); 
                }
                bookingRepository.save(booking);
            }
        }

        return allBookings.stream()
            .map(this::mapBookingToResponseDTO)
            .collect(Collectors.toList());
    }

    private BookingResponseDTO mapBookingToResponseDTO(AccommodationBooking booking) {
        return BookingResponseDTO.builder()
            .bookingId(booking.getBookingID())
            .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
            .roomName(booking.getRoom().getName())
            .checkIn(booking.getCheckInDate())
            .checkOut(booking.getCheckOutDate())
            .totalPrice(booking.getTotalPrice())
            .status(booking.getStatus())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDetailDTO getBookingDetailById(String id) {
        AccommodationBooking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking with ID " + id + " not found."));
        return mapBookingToDetailDTO(booking);
    }

    private BookingDetailDTO mapBookingToDetailDTO(AccommodationBooking booking) {
        return BookingDetailDTO.builder()
            .bookingId(booking.getBookingID())
            .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
            .roomName(booking.getRoom().getName())
            .customerId(booking.getCustomerID())
            .customerName(booking.getCustomerName())
            .customerEmail(booking.getCustomerEmail())
            .customerPhone(booking.getCustomerPhone())
            .checkInDate(booking.getCheckInDate()) 
            .checkOutDate(booking.getCheckOutDate()) 
            .totalDays(booking.getTotalDays())
            .status(booking.getStatus())
            .isBreakfast(booking.isBreakfast())
            .totalPrice(booking.getTotalPrice())
            .extraPay(booking.getExtraPay())
            .refund(booking.getRefund())
            .createdDate(booking.getCreatedDate())
            .updatedAt(booking.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PrefilledBookingDTO getPrefilledBookingData(String idRoom) {
        Room room = roomRepository.findById(idRoom)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return new PrefilledBookingDTO(room.getRoomId(), room.getName(), room.getRoomType().getCapacity());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingSelectionDTO getBookingSelectionData() {
        List<Property> activeProperties = propertyRepository.findByActiveStatus(1);
        for (Property p : activeProperties) {
            Hibernate.initialize(p.getListRoomType());
            for (RoomType rt : p.getListRoomType()) {
                Hibernate.initialize(rt.getListRoom());
            }
        }

        List<BookingSelectionDTO.PropertyOption> propertyOptions = activeProperties.stream()
            .map(p -> new BookingSelectionDTO.PropertyOption(
                p.getPropertyId(),
                p.getPropertyName(),
                p.getListRoomType().stream()
                    .map(rt -> new BookingSelectionDTO.RoomTypeOption(
                        rt.getRoomTypeId(), 
                        rt.getName(),
                        rt.getListRoom().stream()
                            .map(r -> new BookingSelectionDTO.RoomOption(
                                r.getRoomId(), 
                                r.getName(), 
                                rt.getCapacity()
                            ))
                            .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
            
        return new BookingSelectionDTO(propertyOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDetailDTO createBooking(CreateBookingRequestDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        
        LocalDateTime checkIn = dto.getCheckInDate().atTime(14, 0);
        LocalDateTime checkOut = dto.getCheckOutDate().atTime(12, 0);

        if (checkOut.isBefore(checkIn.plusDays(1))) { 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum booking is for one day."); 
        }
        if (checkIn.toLocalDate().isBefore(LocalDate.now())) { 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum check-in date is today."); 
        }
        if (dto.getCapacity() > room.getRoomType().getCapacity()) { 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking capacity exceeds room capacity."); 
        }

        long overlappingBookings = bookingRepository.countOverlappingBookings(dto.getRoomId(), checkIn, checkOut);
        if (overlappingBookings > 0) { 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected dates are not available for this room."); 
        }
        if (room.getMaintenanceStart() != null && room.getMaintenanceEnd() != null) {
            if (checkIn.isBefore(room.getMaintenanceEnd()) && checkOut.isAfter(room.getMaintenanceStart())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is scheduled for maintenance during the selected dates.");
            }
        }

        long totalDays = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
        if (totalDays < 1) {
            totalDays = 1;
        }
        
        int totalPrice = (int) (room.getRoomType().getPrice() * totalDays);
        if (dto.getIsBreakfast()) {
            totalPrice += 50000 * totalDays;
        }

        String roomIdSuffix = room.getRoomId().substring(room.getRoomId().length() - 7);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss"));
        String bookingId = String.format("BOOK-%s-%s", roomIdSuffix, timestamp);

        AccommodationBooking booking = new AccommodationBooking();
        booking.setBookingID(bookingId);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalDays((int) totalDays);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(0);
        booking.setCustomerID(dto.getCustomerId());
        booking.setCustomerName(dto.getCustomerName());
        booking.setCustomerEmail(dto.getCustomerEmail());
        booking.setCustomerPhone(dto.getCustomerPhone());
        booking.setBreakfast(dto.getIsBreakfast());
        booking.setCapacity(dto.getCapacity());
        booking.setExtraPay(0);
        booking.setRefund(0);

        AccommodationBooking savedBooking = bookingRepository.save(booking);
 
        return mapBookingToDetailDTO(savedBooking);
    }
}