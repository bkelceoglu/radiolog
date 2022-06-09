package kelceoglu.beyazit.radio.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import kelceoglu.beyazit.radio.data.entity.Competitor;
import kelceoglu.beyazit.radio.data.entity.CompetitorEntity;
import kelceoglu.beyazit.radio.data.entity.CompetitorRepository;
import kelceoglu.beyazit.radio.data.entity.RegVals;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringComponent
@Slf4j
public class CompetitorConfirmationDialog {

    private Competitor localCompetitor;
    private Ini ini;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CompetitorRepository repository;

    public void processLogRecord(InputStream stream, Competitor competitor) {
        this.localCompetitor = competitor;
        this.writeToAFile (competitor.getCallSign (), stream);
        try {
            Config cfg = new Config ();
            cfg.setEmptyOption (true);
            cfg.setMultiSection (true);
            this.ini = new Ini();
            ini.setConfig (cfg);
            ini.load (FileUtils.openInputStream (new File ("/srv/", competitor.getCallSign () + ".edi")));
            this.processLogsDialog (this.generateLogFormEntity ());
        } catch (IOException e) {
            log.error (e.getLocalizedMessage ());
        }
    }

    private CompetitorEntity generateLogFormEntity() {
        CompetitorEntity competitorEntity =  this.objectMapper.convertValue (this.localCompetitor, CompetitorEntity.class);
        List<RegVals> regListTemp = competitorEntity.getRegVals ();
        ini.remove (ini.get ("Remarks"));
        List<Section> sections = new ArrayList<> (ini.values ());
        AtomicInteger nos = new AtomicInteger (0);
        sections.stream ().forEach (s -> {
            if (s.getName ().contains ("REG")) {
                nos.getAndIncrement ();
            }
        });
        List<Section> sec = ini.getAll (sections.get (0).getName ());
        for (int i = 0; i < ini.values ().size (); i++) {
            if ( sections.get (i).getName ().contains ("REG") ) {
                RegVals regTempVals = new RegVals ();
                List<String> qsorecordstemp = regTempVals.getQsorecords ();
                regTempVals.setTdate ( sec.get (i).get ("TDate") );
                regTempVals.setPWWLo ( sec.get (i).get ( "PWWLo") );
                regTempVals.setPBand ( sec.get (i).get ( "PBand") );
                regTempVals.setPCLub ( sec.get (i).get ( "PClub") == null
                                            ? "no club" : ini.get (competitorEntity.getName (), "PClub"));
                regTempVals.setRcity ( sec.get (i).get ( "RCity") );
                regTempVals.setRcoun ( sec.get (i).get ( "RCoun") );
                regTempVals.setCqsop ( Integer.parseInt (sec.get (i).get ("CQSOP")) );
                String n = sections.get (i + nos.get ()).getName ();
                for ( String optionKey : ini.get (n).keySet () ) {
                    qsorecordstemp.add (optionKey);
                }
                regTempVals.setQsorecords (qsorecordstemp);
                regListTemp.add (regTempVals);
            }
        }
        competitorEntity.setRegVals (regListTemp);
        return competitorEntity;
    }

    private void processLogsDialog(CompetitorEntity competitorEntity) {
        Button saveButton = new Button ("SAVE");
        Button cancelButton = new Button ("CANCEL");
        Dialog logDialog = new Dialog ();
        logDialog.setModal (true);
        logDialog.setCloseOnEsc (false);
        logDialog.setCloseOnOutsideClick (false);
        FormLayout formLayout = new FormLayout ();
        formLayout.setResponsiveSteps (new FormLayout.ResponsiveStep ("25em", 1));
        formLayout.addFormItem (new TextField (), "NAME:");
        formLayout.addFormItem (new TextField (), "SURNAME:");
        HorizontalLayout h = new HorizontalLayout ();
        cancelButton.addClickListener (e -> {
            logDialog.close ();
            FileUtils.deleteQuietly (new File ("/srv/", this.localCompetitor.getCallSign () + ".edi"));
        });
        h.add (saveButton);
        h.add (cancelButton);
        formLayout.add (h);
        logDialog.add (formLayout);
        saveButton.addClickListener (e -> {
            // this.writeToAFile (this.localCompetitor.getCallSign ());
            this.repository.save (competitorEntity);
            logDialog.close ();
        });
        logDialog.open ();
    }

    public void writeToAFile(String callSign, InputStream stream) {
        try {
            File file = new File ("/srv/", callSign + ".edi");
            FileUtils.copyInputStreamToFile (stream, file);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
