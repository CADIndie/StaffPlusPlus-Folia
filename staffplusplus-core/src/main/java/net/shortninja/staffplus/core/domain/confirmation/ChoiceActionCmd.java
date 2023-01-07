package net.shortninja.staffplus.core.domain.confirmation;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Command(
    command = "commands:choice-action",
    permissions = "permissions:mode",
    description = "Selects 1 of 2 action.",
    usage = "[option1|option2] [actionUuid]"
)
@IocBean
@IocMultiProvider(SppCommand.class)
public class ChoiceActionCmd extends AbstractCmd {

    private final ChoiceChatService choiceChatService;

    public ChoiceActionCmd(Messages messages, ChoiceChatService choiceChatService, CommandService commandService, PermissionHandler permissionHandler) {
        super(messages, permissionHandler, commandService);
        this.choiceChatService = choiceChatService;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        if (!(sender instanceof Player)) {
            throw new BusinessException(messages.onlyPlayers);
        }
        String action = args[0];
        UUID uuid = UUID.fromString(args[1]);

        if (action.equalsIgnoreCase("option1")) {
            choiceChatService.selectOption1(uuid, (Player) sender);
            return true;
        }
        if (action.equalsIgnoreCase("option2")) {
            choiceChatService.selectOption2(uuid, (Player) sender);
            return true;
        }
        return false;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 2;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }
}
