package apap.ti._5.accommodation_2306245794_be.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.PropertyRestService;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:5173")
public class PropertyRestController {

    @Autowired
    private PropertyRestService propertyRestService;

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
    public ResponseEntity<BaseResponseDTO<Property>> createProperty(@Valid @RequestBody CreatePropertyRequestDTO createPropertyRequestDTO) {
        var response = new BaseResponseDTO<Property>();
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
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponseDTO<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var response = new BaseResponseDTO<Object>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(new Date());

        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        response.setMessage(errorMessages);
        response.setData(null);

        return response;
    }
}