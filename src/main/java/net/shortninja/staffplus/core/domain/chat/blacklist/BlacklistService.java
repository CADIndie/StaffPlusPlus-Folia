package net.shortninja.staffplus.core.domain.chat.blacklist;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMulti;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.chat.blacklist.censors.ChatCensor;
import net.shortninja.staffplus.core.domain.chat.configuration.ChatConfiguration;
import net.shortninja.staffplusplus.chat.ChatMessageCensoredEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;

@IocBean
public class BlacklistService {

    @ConfigProperty("permissions:blacklist")
    private String permissionBlacklist;

    private final IProtocolService protocolService;
    private final Options options;
    private final PermissionHandler permission;
    private final Messages messages;
    private final List<ChatCensor> chatCensors;
    private final ChatConfiguration chatConfiguration;

    public BlacklistService(IProtocolService protocolService, Options options, PermissionHandler permission, Messages messages, @IocMulti(ChatCensor.class) List<ChatCensor> chatCensors, ChatConfiguration chatConfiguration) {
        this.protocolService = protocolService;
        this.options = options;
        this.permission = permission;
        this.messages = messages;
        this.chatCensors = chatCensors;
        this.chatConfiguration = chatConfiguration;
    }

    public void censorMessage(Player player, AsyncPlayerChatEvent event) {
        if (permission.has(player, permissionBlacklist)) {
            return;
        }

        if (options.blackListConfiguration.isEnabled() && chatConfiguration.chatEnabled) {
            String originalMessage = event.getMessage();
            String censoredMessage = originalMessage;
            for (ChatCensor chatCensor : chatCensors) {
                censoredMessage = chatCensor.censor(censoredMessage);
            }
            event.setMessage(censoredMessage);
            setHoverableMessage(player, event, originalMessage, censoredMessage);
            if(!originalMessage.equals(censoredMessage)) {
                sendEvent(new ChatMessageCensoredEvent(options.serverName, player, censoredMessage, originalMessage));
            }
        }
    }

    private void setHoverableMessage(Player player, AsyncPlayerChatEvent event, String originalMessage, String censoredMessage) {
        if (options.blackListConfiguration.isHoverable()) {
            List<? extends Player> validPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> permission.has(p, permissionBlacklist))
                .collect(Collectors.toList());

            event.getRecipients().removeAll(validPlayers);
            Set<Player> staffPlayers = new HashSet<>(validPlayers);

            protocolService.getVersionProtocol().sendHoverableJsonMessage(staffPlayers, messages.blacklistChatFormat.replace("%player%", player.getName()).replace("%message%", censoredMessage), originalMessage);
        }
    }
}