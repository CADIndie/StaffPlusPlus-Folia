package net.shortninja.staffplus.core.domain.staff.infractions.cmd;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionType;
import net.shortninja.staffplus.core.domain.staff.infractions.gui.InfractionsTopGui;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(
    command = "commands:infractions-top-view",
    permissions = "permissions:infractions.view",
    description = "View the top list of players with the most infractions",
    usage = "[infractionType?]"
)
@IocBean(conditionalOnProperty = "infractions-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class InfractionsTopCmd extends AbstractCmd {

    public InfractionsTopCmd(Messages messages, CommandService commandService, PermissionHandler permissionHandler) {
        super(messages, permissionHandler, commandService);
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        if (!(sender instanceof Player)) {
            throw new BusinessException(messages.onlyPlayers);
        }

        Player p = (Player) sender;
        if (args.length == 1) {
            if(!JavaUtils.isValidEnum(InfractionType.class, args[0])) {
                throw new BusinessException("&CInvalid infraction type provided");
            }
            InfractionType infractionType = InfractionType.valueOf(args[0]);
            new InfractionsTopGui(p,"Top infractions", 0, Arrays.asList(infractionType)).show(p);
        }else {
            new InfractionsTopGui(p,"Top infractions", 0).show(p);
        }

        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 0;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Arrays.stream(InfractionType.values()).map(Enum::name).collect(Collectors.toList());
    }
}
