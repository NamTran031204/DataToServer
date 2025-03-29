package nam.server.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import nam.server.dtos.DataDTO;
import nam.server.models.Temperature;
import nam.server.repositories.TemperatureRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemperatureService implements ITemperatureService{

    private final TemperatureRepository temperatureRepository;

    @Override
    public Temperature saveTemp(DataDTO dataDTO)  {
        LocalDateTime timeCollected;
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            timeCollected = LocalDateTime.parse(dataDTO.getTimestamp(), dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timestamp format. Expected format: yyyy-MM-dd'T'HH:mm:ss'Z'");
        }
        Temperature existingTemp = Temperature.builder()
                .temp(dataDTO.getTemperature())
                .collectedTime(timeCollected)
                .build();
        return temperatureRepository.save(existingTemp);
    }

    @Override
    public List<Temperature> getAllTemp(int numberOfSample) {
        return temperatureRepository.getAllTemperature(numberOfSample);
    }

    @Override
    public void deleteTemp(int id) {
        Temperature temperature = temperatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot find temperature data with id: " + id));
        temperatureRepository.delete(temperature);
    }
}
