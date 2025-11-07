package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.model.Room;
import apap.ti._5.accommodation_2306245794_be.model.RoomType;
import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306245794_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306245794_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.AddRoomTypesRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyHeaderDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.room.RoomDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.roomtype.RoomTypeDetailDTO;
import lombok.RequiredArgsConstructor;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyRestServiceImpl implements PropertyRestService {

    private final PropertyRepository propertyRepository;

    private final RoomTypeRepository roomTypeRepository;

    private final RoomRepository roomRepository;
    
    private final AccommodationBookingRepository bookingRepository;

    private final AtomicLong propertyCounter = new AtomicLong(0);

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDTO> getAllProperties() {
        List<Property> listProperty = propertyRepository.findAllByOrderByUpdatedDateDesc();
        return listProperty.stream()
                .map(this::mapPropertyToResponseDTO)
                .collect(Collectors.toList());
    }

    private PropertyResponseDTO mapPropertyToResponseDTO(Property property) {
        return PropertyResponseDTO.builder()
                .propertyId(property.getPropertyId())
                .propertyName(property.getPropertyName())
                .type(property.getType())
                .activeStatus(property.getActiveStatus())
                .totalRoom(property.getTotalRoom())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDetailDTO getPropertyDetailById(String id) {
        Property property = propertyRepository.findByIdWithRoomTypes(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property with ID " + id + " not found"));

        if (property.getListRoomType() != null) {
            for (RoomType roomType : property.getListRoomType()) {
                Hibernate.initialize(roomType.getListRoom());
            }
        }
        return mapPropertyToDetailDTO(property);
    }

    private PropertyDetailDTO mapPropertyToDetailDTO(Property property) {
        return PropertyDetailDTO.builder()
                .propertyId(property.getPropertyId())
                .propertyName(property.getPropertyName())
                .description(property.getDescription())
                .income(property.getIncome())
                .type(property.getType())
                .province(property.getProvince())
                .address(property.getAddress())
                .totalRoom(property.getTotalRoom())
                .activeStatus(property.getActiveStatus())
                .ownerName(property.getOwnerName())
                .ownerId(property.getOwnerId())
                .createdDate(property.getCreatedDate())
                .updatedDate(property.getUpdatedDate())
                .listRoomType(property.getListRoomType().stream()
                        .map(this::mapRoomTypeToDetailDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private RoomTypeDetailDTO mapRoomTypeToDetailDTO(RoomType roomType) {
        var roomTypeDetailDTO = new RoomTypeDetailDTO();
        roomTypeDetailDTO.setRoomTypeId(roomType.getRoomTypeId());
        roomTypeDetailDTO.setName(roomType.getName());
        roomTypeDetailDTO.setDescription(roomType.getDescription());
        roomTypeDetailDTO.setPrice(roomType.getPrice());
        roomTypeDetailDTO.setCapacity(roomType.getCapacity());
        roomTypeDetailDTO.setFacility(roomType.getFacility());
        roomTypeDetailDTO.setListRoom(roomType.getListRoom().stream()
                .map(this::mapRoomToDetailDTO)
                .collect(Collectors.toList()));
        return roomTypeDetailDTO;
    }

    private RoomDetailDTO mapRoomToDetailDTO(Room room) {
        return new RoomDetailDTO(
                room.getRoomId(),
                room.getName(),
                room.getAvailabilityStatus()
        );
    }

    @Override
    @Transactional
    public Property createProperty(CreatePropertyRequestDTO dto) {
        Set<String> uniqueRoomTypeKeys = new HashSet<>();
        for (CreateRoomTypeRequestDTO rtDto : dto.getListRoomType()) {
            String key = rtDto.getName() + "-" + rtDto.getFloor();
            if (!uniqueRoomTypeKeys.add(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate combination of room type and floor found in the request: " + key);
            }
        }

        Property property = new Property();
        property.setPropertyName(dto.getPropertyName());
        property.setType(dto.getType());
        property.setAddress(dto.getAddress());
        property.setProvince(dto.getProvince());
        property.setDescription(dto.getDescription());
        property.setOwnerName(dto.getOwnerName());
        property.setOwnerId(dto.getOwnerId());
        property.setActiveStatus(1);
        property.setIncome(0);

        long counter = propertyRepository.count() + 1;
        String uuidSuffix = dto.getOwnerId().toString().substring(dto.getOwnerId().toString().length() - 4);
        String prefix = getPropertyPrefix(dto.getType());
        property.setPropertyId(String.format("%s-%s-%03d", prefix, uuidSuffix.toUpperCase(), counter));

        Property savedProperty = propertyRepository.save(property);
        int totalRooms = 0;
        List<RoomType> roomTypeList = new ArrayList<>();

        for (CreateRoomTypeRequestDTO rtDto : dto.getListRoomType()) {
            RoomType roomType = new RoomType();
            roomType.setProperty(savedProperty);
            roomType.setName(rtDto.getName());
            roomType.setPrice(rtDto.getPrice());
            roomType.setDescription(rtDto.getDescription());
            roomType.setCapacity(rtDto.getCapacity());
            roomType.setFacility(rtDto.getFacility());
            roomType.setFloor(rtDto.getFloor());
            roomType.setRoomTypeId(String.format("%03d-%s-%d", counter, rtDto.getName().replace(" ", ""), rtDto.getFloor()));

            List<Room> roomList = new ArrayList<>();
            for (int i = 1; i <= rtDto.getNumberOfUnits(); i++) {
                Room room = new Room();
                room.setRoomType(roomType);
                room.setName(String.valueOf(rtDto.getFloor() * 100 + i));
                room.setAvailabilityStatus(1);
                room.setActiveRoom(1);
                room.setRoomId(String.format("%s-%d", savedProperty.getPropertyId(), (rtDto.getFloor() * 100 + i)));
                roomList.add(room);
            }
            roomType.setListRoom(roomList);
            roomTypeList.add(roomType);
            totalRooms += rtDto.getNumberOfUnits();
        }

        savedProperty.setListRoomType(roomTypeList);
        savedProperty.setTotalRoom(totalRooms);
        return propertyRepository.save(savedProperty);
    }

    private String getPropertyPrefix(int type) {
        switch (type) {
            case 1: return "HOT";
            case 2: return "VIL";
            case 3: return "APT";
            default: throw new IllegalArgumentException("Invalid property type");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDetailDTO getPropertyByIdForUpdate(String id) {
        return getPropertyDetailById(id);
    }

    @Override
    @Transactional
    public Property updateProperty(UpdatePropertyRequestDTO dto) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        property.setPropertyName(dto.getPropertyName());
        property.setAddress(dto.getAddress());
        property.setDescription(dto.getDescription());

        for (UpdateRoomTypeRequestDTO rtDto : dto.getListRoomType()) {
            RoomType roomTypeToUpdate = roomTypeRepository.findById(rtDto.getRoomTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RoomType with ID " + rtDto.getRoomTypeId() + " not found"));

            if (!roomTypeToUpdate.getProperty().getPropertyId().equals(property.getPropertyId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RoomType does not belong to this property");
            }

            roomTypeToUpdate.setCapacity(rtDto.getCapacity());
            roomTypeToUpdate.setPrice(rtDto.getPrice());
            roomTypeToUpdate.setDescription(rtDto.getDescription());
            roomTypeToUpdate.setFacility(rtDto.getFacility());

            roomTypeRepository.save(roomTypeToUpdate);
        }

        return propertyRepository.save(property);
    }

    @Override
    @Transactional
    public void softDeleteProperty(String id) {
        Property property = propertyRepository.findByIdWithRoomTypes(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        long futureBookings = bookingRepository.countByRoomRoomTypePropertyPropertyIdAndCheckInDateGreaterThanEqual(id, LocalDateTime.now());
        if (futureBookings > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot deactivate property. There are " + futureBookings + " upcoming bookings.");
        }

        property.setActiveStatus(0);

        for (RoomType roomType : property.getListRoomType()) {
            for (Room room : roomType.getListRoom()) {
                room.setActiveRoom(0);
            }
        }
        propertyRepository.save(property);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyHeaderDTO getPropertyHeader(String id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        return new PropertyHeaderDTO(property.getPropertyId(), property.getPropertyName(), property.getType());
    }

    @Override
    @Transactional
    public void addRoomTypesToProperty(AddRoomTypesRequestDTO dto) {
        Property property = propertyRepository.findByIdWithRoomTypes(dto.getPropertyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        Set<String> existingKeys = property.getListRoomType().stream()
                .map(rt -> rt.getName() + "-" + rt.getFloor())
                .collect(Collectors.toSet());

        for (CreateRoomTypeRequestDTO newRtDto : dto.getNewRoomTypes()) {
            String newKey = newRtDto.getName() + "-" + newRtDto.getFloor();
            if (existingKeys.contains(newKey)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate combination of room type and floor already exists for this property: " + newKey);
            }
        }

        Set<String> requestKeys = new HashSet<>();
        for (CreateRoomTypeRequestDTO newRtDto : dto.getNewRoomTypes()) {
            String newKey = newRtDto.getName() + "-" + newRtDto.getFloor();
            if (!requestKeys.add(newKey)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate combination of room type and floor found in the request: " + newKey);
            }
        }

        int totalNewRooms = 0;
        long propertyCounter = Long.parseLong(property.getPropertyId().split("-")[2]);

        for (CreateRoomTypeRequestDTO rtDto : dto.getNewRoomTypes()) {
            RoomType roomType = new RoomType();
            roomType.setProperty(property);
            roomType.setName(rtDto.getName());
            roomType.setPrice(rtDto.getPrice());
            roomType.setDescription(rtDto.getDescription());
            roomType.setCapacity(rtDto.getCapacity());
            roomType.setFacility(rtDto.getFacility());
            roomType.setFloor(rtDto.getFloor());
            roomType.setRoomTypeId(String.format("%03d-%s-%d", propertyCounter, rtDto.getName().replace(" ", ""), rtDto.getFloor()));

            List<Room> roomList = new ArrayList<>();
            for (int i = 1; i <= rtDto.getNumberOfUnits(); i++) {
                Room room = new Room();
                room.setRoomType(roomType);
                room.setName(String.valueOf(rtDto.getFloor() * 100 + i));
                room.setAvailabilityStatus(1);
                room.setActiveRoom(1);
                room.setRoomId(String.format("%s-%d", property.getPropertyId(), (rtDto.getFloor() * 100 + i)));
                roomList.add(room);
            }
            roomType.setListRoom(roomList);

            property.getListRoomType().add(roomType);
            totalNewRooms += rtDto.getNumberOfUnits();
        }

        property.setTotalRoom(property.getTotalRoom() + totalNewRooms);
        propertyRepository.save(property);
    }
}