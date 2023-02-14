import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class MainWindow extends Application {
    public static final String AUTHOR = "Matúš Suský";
    public static final String VERSION = "0.1.2";
    private final Hyperlink icons = new Hyperlink("-Icons");
    private final Hyperlink darkThemeC = new Hyperlink("-Dark theme colors");
    private final Hyperlink lightThemeC = new Hyperlink("-Light theme colors");
    private final Hyperlink fileUtilsLink = new Hyperlink("-Library for operations with files");
    private final Hyperlink moreLink = new Hyperlink("Github");

    private MainLogic ml;
    private Scene mainScene;
    private final TabPane tabPane = new TabPane();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage){
        this.ml = new MainLogic(this);
        BorderPane bp = new BorderPane();
        bp.setLeft(this.sidePage());

        mainScene = new Scene(bp, 1200, 700);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/darkTheme.css")).toExternalForm());
        stage.setScene(mainScene);
        stage.setTitle("File explorer");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder_small.png")).toExternalForm()));
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> this.ml.savePinned());

        icons.setOnAction(iconEvent -> getHostServices().showDocument("https://www.flaticon.com/authors/wahyu-adam"));
        icons.getStyleClass().add("label");
        darkThemeC.setOnAction(iconEvent -> getHostServices().showDocument("https://colorhunt.co/palette/261c2c3e2c415c527f6e85b2"));
        darkThemeC.getStyleClass().add("label");
        lightThemeC.setOnAction(iconEvent -> getHostServices().showDocument("https://colorhunt.co/palette/def5e5bcead59ed5c58ec3b0"));
        lightThemeC.getStyleClass().add("label");
        moreLink.setOnAction(iconEvent -> getHostServices().showDocument("https://github.com/Marusko/FileExplorer"));
        moreLink.getStyleClass().add("label");
        fileUtilsLink.setOnAction(iconEvent -> getHostServices().showDocument("https://commons.apache.org/proper/commons-io/index.html"));
        fileUtilsLink.getStyleClass().add("label");

        this.tabPage();
        bp.setCenter(this.tabPane);
        new Loader(this.ml);
        this.ml.refresh(true);
        stage.show();
    }
    protected Scene getMainScene() {
        return mainScene;
    }
    protected TabPane getTabPane() {
        return this.tabPane;
    }

    protected void changeTheme(String theme) {
        switch (theme) {
            case "Dark" -> {
                this.ml.writeTheme("Dark");
                this.ml.setTheme("Dark");
                mainScene.getStylesheets().clear();
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/darkTheme.css")).toExternalForm());
            }
            case "Light" -> {
                this.ml.writeTheme("Light");
                this.ml.setTheme("Light");
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
        desktop.setOnAction(e -> {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            this.ml.openSide("Desktop", fsv.getHomeDirectory().getPath());
        });

        Button download = new Button("Download");
        download.getStyleClass().add("side-button");
        download.setOnAction(e -> this.ml.openSide("Downloads", FileUtils.getUserDirectoryPath() + "/Downloads/"));

        Button docs = new Button("Documents");
        docs.getStyleClass().add("side-button");
        docs.setOnAction(e -> this.ml.openSide("Documents", FileUtils.getUserDirectoryPath() + "/Documents/"));

        Button pics = new Button("Pictures");
        pics.getStyleClass().add("side-button");
        pics.setOnAction(e -> this.ml.openSide("Pictures", FileUtils.getUserDirectoryPath() + "/Pictures/"));

        Button music = new Button("Music");
        music.getStyleClass().add("side-button");
        music.setOnAction(e -> this.ml.openSide("Music", FileUtils.getUserDirectoryPath() + "/Music/"));

        Button videos = new Button("Videos");
        videos.getStyleClass().add("side-button");
        videos.setOnAction(e -> this.ml.openSide("Videos", FileUtils.getUserDirectoryPath() + "/Videos/"));

        side.getChildren().addAll(desktop, download, docs, pics, music, videos);
        side.setMaxWidth(200);
        side.setMinWidth(200);
        side.setSpacing(10);
        side.setAlignment(Pos.TOP_CENTER);
        side.setPadding(new Insets(10));
        return side;
    }
    private void tabPage() {
        Tab addTab = new Tab("Add");
        addTab.setClosable(false);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, new Tab("Home", this.homeTab()));
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
            }
        });
        tabPane.getTabs().addAll(new Tab("Home", this.homeTab()), addTab);
    }

    protected BorderPane homeTab() {
        BorderPane homeBP = new BorderPane();
        Accordion homeAccordion = new Accordion();

        BorderPane pinnedBP = new BorderPane();
        pinnedBP.getStyleClass().add("home-border-pane");
        FlowPane pinnedFP = new FlowPane();

        for (File f : this.ml.getPinnedFiles()) {
            pinnedFP.getChildren().add(this.pinnedUI(f.getName(), f));
        }

        pinnedFP.setPadding(new Insets(20));
        pinnedFP.setHgap(20);
        pinnedFP.setVgap(20);
        pinnedBP.setCenter(pinnedFP);
        TitledPane pinned = new TitledPane("Pinned", pinnedBP);

        BorderPane diskBP = new BorderPane();
        diskBP.getStyleClass().add("home-border-pane");
        FlowPane diskFP = new FlowPane();
        for (DiskClass d : this.ml.getDisks()) {
            diskFP.getChildren().add(this.diskUI(d));
        }
        diskFP.setPadding(new Insets(20));
        diskFP.setHgap(20);
        diskFP.setVgap(20);
        diskBP.setCenter(diskFP);
        TitledPane disks = new TitledPane("Disks", diskBP);
        homeAccordion.getPanes().addAll(pinned, disks);
        homeAccordion.setExpandedPane(homeAccordion.getPanes().get(1));

        HBox topBar = this.setTopBar();
        HBox addressBox = (HBox) topBar.getChildren().get(0);
        Button back = (Button) addressBox.getChildren().get(0);
        back.setDisable(true);

        homeBP.setTop(topBar);
        homeBP.setCenter(homeAccordion);

        return homeBP;
    }
    protected BorderPane folderTab(File file) {
        BorderPane folderBP = new BorderPane();
        folderBP.getStyleClass().add("home-border-pane");
        ScrollPane folderSP = new ScrollPane();
        folderSP.setContextMenu(this.setRightClickMenu(2, file, null));
        folderSP.setOnMouseClicked(e -> this.ml.clearSelected());
        folderSP.setOnMouseEntered(e -> folderSP.lookup(".scroll-bar").setStyle("bar-width: bar-fat; bar-height: bar-fat"));
        folderSP.setOnMouseExited(e -> folderSP.lookup(".scroll-bar").setStyle("bar-width: bar-skinny; bar-height: bar-skinny"));
        FlowPane fileFP = new FlowPane();
        fileFP.setPrefWidth(950);
        int count = 0;
        if (file.isDirectory()) {
            if (file.listFiles() != null) {
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    if (this.ml.isShowHidden()) {
                        fileFP.getChildren().add(this.fileUI(this.ml.isListView(), f, f.getName()));
                        count++;
                    } else {
                        if (!f.isHidden()) {
                            fileFP.getChildren().add(this.fileUI(this.ml.isListView(), f, f.getName()));
                            count++;
                        }
                    }
                }
            }
        }
        fileFP.setPadding(new Insets(20));
        fileFP.setHgap(10);
        fileFP.setVgap(10);
        folderSP.setContent(fileFP);

        HBox topBar = this.setTopBar();
        HBox addressBox = (HBox) topBar.getChildren().get(0);
        Label address = (Label) addressBox.getChildren().get(2);
        address.getStyleClass().add("top-bar-label");
        address.setText(file.getPath());

        Button back = (Button) addressBox.getChildren().get(0);
        if (address.getText().equals("C:\\")) {
            back.setDisable(true);
        }

        folderBP.setTop(topBar);

        HBox bottomBar = this.setBottomBar();
        Label counter = (Label) bottomBar.getChildren().get(0);
        counter.setText("Count: " + count);
        folderBP.setBottom(bottomBar);
        folderBP.setCenter(folderSP);
        return folderBP;
    }
    protected VBox settingsTab() {
        Label themeLabel = new Label("Theme: ");
        themeLabel.getStyleClass().add("settings-label");
        ComboBox<String> themeChooser = new ComboBox<>();
        themeChooser.getItems().addAll("Dark", "Light");
        themeChooser.getSelectionModel().select(this.ml.getTheme());
        themeChooser.setOnMouseEntered(e -> themeChooser.setStyle("combo-color: default-color"));
        themeChooser.setOnMouseExited(e -> themeChooser.setStyle("combo-color: elevated-background-color"));
        themeChooser.valueProperty().addListener(e -> this.changeTheme(themeChooser.getValue()));
        VBox themeBox = new VBox(themeLabel, themeChooser);
        themeBox.setStyle("-fx-spacing: 10px; -fx-padding: 10px");

        Label extensionLabel = new Label("Show file extensions: ");
        VBox extensionBox = setSettingsRadioButtons(extensionLabel, 0);

        Label hiddenLabel = new Label("Show hidden files: ");
        VBox hiddenBox = setSettingsRadioButtons(hiddenLabel, 1);

        Label doubleClickLabel = new Label("Use double click: ");
        VBox doubleClickBox = setSettingsRadioButtons(doubleClickLabel, 2);

        Label whereLabel = new Label("Where to open folders: ");
        whereLabel.getStyleClass().add("settings-label");
        RadioButton sameTabButton = new RadioButton("Same tab");
        sameTabButton.selectedProperty().addListener(e -> {
            this.ml.writeOnSame(true);
            this.ml.setOpenOnSame(true);
        });
        RadioButton newTabButton = new RadioButton("New tab");
        newTabButton.selectedProperty().addListener(e -> {
            this.ml.writeOnSame(false);
            this.ml.setOpenOnSame(false);
        });
        sameTabButton.setSelected(this.ml.isOpenOnSame());
        newTabButton.setSelected(!this.ml.isOpenOnSame());
        ToggleGroup toggleWhere = new ToggleGroup();
        sameTabButton.setToggleGroup(toggleWhere);
        newTabButton.setToggleGroup(toggleWhere);
        VBox radioWhereBox = new VBox(sameTabButton, newTabButton);
        radioWhereBox.setStyle("-fx-spacing: 10px");
        VBox whereBox = new VBox(whereLabel, radioWhereBox);
        whereBox.setStyle("-fx-spacing: 10px; -fx-padding: 10px");

        GridPane settingsGP = new GridPane();
        settingsGP.add(extensionBox, 0, 0);
        settingsGP.add(hiddenBox, 1, 0);
        settingsGP.add(doubleClickBox, 0, 1);
        settingsGP.add(whereBox, 1, 1);
        settingsGP.setHgap(30);
        settingsGP.setVgap(30);

        VBox settingsBox = new VBox(themeBox, settingsGP);
        settingsBox.setStyle("-fx-spacing: 10px");

        Label authorLabel = new Label("Made by: " + MainWindow.AUTHOR);
        Label versionLabel = new Label("Version: " + MainWindow.VERSION);
        Label moreOn = new Label("More on: ");
        HBox moreBox = new HBox(moreOn, moreLink);
        Label copy = new Label("Copyright: ");
        VBox copyBox = new VBox(copy, icons, darkThemeC, lightThemeC, fileUtilsLink);
        VBox infoBox = new VBox(authorLabel, versionLabel, moreBox, copyBox);
        infoBox.setStyle("-fx-spacing: 10px");

        VBox tabBox = new VBox(settingsBox, infoBox);
        tabBox.setStyle("-fx-spacing: 70px; -fx-padding: 50px");

        return tabBox;
    }
    private VBox setSettingsRadioButtons(Label label, int submenu) {
        label.getStyleClass().add("settings-label");
        RadioButton yes = new RadioButton("Yes");
        RadioButton no = new RadioButton("No");
        switch (submenu) {
            case 0 -> {
                yes.setSelected(this.ml.isShowExtensions());
                yes.selectedProperty().addListener(e -> {
                    this.ml.writeExtensions(true);
                    this.ml.setShowExtensions(true);
                    this.ml.refresh(true);
                });
                no.setSelected(!this.ml.isShowExtensions());
                no.selectedProperty().addListener(e -> {
                    this.ml.writeExtensions(false);
                    this.ml.setShowExtensions(false);
                    this.ml.refresh(true);
                });
            }
            case 1 -> {
                yes.setSelected(this.ml.isShowHidden());
                yes.selectedProperty().addListener(e -> {
                    this.ml.writeHidden(true);
                    this.ml.setShowHidden(true);
                    this.ml.refresh(true);
                });
                no.setSelected(!this.ml.isShowHidden());
                no.selectedProperty().addListener(e -> {
                    this.ml.writeHidden(false);
                    this.ml.setShowHidden(false);
                    this.ml.refresh(true);
                });
            }
            case 2 -> {
                yes.setSelected(this.ml.isDoubleClick());
                yes.selectedProperty().addListener(e -> {
                    this.ml.writeDoubleClick(true);
                    this.ml.setDoubleClick(true);
                });
                no.setSelected(!this.ml.isDoubleClick());
                no.selectedProperty().addListener(e -> {
                    this.ml.writeDoubleClick(false);
                    this.ml.setDoubleClick(false);
                });
            }
        }

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
        listView.setOnAction(e -> {
            this.ml.setListView(true);
            this.ml.refresh(false);
        });
        ImageView iconL = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/form.png")).toExternalForm()));
        iconL.setFitWidth(20);
        iconL.setFitHeight(20);
        listView.setGraphic(iconL);

        Button objectView = new Button();
        objectView.getStyleClass().add("bottom-bar-button");
        objectView.setOnAction(e -> {
            this.ml.setListView(false);
            this.ml.refresh(false);
        });
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

        Button refresh = new Button();
        refresh.getStyleClass().add("top-bar-button");
        ImageView iconR = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/refresh.png")).toExternalForm()));
        iconR.setFitWidth(20);
        iconR.setFitHeight(20);
        refresh.setGraphic(iconR);
        refresh.setOnAction(e -> this.ml.refresh(true));

        MenuButton more = new MenuButton();
        more.getStyleClass().add("top-bar-button");
        ImageView iconM = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/menu.png")).toExternalForm()));
        iconM.setFitWidth(20);
        iconM.setFitHeight(20);
        more.setGraphic(iconM);
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(e -> this.ml.copy(null));

        MenuItem cut = new MenuItem("Cut");
        cut.setOnAction(e -> this.ml.cut(null));

        MenuItem paste = new MenuItem("Paste");

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> this.ml.delete(null));

        MenuItem settings = new MenuItem("Settings");
        settings.setOnAction(e -> this.ml.settings());

        MenuItem newMenuItem = new MenuItem("New");

        MenuItem order = new MenuItem("Order");
        order.setDisable(true);

        MenuItem search = new MenuItem("Search");
        search.setDisable(true);

        more.getItems().addAll(copy, cut, paste, newMenuItem, delete, order, search, settings);

        Label address = new Label("");
        address.setMinWidth(600);
        address.getStyleClass().add("top-bar-label");
        HBox moreHBox = new HBox(more);
        moreHBox.setAlignment(Pos.CENTER_RIGHT);
        HBox addressHBox = new HBox(back, refresh, address);
        addressHBox.setSpacing(5);
        addressHBox.setAlignment(Pos.CENTER_LEFT);
        HBox topBar = new HBox(addressHBox, moreHBox);
        topBar.setSpacing(200);
        topBar.setPadding(new Insets(2));
        topBar.getStyleClass().add("hbox-bar");

        back.setOnAction(e -> this.ml.back(address, back));
        paste.setOnAction(e -> this.ml.paste(new File(address.getText())));
        newMenuItem.setOnAction(e -> this.ml.newFile(address));

        return topBar;
    }
    protected ContextMenu setRightClickMenu(int onFile, File file, HBox ui) {
        ContextMenu menu = new ContextMenu();
        switch (onFile) {
            case 0 -> {
                MenuItem pin = new MenuItem("Pin this");
                pin.setOnAction(e -> {
                    this.ml.addPinned(file);
                    this.ml.refresh(true);
                });

                MenuItem pathItem = new MenuItem("Copy path");
                pathItem.setOnAction(e -> this.ml.copyPath(file));

                MenuItem cut = new MenuItem("Cut");
                cut.setOnAction(e -> this.ml.cut(file));

                MenuItem copy = new MenuItem("Copy");
                copy.setOnAction(e -> this.ml.copy(file));

                MenuItem delete = new MenuItem("Delete");
                delete.setOnAction(e -> this.ml.delete(file));

                MenuItem select = new MenuItem("Select");
                select.setOnAction(e -> {
                    if (!this.ml.getSelectedFiles().containsValue(file)){
                        this.ml.addSelected(ui, file);
                        ui.setStyle("-fx-background-color: selected-color");
                    } else {
                        this.ml.removeSelected(ui);
                        ui.setStyle("-fx-background-color: elevated-background-color");
                    }
                });

                MenuItem rename = new MenuItem("Rename");
                rename.setOnAction(e -> this.ml.rename(file));

                MenuItem properties = new MenuItem("Properties");
                properties.setOnAction(e -> new PropertiesWindow(file, mainScene.getStylesheets().get(mainScene.getStylesheets().size() - 1)));

                menu.getItems().addAll(select, copy, cut, delete, pin, pathItem, rename, properties);
            }
            case 1 -> {
                MenuItem unpin = new MenuItem("Unpin this");
                unpin.setOnAction(e -> {
                    this.ml.removePinned(file);
                    this.ml.refresh(true);
                });

                MenuItem pathItem1 = new MenuItem("Copy path");
                pathItem1.setOnAction(e -> this.ml.copyPath(file));

                MenuItem copy1 = new MenuItem("Copy");
                copy1.setOnAction(e -> this.ml.copy(file));

                MenuItem properties = new MenuItem("Properties");
                properties.setOnAction(e -> new PropertiesWindow(file, mainScene.getStylesheets().get(mainScene.getStylesheets().size() - 1)));

                menu.getItems().addAll(copy1, unpin, pathItem1, properties);
            }
            case 2 -> {
                MenuItem paste = new MenuItem("Paste");
                paste.setOnAction(e -> this.ml.paste(file));
                menu.getItems().add(paste);
            }
            case 3 -> {
                MenuItem pathItem = new MenuItem("Copy path");
                pathItem.setOnAction(e -> this.ml.copyPath(file));

                MenuItem cut = new MenuItem("Cut");
                cut.setOnAction(e -> this.ml.cut(file));

                MenuItem copy = new MenuItem("Copy");
                copy.setOnAction(e -> this.ml.copy(file));

                MenuItem delete = new MenuItem("Delete");
                delete.setOnAction(e -> this.ml.delete(file));

                MenuItem select = new MenuItem("Select");
                select.setOnAction(e -> {
                    if (!this.ml.getSelectedFiles().containsValue(file)){
                        this.ml.addSelected(ui, file);
                        ui.setStyle("-fx-background-color: selected-color");
                    } else {
                        this.ml.removeSelected(ui);
                        ui.setStyle("-fx-background-color: elevated-background-color");
                    }
                });

                MenuItem openWith = new MenuItem("Open with");
                openWith.setOnAction(e -> this.ml.openWith(file));

                MenuItem rename = new MenuItem("Rename");
                rename.setOnAction(e -> this.ml.rename(file));

                MenuItem properties = new MenuItem("Properties");
                properties.setOnAction(e -> new PropertiesWindow(file, mainScene.getStylesheets().get(mainScene.getStylesheets().size() - 1)));

                menu.getItems().addAll(select, copy, cut, delete, pathItem, rename, openWith, properties);
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
        control.setOnAction(e -> {
            if (this.ml.isOpenOnSame()) {
                this.tabPane.getTabs().remove(this.tabPane.getTabs().get(this.tabPane.getSelectionModel().getSelectedIndex()));
            }
            File file = new File(d.getPath());
            this.tabPane.getTabs().add(this.tabPane.getTabs().size() - 1, this.ml.addFolderTab(d.getName(), file.getPath()));
            this.tabPane.getSelectionModel().select(this.tabPane.getTabs().size() - 2);
        });
        StackPane diskSP = new StackPane(diskUI, control);

        return new HBox(diskSP);
    }
    private HBox pinnedUI(String name, File file) {
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
        control.setContextMenu(this.setRightClickMenu(1, file, null));
        this.ml.setControlButtonActions(file, fileUI, control);

        StackPane fileSP = new StackPane(fileUI, control);

        return new HBox(fileSP);
    }
    private HBox fileUI(boolean list, File file, String name) {
        HBox fileBox = new HBox();
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            new WarningWindow("Can't read attributes!", mainScene.getStylesheets().get(mainScene.getStylesheets().size() - 1));
            throw new RuntimeException(e);
        }
        fileBox.getStyleClass().add("file-box");
        if (list) {
            String time = attr.lastModifiedTime().toString();
            time = time.replace("T" , ", ").replace("Z", "");
            int index = time.lastIndexOf(":");
            time = time.substring(0, index);

            HBox listBox = new HBox();
            HBox nameBox = new HBox();
            Label date = new Label(time);
            ImageView icon;
            if (file.isDirectory()) {
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder_small.png")).toExternalForm()));
            } else {
                icon = this.ml.iconToImageView(file);
            }

            if (!this.ml.isShowExtensions() && !file.isDirectory()) {
                int i = name.lastIndexOf(".");
                if (i != -1) {
                    name = name.substring(0, i);
                }
            }

            Label fileName = new Label(name);
            fileName.setMinWidth(500);
            nameBox.getChildren().addAll(icon, fileName);
            nameBox.setSpacing(20);
            nameBox.setAlignment(Pos.CENTER_LEFT);
            nameBox.setPrefWidth(200);

            listBox.setStyle("-fx-padding: 10px; -fx-alignment: center-left;-fx-pref-width: 900px; -fx-pref-height: 35; -fx-spacing: 100px;");
            listBox.getChildren().addAll(nameBox, date);

            Button control = new Button();
            control.setStyle("-fx-pref-height: 40px; -fx-pref-width: 900px; -fx-background-color: transparent");
            this.ml.setupControlButtonFile(control, fileBox, file);

            StackPane listSP = new StackPane(listBox, control);
            fileBox.getChildren().add(listSP);
        } else {
            VBox objectBox = new VBox();
            ImageView icon;
            if (file.isDirectory()) {
                icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("icons/normal/folder.png")).toExternalForm()));
            } else {
                icon = this.ml.iconToImageView(file);
            }
            Label fileName = new Label(name);
            objectBox.getChildren().addAll(icon, fileName);
            objectBox.setStyle("-fx-padding: 20px; -fx-spacing: 20px; -fx-alignment: center; -fx-pref-width: 100px; -fx-pref-height: 200px");

            Button control = new Button();
            control.setStyle("-fx-pref-height: 210px; -fx-pref-width: 170px; -fx-background-color: transparent");
            this.ml.setupControlButtonFile(control, fileBox, file);
            StackPane objectSP = new StackPane(objectBox, control);
            fileBox.getChildren().add(objectSP);
        }
        if (this.ml.getSelectedFiles().containsValue(file)) {
            fileBox.setStyle("-fx-background-color: selected-color");
        }
        return fileBox;
    }
}
