package kelceoglu.beyazit.radio.services;

import kelceoglu.beyazit.radio.data.entity.RegVals;
import kelceoglu.beyazit.radio.data.entity.CompetitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
@Service
public class ProcessLogsImpl implements IProcessLogs {
    @Autowired
    private CompetitorRepository repository;

    @Override
    public void saveAdamLogs (RegVals regVals) {

    }

    @Override
    public void deleteAdamsLogs (Long adamId) {

    }

    @Override
    public RegVals getAdam (Long oid) {
        return null;
    }

    @Override
    public void updateAdamLogs (RegVals regVals) {

    }

    @Override
    public void getAllAdamsLogs () {

    }
}
