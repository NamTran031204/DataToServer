package nam.server.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nam.server.dtos.DataDTO;
import nam.server.models.Humidity;
import nam.server.services.HumidityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("api/v1/humidity")
@RestController
public class HumidityController {

    private final HumidityService humidityService;

    @PostMapping("")
    public ResponseEntity<?> postHumid(
            @Valid @RequestBody DataDTO dataDTO
    ){
        try{
            Humidity existingHumidity = humidityService.saveHumid(dataDTO);
            return ResponseEntity.ok(existingHumidity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numberOfSample}")
    public ResponseEntity<?> getHumid(
            @PathVariable("numberOfSample") int numberOfSample
    ){
        return ResponseEntity.ok(humidityService.getAllHumid(numberOfSample));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHumid(@PathVariable int id){
        try{
            humidityService.deleteHumid(id);
            return ResponseEntity.ok("humidity data has been deleted");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
