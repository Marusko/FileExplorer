import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class NameWindow {
    private String name;
    private final String action;
    private final String  stylesheet;
    public NameWindow(String action, String oldName, String stylesheet) {
        this.action = action;
        this.stylesheet = stylesheet;
        this.name = oldName;
        this.setUI(oldName);
    }

    private void setUI(String oldName) {
        Stage s = new Stage();
        s.setTitle(this.action);
        s.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder_small.png")).toExternalForm()));
        Label messageLabel = new Label(this.action);
        messageLabel.setStyle("-fx-font-size: 15px");

        TextField nameText = new TextField(oldName);

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("side-button");
        okButton.setOnAction(e -> {
            this.setName(nameText.getText());
            s.close();
        });

        VBox nameBox = new VBox(messageLabel, nameText, okButton);
        nameBox.getStyleClass().add("vbox");
        nameBox.setAlignment(Pos.TOP_CENTER);
        nameBox.setSpacing(20);
        nameBox.setPadding(new Insets(15));

        Scene sc = new Scene(nameBox, 400, 150);
        sc.getStylesheets().add(this.stylesheet);
        s.setScene(sc);
        s.setResizable(false);
        s.showAndWait();
    }

    private void setName(String name) {
        this.name = name;
    }
    protected String getName() {
        return this.name;
    }
}
