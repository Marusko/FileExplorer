import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class MainLogic {

    private final ArrayList<DiskClass> disks;

    public MainLogic() {
        this.disks = new ArrayList<>();
        this.loadDrives();
    }

    private void loadDrives() {
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
                this.disks.add(new DiskClass(s, fileStore.getTotalSpace(), fileStore.getUsableSpace(), fileStore.getAttribute("volume:isRemovable").toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public ArrayList<DiskClass> getDisks() {
        return this.disks;
    }

}
