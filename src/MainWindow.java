import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class MainWindow extends Application {
    public static final String AUTHOR = "Matúš Suský";
    public static final String VERSION = "0.0.0.1";
    private final Hyperlink icons = new Hyperlink("-Icons");
    private final Hyperlink darkThemeC = new Hyperlink("-Dark theme colors");
    private final Hyperlink lightThemeC = new Hyperlink("-Light theme colors");
    private final Hyperlink moreLink = new Hyperlink("Github");

    private MainLogic ml;

    private Stage mainStage;
    private Scene mainScene;
    private final TabPane tabPane = new TabPane();
    private BorderPane diskBP;
    private BorderPane pinnedBP;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage){
        this.ml = new MainLogic();

        this.mainStage = stage;
        BorderPane bp = new BorderPane();
        bp.setLeft(this.sidePage());

        mainScene = new Scene(bp, 1200, 700);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/darkTheme.css")).toExternalForm());
        this.mainStage.setScene(mainScene);
        this.mainStage.setTitle("File explorer");
        this.mainStage.setResizable(false);

        icons.setOnAction(iconEvent -> getHostServices().showDocument("https://www.flaticon.com/authors/wahyu-adam"));
        icons.getStyleClass().add("label");
        darkThemeC.setOnAction(iconEvent -> getHostServices().showDocument("https://colorhunt.co/palette/261c2c3e2c415c527f6e85b2"));
        darkThemeC.getStyleClass().add("label");
        lightThemeC.setOnAction(iconEvent -> getHostServices().showDocument("https://colorhunt.co/palette/def5e5bcead59ed5c58ec3b0"));
        lightThemeC.getStyleClass().add("label");
        moreLink.setOnAction(iconEvent -> getHostServices().showDocument("https://github.com/Marusko/FileExplorer"));
        moreLink.getStyleClass().add("label");
        moreLink.setDisable(true);

        this.tabPage();
        bp.setCenter(this.tabPane);
        this.mainStage.show();
    }

    private void changeTheme(String theme) {
        switch (theme) {
            case "Dark" -> {
                mainScene.getStylesheets().clear();
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/darkTheme.css")).toExternalForm());
            }
            case "Light" -> {
                mainScene.getStylesheets().clear();
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/lightTheme.css")).toExternalForm());
            }
        }
    }

    //UI--------------------------------------
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
        tabPane.getTabs().addAll(this.homeTab(), this.folderTab(), this.settingsTab(), addTab);
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
        for (DiskClass d : this.ml.getDisks()) {
            diskFP.getChildren().add(this.diskUI(d));
        }
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
    private Tab settingsTab() {
        Tab settings = new Tab("Settings");

        Label themeLabel = new Label("Theme: ");
        themeLabel.getStyleClass().add("settings-label");
        ComboBox<String> themeChooser = new ComboBox<>();
        themeChooser.getItems().addAll("Dark", "Light");
        themeChooser.getSelectionModel().select(0);
        themeChooser.setOnMouseEntered(e -> themeChooser.setStyle("combo-color: default-color"));
        themeChooser.setOnMouseExited(e -> themeChooser.setStyle("combo-color: elevated-background-color"));
        themeChooser.valueProperty().addListener(e -> this.changeTheme(themeChooser.getValue()));
        VBox themeBox = new VBox(themeLabel, themeChooser);
        themeBox.setStyle("-fx-spacing: 10px; -fx-padding: 10px");

        Label extensionLabel = new Label("Show file extensions: ");
        VBox extensionBox = setSettingsRadioButtons(extensionLabel);
        extensionBox.setDisable(true);

        Label hiddenLabel = new Label("Show hidden files: ");
        VBox hiddenBox = setSettingsRadioButtons(hiddenLabel);
        hiddenBox.setDisable(true);

        Label whereLabel = new Label("Where to open folders: ");
        whereLabel.getStyleClass().add("settings-label");
        RadioButton sameTabButton = new RadioButton("Same tab");
        RadioButton newTabButton = new RadioButton("New tab");
        sameTabButton.setSelected(true);
        ToggleGroup toggleWhere = new ToggleGroup();
        sameTabButton.setToggleGroup(toggleWhere);
        newTabButton.setToggleGroup(toggleWhere);
        VBox radioWhereBox = new VBox(sameTabButton, newTabButton);
        radioWhereBox.setStyle("-fx-spacing: 10px");
        VBox whereBox = new VBox(whereLabel, radioWhereBox);
        whereBox.setStyle("-fx-spacing: 10px; -fx-padding: 10px");
        whereBox.setDisable(true);

        VBox settingsBox = new VBox(themeBox, extensionBox, hiddenBox, whereBox);
        settingsBox.setStyle("-fx-spacing: 10px");

        Label authorLabel = new Label("Made by: " + MainWindow.AUTHOR);
        Label versionLabel = new Label("Version: " + MainWindow.VERSION);
        Label moreOn = new Label("More on: ");
        HBox moreBox = new HBox(moreOn, moreLink);
        Label copy = new Label("Copyright: ");
        VBox copyBox = new VBox(copy, icons, darkThemeC, lightThemeC);
        VBox infoBox = new VBox(authorLabel, versionLabel, moreBox, copyBox);
        infoBox.setStyle("-fx-spacing: 10px");

        VBox tabBox = new VBox(settingsBox, infoBox);
        tabBox.setStyle("-fx-spacing: 70px; -fx-padding: 50px");
        settings.setContent(tabBox);

        return settings;
    }

    private VBox setSettingsRadioButtons(Label label) {
        label.getStyleClass().add("settings-label");
        RadioButton yes = new RadioButton("Yes");
        RadioButton no = new RadioButton("No");
        no.setSelected(true);
        ToggleGroup toggleGroup = new ToggleGroup();
        yes.setToggleGroup(toggleGroup);
        no.setToggleGroup(toggleGroup);
        HBox radioBox = new HBox(yes, no);
        radioBox.setStyle("-fx-spacing: 10px");
        VBox completeBox = new VBox(label, radioBox);
        completeBox.setStyle("-fx-spacing: 10px; -fx-padding: 10px");
        return completeBox;
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
        MenuItem order = new MenuItem("Order");
        order.setDisable(true);
        more.getItems().addAll(copy, cut, paste, rename, newMenuItem, delete, order, settings);

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
                MenuItem select = new MenuItem("Select");
                select.setDisable(true);
                menu.getItems().addAll(select, copy, cut, delete, pin, pathItem, properties);
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

    private HBox diskUI(DiskClass d) {
        ImageView icon;
        if (d.isRemovable().equals("false")) {
            icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/harddisk.png")).toExternalForm()));
        } else {
            icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/flashdrive.png")).toExternalForm()));
        }
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        VBox diskBox = new VBox();
        Label diskName = new Label(d.getName());
        diskName.setStyle("-fx-font-size: 15px;");
        ProgressBar pb = new ProgressBar(d.getUsedPercentage());
        Label capacity = new Label(d.getFreeCapacity() + " GB free of " + d.getTotalCapacity() + " GB");
        capacity.setStyle("-fx-font-size: 10px");
        diskBox.getChildren().addAll(diskName, pb, capacity);
        diskBox.setSpacing(5);
        diskBox.setAlignment(Pos.CENTER_LEFT);
        HBox diskUI = new HBox(icon, diskBox);
        diskUI.setSpacing(10);
        diskUI.getStyleClass().add("hbox-disk");
        diskUI.setPadding(new Insets(10));
        Button control = new Button();
        control.getStyleClass().add("pinned-control-right");
        control.setOnMouseEntered(e -> diskUI.setStyle("-fx-background-color: default-color"));
        control.setOnMouseExited(e -> diskUI.setStyle("-fx-background-color: elevated-background-color"));
        StackPane diskSP = new StackPane(diskUI, control);

        return new HBox(diskSP);
    }
    private HBox pinnedUI(String name) {
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder.png")).toExternalForm()));
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        Label fileName = new Label(name);
        fileName.setStyle("-fx-font-size: 15px");
        VBox nameBox = new VBox(fileName);
        nameBox.setAlignment(Pos.CENTER);
        HBox fileUI = new HBox(icon, nameBox);
        fileUI.setSpacing(30);
        fileUI.getStyleClass().add("hbox-disk");
        fileUI.setPadding(new Insets(10));
        Button control = new Button();
        control.getStyleClass().add("pinned-control-right");
        control.setContextMenu(this.setRightClickMenu(1));
        control.setOnMouseEntered(e -> fileUI.setStyle("-fx-background-color: default-color"));
        control.setOnMouseExited(e -> fileUI.setStyle("-fx-background-color: elevated-background-color"));
        StackPane fileSP = new StackPane(fileUI, control);

        return new HBox(fileSP);
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
            nameBox.getChildren().addAll(icon, fileName);
            nameBox.setSpacing(20);
            nameBox.setAlignment(Pos.CENTER_LEFT);
            nameBox.setPrefWidth(200);

            listBox.setStyle("-fx-padding: 10px; -fx-alignment: center-left;-fx-pref-width: 900px; -fx-pref-height: 35; -fx-spacing: 500");
            listBox.getChildren().addAll(nameBox, date);

            Button control = new Button();
            control.setStyle("-fx-pref-height: 40px; -fx-pref-width: 900px; -fx-background-color: transparent");
            control.setContextMenu(this.setRightClickMenu(0));
            control.setOnMouseEntered(e -> fileBox.setStyle("-fx-background-color: default-color"));
            control.setOnMouseExited(e -> fileBox.setStyle("-fx-background-color: elevated-background-color"));
            StackPane listSP = new StackPane(listBox, control);

            fileBox.getChildren().add(listSP);
        } else {
            VBox objectBox = new VBox();
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder.png")).toExternalForm()));
            Label fileName = new Label(name);
            objectBox.getChildren().addAll(icon, fileName);
            objectBox.setStyle("-fx-padding: 20px; -fx-spacing: 20px; -fx-alignment: center; -fx-pref-width: 100px; -fx-pref-height: 200px");

            Button control = new Button();
            control.setStyle("-fx-pref-height: 210px; -fx-pref-width: 170px; -fx-background-color: transparent");
            control.setContextMenu(this.setRightClickMenu(0));
            control.setOnMouseEntered(e -> fileBox.setStyle("-fx-background-color: default-color"));
            control.setOnMouseExited(e -> fileBox.setStyle("-fx-background-color: elevated-background-color"));
            StackPane objectSP = new StackPane(objectBox, control);

            fileBox.getChildren().add(objectSP);
        }
        return fileBox;
    }

}
