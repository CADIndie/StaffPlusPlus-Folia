package net.shortninja.staffplus.core.domain.staff;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.vanish.VanishConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@Command(
    command = "commands:staff-list",
    permissions = "permissions:staff-list.view",
    description = "Lists all registered staff members.",
    usage = "{all | online | away | offline}"
)
@IocBean
@IocMultiProvider(SppCommand.class)
public class PersonnelCmd extends AbstractCmd {

    @ConfigProperty("permissions:member")
    private String permissionMember;

    private final PermissionHandler permissionHandler;
    private final OnlineSessionsManager sessionManager;
    private final VanishConfiguration vanishConfiguration;

    public PersonnelCmd(PermissionHandler permissionHandler, Messages messages, OnlineSessionsManager sessionManager, CommandService commandService, VanishConfiguration vanishConfiguration) {
        super(messages, permissionHandler, commandService);
        this.permissionHandler = permissionHandler;
        this.sessionManager = sessionManager;
        this.vanishConfiguration = vanishConfiguration;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer sppPlayer, Map<String, String> optionalParameters) {
        String status = "all";

        if (args.length == 1) {
            status = args[0];
        }

        for (String message : messages.staffListStart) {
            this.messages.send(sender, message.replace("%longline%", this.messages.LONG_LINE), message.contains("%longline%") ? "" : messages.prefixGeneral);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            OnlinePlayerSession session = sessionManager.get(player);
            if (hasStatus(session, status, player)) {
                messages.send(sender, messages.staffListMember.replace("%player%", player.getName()).replace("%statuscolor%", getStatusColor(session, player)), messages.prefixGeneral);
            }
        }

        for (String message : messages.staffListEnd) {
            this.messages.send(sender, message.replace("%longline%", this.messages.LONG_LINE), message.contains("%longline%") ? "" : messages.prefixGeneral);
        }

        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 0;
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return false;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }

    private boolean hasStatus(OnlinePlayerSession session, String status, Player player) {
        VanishType vanishType = session.getVanishType();
        if (!permissionHandler.has(player, permissionMember)) {
            return false;
        }

        switch (status.toLowerCase()) {
            case "online":
                return vanishType == VanishType.NONE || vanishType == VanishType.PLAYER;
            case "offline":
                return vanishType == VanishType.TOTAL || (vanishType == VanishType.LIST && !vanishConfiguration.vanishShowAway);
            case "away":
                return vanishType == VanishType.NONE || (vanishType == VanishType.LIST && vanishConfiguration.vanishShowAway);
        }
        return true;
    }

    private String getStatusColor(OnlinePlayerSession user, Player player) {
        String statusColor = "4";

        if (hasStatus(user, "online", player)) {
            statusColor = "a";
        } else if (hasStatus(user, "away", player)) {
            statusColor = "e";
        }

        return statusColor;
    }
}