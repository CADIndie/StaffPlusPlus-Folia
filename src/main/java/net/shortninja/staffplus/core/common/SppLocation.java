package net.shortninja.staffplus.core.common;

import net.shortninja.staffplusplus.ILocation;
import org.bukkit.Location;

public class SppLocation implements ILocation {
    private String worldName;
    private double x;
    private double y;
    private double z;
    private String serverName;

    public SppLocation(String worldName, double x, double y, double z, String serverName) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.serverName = serverName;
    }

    public SppLocation(Location location, String serverName) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.serverName = serverName;
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public String getServerName() {
        return serverName;
    }
}
