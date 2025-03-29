package nam.server.services;

import lombok.RequiredArgsConstructor;
import nam.server.dtos.DataDTO;
import nam.server.models.Humidity;
import nam.server.models.Temperature;
import nam.server.repositories.HumidityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HumidityService implements IHumidityService{

    private final HumidityRepository humidityRepository;

    @Override
    public Humidity saveHumid(DataDTO dataDTO) {
        LocalDateTime timeCollected;
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            timeCollected = LocalDateTime.parse(dataDTO.getTimestamp(), dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timestamp format. Expected format: yyyy-MM-dd'T'HH:mm:ss'Z'");
        }
        Humidity existingHumid = Humidity.builder()
                .humid(dataDTO.getHumidity())
                .collectedTime(timeCollected)
                .build();
        return humidityRepository.save(existingHumid);
    }

    @Override
    public List<Humidity> getAllHumid(int numberOfSample) {
        return humidityRepository.getAllHumidity(numberOfSample);
    }

    @Override
    public void deleteHumid(int id) {
        Humidity humidity = humidityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot find humidity data by id: " + id));
        humidityRepository.delete(humidity);
    }
}
