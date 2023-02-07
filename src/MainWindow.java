import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class MainWindow extends Application {

    private Stage mainStage;

    private final TabPane tabPane = new TabPane();
    private BorderPane diskBP;
    private BorderPane pinnedBP;

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
        this.mainStage.setResizable(false);


        this.tabPage();
        bp.setCenter(this.tabPane);
        this.mainStage.show();
    }

    private VBox sidePage() {
        VBox side = new VBox();
        side.getStyleClass().add("vbox");
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
        //side.setStyle("-fx-background-color: #261C2C");
        return side;
    }

    private void tabPage() {
        Tab addTab = new Tab("Add");
        addTab.setClosable(false);
        tabPane.getTabs().addAll(this.homeTab(), this.folderTab(), addTab);
    }

    private Tab homeTab() {
        Tab home = new Tab("Home");
        BorderPane homeBP = new BorderPane();
        Accordion homeAccordion = new Accordion();

        pinnedBP = new BorderPane();
        pinnedBP.getStyleClass().add("home-border-pane");
        FlowPane pinnedFP = new FlowPane();
        pinnedFP.getChildren().add(this.pinnedUI("TestPinned"));
        pinnedFP.setPadding(new Insets(20));
        pinnedFP.setHgap(20);
        pinnedFP.setVgap(20);
        pinnedBP.setCenter(pinnedFP);
        TitledPane pinned = new TitledPane("Pinned  - - - - - - ", pinnedBP);

        diskBP = new BorderPane();
        diskBP.getStyleClass().add("home-border-pane");
        FlowPane diskFP = new FlowPane();
        diskFP.getChildren().add(this.diskUI("Test", 1, 0.8));
        diskFP.setPadding(new Insets(20));
        diskFP.setHgap(20);
        diskFP.setVgap(20);
        diskBP.setCenter(diskFP);
        TitledPane disks = new TitledPane("Disks  - - - - - - ", diskBP);
        homeAccordion.getPanes().addAll(pinned, disks);
        homeAccordion.setExpandedPane(homeAccordion.getPanes().get(1));

        homeBP.setTop(this.setTopBar());
        homeBP.setBottom(this.setBottomBar());
        homeBP.setCenter(homeAccordion);
        home.setContent(homeBP);

        return home;
    }
    private Tab folderTab() {
        Tab folder = new Tab("Test folder");
        BorderPane folderBP = new BorderPane();
        folderBP.getStyleClass().add("home-border-pane");
        ScrollPane folderSP = new ScrollPane();
        folderSP.setContextMenu(this.setRightClickMenu(2));
        folderSP.setOnMouseEntered(e ->folderSP.lookup(".scroll-bar").setStyle("bar-width: bar-fat; bar-height: bar-fat"));
        folderSP.setOnMouseExited(e -> folderSP.lookup(".scroll-bar").setStyle("bar-width: bar-skinny; bar-height: bar-skinny"));
        FlowPane fileFP = new FlowPane();
        fileFP.setPrefWidth(950);
        for (int i = 0; i < 50; i++) {
            fileFP.getChildren().add(this.fileUI(true, null, "Testovane " + i));
        }
        fileFP.setPadding(new Insets(20));
        fileFP.setHgap(10);
        fileFP.setVgap(10);
        folderSP.setContent(fileFP);

        folderBP.setTop(this.setTopBar());
        folderBP.setBottom(this.setBottomBar());
        folderBP.setCenter(folderSP);
        folder.setContent(folderBP);
        return folder;
    }

    private HBox setBottomBar() {
        Label counter = new Label("Count: ??");
        counter.getStyleClass().add("bottom-bar-label");

        Button listView = new Button();
        listView.getStyleClass().add("bottom-bar-button");
        ImageView iconL = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/form.png")).toExternalForm()));
        iconL.setFitWidth(20);
        iconL.setFitHeight(20);
        listView.setGraphic(iconL);

        Button objectView = new Button();
        objectView.getStyleClass().add("bottom-bar-button");
        ImageView iconO = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/blog.png")).toExternalForm()));
        iconO.setFitWidth(20);
        iconO.setFitHeight(20);
        objectView.setGraphic(iconO);

        HBox viewBox = new HBox(listView, objectView);
        viewBox.setSpacing(5);
        HBox bottomBar = new HBox(counter, viewBox);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(2));
        bottomBar.setSpacing(850);
        bottomBar.getStyleClass().add("hbox-bar");
        return bottomBar;
    }
    private HBox setTopBar() {
        Button back = new Button();
        back.getStyleClass().add("top-bar-button");
        ImageView iconB = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/back.png")).toExternalForm()));
        iconB.setFitWidth(20);
        iconB.setFitHeight(20);
        back.setGraphic(iconB);

        Button forth = new Button();
        forth.getStyleClass().add("top-bar-button");
        ImageView iconF = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/next.png")).toExternalForm()));
        iconF.setFitWidth(20);
        iconF.setFitHeight(20);
        forth.setGraphic(iconF);

        Button refresh = new Button();
        refresh.getStyleClass().add("top-bar-button");
        ImageView iconR = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/refresh.png")).toExternalForm()));
        iconR.setFitWidth(20);
        iconR.setFitHeight(20);
        refresh.setGraphic(iconR);

        MenuButton more = new MenuButton();
        more.getStyleClass().add("top-bar-button");
        ImageView iconM = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/menu.png")).toExternalForm()));
        iconM.setFitWidth(20);
        iconM.setFitHeight(20);
        more.setGraphic(iconM);

        MenuItem copy = new MenuItem("Copy");
        MenuItem cut = new MenuItem("Cut");
        MenuItem paste = new MenuItem("Paste");
        MenuItem delete = new MenuItem("Delete");
        MenuItem settings = new MenuItem("Settings");
        MenuItem rename = new MenuItem("Rename");
        MenuItem newMenuItem = new MenuItem("New folder");
        more.getItems().addAll(copy, cut, paste, rename, newMenuItem, delete, settings);

        Label address = new Label("C:/TEST/TSET");
        address.getStyleClass().add("top-bar-label");
        HBox moreHBox = new HBox(more);
        moreHBox.setAlignment(Pos.CENTER_RIGHT);
        HBox addressHBox = new HBox(back, forth, refresh, address);
        addressHBox.setSpacing(5);
        addressHBox.setAlignment(Pos.CENTER_LEFT);
        HBox topBar = new HBox(addressHBox, moreHBox);
        topBar.setSpacing(700);
        topBar.setPadding(new Insets(2));
        topBar.getStyleClass().add("hbox-bar");
        return topBar;
    }

    private ContextMenu setRightClickMenu(int onFile) {
        ContextMenu menu = new ContextMenu();
        switch (onFile) {
            case 0 -> {
                MenuItem properties = new MenuItem("Properties");
                MenuItem pin = new MenuItem("Pin this");
                MenuItem pathItem = new MenuItem("Copy path");
                MenuItem cut = new MenuItem("Cut");
                MenuItem copy = new MenuItem("Copy");
                MenuItem delete = new MenuItem("Delete");
                menu.getItems().addAll(copy, cut, delete, pin, pathItem, properties);
            }
            case 1 -> {
                MenuItem properties1 = new MenuItem("Properties");
                MenuItem unpin = new MenuItem("Unpin this");
                MenuItem pathItem1 = new MenuItem("Copy path");
                MenuItem copy1 = new MenuItem("Copy");
                MenuItem delete1 = new MenuItem("Delete");
                menu.getItems().addAll(copy1, delete1, unpin, pathItem1, properties1);
            }
            case 2 -> {
                MenuItem paste = new MenuItem("Paste");
                menu.getItems().add(paste);
            }
        }
        return menu;
    }

    private HBox diskUI(String name, double capacity, double used) {
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/harddisk.png")).toExternalForm()));
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        VBox diskBox = new VBox();
        Label diskName = new Label(name);
        diskName.setStyle("-fx-font-size: 15px");
        ProgressBar pb = new ProgressBar(used);
        diskBox.getChildren().addAll(diskName, pb);
        diskBox.setSpacing(10);
        diskBox.setAlignment(Pos.CENTER_LEFT);
        HBox disk = new HBox(icon, diskBox);
        disk.setSpacing(10);
        disk.getStyleClass().add("hbox-disk");
        disk.setPadding(new Insets(10));
        return disk;
    }
    private HBox pinnedUI(String name) {
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder.png")).toExternalForm()));
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        Label fileName = new Label(name);
        fileName.setContextMenu(this.setRightClickMenu(1));
        fileName.setStyle("-fx-font-size: 15px");
        VBox nameBox = new VBox(fileName);
        nameBox.setAlignment(Pos.CENTER);
        HBox file = new HBox(icon, nameBox);
        file.setSpacing(30);
        file.getStyleClass().add("hbox-disk");
        file.setPadding(new Insets(10));
        return file;
    }
    private HBox fileUI(boolean list, File file, String name) {
        HBox fileBox = new HBox();
        fileBox.getStyleClass().add("file-box");
        if (list) {
            HBox listBox = new HBox();
            HBox nameBox = new HBox();
            Label date = new Label("Date");
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder_small.png")).toExternalForm()));
            Label fileName = new Label(name);
            fileName.setContextMenu(this.setRightClickMenu(0));
            nameBox.getChildren().addAll(icon, fileName);
            nameBox.setSpacing(20);
            nameBox.setAlignment(Pos.CENTER_LEFT);
            nameBox.setPrefWidth(200);

            listBox.setPadding(new Insets(10));
            listBox.setAlignment(Pos.CENTER_LEFT);
            listBox.setPrefWidth(900);
            listBox.setPrefHeight(35);
            listBox.getChildren().addAll(nameBox, date);
            listBox.setSpacing(500);

            fileBox.getChildren().add(listBox);
        } else {
            VBox objectBox = new VBox();
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder.png")).toExternalForm()));
            Label fileName = new Label(name);
            objectBox.getChildren().addAll(icon, fileName);
            objectBox.setPadding(new Insets(20));
            objectBox.setSpacing(20);
            objectBox.setAlignment(Pos.CENTER);
            fileBox.getChildren().add(objectBox);
        }
        return fileBox;
    }

}
