package nam.server.controllers;


import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nam.server.dtos.DataDTO;
import nam.server.models.Humidity;
import nam.server.services.HumidityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public ResponseEntity<?> getAllHumid(
            @PathVariable("numberOfSample") int numberOfSample
    ){
        return ResponseEntity.ok(humidityService.getAllHumid(numberOfSample));
    }

//    @GetMapping("")
//    public ResponseEntity<?> getHumid(){
//        return ResponseEntity.ok("");
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHumid(@PathVariable int id){
        try{
            humidityService.deleteHumid(id);
            return ResponseEntity.ok("humidity data has been deleted");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generateFakeData")
    public ResponseEntity<?> generateFakeData(){
        Faker faker = new Faker();
        Random random = new Random();

        // Define the starting timestamp
        LocalDateTime startTime = LocalDateTime.of(2025, 3, 29, 14, 30, 0);

        // Define the DateTimeFormatter for the required format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<Humidity> generatedData = new ArrayList<>();

        for(int i=0; i<300; i++){
            // Generate random temperature between 0 and 100 percent
            float humidData = random.nextFloat() * 100;

            // Calculate the current timestamp by adding i minutes to the start time
            LocalDateTime currentTime = startTime.plusMinutes(i);
            String timestamp = currentTime.format(formatter);

            // Create a DataDTO to use the existing service method
            DataDTO dataDTO = new DataDTO();
            dataDTO.setHumidity(humidData);
            dataDTO.setTimestamp(timestamp);

            // Save the data using the service
            try {
                Humidity humidity = humidityService.saveHumid(dataDTO);
                generatedData.add(humidity);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error generating fake data: " + e.getMessage());
            }        }

        return ResponseEntity.ok("Successfully generated " + generatedData.size() + " fake humidity records");
    }
}
