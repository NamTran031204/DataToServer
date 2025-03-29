package nam.server.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nam.server.dtos.DataDTO;
import nam.server.models.Temperature;
import nam.server.services.TemperatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/temperature")
@RequiredArgsConstructor
public class TemperatureController {

    private final TemperatureService temperatureService;

    @PostMapping("")
    public ResponseEntity<?> postTempData(
            @Valid @RequestBody DataDTO dataDTO
    ){
        try{
            Temperature temperature = temperatureService.saveTemp(dataDTO);
            return ResponseEntity.ok(temperature);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numberOfSample}")
    public ResponseEntity<?> getTempData(
            @PathVariable int numberOfSample
    ){
        List<Temperature> listOfTemp = temperatureService.getAllTemp(numberOfSample);
        return ResponseEntity.ok(listOfTemp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemp(
            @PathVariable int id
    ){
        try {
            temperatureService.deleteTemp(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("temperature data has been deleted");
    }
}
