import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WarningWindow {
    private final String message;
    private final String  stylesheet;
    public WarningWindow(String message, String stylesheet) {
        this.message = message;
        this.stylesheet = stylesheet;
        this.setUI();
    }

    private void setUI() {
        Stage s = new Stage();
        s.setTitle("Warning");
        Label messageLabel = new Label(this.message);
        messageLabel.setStyle("-fx-font-size: 15px");

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("side-button");
        okButton.setOnAction(e -> s.close());

        VBox warningBox = new VBox(messageLabel, okButton);
        warningBox.getStyleClass().add("vbox");
        warningBox.setAlignment(Pos.TOP_CENTER);
        warningBox.setSpacing(20);
        warningBox.setPadding(new Insets(30));

        Scene sc = new Scene(warningBox, 300, 150);
        sc.getStylesheets().add(this.stylesheet);
        s.setScene(sc);
        s.setResizable(false);
        s.show();
    }
}
