package net.shortninja.staffplus.core.domain.actions;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.domain.actions.database.ActionableRepository;
import net.shortninja.staffplus.core.domain.delayedactions.database.DelayedActionsRepository;
import net.shortninja.staffplusplus.Actionable;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.domain.actions.ActionRunStrategy.ALWAYS;
import static net.shortninja.staffplus.core.domain.actions.ActionRunStrategy.DELAY;
import static net.shortninja.staffplus.core.domain.actions.ActionRunStrategy.ONLINE;
import static net.shortninja.staffplus.core.domain.delayedactions.Executor.CONSOLE;

@IocBean
public class ActionExecutioner {


    private final ActionableRepository actionableRepository;
    private final DelayedActionsRepository delayedActionsRepository;

    public ActionExecutioner(ActionableRepository actionableRepository, DelayedActionsRepository delayedActionsRepository) {
        this.actionableRepository = actionableRepository;
        this.delayedActionsRepository = delayedActionsRepository;
    }

    boolean executeAction(Actionable actionable, ActionTargetProvider targetProvider, ConfiguredAction action, List<ActionFilter> actionFilters) {
        Optional<SppPlayer> target = targetProvider.getTarget(action);
        if (!target.isPresent()) {
            return false;
        }

        if (actionFilters != null && actionFilters.stream().anyMatch(a -> !a.isValidAction(target.get(), action))) {
            return false;
        }
        if (runActionNow(target.get(), action.getRunStrategy())) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.getCommand().replace("%player%", target.get().getUsername()));
            ExecutableActionEntity executableActionEntity = new ExecutableActionEntity(action, actionable, true);
            actionableRepository.saveActionable(executableActionEntity);
            return true;
        } else if (action.getRunStrategy() == DELAY && !target.get().isOnline()) {
            ExecutableActionEntity executableActionEntity = new ExecutableActionEntity(action, actionable, false);
            int executableActionId = actionableRepository.saveActionable(executableActionEntity);
            delayedActionsRepository.saveDelayedAction(target.get().getId(), action.getCommand(), CONSOLE, executableActionId, false);
            return true;
        }
        return false;
    }

    void executeAction(ActionTargetProvider targetProvider, ConfiguredAction action, List<ActionFilter> actionFilters, Map<String, String> placeholders) {
        Optional<SppPlayer> target = targetProvider.getTarget(action);
        if (!target.isPresent()) {
            return;
        }
        placeholders.putIfAbsent("%player%", target.get().getUsername());

        if (actionFilters != null && actionFilters.stream().anyMatch(a -> !a.isValidAction(target.get(), action))) {
            return;
        }
        String commandLine = replacePlaceholders(action.getCommand(), placeholders);

        if (runActionNow(target.get(), action.getRunStrategy())) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
        } else if (action.getRunStrategy() == DELAY && !target.get().isOnline()) {
            delayedActionsRepository.saveDelayedAction(target.get().getId(), commandLine, CONSOLE);
        }
    }

    void rollbackActions(List<ExecutableActionEntity> actions, SppPlayer target) {
        if (actions.isEmpty()) {
            return;
        }

        List<ExecutableActionEntity> actionsToRun = actions.stream().filter(a -> runActionNow(target, a.getRollbackRunStrategy())).collect(Collectors.toList());
        actionsToRun.forEach(action -> Bukkit.getScheduler().runTask(StaffPlus.get(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.getRollbackCommand().replace("%player%", target.getUsername()))));
        actionableRepository.markRollbacked(actionsToRun.stream().map(ExecutableActionEntity::getId).collect(Collectors.toList()));

        if (!target.isOnline()) {
            List<ExecutableActionEntity> actionsToDelay = actions.stream().filter(a -> a.getRollbackRunStrategy() == DELAY).collect(Collectors.toList());
            actionsToDelay.forEach(action -> delayedActionsRepository.saveDelayedAction(target.getId(), action.getRollbackCommand(), CONSOLE, action.getId(), true));
        }
    }

    private String replacePlaceholders(String message, Map<String, String> placeholders) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private boolean runActionNow(SppPlayer target, ActionRunStrategy runStrategy) {
        return runStrategy == ALWAYS
            || (runStrategy == ONLINE && target.isOnline())
            || (runStrategy == DELAY && target.isOnline());
    }
}
