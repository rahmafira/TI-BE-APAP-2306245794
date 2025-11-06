package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.*;
import apap.ti._5.accommodation_2306245794_be.repository.*;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.booking.*;
import apap.ti._5.accommodation_2306245794_be.restdto.response.chart.ChartDataDTO;

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
    @Transactional 
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
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking with ID " + id + " not found."
            ));
        return mapBookingToDetailDTO(booking);
    }

    private BookingDetailDTO mapBookingToDetailDTO(AccommodationBooking booking) {
        return BookingDetailDTO.builder()
            .bookingId(booking.getBookingID())
            .propertyName(booking.getRoom().getRoomType().getProperty().getPropertyName())
            .roomName(booking.getRoom().getName())
            .roomId(booking.getRoom().getRoomId())
            .customerId(booking.getCustomerID())
            .customerName(booking.getCustomerName())
            .customerEmail(booking.getCustomerEmail())
            .customerPhone(booking.getCustomerPhone())
            .capacity(booking.getCapacity())
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
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Room not found"
            ));
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
    @Transactional
    public BookingDetailDTO createBooking(CreateBookingRequestDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Room not found"
            ));
        
        LocalDateTime checkIn = dto.getCheckInDate().atTime(14, 0);
        LocalDateTime checkOut = dto.getCheckOutDate().atTime(12, 0);

        if (checkOut.isBefore(checkIn.plusDays(1))) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Minimum booking is for one day."
            ); 
        }
        if (checkIn.toLocalDate().isBefore(LocalDate.now())) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Minimum check-in date is today."
            ); 
        }
        if (dto.getCapacity() > room.getRoomType().getCapacity()) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Booking capacity exceeds room capacity."
            ); 
        }

        long overlappingBookings = bookingRepository.countByRoomRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            dto.getRoomId(), checkOut, checkIn
        );
        if (overlappingBookings > 0) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Selected dates are not available for this room."
            ); 
        }
        if (room.getMaintenanceStart() != null && room.getMaintenanceEnd() != null) {
            if (checkIn.isBefore(room.getMaintenanceEnd()) && checkOut.isAfter(room.getMaintenanceStart())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Room is scheduled for maintenance during the selected dates."
                );
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
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
        );
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

    @Override
    @Transactional(readOnly = true)
    public UpdateBookingFormDTO getBookingDataForUpdate(String id) {
        AccommodationBooking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found"
            ));
        
        if (booking.getExtraPay() > 0 || booking.getRefund() > 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Booking with pending extra payment or refund cannot be updated."
            );
        }

        BookingDetailDTO currentBooking = mapBookingToDetailDTO(booking);
        BookingSelectionDTO selectionData = getBookingSelectionData();

        String currentPropertyId = booking.getRoom().getRoomType().getProperty().getPropertyId();
        boolean isPropertyInList = selectionData.getProperties().stream()
            .anyMatch(p -> p.getId().equals(currentPropertyId));

        if (!isPropertyInList) {
            Property currentProperty = booking.getRoom().getRoomType().getProperty();

            Hibernate.initialize(currentProperty.getListRoomType());
            for (RoomType rt : currentProperty.getListRoomType()) {
                Hibernate.initialize(rt.getListRoom());
            }

            BookingSelectionDTO.PropertyOption inactivePropertyOption = 
                new BookingSelectionDTO.PropertyOption(
                    currentProperty.getPropertyId(),
                    currentProperty.getPropertyName(),
                    currentProperty.getListRoomType().stream()
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
                );

            selectionData.getProperties().add(inactivePropertyOption);
        }

        return new UpdateBookingFormDTO(currentBooking, selectionData);
    }

    @Override
    @Transactional 
    public BookingDetailDTO updateBooking(UpdateBookingRequestDTO dto) {
        AccommodationBooking booking = bookingRepository.findById(dto.getBookingId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found"
            ));
        
        if (booking.getExtraPay() > 0 || booking.getRefund() > 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Booking with pending extra payment or refund cannot be updated."
            );
        }

        Room newRoom = roomRepository.findById(dto.getRoomId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Selected room not found"
            ));

        LocalDateTime newCheckIn = dto.getCheckInDate().atTime(14, 0);
        LocalDateTime newCheckOut = dto.getCheckOutDate().atTime(12, 0);

        if (newCheckOut.isBefore(newCheckIn.plusDays(1))) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Minimum booking is for one day."
            ); 
        }
        if (newCheckIn.toLocalDate().isBefore(LocalDate.now())) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Minimum check-in date is today."
            ); 
        }
        if (dto.getCapacity() > newRoom.getRoomType().getCapacity()) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Booking capacity exceeds room capacity."
            ); 
        }

        long overlappingBookings = bookingRepository.countByRoomRoomIdAndBookingIDNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            dto.getRoomId(),      
            dto.getBookingId(), 
            newCheckOut,     
            newCheckIn   
        );
        if (overlappingBookings > 0) { 
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Selected dates are not available for this room."
            ); 
        }
        if (newRoom.getMaintenanceStart() != null && newRoom.getMaintenanceEnd() != null) {
            if (newCheckIn.isBefore(newRoom.getMaintenanceEnd()) && newCheckOut.isAfter(newRoom.getMaintenanceStart())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Room is scheduled for maintenance during the selected dates."
                );
            }
        }

        long newTotalDays = ChronoUnit.DAYS.between(newCheckIn.toLocalDate(), newCheckOut.toLocalDate());
        if (newTotalDays < 1) newTotalDays = 1;
        int newTotalPrice = (int) (newRoom.getRoomType().getPrice() * newTotalDays);
        if (dto.getIsBreakfast()) {
            newTotalPrice += 50000 * newTotalDays;
        }

        int oldTotalPrice = booking.getTotalPrice();
        if (newTotalPrice > oldTotalPrice) {
            booking.setExtraPay(newTotalPrice - oldTotalPrice);
            booking.setStatus(0); 
        } else if (newTotalPrice < oldTotalPrice) {
            booking.setRefund(oldTotalPrice - newTotalPrice);
            booking.setStatus(3); 
        }
        
        booking.setRoom(newRoom);
        booking.setCheckInDate(newCheckIn);
        booking.setCheckOutDate(newCheckOut);
        booking.setTotalDays((int) newTotalDays);
        booking.setTotalPrice(newTotalPrice);
        booking.setCustomerID(dto.getCustomerId());
        booking.setCustomerName(dto.getCustomerName());
        booking.setCustomerEmail(dto.getCustomerEmail());
        booking.setCustomerPhone(dto.getCustomerPhone());
        booking.setBreakfast(dto.getIsBreakfast());
        booking.setCapacity(dto.getCapacity());

        AccommodationBooking updatedBooking = bookingRepository.save(booking);
        return mapBookingToDetailDTO(updatedBooking);
    }

    @Override
    @Transactional
    public void confirmPayment(String bookingId) {
        AccommodationBooking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found."
            ));

        if (booking.getStatus() != 0 && booking.getExtraPay() == 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Payment can only be confirmed for bookings with status 'Waiting for Payment'."
            );
        }

        Property property = booking.getRoom().getRoomType().getProperty();
        int paymentAmount = booking.getTotalPrice() + booking.getExtraPay();

        property.setIncome(property.getIncome() + paymentAmount);

        booking.setStatus(1); 
        booking.setExtraPay(0); 
        propertyRepository.save(property);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(String bookingId) {
        AccommodationBooking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found."
            ));
        
        if (booking.getStatus() != 0 && booking.getStatus() != 1 && booking.getStatus() != 3) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "This booking cannot be cancelled."
            );
        }

        Property property = booking.getRoom().getRoomType().getProperty();

        switch (booking.getStatus()) {
            case 0:
                if (booking.getExtraPay() > 0) {
                    property.setIncome(
                        property.getIncome() + (booking.getTotalPrice() - booking.getExtraPay())
                    );
                }
                break;
            case 1: 
                property.setIncome(property.getIncome() - booking.getTotalPrice());
                break;
            case 3:
                property.setIncome(
                    property.getIncome() - (booking.getTotalPrice() + booking.getRefund())
                );
                break;
        }

        booking.setStatus(2); 
        propertyRepository.save(property);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void processRefund(String bookingId) {
        AccommodationBooking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found."
            ));

        if (booking.getStatus() != 3) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Refund can only be processed for bookings with status 'Request Refund'."
            );
        }

        Property property = booking.getRoom().getRoomType().getProperty();
  
        property.setIncome(property.getIncome() - booking.getRefund());

        booking.setStatus(1); 
        booking.setRefund(0); 

        propertyRepository.save(property);
        bookingRepository.save(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChartDataDTO> getChartData(int month, int year) {
        return bookingRepository.findMonthlyIncomeByProperty(year, month);
    }
}