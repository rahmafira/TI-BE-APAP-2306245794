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
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.room.RoomDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.roomtype.RoomTypeDetailDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyRestServiceImpl implements PropertyRestService {

    @Autowired
    private PropertyRepository propertyRepository;

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
}