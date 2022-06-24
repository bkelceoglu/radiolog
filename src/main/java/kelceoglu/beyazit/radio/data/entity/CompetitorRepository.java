package kelceoglu.beyazit.radio.data.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitorRepository extends JpaRepository<CompetitorEntity, Long> {

    CompetitorEntity getByCallSign (String callSign);

}
