package nam.server.repositories;

import nam.server.models.Humidity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HumidityRepository extends JpaRepository<Humidity, Long> {
    Optional<Humidity> findById(int id);

    @Query(value = "SELECT * FROM humidity ORDER BY id DESC LIMIT :numberOfSample", nativeQuery = true)
    List<Humidity> getAllHumidity(@Param("numberOfSample") long numberOfSample);
}
