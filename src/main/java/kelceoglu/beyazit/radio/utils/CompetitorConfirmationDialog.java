package kelceoglu.beyazit.radio.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import kelceoglu.beyazit.radio.data.entity.Competitor;
import kelceoglu.beyazit.radio.data.entity.CompetitorEntity;
import kelceoglu.beyazit.radio.data.entity.CompetitorRepository;
import kelceoglu.beyazit.radio.data.entity.RegValsEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


@SpringComponent
@Slf4j
public class CompetitorConfirmationDialog {

    private Competitor localCompetitor;
    private Ini ini;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CompetitorRepository repository;
    @Autowired private JavaMailSender mailSender;
    private String path;
    private String tempPath = "/srv/tmp/";


    public void processLogRecord(InputStream stream, Competitor competitor) {

        // after save button clicked.
        competitor.setCallSign (competitor.getCallSign ().replace ("/", "-"));
        this.localCompetitor = competitor;
        this.writeToAFile (competitor.getCallSign (), stream, true);
        try {
            Config cfg = new Config ();
            cfg.setEmptyOption (true);
            cfg.setMultiSection (true);
            this.ini = new Ini();
            ini.setConfig (cfg);
            ini.load (FileUtils.openInputStream (new File (this.tempPath, competitor.getCallSign () + ".edi")));
            this.processLogsDialog (this.generateLogFormEntity ());
        } catch (IOException e) {
            log.error (e.getLocalizedMessage ());
        }
    }
    private CompetitorEntity generateLogFormEntity() {
        CompetitorEntity competitorEntity =  this.objectMapper.convertValue (this.localCompetitor, CompetitorEntity.class);
        List<RegValsEntity> regListTemp = competitorEntity.getRegValEntities ();
        ini.remove (ini.get ("Remarks"));
        List<Section> sections = new ArrayList<> (ini.values ());
        AtomicInteger nos = new AtomicInteger (0);
        sections.forEach (s -> {
            if (s.getName ().contains ("REG")) {
                nos.getAndIncrement ();
            }
        });
        List<Section> sec = ini.getAll (sections.get (0).getName ());
        IntStream.range (0, ini.values ().size ()).filter (i -> sections.get (i).getName ().contains ("REG"))
                .forEach (i -> {
                    RegValsEntity regTempVals = new RegValsEntity ();
                    List<String> qsorecordstemp = regTempVals.getQsorecords ();
                    regTempVals.setTdate (sec.get (i).get ("TDate"));
                    regTempVals.setPWWLo (sec.get (i).get ("PWWLo"));
                    regTempVals.setPBand (sec.get (i).get ("PBand"));
                    regTempVals.setPCLub (sec.get (i).get ("PClub") == null ? "no club" : ini.get (competitorEntity.getName (), "PClub"));
                    regTempVals.setRcity (sec.get (i).get ("RCity"));
                    regTempVals.setRcoun (sec.get (i).get ("RCoun"));
                    regTempVals.setCqsos (sec.get (i).get ("CQSOs"));
                    regTempVals.setCqsop (Integer.parseInt (sec.get (i).get ("CQSOP")));
                    String n = sections.get (i + nos.get ()).getName ();
                    qsorecordstemp.addAll (ini.get (n).keySet ());
                    regTempVals.setQsorecords (qsorecordstemp);
                    regListTemp.add (regTempVals);
                });
        competitorEntity.setRegValEntities (regListTemp);
        int score = 0;
        for (RegValsEntity r : regListTemp) score = score + r.getCqsop ();
        competitorEntity.setTotalScore (score);
        return competitorEntity;
    }

    private void processLogsDialog(CompetitorEntity competitorEntity) {
        Button saveButton = new Button ("ONAY");
        Button cancelButton = new Button ("İPTAL");
        TextField callSignField = new TextField ("Callsign", competitorEntity.getCallSign ());
        TextField pwwloField = new TextField ("Locator", String.valueOf (competitorEntity.getRegValEntities ().get (0).getPWWLo ()), "");
        TextField pSectField = new TextField ("Single/Multi", competitorEntity.getSingleOrMulti (), "");
        Label pBandField = new Label ("PBand");
        TextField tScore = new TextField ("Toplam Puan", String.valueOf (competitorEntity.getTotalScore ()), "");
        Dialog logDialog = new Dialog ();
        logDialog.setModal (true);
        logDialog.setCloseOnEsc (false);
        logDialog.setCloseOnOutsideClick (false);
        VerticalLayout v = new VerticalLayout ();
        v.add (new H3 (competitorEntity.getCallSign () + " İçin Log Özeti"));

        FormLayout formLayout = new FormLayout ();
        formLayout.setResponsiveSteps (new FormLayout.ResponsiveStep ("25em", 1));
        formLayout.add(callSignField, pwwloField, pSectField, pBandField, this.getScoreValues (competitorEntity), tScore);
        v.add (formLayout);
        HorizontalLayout h = new HorizontalLayout ();
        cancelButton.addClickListener (e -> {
            logDialog.close ();
            FileUtils.deleteQuietly (new File (this.tempPath, this.localCompetitor.getCallSign () + ".edi"));
        });
        h.add (saveButton);
        h.add (cancelButton);
        v.add (h);
        logDialog.add (v);
        saveButton.addClickListener (e -> {
            try {
                CompetitorEntity temp = this.repository.getByCallSign (competitorEntity.getCallSign ());
                if ( temp.getCallSign ().equals (competitorEntity.getCallSign ()) ) {
                    this.repository.delete (temp);
                }
            } catch (NullPointerException nullPointerException) {
                log.error (nullPointerException.getLocalizedMessage ());
            } finally {
                this.createRealPath (competitorEntity);
                this.writeToAFile (competitorEntity.getCallSign (), null, false);
                this.repository.save (competitorEntity);
                this.informUser ();
                this.mailToUser (competitorEntity.getEmail ());
                logDialog.close ();
            }
        });
        logDialog.open ();
    }

    private void createRealPath (CompetitorEntity competitorEntity) {
        if ( competitorEntity.getSingleOrMulti ().equals ("Single FM-Tek Band") ) {
            String s = competitorEntity.getRegValEntities ()
                    .get (0)
                    .getPBand ()
                    .split (" ")[0];
            this.path = "/srv/" + competitorEntity.getSingleOrMulti () + "/"
                    +  s
                    + "/";
        } else {
            this.path = "/srv/" + competitorEntity.getSingleOrMulti () + "/";
        }
        System.out.println ("PATH: " + this.path);

    }
    private void mailToUser(String emailAddress) {
        SimpleMailMessage message = new SimpleMailMessage ();
        message.setSubject ("LOG KAYDEDİLDİ");
        message.setText ("LOG KAYDEDİLDİ");
        message.setTo (emailAddress);
        message.setBcc ("oguzhan@kayhan.name.tr");
        this.mailSender.send (message);
    }

    private void informUser() {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout ();
        dialog.add(dialogLayout);
        dialogLayout.add (new Label ("LOG KAYDEDİLMİŞTİR. BAŞARILAR"));
        dialogLayout.add(new Button("KAPAT", e -> dialog.close()));
        dialog.open ();
    }

    private Grid getScoreValues(CompetitorEntity c) {
        List<RegValsEntity> regValEntities = c.getRegValEntities ();
        Grid<RegValsEntity> scoreGrid = new Grid<> (RegValsEntity.class, false);
        // scoreGrid.setMaxHeight ();
        scoreGrid.addColumn (RegValsEntity ::getPBand).setHeader ("PBand");
        scoreGrid.addColumn (RegValsEntity ::getCqsos).setHeader ("CQSOs");
        scoreGrid.addColumn (RegValsEntity ::getCqsop).setHeader ("CQSOP");
        scoreGrid.setItems (regValEntities);
        return  scoreGrid;
    }

    public void writeToAFile(String callSign, InputStream stream,  boolean temp) {
        try {
            File file;
            if (temp) {
                file = new File (this.tempPath, callSign + ".edi");
                FileUtils.copyInputStreamToFile (stream, file);
            } else {
                FileUtils.copyFile (new File (this.tempPath + callSign + ".edi"), new File (this.path + callSign + ".edi"));
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
