public class DiskClass {
    private final long totalCapacity;
    private final long freeCapacity;
    private final String name;
    private final String removable;

    public DiskClass(String name, long total, long free, String removable) {
        this.name = name;
        this.totalCapacity = total;
        this.freeCapacity = free;
        this.removable = removable;
    }

    public String getName() {
        return name;
    }
    public double getUsedPercentage() {
        return Math.round((1 - ((double) this.freeCapacity / (double) this.totalCapacity)) * 100.0) / 100.0;
    }
    public long getTotalCapacity() {
        return totalCapacity / 1000000000;
    }
    public long getFreeCapacity() {
        return freeCapacity / 1000000000;
    }
    public String  isRemovable() {
        return removable;
    }
}
