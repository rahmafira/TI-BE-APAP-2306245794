package apap.ti._5.accommodation_2306245794_be.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.hibernate.Hibernate; 

import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.model.Room;
import apap.ti._5.accommodation_2306245794_be.model.RoomType;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306245794_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306245794_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.room.RoomDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.roomtype.RoomTypeDetailDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PropertyRestServiceImpl implements PropertyRestService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private RoomRepository roomRepository;
    
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
        return new RoomTypeDetailDTO(
                roomType.getRoomTypeId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getPrice(),
                roomType.getListRoom().stream()
                        .map(this::mapRoomToDetailDTO)
                        .collect(Collectors.toList())
        );
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
        // 1. Validasi duplikasi internal di dalam request
        Set<String> uniqueRoomTypeKeys = new HashSet<>();
        for (CreateRoomTypeRequestDTO rtDto : dto.getListRoomType()) {
            String key = rtDto.getName() + "-" + rtDto.getFloor();
            if (!uniqueRoomTypeKeys.add(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate combination of room type and floor found in the request: " + key);
            }
        }

        // 2. Buat dan simpan entitas Property
        Property property = new Property();
        property.setPropertyName(dto.getPropertyName());
        property.setType(dto.getType());
        property.setAddress(dto.getAddress());
        property.setProvince(dto.getProvince());
        property.setDescription(dto.getDescription());
        property.setOwnerName(dto.getOwnerName());
        property.setOwnerId(dto.getOwnerId());
        property.setActiveStatus(1); // Default active
        property.setIncome(0);

        // Generate Property ID
        long counter = propertyRepository.count() + 1;
        String uuidSuffix = dto.getOwnerId().toString().substring(dto.getOwnerId().toString().length() - 4);
        String prefix = getPropertyPrefix(dto.getType());
        property.setPropertyId(String.format("%s-%s-%03d", prefix, uuidSuffix, counter));

        Property savedProperty = propertyRepository.save(property);
        
        int totalRooms = 0;

        // 3. Loop untuk membuat RoomType dan Room
        for (CreateRoomTypeRequestDTO rtDto : dto.getListRoomType()) {
            RoomType roomType = new RoomType();
            roomType.setProperty(savedProperty);
            roomType.setName(rtDto.getName());
            roomType.setPrice(rtDto.getPrice());
            roomType.setDescription(rtDto.getDescription());
            roomType.setCapacity(rtDto.getCapacity());
            roomType.setFacility(rtDto.getFacility());
            roomType.setFloor(rtDto.getFloor());
            
            // Generate RoomType ID
            roomType.setRoomTypeId(String.format("%03d-%s-%d", counter, rtDto.getName().replace(" ", ""), rtDto.getFloor()));
            
            RoomType savedRoomType = roomTypeRepository.save(roomType);

            for (int i = 1; i <= rtDto.getNumberOfUnits(); i++) {
                Room room = new Room();
                room.setRoomType(savedRoomType);
                room.setName(String.valueOf(rtDto.getFloor() * 100 + i)); // Contoh: 201, 202
                room.setAvailabilityStatus(1); // Default available
                room.setActiveRoom(1); // Default active
                
                // Generate Room ID
                room.setRoomId(String.format("%s-%d%02d", savedProperty.getPropertyId(), rtDto.getFloor(), i));
                
                roomRepository.save(room);
            }
            totalRooms += rtDto.getNumberOfUnits();
        }

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
}