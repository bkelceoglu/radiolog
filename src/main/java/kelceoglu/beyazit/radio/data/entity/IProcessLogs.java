package kelceoglu.beyazit.radio.data.entity;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public interface IProcessLogs extends Serializable {
    void saveAdamLogs(Adam adam);
    void deleteAdamsLogs(Long adamId);
    void updateAdamLogs(Adam adam);
    void getAllAdamsLogs();

}
