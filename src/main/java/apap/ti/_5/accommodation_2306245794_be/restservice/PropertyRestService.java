package apap.ti._5.accommodation_2306245794_be.restservice;

import java.util.List;

import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.restdto.request.AddRoomTypesRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyHeaderDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;

public interface PropertyRestService {
    List<PropertyResponseDTO> getAllProperties();
    PropertyDetailDTO getPropertyDetailById(String id);
    Property createProperty(CreatePropertyRequestDTO createPropertyRequestDTO);
    PropertyDetailDTO getPropertyByIdForUpdate(String id);
    Property updateProperty(UpdatePropertyRequestDTO updatePropertyRequestDTO);
    void softDeleteProperty(String id);
    PropertyHeaderDTO getPropertyHeader(String id);
    void addRoomTypesToProperty(AddRoomTypesRequestDTO dto);
}