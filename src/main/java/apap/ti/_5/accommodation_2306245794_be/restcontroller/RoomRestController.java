package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.CreateMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.RoomRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:5173")
public class RoomRestController {

    @Autowired
    private RoomRestService roomRestService;

    @PostMapping("/maintenance/add")
    public ResponseEntity<BaseResponseDTO<Object>> addMaintenanceSchedule(@Valid @RequestBody CreateMaintenanceRequestDTO requestDTO) {
        try {
            roomRestService.createMaintenanceSchedule(requestDTO);
            return ResponseEntity.ok(new BaseResponseDTO<>(HttpStatus.OK.value(), "Maintenance schedule added successfully.", new Date(), null));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new BaseResponseDTO<>(e.getStatusCode().value(), e.getReason(), new Date(), null));
        }
    }
}