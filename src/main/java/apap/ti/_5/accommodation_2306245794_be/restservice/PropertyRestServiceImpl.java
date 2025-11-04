package apap.ti._5.accommodation_2306245794_be.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.response.PropertyResponseDTO;

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
}