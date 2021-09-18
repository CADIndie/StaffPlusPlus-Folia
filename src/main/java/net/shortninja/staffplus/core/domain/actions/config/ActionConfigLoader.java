package net.shortninja.staffplus.core.domain.actions.config;

import be.garagepoort.mcioc.configuration.IConfigTransformer;
import net.shortninja.staffplus.core.application.config.ConfigurationUtil;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;
import net.shortninja.staffplus.core.domain.actions.ActionRunStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionConfigLoader implements IConfigTransformer<List<ConfiguredCommand>, List<LinkedHashMap<String, Object>>> {

    public static List<ConfiguredCommand> loadActions(List<LinkedHashMap<String, Object>> list) {
        return mapConfigAction(list);
    }

    @Override
    public List<ConfiguredCommand> mapConfig(List<LinkedHashMap<String, Object>> list) {
        return mapConfigAction(list);
    }

    @NotNull
    private static List<ConfiguredCommand> mapConfigAction(List<LinkedHashMap<String, Object>> list) {
        return list.stream().map(ActionConfigLoader::mapCommand).collect(Collectors.toList());
    }

    @NotNull
    private static ConfiguredCommand mapCommand(LinkedHashMap<String, Object> map) {
        if (!map.containsKey("command")) {
            throw new ConfigurationException("Invalid actions configuration. Actions should define a command");
        }
        String command = (String) map.get("command");
        ConfiguredCommand rollbackCommand = map.containsKey("rollback-command") ? mapCommand((LinkedHashMap<String, Object>) map.get("rollback-command")) : null;
        String executor = map.containsKey("executor") ? (String) map.get("executor") : "console";
        String target = (String) map.get("target");
        ActionRunStrategy executorRunStrategy = map.containsKey("executor-run-strategy") ? ActionRunStrategy.valueOf((String) map.get("executor-run-strategy")) : ActionRunStrategy.ONLINE;
        ActionRunStrategy targetRunStrategy = map.containsKey("target-run-strategy") ? ActionRunStrategy.valueOf((String) map.get("target-run-strategy")) : null;
        String filtersString = map.containsKey("filters") ? (String) map.get("filters") : null;

        Map<String, String> filterMap = ConfigurationUtil.loadFilters(filtersString);

        return new ConfiguredCommand(command, executor, executorRunStrategy, target, targetRunStrategy, filterMap, rollbackCommand);
    }
}
