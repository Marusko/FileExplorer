import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainWindow extends Application {

    private Stage mainStage;
    private final TabPane tabPane = new TabPane();
    private HBox topBar;
    private HBox bottomBar;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage){
        this.mainStage = stage;
        BorderPane bp = new BorderPane();
        bp.setLeft(this.sidePage());

        Scene mainScene = new Scene(bp, 1200, 700);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/darkTheme.css")).toExternalForm());
        this.mainStage.setScene(mainScene);
        this.mainStage.setTitle("File explorer");
        this.mainStage.widthProperty().addListener(e -> {
            this.topBar.setSpacing(this.mainStage.getWidth() - 500);
            this.bottomBar.setSpacing(this.mainStage.getWidth() - 375);
        });


        this.setBars();
        this.tabPage();
        bp.setCenter(this.tabPane);
        this.mainStage.show();
    }

    private VBox sidePage() {
        VBox side = new VBox();
        Button desktop = new Button("Desktop");
        desktop.getStyleClass().add("side-button");
        Button download = new Button("Download");
        download.getStyleClass().add("side-button");
        Button docs = new Button("Docs");
        docs.getStyleClass().add("side-button");
        Button pics = new Button("Pictures");
        pics.getStyleClass().add("side-button");
        Button music = new Button("Music");
        music.getStyleClass().add("side-button");
        Button videos = new Button("Videos");
        videos.getStyleClass().add("side-button");


        side.getChildren().addAll(desktop, download, docs, pics, music, videos);
        side.setMaxWidth(200);
        side.setMinWidth(200);
        side.setSpacing(10);
        side.setAlignment(Pos.TOP_CENTER);
        side.setPadding(new Insets(10));
        side.setStyle("-fx-background-color: #261C2C");
        return side;
    }

    private void tabPage() {
        Tab addTab = new Tab("Add");
        addTab.setClosable(false);
        tabPane.getTabs().addAll(this.homeTab(), addTab);
    }

    private Tab homeTab() {
        Tab home = new Tab("Home");
        BorderPane homeBP = new BorderPane();

        Accordion homeAccordion = new Accordion();

        Label pinnedLabel = new Label("Pinned");
        BorderPane pinnedBP = new BorderPane();
        pinnedBP.getStyleClass().add("home-border-pane");
        pinnedBP.setTop(pinnedLabel);
        TitledPane pinned = new TitledPane("Pinned  - - - - - - ", pinnedBP);


        Label diskLabel = new Label("Disks");
        BorderPane diskBP = new BorderPane();
        diskBP.getStyleClass().add("home-border-pane");
        diskBP.setTop(diskLabel);
        TitledPane disks = new TitledPane("Disks  - - - - - - ", diskBP);
        homeAccordion.getPanes().addAll(pinned, disks);
        homeAccordion.setExpandedPane(homeAccordion.getPanes().get(1));

        homeBP.setTop(topBar);
        homeBP.setBottom(bottomBar);
        homeBP.setCenter(homeAccordion);
        home.setContent(homeBP);

        return home;
    }

    private void setBars() {
        Button back = new Button("Back");
        back.getStyleClass().add("top-bar-button");
        Button forth = new Button("Forth");
        forth.getStyleClass().add("top-bar-button");
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("top-bar-button");
        Button more = new Button("More");
        more.getStyleClass().add("top-bar-button");
        Label address = new Label("C:/TEST/TSET");
        address.getStyleClass().add("top-bar-label");
        HBox moreHBox = new HBox(more);
        moreHBox.setAlignment(Pos.CENTER_RIGHT);
        HBox addressHBox = new HBox(back, forth, refresh, address);
        addressHBox.setSpacing(5);
        addressHBox.setAlignment(Pos.CENTER_LEFT);
        topBar = new HBox(addressHBox, moreHBox);
        topBar.setSpacing(this.mainStage.getWidth());
        topBar.setPadding(new Insets(2));
        topBar.setStyle("-fx-background-color: #3E2C41");

        Label counter = new Label("Count: ??");
        counter.getStyleClass().add("bottom-bar-label");
        Button listView = new Button("L");
        listView.getStyleClass().add("bottom-bar-button");
        Button objectView = new Button("O");
        objectView.getStyleClass().add("bottom-bar-button");
        HBox viewBox = new HBox(listView, objectView);
        viewBox.setSpacing(5);
        bottomBar = new HBox(counter, viewBox);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(2));
        bottomBar.setSpacing(this.mainStage.getWidth() - 350);
        bottomBar.setStyle("-fx-background-color: #3E2C41");
    }

}
