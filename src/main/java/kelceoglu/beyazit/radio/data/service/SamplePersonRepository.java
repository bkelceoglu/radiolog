package kelceoglu.beyazit.radio.data.service;

import java.util.UUID;
import kelceoglu.beyazit.radio.data.entity.SamplePerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID> {

}