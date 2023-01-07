package net.shortninja.staffplus.core.domain.staff.ban.ipbans.gui.cmd;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.player.ip.database.PlayerIpRepository;
import net.shortninja.staffplus.core.domain.staff.ban.ipbans.IpBan;
import net.shortninja.staffplus.core.domain.staff.ban.ipbans.IpBanService;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.JavaUtils.isValidCidrOrIp;
import static net.shortninja.staffplus.core.common.utils.BukkitUtils.getIpFromPlayer;

@Command(
    command = "commands:ipban.unban",
    permissions = "permissions:ipban.unban",
    description = "Unban an ip-address",
    usage = "[player/ip-address]"
)
@IocBean(conditionalOnProperty = "ban-module.enabled=true && ban-module.ipban.enabled=true")
@IocMultiProvider(SppCommand.class)
public class IpUnBanCmd extends AbstractCmd {

    private final IpBanService banService;
    private final PlayerManager playerManager;
    private final PlayerIpRepository playerIpRepository;
    private final IpBanCmdUtil ipBanCmdUtil;

    public IpUnBanCmd(Messages messages,
                      Options options,
                      IpBanService banService,
                      CommandService commandService,
                      PlayerManager playerManager,
                      PlayerIpRepository playerIpRepository,
                      IpBanCmdUtil ipBanCmdUtil,
                      PermissionHandler permissionHandler) {
        super(messages, permissionHandler, commandService);
        this.banService = banService;
        this.playerManager = playerManager;
        this.playerIpRepository = playerIpRepository;
        this.ipBanCmdUtil = ipBanCmdUtil;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        String ipArg = args[0];
        if (!isValidCidrOrIp(ipArg)) {
            SppPlayer sppPlayer = playerManager.getOnOrOfflinePlayer(ipArg).orElseThrow(() -> new BusinessException("No player found with or Ip address given is invalid"));
            ipArg = getIpAddress(sppPlayer);
        }
        String ipAddress = ipArg;

        List<IpBan> matchingIpBans = banService.findMatchingIpBans(ipAddress);
        if (sender instanceof Player) {
            ipBanCmdUtil.sendUnbanChoiceMessage((Player) sender, matchingIpBans, p -> banService.unbanIp(sender, ipAddress, optionalParameters.containsKey("-silent")));
        } else {
            banService.unbanIp(sender, ipAddress, optionalParameters.containsKey("-silent"));
        }

        return true;
    }

    @Override
    protected List<String> getOptionalParameters() {
        return Collections.singletonList("-silent");
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 1;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
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

    private String getIpAddress(SppPlayer sppPlayer) {
        return sppPlayer.isOnline() ? getIpFromPlayer(sppPlayer.getPlayer()) : playerIpRepository.getLastIp(sppPlayer.getId()).orElseThrow(() -> new BusinessException("This player's Ip address is unknown."));
    }

}
