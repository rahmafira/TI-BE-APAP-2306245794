package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.AddRoomTypesRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyHeaderDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.PropertyRestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyRestControllerTest {

    @Mock
    private PropertyRestService propertyRestService;

    @InjectMocks
    private PropertyRestController propertyRestController;
    
    private BindingResult mockBindingResultWithError(String message) {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(
            Collections.singletonList(new FieldError("dto", "fieldName", message))
        );
        return bindingResult;
    }

    @Test
    void whenGetAllProperties_success_shouldReturnOk() {
        when(propertyRestService.getAllProperties()).thenReturn(List.of(new PropertyResponseDTO()));
        
        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = propertyRestController.getAllProperties();
        
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Success", response.getBody().getMessage());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    void whenGetAllProperties_serviceThrowsGenericException_shouldReturnInternalServerError() {
        when(propertyRestService.getAllProperties()).thenThrow(new RuntimeException("Database error on list"));

        ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> response = propertyRestController.getAllProperties();
        
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getMessage().contains("Database error on list"));
    }

    @Test
    void whenGetPropertyDetail_withValidId_shouldReturnOk() {
        String id = "PROP-1";
        when(propertyRestService.getPropertyDetailById(id)).thenReturn(new PropertyDetailDTO());

        ResponseEntity<BaseResponseDTO<PropertyDetailDTO>> response = propertyRestController.getPropertyDetailById(id);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Success", response.getBody().getMessage());
    }
    
    @Test
    void whenGetPropertyDetail_serviceThrowsResponseStatusException_shouldReturnCorrectStatus() {
        String id = "INVALID";
        when(propertyRestService.getPropertyDetailById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        ResponseEntity<BaseResponseDTO<PropertyDetailDTO>> response = propertyRestController.getPropertyDetailById(id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Property not found", response.getBody().getMessage());
    }
    
    @Test
    void whenGetPropertyDetail_serviceThrowsGenericException_shouldReturnInternalServerError() {
        String id = "PROP-1";
        when(propertyRestService.getPropertyDetailById(id)).thenThrow(new RuntimeException("Service error on detail"));

        ResponseEntity<BaseResponseDTO<PropertyDetailDTO>> response = propertyRestController.getPropertyDetailById(id);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getMessage().contains("Service error on detail"));
    }

    @Test
    void whenCreateProperty_withValidInput_shouldReturnCreated() {
        CreatePropertyRequestDTO requestDTO = new CreatePropertyRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class))).thenReturn(new Property());

        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.createProperty(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Property created successfully", response.getBody().getMessage());
    }

    @Test
    void whenCreateProperty_withInvalidInput_shouldReturnBadRequest() {
        CreatePropertyRequestDTO requestDTO = new CreatePropertyRequestDTO();
        BindingResult bindingResult = mockBindingResultWithError("Field error message");

        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.createProperty(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Field error message", response.getBody().getMessage());
    }

    @Test
    void whenCreateProperty_serviceThrowsResponseStatusException_shouldReturnCorrectStatus() {
        CreatePropertyRequestDTO requestDTO = new CreatePropertyRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate room type"));
            
        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.createProperty(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Duplicate room type", response.getBody().getMessage());
    }

    @Test
    void whenCreateProperty_serviceThrowsGenericException_shouldReturnInternalServerError() {
        CreatePropertyRequestDTO requestDTO = new CreatePropertyRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(propertyRestService.createProperty(any(CreatePropertyRequestDTO.class)))
            .thenThrow(new RuntimeException("Unexpected error during create"));

        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.createProperty(requestDTO, bindingResult);
        
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getMessage().contains("Unexpected error during create"));
    }

    @Test
    void whenGetPropertyForUpdate_withValidId_shouldReturnOk() {
        String id = "PROP-1";
        when(propertyRestService.getPropertyDetailById(id)).thenReturn(new PropertyDetailDTO()); 

        ResponseEntity<BaseResponseDTO<PropertyDetailDTO>> response = propertyRestController.getPropertyForUpdate(id);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Success", response.getBody().getMessage());
    }

    @Test
    void whenUpdateProperty_withValidInput_shouldReturnOk() {
        UpdatePropertyRequestDTO requestDTO = new UpdatePropertyRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(propertyRestService.updateProperty(any(UpdatePropertyRequestDTO.class))).thenReturn(new Property());

        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.updateProperty(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Property updated successfully", response.getBody().getMessage());
    }
    
    @Test
    void whenUpdateProperty_withInvalidInput_shouldReturnBadRequest() {
        UpdatePropertyRequestDTO requestDTO = new UpdatePropertyRequestDTO();
        BindingResult bindingResult = mockBindingResultWithError("Update field required");

        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.updateProperty(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Update field required", response.getBody().getMessage());
    }

    @Test
    void whenUpdateProperty_serviceThrowsResponseStatusException_shouldReturnCorrectStatus() {
        UpdatePropertyRequestDTO requestDTO = new UpdatePropertyRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(propertyRestService.updateProperty(any(UpdatePropertyRequestDTO.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Property or RoomType not found"));
            
        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.updateProperty(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Property or RoomType not found", response.getBody().getMessage());
    }

    @Test
    void whenUpdateProperty_serviceThrowsGenericException_shouldReturnInternalServerError() {
        UpdatePropertyRequestDTO requestDTO = new UpdatePropertyRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(propertyRestService.updateProperty(any(UpdatePropertyRequestDTO.class)))
            .thenThrow(new RuntimeException("Unexpected error during update"));

        ResponseEntity<BaseResponseDTO<Property>> response = propertyRestController.updateProperty(requestDTO, bindingResult);
        
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getMessage().contains("Unexpected error during update"));
    }

    @Test
    void whenSoftDeleteProperty_withValidId_shouldReturnOk() {
        String id = "PROP-1";
        doNothing().when(propertyRestService).softDeleteProperty(id);
        
        ResponseEntity<BaseResponseDTO<Object>> response = propertyRestController.softDeleteProperty(id);
        
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Property has been successfully deactivated.", response.getBody().getMessage());
    }
    
    @Test
    void whenSoftDeleteProperty_serviceThrowsResponseStatusException_shouldReturnCorrectStatus() {
        String id = "PROP-1";
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot deactivate due to bookings")).when(propertyRestService).softDeleteProperty(id);
        
        ResponseEntity<BaseResponseDTO<Object>> response = propertyRestController.softDeleteProperty(id);
        
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Cannot deactivate due to bookings", response.getBody().getMessage());
    }
    
    @Test
    void whenSoftDeleteProperty_serviceThrowsGenericException_shouldReturnInternalServerError() {
        String id = "PROP-1";
        doThrow(new RuntimeException("Unexpected error during delete")).when(propertyRestService).softDeleteProperty(id);
        
        ResponseEntity<BaseResponseDTO<Object>> response = propertyRestController.softDeleteProperty(id);
        
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getMessage().contains("Unexpected error during delete"));
    }

    @Test
    void whenGetPropertyHeaderForAddRoom_withValidId_shouldReturnOk() {
        String id = "PROP-1";
        when(propertyRestService.getPropertyHeader(id)).thenReturn(new PropertyHeaderDTO()); 

        ResponseEntity<BaseResponseDTO<PropertyHeaderDTO>> response = propertyRestController.getPropertyHeaderForAddRoom(id);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Success", response.getBody().getMessage());
    }
    
    @Test
    void whenGetPropertyHeaderForAddRoom_serviceThrowsResponseStatusException_shouldReturnCorrectStatus() {
        String id = "INVALID";
        when(propertyRestService.getPropertyHeader(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Header not found"));

        ResponseEntity<BaseResponseDTO<PropertyHeaderDTO>> response = propertyRestController.getPropertyHeaderForAddRoom(id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Header not found", response.getBody().getMessage());
    }
    
    @Test
    void whenGetPropertyHeaderForAddRoom_serviceThrowsGenericException_shouldReturnInternalServerError() {
        String id = "PROP-1";
        when(propertyRestService.getPropertyHeader(id)).thenThrow(new RuntimeException("Unexpected error on header"));

        ResponseEntity<BaseResponseDTO<PropertyHeaderDTO>> response = propertyRestController.getPropertyHeaderForAddRoom(id);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getMessage().contains("Unexpected error on header"));
    }
    
    @Test
    void whenAddRoomTypes_withValidInput_shouldReturnCreated() {
        AddRoomTypesRequestDTO requestDTO = new AddRoomTypesRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(propertyRestService).addRoomTypesToProperty(any(AddRoomTypesRequestDTO.class));
        
        ResponseEntity<BaseResponseDTO<Object>> response = propertyRestController.addRoomTypes(requestDTO, bindingResult);
        
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Room types added successfully.", response.getBody().getMessage());
    }
    
    @Test
    void whenAddRoomTypes_withInvalidInput_shouldReturnBadRequest() {
        AddRoomTypesRequestDTO requestDTO = new AddRoomTypesRequestDTO();
        BindingResult bindingResult = mockBindingResultWithError("Room type field required");
        
        ResponseEntity<BaseResponseDTO<Object>> response = propertyRestController.addRoomTypes(requestDTO, bindingResult);
        
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Room type field required", response.getBody().getMessage());
    }
    
    @Test
    void whenAddRoomTypes_serviceThrowsResponseStatusException_shouldReturnCorrectStatus() {
        AddRoomTypesRequestDTO requestDTO = new AddRoomTypesRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate room type or floor")).when(propertyRestService).addRoomTypesToProperty(any(AddRoomTypesRequestDTO.class));

        ResponseEntity<BaseResponseDTO<Object>> response = propertyRestController.addRoomTypes(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Duplicate room type or floor", response.getBody().getMessage());
    }

    @Test
    void whenAddRoomTypes_serviceThrowsGenericException_shouldReturnInternalServerError() {
        AddRoomTypesRequestDTO requestDTO = new AddRoomTypesRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Unexpected error during adding room types")).when(propertyRestService).addRoomTypesToProperty(any(AddRoomTypesRequestDTO.class));

        ResponseEntity<BaseResponseDTO<Object>> response = propertyRestController.addRoomTypes(requestDTO, bindingResult);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getMessage().contains("Unexpected error during adding room types"));
    }
}