package net.shortninja.staffplus.core.application.config.migrators;

import be.garagepoort.mcioc.configuration.files.ConfigurationFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class PermissionsMigrator implements StaffPlusPlusConfigMigrator {

    private void migrateModule(FileConfiguration defaultConfig, FileConfiguration modulesConfig, String from, String to) {
        ConfigurationSection configurationSection = defaultConfig.getConfigurationSection(from);
        if (configurationSection != null) {
            modulesConfig.set(to, configurationSection);
            defaultConfig.set(from, null);
        }
    }

    @Override
    public void migrate(List<ConfigurationFile> configs) {
        FileConfiguration defaultConfig = getConfig(configs, "config");
        FileConfiguration permissionsConfig = getConfig(configs, "permissions");
        migrateModule(defaultConfig, permissionsConfig, "permissions", "permissions");
    }
}
