package kelceoglu.beyazit.radio.services;

import kelceoglu.beyazit.radio.data.entity.RegVals;
import org.springframework.stereotype.Component;
import java.io.Serializable;

@Component
public interface IProcessLogs extends Serializable {
    void saveAdamLogs(RegVals regVals);
    void deleteAdamsLogs(Long adamId);
    RegVals getAdam(Long oid);
    void updateAdamLogs(RegVals regVals);
    void getAllAdamsLogs();

}
