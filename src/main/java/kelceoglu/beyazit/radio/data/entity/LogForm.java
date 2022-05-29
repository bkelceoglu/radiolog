package kelceoglu.beyazit.radio.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LogForm {
    private String name;
    private String surname;
    private String callSign;
    private String email;
    private String logFile;
}
