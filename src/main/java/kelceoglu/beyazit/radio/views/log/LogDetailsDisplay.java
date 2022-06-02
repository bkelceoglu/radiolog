package kelceoglu.beyazit.radio.views.log;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class LogDetailsDisplay extends Dialog{


    Button saveButton = new Button ("SAVE");
    Button cancelButton = new Button("CANCEL");

    @Override
    public Registration addDialogCloseActionListener (ComponentEventListener<DialogCloseActionEvent> listener) {
        System.out.println ("closed mlosed");
        return super.addDialogCloseActionListener (listener);
    }

    public void displayLog() {

        setModal (true);
        setCloseOnEsc (false);
        setCloseOnOutsideClick (false);
        FormLayout formLayout = new FormLayout ();
        formLayout.setResponsiveSteps (new FormLayout.ResponsiveStep ("25em", 1));
        formLayout.addFormItem (new TextField (), "NAME:");
        formLayout.addFormItem (new TextField (), "SURNAME:");
        formLayout.add (this.buttonLayout ());
        add (formLayout);
        this.saveButton.addClickListener (e -> {
            close ();
        });
        open ();
    }

    private Component buttonLayout() {
        HorizontalLayout h = new HorizontalLayout ();
        this.cancelButton.addClickListener (e -> close ());
        h.add (saveButton);
        h.add (cancelButton);
        return h;
    }

}
