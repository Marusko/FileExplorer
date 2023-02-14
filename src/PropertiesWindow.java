import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class PropertiesWindow {
    private final File file;
    private final String stylesheet;
    public PropertiesWindow(File file, String stylesheet) {
        this.file = file;
        this.stylesheet = stylesheet;
        this.setUI();
    }

    private void setUI() {
        Stage s = new Stage();

        s.setTitle(this.getName());
        s.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder_small.png")).toExternalForm()));

        Label nameLabel = new Label("Name: ");
        Label name = new Label(this.getName());
        nameLabel.setStyle("-fx-font-size: 15px; -fx-min-width: 50px");
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: normal");
        ScrollPane nameSP = new ScrollPane(name);
        HBox nameBox = new HBox(nameLabel, nameSP);

        Label typeLabel = new Label("Type: ");
        Label type = new Label(this.getType());
        typeLabel.setStyle("-fx-font-size: 15px; -fx-min-width: 50px");
        type.setStyle("-fx-font-size: 15px; -fx-font-weight: normal");
        ScrollPane typeSP = new ScrollPane(type);
        HBox typeBox = new HBox(typeLabel, typeSP);

        Label pathLabel = new Label("Path: ");
        Label path = new Label(this.file.getPath());
        pathLabel.setStyle("-fx-font-size: 15px; -fx-min-width: 50px");
        path.setStyle("-fx-font-size: 15px; -fx-font-weight: normal");
        ScrollPane pathSP = new ScrollPane(path);
        HBox pathBox = new HBox(pathLabel, pathSP);

        Label sizeLabel = new Label("Size: ");
        Label size = new Label(this.getSize());
        sizeLabel.setStyle("-fx-font-size: 15px; -fx-min-width: 50px");
        size.setStyle("-fx-font-size: 15px; -fx-font-weight: normal");
        HBox sizeBox = new HBox(sizeLabel, size);

        Label lastModifiedLabel = new Label("Modified: ");
        Label modified = new Label(this.getDate(0));
        lastModifiedLabel.setStyle("-fx-font-size: 15px; -fx-min-width: 50px");
        modified.setStyle("-fx-font-size: 15px; -fx-font-weight: normal");
        HBox modifiedBox = new HBox(lastModifiedLabel, modified);

        Label createdLabel = new Label("Created: ");
        Label created = new Label(this.getDate(1));
        createdLabel.setStyle("-fx-font-size: 15px; -fx-min-width: 50px");
        created.setStyle("-fx-font-size: 15px; -fx-font-weight: normal");
        HBox createdBox = new HBox(createdLabel, created);

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("side-button");
        okButton.setOnAction(e -> s.close());
        HBox okBox = new HBox(okButton);
        okBox.setAlignment(Pos.CENTER);

        VBox warningBox = new VBox(nameBox, typeBox, pathBox, sizeBox, modifiedBox, createdBox, okBox);
        warningBox.getStyleClass().add("vbox");
        warningBox.setSpacing(20);
        warningBox.setPadding(new Insets(30));

        Scene sc = new Scene(warningBox, 400, 370);
        sc.getStylesheets().add(this.stylesheet);
        s.setScene(sc);
        s.setResizable(false);
        s.setAlwaysOnTop(true);
        s.showAndWait();
    }
    private String getName() {
        int index = this.file.getName().lastIndexOf(".");
        String name;
        if (index == -1) {
            name = this.file.getName();
        } else {
            name = this.file.getName().substring(0, index);
        }
        return name;
    }
    private String getType() {
        String type;
        try {
            type = Files.probeContentType(this.file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (type == null && this.file.isDirectory()) {
            type = "Directory";
        } else if (type == null) {
            type = "Unknown";
        }
        return type;
    }
    private String getSize() {
        String s = "";
        long i;
        long size;
        if (this.file.isDirectory()) {
            i = FileUtils.sizeOfDirectory(this.file);
        } else {
            i = FileUtils.sizeOf(this.file);
        }
        size = i;
        int count = 0;
        while (true) {
            long i1 = i / 1000;
            count++;
            if (i1 == 0) {
                count--;
                break;
            }
            i = i1;
        }
        s += i;
        switch (count) {
            case 0 -> s += "B";
            case 1 -> s += "kB";
            case 2 -> s += "MB";
            case 3 -> s += "GB";
        }
        s = s + "  (" + size + " B)";
        return s;
    }
    private String getDate(int create) {
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            new WarningWindow("Can't read attributes!", this.stylesheet);
            throw new RuntimeException(e);
        }
        String time;
        if (create == 0) {
            time = attr.lastModifiedTime().toString();
        } else {
            time = attr.creationTime().toString();
        }
        time = time.replace("T" , ", ").replace("Z", "");
        int index = time.lastIndexOf(":");
        time = time.substring(0, index);
        return time;
    }
}
