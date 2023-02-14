public class DiskClass {
    private final long totalCapacity;
    private final long freeCapacity;
    private final String name;
    private final String removable;
    private final String path;

    public DiskClass(String name, long total, long free, String removable, String path) {
        this.name = name;
        this.totalCapacity = total;
        this.freeCapacity = free;
        this.removable = removable;
        this.path = path;
    }

    protected String getName() {
        return name;
    }
    protected double getUsedPercentage() {
        return Math.round((1 - ((double) this.freeCapacity / (double) this.totalCapacity)) * 100.0) / 100.0;
    }
    protected long getTotalCapacity() {
        return totalCapacity / 1000000000;
    }
    protected long getFreeCapacity() {
        return freeCapacity / 1000000000;
    }
    protected String  isRemovable() {
        return removable;
    }
    protected String getPath() {
        return path;
    }
}
