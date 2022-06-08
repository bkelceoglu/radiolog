package kelceoglu.beyazit.radio.services;

import kelceoglu.beyazit.radio.data.entity.Adam;
import kelceoglu.beyazit.radio.data.entity.LogFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
@Service
public class ProcessLogsImpl implements IProcessLogs {
    @Autowired
    private LogFormRepository repository;

    @Override
    public void saveAdamLogs (Adam adam) {

    }

    @Override
    public void deleteAdamsLogs (Long adamId) {

    }

    @Override
    public Adam getAdam (Long oid) {
        return null;
    }

    @Override
    public void updateAdamLogs (Adam adam) {

    }

    @Override
    public void getAllAdamsLogs () {

    }
}
