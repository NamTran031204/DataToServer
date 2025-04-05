package nam.server.repositories;

import nam.server.models.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    Optional<Temperature> findById(int id);

    @Query(value = "SELECT * FROM temperatures t ORDER BY id DESC LIMIT :numberOfSample", nativeQuery = true)
    List<Temperature> getAllTemperature(@Param("numberOfSample") long numberOfSample);

}
