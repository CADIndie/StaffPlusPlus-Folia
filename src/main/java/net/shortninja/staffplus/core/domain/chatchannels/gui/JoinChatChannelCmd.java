package net.shortninja.staffplus.core.domain.chatchannels.gui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.domain.chatchannels.ChatChannelService;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplusplus.chatchannels.ChatChannelType;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.NONE;

@Command(
    command = "commands:chatchannels.join",
    description = "Join an existing chat channel",
    usage = "[channelName]",
    playerRetrievalStrategy = NONE
)
@IocBean
@IocMultiProvider(SppCommand.class)
public class JoinChatChannelCmd extends AbstractCmd {

    @ConfigProperty("permissions:chatchannels.join")
    private String joinPermission;

    private final ChatChannelService chatChannelService;
    private final BukkitUtils bukkitUtils;
    private final PlayerManager playerManager;

    public JoinChatChannelCmd(PermissionHandler permissionHandler,
                              Messages messages,
                              CommandService commandService,
                              ChatChannelService chatChannelService,
                              BukkitUtils bukkitUtils,
                              PlayerManager playerManager) {
        super(messages, permissionHandler, commandService);
        this.chatChannelService = chatChannelService;
        this.bukkitUtils = bukkitUtils;
        this.playerManager = playerManager;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        String[] split = args[0].split("_");
        
        if (split.length < 2) {
            throw new BusinessException("&CChat channel not found");
        }
        
        ChatChannelType chatChannelType;
        try {
            chatChannelType = ChatChannelType.valueOf(split[0]);
        } catch (IllegalArgumentException ignored) {
            throw new BusinessException("&CChat channel not found");
        }
        String chatChannelId = split[1];

        permissionHandler.validate(sender, joinPermission + "." + chatChannelType.name().toLowerCase());
        validateIsPlayer(sender);

        Optional<SppPlayer> onlinePlayer = playerManager.getOnlinePlayer(sender.getName());
        bukkitUtils.runTaskAsync(sender, () -> chatChannelService.joinChannel(onlinePlayer.get(), chatChannelId, chatChannelType));
        return true;
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
            return chatChannelService.getAllChannelNames().stream()
                .filter(s -> currentArg.isEmpty() || s.contains(currentArg))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
