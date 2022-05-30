package kelceoglu.beyazit.radio.data.entity;

import com.vaadin.flow.component.upload.Upload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LogForm {
    private String name;
    private String surname;
    private String callSign;
    private String email;
    private String fileName;
    @NotNull
    private Upload logFile;
}
