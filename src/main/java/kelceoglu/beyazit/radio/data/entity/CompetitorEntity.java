package kelceoglu.beyazit.radio.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
public class CompetitorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long oid;
    private String name;
    private String surname;
    private String callSign;
    private String email;
    private String fileName;
    private Boolean spottingAssistance;
    private String singleOrMulti;
    private String operatorsCallSigns;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegVals> regVals = new ArrayList<> ();

}
