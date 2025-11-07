package apap.ti._5.accommodation_2306245794_be.restcontroller;

import apap.ti._5.accommodation_2306245794_be.restdto.BaseResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.BerandaResponseDTO;
import apap.ti._5.accommodation_2306245794_be.restservice.BerandaRestService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin; 

import java.util.Date;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BerandaRestController {

    private final BerandaRestService berandaRestService;

    @GetMapping("/")
    public ResponseEntity<BaseResponseDTO<BerandaResponseDTO>> getBerandaStatistics() {
        var response = new BaseResponseDTO<BerandaResponseDTO>();
        try {
            BerandaResponseDTO stats = berandaRestService.getBerandaStatistics();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Success");
            response.setData(stats);
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
}