package net.shortninja.staffplus.core.domain.staff.investigate.gui.cmd;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.staff.investigate.InvestigationService;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@Command(
    command = "commands:investigations.manage.conclude",
    permissions = "permissions:investigations.manage.investigate",
    description = "Conclude your investigation"
)
@IocBean(conditionalOnProperty = "investigations-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class ConcludeInvestigationCmd extends AbstractCmd {

    private final InvestigationService investigationService;
    private final BukkitUtils bukkitUtils;

    public ConcludeInvestigationCmd(Messages messages,
                                    CommandService commandService,
                                    InvestigationService investigationService,
                                    PermissionHandler permissionHandler, BukkitUtils bukkitUtils) {
        super(messages, permissionHandler, commandService);
        this.investigationService = investigationService;
        this.bukkitUtils = bukkitUtils;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        if (!(sender instanceof Player)) {
            throw new BusinessException(messages.onlyPlayers);
        }

        bukkitUtils.runTaskAsync(sender, () -> investigationService.concludeInvestigation((Player) sender));

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
}
