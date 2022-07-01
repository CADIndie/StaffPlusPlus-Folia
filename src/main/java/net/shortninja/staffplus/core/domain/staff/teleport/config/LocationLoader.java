package net.shortninja.staffplus.core.domain.staff.teleport.config;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigurationLoader;
import be.garagepoort.mcioc.configuration.yaml.configuration.ConfigurationSection;
import net.shortninja.staffplus.core.application.config.AbstractConfigLoader;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;

@IocBean
public class LocationLoader extends AbstractConfigLoader<Map<String, Location>> {

    public LocationLoader(ConfigurationLoader configurationLoader) {
        super(configurationLoader);
    }

    @Override
    protected Map<String, Location> load() {
        ConfigurationSection locationsConfig = defaultConfig.getConfigurationSection("locations");
        if (locationsConfig == null) {
            return Collections.emptyMap();
        }

        Map<String, Location> locations = new HashMap<>();
        for (String identifier : locationsConfig.getKeys(false)) {
            String locationString = defaultConfig.getString("locations." + identifier);
            String[] points = locationString.split(";");
            if (points.length < 3) {
                throw new ConfigurationException("Invalid locations configuration. Make sure your location points are in format x;y;z;worldname");
            }

            World world = points.length == 4 ? Bukkit.getWorld(points[3]) : Bukkit.getWorlds().get(0);
            Location location = new Location(world, parseDouble(points[0]), parseDouble(points[1]), parseDouble(points[2]));
            locations.put(identifier, location);
        }
        return locations;
    }
}
