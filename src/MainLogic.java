import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainLogic {
    private final MainWindow mw;
    private final Clipboard clipboard;

    private final ArrayList<DiskClass> disks;
    private final ArrayList<File> pinnedFiles;
    private final HashMap<Tab, File> openedTabs;
    private boolean listView = true;
    private boolean openOnSame = true;
    private boolean doubleClick = true;
    private boolean showHidden = false;
    private boolean showExtensions = false;
    private String theme = "";
    private Tab settingsTab = null;
    private boolean cutting = false;

    public MainLogic(MainWindow mw) {
        this.clipboard = Clipboard.getSystemClipboard();
        this.mw = mw;
        this.disks = new ArrayList<>();
        this.pinnedFiles = new ArrayList<>();
        this.openedTabs = new HashMap<>();
        this.loadDrives();
    }

    protected void addPinned(File file) {
        this.pinnedFiles.add(file);
    }
    protected void removePinned(File file) {
        this.pinnedFiles.remove(file);
    }
    protected ArrayList<File> getPinnedFiles() {
        return this.pinnedFiles;
    }
    protected void savePinned() {
        try {
            PrintWriter pw = new PrintWriter(Loader.PATH_TO_CONFIG + "/pinned.txt");
            for (File f : this.pinnedFiles) {
                pw.print(f.getAbsolutePath() + ";");
            }
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            new WarningWindow("Can't find file!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
            throw new RuntimeException(e);
        }
    }

    private void loadDrives() {
        this.disks.clear();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            FileStore fileStore;
            try {
                fileStore = Files.getFileStore(root);
            } catch (IOException e) {
                new WarningWindow("Can't find root!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
                throw new RuntimeException(e);
            }
            try {
                String s = "(" + root + ")";
                s = s.replace("\\", "");
                s = s + fileStore.name();
                this.disks.add(new DiskClass(s, fileStore.getTotalSpace(), fileStore.getUsableSpace(), fileStore.getAttribute("volume:isRemovable").toString(), root.toString()));
            } catch (IOException e) {
                new WarningWindow("Can't load disk attributes!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
                throw new RuntimeException(e);
            }
        }
    }
    protected ArrayList<DiskClass> getDisks() {
        return this.disks;
    }

    protected boolean isListView() {
        return listView;
    }
    protected void setListView(boolean listView) {
        this.listView = listView;
    }

    protected boolean isOpenOnSame() {
        return openOnSame;
    }
    protected void setOpenOnSame(boolean openOnSame) {
        this.openOnSame = openOnSame;
    }

    protected boolean isDoubleClick() {
        return doubleClick;
    }
    protected void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    protected boolean isShowHidden() {
        return showHidden;
    }
    protected void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    protected boolean isShowExtensions() {
        return showExtensions;
    }
    protected void setShowExtensions(boolean showExtensions) {
        this.showExtensions = showExtensions;
    }

    protected String getTheme() {
        return theme;
    }
    protected void setTheme(String theme) {
        this.theme = theme;
    }
    protected void changeTheme(String theme) {
        this.mw.changeTheme(theme);
    }

    private Tab getSettingsTab() {
        return settingsTab;
    }
    private void setSettingsTab(Tab settingsTab) {
        this.settingsTab = settingsTab;
    }

    private void addOpenedTab(Tab tab, File file) {
        this.openedTabs.put(tab, file);
    }
    private void removeOpenedTab(Tab tab) {
        this.openedTabs.remove(tab);
    }
    private File getFileFromTab(Tab tab) {
        return this.openedTabs.get(tab);
    }
    protected Tab addFolderTab(String name, String path) {
        File f = new File(path);
        Tab t = new Tab(name, this.mw.folderTab(f));
        this.addOpenedTab(t, f);
        t.setOnClosed(e -> this.removeOpenedTab(t));
        return t;
    }

    protected void writeTheme(String theme) {
        this.replaceValueInConfig(this.theme, theme);
    }
    protected void writeExtensions(boolean showExtensions) {
        String s;
        if (this.showExtensions) {
            s = "yes";
        } else {
            s = "no";
        }
        String s1;
        if (showExtensions) {
            s1 = "yes";
        } else {
            s1 = "no";
        }
        this.replaceValueInConfig(s, s1);
    }
    protected void writeHidden(boolean showHidden) {
        String s;
        if (this.showHidden) {
            s = "true";
        } else {
            s = "false";
        }
        String s1;
        if (showHidden) {
            s1 = "true";
        } else {
            s1 = "false";
        }
        this.replaceValueInConfig(s, s1);
    }
    protected void writeDoubleClick(boolean doubleClick) {
        String s;
        if (this.doubleClick) {
            s = "1";
        } else {
            s = "0";
        }
        String s1;
        if (doubleClick) {
            s1 = "1";
        } else {
            s1 = "0";
        }
        this.replaceValueInConfig(s, s1);
    }
    protected void writeOnSame(boolean openOnSame) {
        String s;
        if (this.openOnSame) {
            s = "same";
        } else {
            s = "new";
        }
        String s1;
        if (openOnSame) {
            s1 = "same";
        } else {
            s1 = "new";
        }
        this.replaceValueInConfig(s, s1);
    }
    private void replaceValueInConfig(String oldValue, String newValue) {
        File config = new File(Loader.PATH_TO_CONFIG + "/config.cf");
        String s;
        BufferedReader br;
        PrintWriter pw;
        try {
            br = new BufferedReader(new FileReader(config));
            s = br.readLine();
            br.close();
            s = s.replace(oldValue, newValue);
            pw = new PrintWriter(config);
            pw.print(s);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            new WarningWindow("Can't replace value in config!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
            throw new RuntimeException(e);
        }
    }

    protected void copyPath(File file) {
        if (file != null) {
            ClipboardContent content = new ClipboardContent();
            content.putString(file.getPath());
            this.clipboard.setContent(content);
        }
    }
    protected void copy(File file) {
        if (file != null) {
            ClipboardContent content = new ClipboardContent();
            List<File> files = new ArrayList<>();
            files.add(file);
            content.putFiles(files);
            this.clipboard.setContent(content);
            this.cutting = false;
        }
    }
    protected void cut(File file) {
        if (file != null) {
            ClipboardContent content = new ClipboardContent();
            List<File> files = new ArrayList<>();
            files.add(file);
            content.putFiles(files);
            this.clipboard.setContent(content);
            this.cutting = true;
        }
    }
    protected void paste(File file) {
        if (this.clipboard.hasContent(DataFormat.FILES)) {
            File f = this.clipboard.getFiles().get(0);
            File f1 = new File(file.getPath() + "\\" + f.getName());
            try {
                if (cutting) {
                    if (f.isDirectory()) {
                        FileUtils.copyDirectory(f, f1);
                        FileUtils.deleteDirectory(f);
                    } else {
                        FileUtils.copyFile(f, f1);
                        if (!f.delete()) {
                            new WarningWindow("Can't cut file!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
                        }
                    }
                    this.cutting = false;
                    this.refresh(true);
                } else {
                    if (f.isDirectory()) {
                        FileUtils.copyDirectory(f, f1);
                    } else {
                        FileUtils.copyFile(f, f1);
                    }
                    this.refresh(false);
                }
            } catch (IOException e) {
                new WarningWindow("Can't paste!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
                throw new RuntimeException(e);
            }
        }
    }
    protected void rename(File file) {
        NameWindow nw = new NameWindow("Rename", file.getName(), this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
        String newPath = file.getPath().replace(file.getName(), "").replace("\\\\", "\\") + nw.getName();
        if (!file.renameTo(new File(newPath))) {
            new WarningWindow("Can't rename file!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
        }
        this.refresh(false);
    }
    protected void openWith(File file) {
        ProcessBuilder builder = new ProcessBuilder("RUNDLL32.EXE", "SHELL32.DLL,OpenAs_RunDLL", file.getAbsolutePath());
        if (builder.redirectErrorStream()) {
            new WarningWindow("Redirect error stream error!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
        }
        if (builder.redirectOutput() != ProcessBuilder.Redirect.PIPE) {
            new WarningWindow("Redirect output error!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
        }
        Process process;
        try {
            process = builder.start();
            process.waitFor();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
    protected void delete(File file) {
        if (file.isDirectory()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException ex) {
                new WarningWindow("Can't delete directory!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
                throw new RuntimeException(ex);
            }
        } else {
            if (!file.delete()) {
                new WarningWindow("Can't delete file!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
            }
        }
        this.refresh(false);
    }

    private String backPath(String path) {
        int index = path.lastIndexOf("\\");
        path = path.substring(0, index + 1);
        return path;
    }
    protected void back(Label address, Button back) {
        if (!address.getText().equals("C:\\")) {
            Tab t = this.mw.getTabPane().getSelectionModel().getSelectedItem();
            t.setContent(null);
            this.removeOpenedTab(t);
            String backString = this.backPath(address.getText());
            File f = new File(backString);
            t.setContent(this.mw.folderTab(f));
            t.setText(f.getName());
            if (backString.equals("C:\\")) {
                t.setText("(C:)OS");
            }
            this.addOpenedTab(t, f);
        } else {
            back.setDisable(false);
        }
    }
    protected void settings() {
        if (this.getSettingsTab() == null) {
            Tab settingsTab = new Tab("Settings", this.mw.settingsTab());
            settingsTab.setOnClosed(l -> this.setSettingsTab(null));
            this.mw.getTabPane().getTabs().add(this.mw.getTabPane().getTabs().size() - 1, settingsTab);
            this.mw.getTabPane().getSelectionModel().select(this.mw.getTabPane().getTabs().size() - 2);
            this.setSettingsTab(settingsTab);
        } else {
            this.mw.getTabPane().getSelectionModel().select(this.getSettingsTab());
        }
    }
    protected void newFile(Label address) {
        NameWindow nw = new NameWindow("Create new: ", "", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
        String name = nw.getName();
        int index = name.lastIndexOf(".");
        boolean b;
        if (index == -1) {
            b = new File(address.getText() + "\\" + name).mkdir();
        } else {
            try {
                b = new File(address.getText() + "\\" + name).createNewFile();
            } catch (IOException e) {
                new WarningWindow("Can't create file!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
                throw new RuntimeException(e);
            }
        }

        if (!b) {
            new WarningWindow("Can't create directory!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
        }
        this.refresh(false);
    }

    protected void openSide(String name, String path) {
        this.mw.getTabPane().getTabs().add(this.mw.getTabPane().getTabs().size() - 1, this.addFolderTab(name, path));
        this.mw.getTabPane().getSelectionModel().select(this.mw.getTabPane().getTabs().size() - 2);
    }

    protected void refresh(boolean all) {
        if (all) {
            for (Tab t : this.mw.getTabPane().getTabs()) {
                t.setContent(null);
                if (t.getText().equals("Home")) {
                    this.loadDrives();
                    t.setContent(this.mw.homeTab());
                } else if (t.getText().equals("Settings")) {
                    t.setContent(this.mw.settingsTab());
                } else if (!t.getText().equals("Add")) {
                    t.setContent(this.mw.folderTab(this.getFileFromTab(t)));
                }
            }
        } else {
            Tab t = this.mw.getTabPane().getSelectionModel().getSelectedItem();
            t.setContent(null);
            t.setContent(this.mw.folderTab(this.getFileFromTab(t)));
        }
    }

    protected void setControlButtonActions(File file, HBox fileUI, Button control) {
        control.setOnMouseEntered(e -> fileUI.setStyle("-fx-background-color: default-color"));
        control.setOnMouseExited(e -> fileUI.setStyle("-fx-background-color: elevated-background-color"));

        control.setOnAction(e -> {
            if (!this.isDoubleClick()) {
                clickOnFile(file);
            } else {
                fileUI.setStyle("-fx-background-color: selected-color");
            }
        });
        control.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY)){
                if(e.getClickCount() == 2){
                    clickOnFile(file);
                }
            }
        });
    }
    protected void setupControlButtonFile(Button control, HBox fileBox, File file) {
        if (file.isDirectory()) {
            control.setContextMenu(this.mw.setRightClickMenu(0, file));
        } else {
            control.setContextMenu(this.mw.setRightClickMenu(3, file));
        }
        setControlButtonActions(file, fileBox, control);
    }
    private void clickOnFile(File file) {
        if (file.isDirectory()) {
            int index = this.mw.getTabPane().getTabs().size() - 1;
            if (this.isOpenOnSame()) {
                index = this.mw.getTabPane().getSelectionModel().getSelectedIndex();
                Tab t = this.mw.getTabPane().getTabs().get(index);
                this.removeOpenedTab(t);
                this.mw.getTabPane().getTabs().remove(t);
            }
            this.mw.getTabPane().getTabs().add(index, this.addFolderTab(file.getName(), file.getPath()));
            this.mw.getTabPane().getSelectionModel().select(index);
        } else {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                new WarningWindow("Can't open file!", this.mw.getMainScene().getStylesheets().get(this.mw.getMainScene().getStylesheets().size() - 1));
                throw new RuntimeException(ex);
            }
            this.refresh(false);
        }
    }
    protected ImageView iconToImageView(File file) {
        ImageView icon;
        Icon i = FileSystemView.getFileSystemView().getSystemIcon(file);
        BufferedImage bi = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        i.paintIcon(null, bi.getGraphics(), 0, 0);
        Image im = SwingFXUtils.toFXImage(bi, null);
        icon = new ImageView(im);
        return icon;
    }
}
