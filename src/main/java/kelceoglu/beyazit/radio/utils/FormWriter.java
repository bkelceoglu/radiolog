package kelceoglu.beyazit.radio.utils;

import com.vaadin.flow.spring.annotation.SpringComponent;
import kelceoglu.beyazit.radio.data.entity.Adam;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@Slf4j
public class FormWriter {

    private List<String> lines;

    public Adam processLogRecord(InputStream stream) {
        Adam adam = new Adam ();
        try {
            this.lines = IOUtils.readLines (stream, "UTF-8");
            // create adam here
        } catch (IOException e) {
            log.error (e.getLocalizedMessage ());
        }
        return adam;
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
