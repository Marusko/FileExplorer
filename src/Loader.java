import javax.swing.filechooser.FileSystemView;
import java.io.*;

public class Loader {
    private static final String FALL_BACK = "[Theme]=Dark;[Extensions]=no;[Hidden]=false;[Double]=1;[Where]=same";
    public static final String PATH_TO_CONFIG = FileSystemView.getFileSystemView().getHomeDirectory().toString() + "/FileExplorer";

    private final MainLogic ml;

    public Loader(MainLogic ml) {
        this.ml = ml;
        try {
            this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() throws IOException {
        File folder = new File(Loader.PATH_TO_CONFIG);
        if(folder.exists()) {
            this.existing(folder);
        } else {
            String[][] splitFallBack = this.splitString(Loader.FALL_BACK);
            for (String[] s : splitFallBack) {
                this.configSettings(s);
            }

            if (folder.mkdir()) {
                this.notExisting(folder);
            } else {
                throw new IOException("Can't create the directory!");
            }
        }
    }

    private void existing(File folder) {
        this.notExisting(folder);
        File config = new File(folder.getPath() + "/config.cf");
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(config));
            line = br.readLine();
            String[][] splitConfig = this.splitString(line);
            for (String[] s : splitConfig) {
                this.configSettings(s);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //File pinned = new File(folder.getPath() + "/pinned.txt");
    }

    private void notExisting(File folder) {
        File config = new File(folder.getPath() + "/config.cf");
        if (!config.exists()) {
            PrintWriter pw;
            try {
                if (config.createNewFile()) {
                    pw = new PrintWriter(config);
                } else {
                    throw new IOException("Can't create the config file!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pw.print(Loader.FALL_BACK);
            pw.flush();
            pw.close();
        }
        File pinned = new File(folder.getPath() + "/pinned.txt");
        if (!pinned.exists()) {
            try {
                if (!pinned.createNewFile()) {
                    throw new IOException("Can't create pinned file");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void configSettings(String[] s) {
        switch (s[0]) {
            case "Theme" -> {
                this.ml.setTheme(s[1]);
                this.ml.changeTheme(s[1]);
            }
            case "Extensions" -> {
                if (s[1].equals("no")) {
                    this.ml.setShowExtensions(false);
                } else if (s[1].equals("yes")){
                    this.ml.setShowExtensions(true);
                }
            }
            case "Hidden" -> this.ml.setShowHidden(Boolean.parseBoolean(s[1]));
            case "Double" -> {
                if (s[1].equals("0")) {
                    this.ml.setDoubleClick(false);
                } else if (s[1].equals("1")){
                    this.ml.setDoubleClick(true);
                }
            }
            case "Where" -> {
                if (s[1].equals("new")) {
                    this.ml.setOpenOnSame(false);
                } else if (s[1].equals("same")){
                    this.ml.setOpenOnSame(true);
                }
            }
        }
    }
    private String[][] splitString(String s) {
        String[][] split = new String[5][2];
        String[] firstSplit = s.split(";");
        for (int i = 0; i < firstSplit.length; i++) {
            String[] secondSplit = firstSplit[i].split("=");
            secondSplit[0] = secondSplit[0].replace("[", "").replace("]", "");
            split[i][0] = secondSplit[0];
            split[i][1] = secondSplit[1];
        }
        return split;
    }
}
