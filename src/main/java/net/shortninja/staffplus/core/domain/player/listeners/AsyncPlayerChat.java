package net.shortninja.staffplus.core.domain.player.listeners;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMulti;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.chat.ChatInterceptor;
import net.shortninja.staffplus.core.domain.chat.blacklist.BlacklistService;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceService;
import net.shortninja.staffplusplus.chat.PlayerMentionedEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.domain.staff.tracing.TraceType.CHAT;

@IocBean
public class AsyncPlayerChat implements Listener {
    private final Options options;
    private final List<ChatInterceptor> chatInterceptors;
    private final BlacklistService blacklistService;
    private final TraceService traceService;
    private final PlayerManager playerManager;
    private final BukkitUtils bukkitUtils;

    public AsyncPlayerChat(Options options, @IocMulti(ChatInterceptor.class) List<ChatInterceptor> chatInterceptors, BlacklistService blacklistService, TraceService traceService, PlayerManager playerManager, BukkitUtils bukkitUtils) {
        this.options = options;
        this.chatInterceptors = chatInterceptors.stream().sorted(Comparator.comparingInt(ChatInterceptor::getPriority)).collect(Collectors.toList());
        this.blacklistService = blacklistService;
        this.traceService = traceService;
        this.playerManager = playerManager;
        this.bukkitUtils = bukkitUtils;
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        for (ChatInterceptor chatInterceptor : chatInterceptors) {
            boolean cancel = chatInterceptor.intercept(event);
            if (cancel) {
                event.setCancelled(true);
                return;
            }
        }

        traceService.sendTraceMessage(CHAT, player.getUniqueId(), event.getMessage());
        notifyMentioned(player, message);
        blacklistService.censorMessage(player, event);
    }

    private void notifyMentioned(Player player, String message) {
        bukkitUtils.runTaskAsync(() -> {
            getMentioned(message).stream()
                .map(user -> new PlayerMentionedEvent(options.serverName, player, user, message))
                .forEach(BukkitUtils::sendEvent);
        });
    }

    private List<OfflinePlayer> getMentioned(String message) {
        return playerManager.getOnAndOfflinePlayers().stream()
            .filter(offlinePlayer -> message.toLowerCase().contains(offlinePlayer.getUsername().toLowerCase()))
            .map(p -> Bukkit.getOfflinePlayer(p.getId()))
            .collect(Collectors.toList());
    }
}