package net.shortninja.staffplus.core.application.config.migrators;

import be.garagepoort.mcioc.configuration.files.ConfigurationFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Set;

public class CommandsV2Migrator implements StaffPlusPlusConfigMigrator {

    @Override
    public void migrate(List<ConfigurationFile> configs) {
        FileConfiguration config = getConfig(configs, "commands");

        ConfigurationSection commands = config.getConfigurationSection("commands");
        if (commands != null) {
            Set<String> keys = commands.getKeys(false);
            for (String key : keys) {
                config.set(key, commands.get(key));
            }
            config.set("commands", null);
        }
    }
}
