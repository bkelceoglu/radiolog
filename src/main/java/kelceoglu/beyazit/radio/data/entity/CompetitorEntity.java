package kelceoglu.beyazit.radio.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
public class CompetitorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "oid", nullable = false)
    private Long oid;
    private String name;
    private String surname;
    private String callSign;
    private String email;
    private String fileName;
    private Boolean spottingAssistance;
    private String singleOrMulti;
    private String operatorsCallSigns;
    private int totalScore;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegValsEntity> regValEntities = new ArrayList<> ();

}
