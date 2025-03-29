package nam.server.services;

import nam.server.dtos.DataDTO;
import nam.server.models.Temperature;

import java.util.List;

public interface ITemperatureService {
    Temperature saveTemp(DataDTO dataDTO);
    List<Temperature> getAllTemp(int numberOfSample);
    void deleteTemp(int id);
}
