package kelceoglu.beyazit.radio.data.entity;

import com.vaadin.flow.component.upload.Upload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

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
    @NotNull
    private Upload logFile;
}
