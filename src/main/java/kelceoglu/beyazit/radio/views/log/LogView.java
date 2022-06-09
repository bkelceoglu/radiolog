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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import kelceoglu.beyazit.radio.data.entity.Competitor;
import kelceoglu.beyazit.radio.utils.CompetitorConfirmationDialog;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Calendar;

@PageTitle("Log")
@Route(value = "")
@Uses(Icon.class)
@Slf4j
public class LogView extends Div {
    Button cancel = new Button ("CLEAR");
    Button save = new Button ("UPLOAD");
    private final TextField name = new TextField ("NAME");
    private final TextField surname = new TextField ("SURNAME");
    private final TextField callSign = new TextField ("CALLSIGN");
    private final EmailField email = new EmailField ("EMAIL");
    private final TextField fileName = new TextField ("FILE NAME");
    private final String tek = "Tekli";
    private final String cok = "Çoklu";
    private  Upload logFile;
    private final RadioButtonGroup<Boolean> spotting = new RadioButtonGroup<> ();
    private final RadioButtonGroup<String> singleOrMulti = new RadioButtonGroup<> ();
    private final TextField operatorsCallSigns = new TextField ("Operator Callsigns");
    private InputStream stream;
    private int MAX_FILE_SIZE = 50 * 1024;
    private MemoryBuffer memoryBuffer;
    private final Binder<Competitor> binder = new Binder<> (Competitor.class);
    private Competitor competitor = new Competitor ();

    @Autowired
    private CompetitorConfirmationDialog competitorConfirmationDialog;

    public LogView () {
        this.prepareSetup ();
    }

    public void prepareSetup() {
        this.memoryBuffer = new MemoryBuffer ();
        this.logFile = new Upload (memoryBuffer);
        this.logFile.setDropAllowed (true);
        this.logFile.setAcceptedFileTypes (".edi");
        this.logFile.setMaxFileSize (this.MAX_FILE_SIZE);
        this.fileName.setReadOnly (true);
        this.setFileUploaderListeners();
        this.save.setEnabled (false);
        this.spotting.setLabel ("Did you use spotting Assistance?");
        this.spotting.setItems (Boolean.TRUE, Boolean.FALSE);
        this.spotting.setValue (Boolean.FALSE);
        this.singleOrMulti.setLabel ("Tek ya da Çoklu Katilim?");
        this.singleOrMulti.setItems (this.tek, this.cok);
        this.singleOrMulti.setValue (this.tek);
        this.callSign.setValueChangeMode (ValueChangeMode.EAGER);
        this.callSign.addValueChangeListener (event -> {
            this.operatorsCallSigns.setValue (this.callSign.getValue ());
        });
        add (new H3 ("TA VHF/UHF CONTEST: " + Calendar.getInstance ().get (Calendar.YEAR)));
        add (createFormLayout ());
        add (createButtonLayout ());

        binder.bindInstanceFields (this);
        this.bindFields ();
        clearForm ();
        this.binder.addStatusChangeListener (e -> this.save.setEnabled (this.binder.isValid ()));
        cancel.addClickListener (e -> clearForm ());
        save.addClickListener (e -> {
            // this.processLogsDialog ();
            // System.out.println (this.binder.getBean ().getCallSign ());
            this.competitorConfirmationDialog.processLogRecord (this.stream, this.binder.getBean ());
            this.clearForm ();
        });
    }



    /*
    private void saveAdamsLogs () {
        System.out.println ("alooooooooo");
        ConfirmDialog
                .createQuestion()
                .withCaption("System alert")
                .withMessage("Do you want to continue?")
                .withOkButton(() -> {
                    System.out.println("YES. Implement logic here.");
                }, ButtonOption.focus(), ButtonOption.caption("YES"))
                .withCancelButton(() -> {
                    System.out.println ("please no derim");
                }, ButtonOption.caption ("NO"))
                .open();

        // process pop - up first then save
        //this.logDetailsDisplay.displayLog ();
        //this.processLogs.saveAdamLogs (this.formWriter.processLogRecord (this.stream));
    }

     */
    private void bindFields () {
        this.binder.forField (this.email)
                .withValidator (new EmailValidator ("Email seems wrong"))
                .bind (Competitor :: getEmail, Competitor :: setEmail);
        this.binder.forField (this.name)
                .withValidator (new StringLengthValidator ("between 3 - 12 characters", 3, 12))
                .bind (Competitor :: getName, Competitor :: setName);
        this.binder.forField (this.surname)
                .withValidator (new StringLengthValidator ("between 3 - 12 characters", 3, 12))
                .bind (Competitor :: getSurname, Competitor :: setSurname);
        this.binder.forField (this.callSign)
                .withValidator (new StringLengthValidator ("between 3 - 8", 3, 8))
                .bind (Competitor :: getCallSign, Competitor :: setCallSign);
        this.binder.forField (this.fileName)
                .withValidator (new StringLengthValidator ("empty", 1, 12))
                .bind (Competitor :: getFileName, Competitor :: setFileName);
        this.binder.forField (this.operatorsCallSigns)
                .withValidator (new StringLengthValidator ("Number of Operators Exceeded", 3, 100))
                .bind (Competitor :: getOperatorsCallSigns, Competitor :: setOperatorsCallSigns);
        this.binder.forField (this.singleOrMulti)
                .withValidator (new StringLengthValidator ("", 0, 10))
                .bind (Competitor :: getSingleOrMulti, Competitor :: setSingleOrMulti);
        this.binder.forField (this.spotting).bind (Competitor::getSpottingAssistance, Competitor::setSpottingAssistance);
    }

    public void clearForm () {
        binder.setBean (new Competitor ());
        this.logFile.clearFileList ();
    }

    private Component createFormLayout () {
        // VerticalLayout vertical = new VerticalLayout();
        // vertical.setMaxWidth (200, Unit.PIXELS);
        FormLayout formLayout = new FormLayout ();
        formLayout.setResponsiveSteps (new FormLayout.ResponsiveStep ("25em", 3));
        formLayout.add (this.name, this.surname, this.callSign,
                        this.email, this.spotting, this.singleOrMulti,
                        this.operatorsCallSigns, this.logFile, this.fileName);
        // vertical.add (formLayout);
        return formLayout;
    }

    public void setFileUploaderListeners () {
        logFile.addSucceededListener (event -> {
            this.stream = memoryBuffer.getInputStream ();
            this.fileName.setValue (event.getFileName ());
        });
        logFile.addFileRejectedListener (event -> {
            Notification.show (event.getErrorMessage (), 5 * 1000, Notification.Position.TOP_CENTER)
                    .addThemeVariants (NotificationVariant.LUMO_ERROR);
        });
    }

    private Component createButtonLayout () {
        HorizontalLayout buttonLayout = new HorizontalLayout ();
        buttonLayout.setAlignItems (FlexComponent.Alignment.CENTER);
        buttonLayout.addClassName ("button-layout");
        save.addThemeVariants (ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add (save);
        buttonLayout.add (cancel);
        return buttonLayout;
    }



}
