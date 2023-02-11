import javafx.scene.control.Tab;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MainLogic {
    private final MainWindow mw;
    private final Clipboard clipboard;

    private final ArrayList<DiskClass> disks;
    private final ArrayList<File> pinnedFiles;
    private File activeFile = null;
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
        this.loadDrives();
    }

    public void addPinned(File file) {
        this.pinnedFiles.add(file);
    }
    public void removePinned(File file) {
        this.pinnedFiles.remove(file);
    }
    public ArrayList<File> getPinnedFiles() {
        return this.pinnedFiles;
    }
    public void savePinned() {
        try {
            PrintWriter pw = new PrintWriter(Loader.PATH_TO_CONFIG + "/pinned.txt");
            for (File f : this.pinnedFiles) {
                pw.print(f.getAbsolutePath() + ";");
            }
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadDrives() {
        this.disks.clear();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            FileStore fileStore;
            try {
                fileStore = Files.getFileStore(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                String s = "(" + root + ")";
                s = s.replace("\\", "");
                s = s + fileStore.name();
                this.disks.add(new DiskClass(s, fileStore.getTotalSpace(), fileStore.getUsableSpace(), fileStore.getAttribute("volume:isRemovable").toString(), root.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public ArrayList<DiskClass> getDisks() {
        return this.disks;
    }

    public boolean isListView() {
        return listView;
    }
    public void setListView(boolean listView) {
        this.listView = listView;
    }

    public boolean isOpenOnSame() {
        return openOnSame;
    }
    public void setOpenOnSame(boolean openOnSame) {
        this.openOnSame = openOnSame;
    }

    public File getActiveFile() {
        return activeFile;
    }
    public void setActiveFile(File activeFile) {
        this.activeFile = activeFile;
    }

    public boolean isDoubleClick() {
        return doubleClick;
    }
    public void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public boolean isShowHidden() {
        return showHidden;
    }
    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    public boolean isShowExtensions() {
        return showExtensions;
    }
    public void setShowExtensions(boolean showExtensions) {
        this.showExtensions = showExtensions;
    }

    public String getTheme() {
        return theme;
    }
    public void setTheme(String theme) {
        this.theme = theme;
    }
    public void changeTheme(String theme) {
        this.mw.changeTheme(theme);
    }

    public Tab getSettingsTab() {
        return settingsTab;
    }
    public void setSettingsTab(Tab settingsTab) {
        this.settingsTab = settingsTab;
    }

    public void writeTheme(String theme) {
        this.replaceValueInConfig(this.theme, theme);
    }
    public void writeExtensions(boolean showExtensions) {
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
    public void writeHidden(boolean showHidden) {
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
    public void writeDoubleClick(boolean doubleClick) {
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
    public void writeOnSame(boolean openOnSame) {
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
            throw new RuntimeException(e);
        }
    }

    public void copyPath(File file) {
        if (file != null) {
            ClipboardContent content = new ClipboardContent();
            content.putString(file.getPath());
            this.clipboard.setContent(content);
        }
    }
    public void copy(File file) {
        if (file != null) {
            ClipboardContent content = new ClipboardContent();
            List<File> files = new ArrayList<>();
            files.add(file);
            content.putFiles(files);
            this.clipboard.setContent(content);
            this.cutting = false;
        }
    }
    public void cut(File file) {
        if (file != null && !file.isDirectory()) {
            ClipboardContent content = new ClipboardContent();
            List<File> files = new ArrayList<>();
            files.add(file);
            content.putFiles(files);
            this.clipboard.setContent(content);
            this.cutting = true;
        }
    }
    public void paste(File file) {
        if (this.clipboard.hasContent(DataFormat.FILES)) {
            File f = this.clipboard.getFiles().get(0);
            File f1 = new File(file.getPath() + "\\" + f.getName());
            try {
                if (cutting) {
                    if (f.isDirectory()) {
                        //cut and paste directory is not working
                    } else {
                        Files.copy(f.toPath(), f1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        f.delete();
                        this.mw.refresh(true);
                    }
                    this.cutting = false;
                } else {
                    if (f.isDirectory()) {
                        //copy and paste directory is not working
                    } else {
                        Files.copy(f.toPath(), f1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        this.mw.refresh(true);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
