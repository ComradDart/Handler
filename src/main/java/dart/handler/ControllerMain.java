package dart.handler;

import dart.handler.handlers.AttachmentsHandler;
import dart.handler.handlers.EmlHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.A;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public class ControllerMain {
    private static File emlDir;
    private static File handledDir;
    private final String iniPath = "path.ini";

    //Static block to load defaults from ini
    {
        try {
            Wini ini = new Wini(new File(iniPath));
            Profile.Section section = ini.get("Path");
            if (!section.get("initialEmlDir").equals("")) {
                emlDir = new File(section.get("initialEmlDir"));
            }
            if (!section.get("initialHandledDir").equals("")) {
                handledDir = new File(section.get("initialHandledDir"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button emlBtn;

    @FXML
    private TextField emlText;

    @FXML
    private TextField handledText;

    @FXML
    private Button handledBtn;

    @FXML
    private Button handleAttachBtn;

    @FXML
    private Button handleEmlsBtn;

    @FXML
    private Label emlsHandled;

    @FXML
    private Label attachHandled;

    @FXML
    void initialize() {
        //Setiing up dir with emls
        emlBtn.setOnAction(event -> {
            emlText.clear();
            DirectoryChooser dc = new DirectoryChooser();
            if (emlDir != null) {
                dc.setInitialDirectory(emlDir);
            }
            emlDir = dc.showDialog(new Stage());
            try {
                Wini wini = new Wini(new File(iniPath));
                wini.put("Path", "initialEmlDir", emlDir.getParent());
                wini.store();
            } catch (IOException e) {
                e.printStackTrace();
            }
            emlText.setText(emlDir.getAbsolutePath());
        });

        //Setting up handled dir
        handledBtn.setOnAction(event -> {
            handledText.clear();
            DirectoryChooser dc = new DirectoryChooser();
            if (handledDir != null) {
                dc.setInitialDirectory(handledDir);
            }
            handledDir = dc.showDialog(new Stage());
            try {
                Wini wini = new Wini(new File(iniPath));
                wini.put("Path", "initialHandledDir", handledDir.getParent());
                wini.store();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handledText.setText(handledDir.getAbsolutePath());
        });

        //Handle eml btn logics
        handleEmlsBtn.setOnAction(event -> {
            EmlHandler emlHandler = new EmlHandler();
            emlHandler.handleEml();
            emlsHandled.setText("eml файлы обработаны");
        });

        //Handle attachments btn logics
        handleAttachBtn.setOnAction(event -> {
            AttachmentsHandler handler = new AttachmentsHandler();
            handler.handleAttachments();
            attachHandled.setText("Вложения обработаны!");
        });
    }

    public static File getEmlDir() {
        return emlDir;
    }

    public static File getHandledDir() {
        return handledDir;
    }
}
