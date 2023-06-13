package kelceoglu.beyazit.radio.data.entity;

import com.vaadin.flow.component.upload.Upload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Competitor {
    private String name;
    private String surname;
    private String callSign;
    private String email;
    private String fileName;
    private Boolean spottingAssistance;
    private String singleOrMulti;
    private String operatorsCallSigns;
    private int totalScore;
    @NotNull
    private Upload logFile;
    private List<RegVals> regVals = new ArrayList<> ();
}
