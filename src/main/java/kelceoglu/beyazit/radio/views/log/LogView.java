package kelceoglu.beyazit.radio.views.log;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.internal.MessageDigestUtil;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import kelceoglu.beyazit.radio.data.entity.LogForm;
import lombok.extern.flogger.Flogger;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.juli.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

@PageTitle("Log")
@Route(value = "")
@Uses(Icon.class)
@Slf4j
public class LogView extends Div {
    Button cancel = new Button("CLEAR");
    Button save = new Button ("UPLOAD");
    private TextField name = new TextField ("NAME");
    private TextField surname = new TextField ("SURNAME");
    private TextField callSign = new TextField ("CALLSIGN");
    private EmailField email = new EmailField ("EMAIL");
    private Upload logFile;
    private MultiFileMemoryBuffer multiFileMemoryBuffer;
    private Binder<LogForm> binder = new Binder<>(LogForm.class);
    private Div content;
    private List<String> lines;

    public LogView() {
        this.multiFileMemoryBuffer = new MultiFileMemoryBuffer ();
        this.logFile = new Upload (multiFileMemoryBuffer);
        this.logFile.setDropAllowed (true);
        this.logFile.setAcceptedFileTypes (".edi");

        addClassName("radio_log");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        this.bindFields ();
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
           clearForm();
        });
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

    }

    private void clearForm() {
        binder.setBean(new LogForm ());
    }

    private Component createTitle() {
        return new H3("RADIO LOG CONTEST UPLOAD");

    }

    private Component createFormLayout() {
        // VerticalLayout vertical = new VerticalLayout();
        // vertical.setMaxWidth (200, Unit.PIXELS);
        FormLayout formLayout = new FormLayout();
        formLayout.add(this.name, this.surname, this.callSign, this.email, this.logFile);
        // vertical.add (formLayout);
        return formLayout;
    }
    public Component getFileUploder()
    {
        Div output = new Div();

        logFile.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  multiFileMemoryBuffer
                                                          .getInputStream(event.getFileName()));
        });
        logFile.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            log.error ("file rejected");
        });
        HorizontalLayout h = new HorizontalLayout(logFile, output);
        h.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        return h;
    }

    private Component createComponent(
            String mimeType, String fileName,
            InputStream stream)
    {
        content = new Div();
        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
                                    mimeType, MessageDigestUtil.sha256(stream.toString()));
        try {
            this.lines = IOUtils.readLines (stream);
        } catch (IOException e) {
            log.error (e.getMessage ());
        }
        content.setText(text);
        return content;
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
