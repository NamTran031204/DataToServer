package nam.server.controllers;

import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nam.server.dtos.DataDTO;
import nam.server.models.Temperature;
import nam.server.services.TemperatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @PostMapping("/generateFakeData")
    public ResponseEntity<?> generateFakeData(){
        Faker faker = new Faker();
        Random random = new Random();

        // Define the starting timestamp
        LocalDateTime startTime = LocalDateTime.of(2025, 3, 29, 14, 30, 0);

        // Define the DateTimeFormatter for the required format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<Temperature> generatedData = new ArrayList<>();

        for(int i=0; i<300; i++){
            // Generate random temperature between 0 and 50
            float tempData = random.nextFloat() * 50;

            // Calculate the current timestamp by adding i minutes to the start time
            LocalDateTime currentTime = startTime.plusMinutes(i);
            String timestamp = currentTime.format(formatter);

            // Create a DataDTO to use the existing service method
            DataDTO dataDTO = new DataDTO();
            dataDTO.setTemperature(tempData);
            dataDTO.setTimestamp(timestamp);

            // Save the data using the service
            try {
                Temperature temperature = temperatureService.saveTemp(dataDTO);
                generatedData.add(temperature);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error generating fake data: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Successfully generated " + generatedData.size() + " fake temperature records");
    }
}
