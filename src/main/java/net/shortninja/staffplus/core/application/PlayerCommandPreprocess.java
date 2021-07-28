package net.shortninja.staffplus.core.application;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.cmd.BaseCmd;
import net.shortninja.staffplus.core.common.cmd.CmdHandler;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.freeze.FreezeHandler;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.domain.staff.tracing.TraceType.COMMANDS;

@IocBean
public class PlayerCommandPreprocess implements Listener {
    private final PermissionHandler permission;

    private final Options options;
    private final Messages messages;
    private final FreezeHandler freezeHandler;
    private final CmdHandler cmdHandler;
    private final TraceService traceService;
    private final SessionManagerImpl sessionManager;

    @ConfigProperty("commands:login")
    private String commandLogin;
    @ConfigProperty("permissions:block")
    private String permissionBlock;

    public PlayerCommandPreprocess(PermissionHandler permission, Options options, Messages messages, FreezeHandler freezeHandler, CmdHandler cmdHandler, TraceService traceService, SessionManagerImpl sessionManager) {
        this.permission = permission;

        this.options = options;
        this.messages = messages;
        this.freezeHandler = freezeHandler;
        this.cmdHandler = cmdHandler;
        this.traceService = traceService;
        this.sessionManager = sessionManager;
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String command = event.getMessage().toLowerCase();
        traceService.sendTraceMessage(COMMANDS, uuid, "Player invoked command: [" + command + "]");

        if (command.startsWith("/help staffplus") || command.startsWith("/help staff+")) {
            sendHelp(player);
            event.setCancelled(true);
            return;
        }

        if (options.blockedCommands.contains(command) && permission.hasOnly(player, permissionBlock)) {
            messages.send(player, messages.commandBlocked, messages.prefixGeneral);
            event.setCancelled(true);
        } else if (sessionManager.get(uuid).isInStaffMode() && options.blockedModeCommands.contains(command)) {
            messages.send(player, messages.modeCommandBlocked, messages.prefixGeneral);
            event.setCancelled(true);
        } else if (freezeHandler.isFrozen(uuid) && (!options.staffItemsConfiguration.getFreezeModeConfiguration().isModeFreezeChat() && !command.startsWith("/" + commandLogin))) {
            messages.send(player, messages.chatPrevented, messages.prefixGeneral);
            event.setCancelled(true);
        }
    }


    private void sendHelp(Player player) {
        int count = 0;

        messages.send(player, "&7" + messages.LONG_LINE, "");

        List<BaseCmd> sortedCommands = cmdHandler.commands.stream()
            .sorted(Comparator.comparing(o -> o.getCommand().getName()))
            .collect(Collectors.toList());

        for (BaseCmd baseCmd : sortedCommands) {
            if (baseCmd.getPermissions().isEmpty()) {
                messages.send(player, "&b/" + baseCmd.getCommand().getName() + " &7: " + baseCmd.getDescription().toLowerCase(), "");
                count++;
            } else {
                for (String permission : baseCmd.getPermissions()) {
                    if (this.permission.has(player, permission)) {
                        messages.send(player, "&b/" + baseCmd.getCommand().getName() + " &7: " + baseCmd.getDescription().toLowerCase(), "");
                        count++;
                        break;
                    }
                }
            }
        }

        if (count == 0) {
            messages.send(player, messages.noPermission, messages.prefixGeneral);
        }

        messages.send(player, "&7" + messages.LONG_LINE, "");
    }
}