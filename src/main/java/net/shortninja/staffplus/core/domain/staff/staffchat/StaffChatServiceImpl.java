package net.shortninja.staffplus.core.domain.staff.staffchat;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.player.settings.PlayerSettings;
import net.shortninja.staffplus.core.domain.player.settings.PlayerSettingsRepository;
import net.shortninja.staffplus.core.domain.staff.staffchat.bungee.StaffChatBungeeMessage;
import net.shortninja.staffplus.core.domain.staff.staffchat.bungee.StaffChatReceivedBungeeEvent;
import net.shortninja.staffplus.core.domain.staff.staffchat.config.StaffChatConfiguration;
import net.shortninja.staffplusplus.staffmode.chat.StaffChatEvent;
import net.shortninja.staffplusplus.staffmode.chat.StaffChatService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;

@IocBukkitListener
public class StaffChatServiceImpl implements StaffChatService, Listener {

    private static final String STAFFCHAT = "staffchat";
    private final Messages messages;
    private final Options options;
    private final PermissionHandler permissionHandler;
    private final StaffChatMessageFormatter staffChatMessageFormatter;
    private final StaffChatConfiguration staffChatConfiguration;
    private final PlayerSettingsRepository playerSettingsRepository;

    public StaffChatServiceImpl(Messages messages, Options options, PermissionHandler permissionHandler, StaffChatMessageFormatter staffChatMessageFormatter, StaffChatConfiguration staffChatConfiguration, PlayerSettingsRepository playerSettingsRepository) {
        this.messages = messages;
        this.options = options;
        this.permissionHandler = permissionHandler;
        this.staffChatMessageFormatter = staffChatMessageFormatter;
        this.staffChatConfiguration = staffChatConfiguration;
        this.playerSettingsRepository = playerSettingsRepository;
    }

    @EventHandler
    public void handleBungeeMessage(StaffChatReceivedBungeeEvent event) {
        StaffChatBungeeMessage staffChatMessage = event.getStaffChatMessage();
        StaffChatChannelConfiguration channel = getChannel(staffChatMessage.getChannel());

        String formattedMessage = staffChatMessageFormatter.formatMessage(staffChatMessage.getPlayerName(),
            channel,
            staffChatMessage.getMessage(),
            event.getStaffChatMessage().getServerName());

        sendMessageToStaff(channel, formattedMessage, null);
    }

    public void sendMessage(CommandSender sender, String channelName, String message) {
        StaffChatChannelConfiguration channel = getChannel(channelName);

        String formattedMessage = staffChatMessageFormatter.formatMessage(sender, channel, message, options.serverName);
        sendMessageToStaff(channel, formattedMessage, sender);

        if (sender instanceof Player) {
            sendEvent(new StaffChatEvent((Player) sender, options.serverName, message, channelName));
        }
    }

    private StaffChatChannelConfiguration getChannel(String channelName) {
        return staffChatConfiguration.getChannelConfigurations().stream()
            .filter(c -> c.getName().equalsIgnoreCase(channelName))
            .findFirst().orElseThrow(() -> new ConfigurationException("No channel with name [" + channelName + "] configured"));
    }

    public boolean hasHandle(String channelName, String message) {
        StaffChatChannelConfiguration channel = getChannel(channelName);
        return channel.getHandle().isPresent() && StringUtils.isNotEmpty(channel.getHandle().get()) && message.startsWith(channel.getHandle().get());
    }

    /**
     * * @deprecated Please use sendMessage(String channelName, String message)      
     */
    @Deprecated
    @Override
    public void sendMessage(String message) {
        StaffChatChannelConfiguration channel = getChannel(STAFFCHAT);
        sendMessageToStaff(channel, message, null);
    }

    @Override
    public void sendMessage(String channelName, String message) {
        StaffChatChannelConfiguration channel = getChannel(channelName);
        sendMessageToStaff(channel, message, null);
    }

    private void sendMessageToStaff(StaffChatChannelConfiguration channel, String formattedMessage, CommandSender sender) {
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> !playerSettingsRepository.get(p).isStaffChatMuted(channel.getName()))
            .filter(player -> permissionHandler.has(player, channel.getPermission().orElse(null)))
            .forEach(player -> {
                messages.send(player, formattedMessage, channel.getPrefix());
                sendNotificationSound(channel, sender, player);
            });
        messages.send(Bukkit.getConsoleSender(), formattedMessage, channel.getPrefix());
    }

    private void sendNotificationSound(StaffChatChannelConfiguration channel, CommandSender sender, Player player) {
        PlayerSettings playerSettings = playerSettingsRepository.get(player);
        if (sender instanceof Player && !((Player) sender).getUniqueId().equals(player.getUniqueId()) && playerSettings.isStaffChatSoundEnabled(channel.getName())) {
            channel.getNotificationSound().ifPresent(s -> s.play(player));
        }
    }
}
