package net.shortninja.staffplus.core.domain.staff.mode.cmd;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.player.StripService;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.ONLINE;

@Command(
    command = "commands:strip",
    permissions = "permissions:strip",
    description = "Completely removes the target player's armor.",
    usage = "[player]",
    playerRetrievalStrategy = ONLINE
)
@IocBean
@IocMultiProvider(SppCommand.class)
public class StripCmd extends AbstractCmd {
    private final StripService stripService;

    public StripCmd(Messages messages,
                    StripService stripService,
                    CommandService commandService,
                    PermissionHandler permissionHandler) {
        super(messages, permissionHandler, commandService);
        this.stripService = stripService;
    }

    @Override
    public boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer targetPlayer, Map<String, String> optionalParameters) {
        stripService.strip(sender, targetPlayer.getPlayer());
        return true;
    }

    @Override
    protected void validateExecution(SppPlayer player) {
        if (!JavaUtils.hasInventorySpace(player.getPlayer())) {
            throw new BusinessException("&CCannot strip this player. His inventory has no more space left");
        }
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return 0;
        }
        return 1;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        if (args.length == 0 && (sender instanceof Player)) {
            return Optional.of(sender.getName());
        }
        return Optional.of(args[0]);
    }
}