package net.shortninja.staffplus.core.domain.player.gui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcioc.tubinggui.GuiActionService;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.OPTIONAL_BOTH;

@Command(
    command = "commands:players",
    permissions = "permissions:players",
    description = "Open player details gui.",
    usage = "[player]",
    playerRetrievalStrategy = OPTIONAL_BOTH
)
@IocBean
@IocMultiProvider(SppCommand.class)
public class PlayersCmd extends AbstractCmd {

    private final GuiActionService guiActionService;
    private final PlayerManager playerManager;

    public PlayersCmd(Messages messages,
                      PermissionHandler permissionHandler,
                      CommandService commandService,
                      GuiActionService guiActionService,
                      PlayerManager playerManager) {
        super(messages, permissionHandler, commandService);
        this.guiActionService = guiActionService;
        this.playerManager = playerManager;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        validateIsPlayer(sender);
        if (args.length == 0) {
            guiActionService.executeAction((Player) sender, "players/view/select-overview-type");
            return true;
        }
        guiActionService.executeAction((Player) sender, "players/view/detail?targetPlayerName=" + player.getUsername());
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 0;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Optional.empty();
        }
        return Optional.of(args[0]);
    }

    @Override
    public List<String> autoComplete(CommandSender sender, String[] args, String[] sppArgs) throws IllegalArgumentException {
        String currentArg = args.length > 0 ? args[args.length - 1] : "";

        if (args.length == 1) {
            return playerManager.getAllPlayerNames().stream()
                .filter(s -> currentArg.isEmpty() || s.contains(currentArg))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
