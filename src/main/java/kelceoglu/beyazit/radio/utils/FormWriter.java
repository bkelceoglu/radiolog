package kelceoglu.beyazit.radio.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import kelceoglu.beyazit.radio.data.entity.Adam;
import kelceoglu.beyazit.radio.data.entity.LogForm;
import kelceoglu.beyazit.radio.data.entity.LogFormEntity;
import kelceoglu.beyazit.radio.views.log.LogView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringComponent
@Slf4j
public class FormWriter {

    private List<String>  lines;
    private InputStream localStream;
    private LogForm localLogForm;
    private Ini ini;

    public void processLogRecord(InputStream stream, LogForm logForm) {
        this.localStream = stream;
        this.localLogForm = logForm;
        try {
            Config cfg = new Config ();
            cfg.setEmptyOption (true);
            cfg.setMultiSection (true);
            this.ini = new Ini();
            ini.setConfig (cfg);
            ini.load (stream);
            this.processLogsDialog (this.generateLogFormEntity ());
        } catch (IOException e) {
            log.error (e.getLocalizedMessage ());
        }
    }

    private LogFormEntity generateLogFormEntity() {
        LogFormEntity logFormEntity = new LogFormEntity ();
        ini.values ().stream ().forEach (e -> {
            System.out.println (e.getName ());
            if (e.getName ().contains ("QSO")) {
                Section s = ini.get (e.getName ());
                for (String optionKey : s.keySet ()) {
                    System.out.println (optionKey);
                }
            }
        });
        System.out.println ( ini.get ("REG1TEST;1", "TName", String.class));
        return logFormEntity;
    }

    private void processLogsDialog(LogFormEntity l) {
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
        });
        h.add (saveButton);
        h.add (cancelButton);
        formLayout.add (h);
        logDialog.add (formLayout);
        saveButton.addClickListener (e -> {
            // save to db

            // save to a file
            try {
                this.lines = IOUtils.readLines (this.localStream, "UTF-8");
                this.localStream.close ();
                this.writeToAFile (this.localLogForm.getCallSign ());
            } catch (IOException ex) {
                ex.printStackTrace ();
            }
            logDialog.close ();
        });
        logDialog.open ();
    }



    public void writeToAFile(String callSign) {
        try {
            File f = new File ("/srv/", (callSign+".edi"));
            FileWriter fileWriter = null;
            fileWriter = new FileWriter (f, true);
            IOUtils.writeLines (lines, "\n", fileWriter);
            IOUtils.close (fileWriter);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
