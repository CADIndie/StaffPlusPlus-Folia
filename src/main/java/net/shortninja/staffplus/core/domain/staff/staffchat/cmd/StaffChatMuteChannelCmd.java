package net.shortninja.staffplus.core.domain.staff.staffchat.cmd;

import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.session.PlayerSession;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChannelConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public class StaffChatMuteChannelCmd extends AbstractCmd {
    private final SessionManagerImpl sessionManager;
    private final StaffChatChannelConfiguration channelConfiguration;

    public StaffChatMuteChannelCmd(Messages messages, SessionManagerImpl sessionManager, CommandService commandService, StaffChatChannelConfiguration channelConfiguration, PermissionHandler permissionHandler) {
        super(channelConfiguration.getCommand() + "-mute", messages, permissionHandler, commandService);
        this.sessionManager = sessionManager;
        this.channelConfiguration = channelConfiguration;
        setDescription("Mutes all staff chat for channel. You can still send messages to staff chat but you won't see anything.");
        if(channelConfiguration.getPermission().isPresent()) {
            setPermission(channelConfiguration.getPermission().get() + ".mute");
        }
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        PlayerSession session = sessionManager.get(((Player) sender).getUniqueId());

        if (session.isStaffChatMuted(channelConfiguration.getName())) {
            messages.send(sender, messages.staffChatUnmuted, channelConfiguration.getPrefix());
        } else {
            messages.send(sender, messages.staffChatMuted, channelConfiguration.getPrefix());
        }

        session.setStaffChatMuted(channelConfiguration.getName(), !session.isStaffChatMuted(channelConfiguration.getName()));
        return true;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return PlayerRetrievalStrategy.NONE;
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