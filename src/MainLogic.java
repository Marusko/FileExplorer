import javafx.scene.control.Tab;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class MainLogic {

    private final ArrayList<DiskClass> disks;
    private File activeFile = null;
    private boolean listView = true;
    private boolean openOnSame = true;
    private boolean doubleClick = true;
    private boolean showHidden = false;
    private boolean showExtensions = false;
    private Tab settingsTab = null;

    public MainLogic() {
        this.disks = new ArrayList<>();
        this.loadDrives();
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

    public Tab getSettingsTab() {
        return settingsTab;
    }
    public void setSettingsTab(Tab settingsTab) {
        this.settingsTab = settingsTab;
    }
}
