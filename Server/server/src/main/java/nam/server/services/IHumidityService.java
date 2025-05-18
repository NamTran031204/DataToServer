package nam.server.services;

import nam.server.dtos.DataDTO;
import nam.server.models.Humidity;
import nam.server.models.Temperature;

import java.util.List;

public interface IHumidityService {
    Humidity saveHumid(DataDTO dataDTO);
    List<Humidity> getAllHumid(int numberOfSample);
//    Humidity getHumid();
    void deleteHumid(int id);
}
