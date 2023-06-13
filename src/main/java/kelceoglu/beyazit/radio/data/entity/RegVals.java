package kelceoglu.beyazit.radio.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegVals {
    private String Tdate;
    private String PWWLo;
    private String PBand;
    private String PCLub;
    private String Rcity;
    private String Rcoun;
    private String cqsos;
    private int cqsop;
    @ElementCollection(targetClass=String.class)
    private List<String> qsorecords = new ArrayList<> ();
}
