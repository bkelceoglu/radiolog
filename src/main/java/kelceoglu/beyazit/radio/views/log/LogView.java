package kelceoglu.beyazit.radio.views.log;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import kelceoglu.beyazit.radio.data.entity.Adam;
import kelceoglu.beyazit.radio.data.entity.IProcessLogs;
import kelceoglu.beyazit.radio.data.entity.LogForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@PageTitle("Log")
@Route(value = "")
@Uses(Icon.class)
@Slf4j
public class LogView extends Div {
    Button cancel = new Button("CLEAR");
    Button save = new Button ("UPLOAD");
    private final TextField name = new TextField ("NAME");
    private final TextField surname = new TextField ("SURNAME");
    private final TextField callSign = new TextField ("CALLSIGN");
    private final EmailField email = new EmailField ("EMAIL");
    private final TextField fileName = new TextField ("FILE NAME");
    private final Upload logFile;
    private InputStream stream;
    private final int MAX_FILE_SIZE = 50*1024;
    private final MemoryBuffer memoryBuffer;
    private final Binder<LogForm> binder = new Binder<>(LogForm.class);
    private Div content;
    private List<String> lines;

    private Adam adam;

    @Autowired private IProcessLogs processLogs;

    public LogView() {
        this.memoryBuffer = new MemoryBuffer ();
        this.logFile = new Upload (memoryBuffer);
        this.logFile.setDropAllowed (true);
        this.logFile.setAcceptedFileTypes (".edi");
        this.logFile.setMaxFileSize (this.MAX_FILE_SIZE);
        this.fileName.setReadOnly (true);
        this.getFileUploader ();
        this.save.setEnabled (false);
        // addClassName("radio_log");
        add(new H3("RADIO LOG CONTEST " + Calendar.getInstance ().get (Calendar.YEAR)));
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        this.bindFields ();
        clearForm();
        this.binder.addStatusChangeListener (e -> this.save.setEnabled (this.binder.isValid ()));
        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            this.processForm();
            clearForm();
        });
    }

    private void processForm() {
        // create adam here...
        System.out.println (this.name.getValue ());
        System.out.println (this.surname.getValue ());
        System.out.println (this.email.getValue ());
        System.out.println (this.callSign.getValue ());
        try {
            this.lines = IOUtils.readLines (this.stream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace ();
        }
        System.out.println ("lines: " + this.lines);
        File f = new File ("/srv/", String.valueOf (this.callSign.getValue ()));
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter (f, true);
            IOUtils.writeLines (this.lines, "\n", fileWriter);
            IOUtils.close (fileWriter);
        } catch (IOException e) {
            e.printStackTrace ();
        }

        this.saveAdamsLogs (adam);
    }

    private void saveAdamsLogs(Adam a) {
        this.processLogs.saveAdamLogs (a);
    }

    private void bindFields(){
        this.binder.forField (this.email)
                .withValidator (new EmailValidator ("Email seems wrong"))
                .bind (LogForm::getEmail, LogForm::setEmail);
        this.binder.forField (this.name)
                .withValidator (new StringLengthValidator ("between 3 - 12 characters", 3 ,12))
                .bind (LogForm::getName, LogForm::setName);
        this.binder.forField (this.surname)
                .withValidator (new StringLengthValidator ("between 3 - 12 characters", 3,12))
                .bind (LogForm::getSurname, LogForm::setSurname);
        this.binder.forField (this.callSign)
                .withValidator (new StringLengthValidator ("between 3 - 8", 3, 8))
                .bind(LogForm::getCallSign, LogForm::setCallSign);
        this.binder.forField (this.fileName)
                .withValidator (new StringLengthValidator ("empty", 1,12))
                .bind (LogForm::getFileName, LogForm::setFileName);

    }

    private void clearForm() {
        binder.setBean(new LogForm ());
        this.logFile.clearFileList ();
    }

    private Component createFormLayout() {
        // VerticalLayout vertical = new VerticalLayout();
        // vertical.setMaxWidth (200, Unit.PIXELS);
        FormLayout formLayout = new FormLayout();
        formLayout.add(this.name, this.surname, this.callSign, this.email, this.logFile, this.fileName);
        // vertical.add (formLayout);
        return formLayout;
    }
    public void getFileUploader()
    {
        logFile.addSucceededListener(event -> {
            this.stream = memoryBuffer.getInputStream ();
            this.fileName.setValue (event.getFileName ());
        });
        logFile.addFileRejectedListener(event -> {
            Notification.show (event.getErrorMessage (), 5*1000, Notification.Position.TOP_CENTER).addThemeVariants (NotificationVariant.LUMO_ERROR);
        });
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

}
