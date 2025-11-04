package apap.ti._5.accommodation_2306245794_be.restservice;

import java.util.List;
import apap.ti._5.accommodation_2306245794_be.restdto.response.PropertyResponseDTO;

public interface PropertyRestService {
    List<PropertyResponseDTO> getAllProperties();
}