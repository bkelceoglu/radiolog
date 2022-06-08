package kelceoglu.beyazit.radio.services;

import kelceoglu.beyazit.radio.data.entity.Adam;
import org.springframework.stereotype.Component;
import java.io.Serializable;

@Component
public interface IProcessLogs extends Serializable {
    void saveAdamLogs(Adam adam);
    void deleteAdamsLogs(Long adamId);
    Adam getAdam(Long oid);
    void updateAdamLogs(Adam adam);
    void getAllAdamsLogs();

}
