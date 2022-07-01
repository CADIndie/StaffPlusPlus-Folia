package net.shortninja.staffplus.core.application.config.migrators;

import be.garagepoort.mcioc.configuration.files.ConfigurationFile;
import net.shortninja.staffplus.core.domain.actions.ActionRunStrategy;
import be.garagepoort.mcioc.configuration.yaml.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.List;

public class WarningCommandsMigrator implements StaffPlusPlusConfigMigrator {

    @Override
    public void migrate(List<ConfigurationFile> configs) {
        FileConfiguration config = getConfig(configs, "config");

        List<LinkedHashMap<String, Object>> actions = (List<LinkedHashMap<String, Object>>) config.get("warnings-module.actions");
        if (actions != null) {
            actions.forEach(this::migrateChannel);
        }
    }

    private void migrateChannel(LinkedHashMap<String, Object> action) {
        String command = (String) action.get("command");
        action.put("command", command.replace("%player%", "%target%"));

        Object rollbackCommand = action.get("rollback-command");
        if (rollbackCommand instanceof String) {
            LinkedHashMap<String, Object> rollbackCommandConfig = new LinkedHashMap<>();
            rollbackCommandConfig.put("command", ((String) rollbackCommand).replace("%player%", "%target%"));
            action.put("rollback-command", rollbackCommandConfig);
        }

        if (action.containsKey("run-strategy")) {
            ActionRunStrategy runStrategy = ActionRunStrategy.valueOf((String) action.get("run-strategy"));
            if (runStrategy != ActionRunStrategy.ALWAYS) {
                action.put("target", "target");
                action.put("target-run-strategy", runStrategy.name());
            }
            action.remove("run-strategy");
        }
    }
}
