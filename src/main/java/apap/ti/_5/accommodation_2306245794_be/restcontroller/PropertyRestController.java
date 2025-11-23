package apap.ti._5.accommodation_2306245794_be.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.AddRoomTypesRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyHeaderDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.PropertyRestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
// @CrossOrigin(origins = {"http://2306245794-fe.hafizmuh.site", "http://localhost:5173"})
@CrossOrigin(origins ="${CORS_ALLOWED_ORIGINS}")
@RequiredArgsConstructor
public class PropertyRestController {

    private final PropertyRestService propertyRestService;

    @GetMapping("")
    public ResponseEntity<BaseResponseDTO<List<PropertyResponseDTO>>> getAllProperties() {
        var response = new BaseResponseDTO<List<PropertyResponseDTO>>();
        try {
            List<PropertyResponseDTO> properties = propertyRestService.getAllProperties();
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success");
            response.setData(properties);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred: " + e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<PropertyDetailDTO>> getPropertyDetailById(@PathVariable("id") String id) {
        var response = new BaseResponseDTO<PropertyDetailDTO>();
        try {
            PropertyDetailDTO propertyDetail = propertyRestService.getPropertyDetailById(id);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success");
            response.setData(propertyDetail);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            response.setStatus(e.getStatusCode().value());
            response.setMessage(e.getReason());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(e.getStatusCode()).body(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred: " + e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("")
    public ResponseEntity<BaseResponseDTO<Property>> createProperty(
        @Valid @RequestBody CreatePropertyRequestDTO createPropertyRequestDTO,
        BindingResult bindingResult
    ) {
        var response = new BaseResponseDTO<Property>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages);
            response.setData(null);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Property newProperty = propertyRestService.createProperty(createPropertyRequestDTO);
            
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Property created successfully");
            response.setData(newProperty);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ResponseStatusException e) {
            response.setStatus(e.getStatusCode().value());
            response.setMessage(e.getReason());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(e.getStatusCode()).body(response);
        
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred: " + e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/update/{id}")
    public ResponseEntity<BaseResponseDTO<PropertyDetailDTO>> getPropertyForUpdate(@PathVariable("id") String id) {
        return getPropertyDetailById(id);
    }
    
    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO<Property>> updateProperty(
        @Valid @RequestBody UpdatePropertyRequestDTO updatePropertyRequestDTO,
        BindingResult bindingResult
    ) {
        var response = new BaseResponseDTO<Property>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages);
            response.setData(null);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Property updatedProperty = propertyRestService.updateProperty(updatePropertyRequestDTO);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Property updated successfully");
            response.setData(updatedProperty);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            response.setStatus(e.getStatusCode().value());
            response.setMessage(e.getReason());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(e.getStatusCode()).body(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred: " + e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponseDTO<Object>> softDeleteProperty(@PathVariable("id") String id) {
        var response = new BaseResponseDTO<>();
        try {
            propertyRestService.softDeleteProperty(id);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Property has been successfully deactivated.");
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            response.setStatus(e.getStatusCode().value());
            response.setMessage(e.getReason());
            response.setTimestamp(new Date());
            return ResponseEntity.status(e.getStatusCode()).body(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred: " + e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/updateroom/{id}")
    public ResponseEntity<BaseResponseDTO<PropertyHeaderDTO>> getPropertyHeaderForAddRoom(@PathVariable("id") String id) {
        var response = new BaseResponseDTO<PropertyHeaderDTO>();
        try {
            PropertyHeaderDTO header = propertyRestService.getPropertyHeader(id);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success");
            response.setData(header);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            response.setStatus(e.getStatusCode().value());
            response.setMessage(e.getReason());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(e.getStatusCode()).body(response);
        
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred: " + e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/updateroom")
    public ResponseEntity<BaseResponseDTO<Object>> addRoomTypes(
        @Valid @RequestBody AddRoomTypesRequestDTO requestDTO,
        BindingResult bindingResult
    ) {
        var response = new BaseResponseDTO<Object>();

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages);
            response.setData(null);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            propertyRestService.addRoomTypesToProperty(requestDTO);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Room types added successfully.");
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ResponseStatusException e) {
            response.setStatus(e.getStatusCode().value());
            response.setMessage(e.getReason());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(e.getStatusCode()).body(response);
        
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred: " + e.getMessage());
            response.setData(null);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponseDTO<Object> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        var response = new BaseResponseDTO<Object>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(errorMessages);
        response.setData(null);
        response.setTimestamp(new Date());
        return response;
    }
}