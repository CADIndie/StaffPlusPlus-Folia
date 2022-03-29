package net.shortninja.staffplus.core.domain.staff.staffchat.bungee;

import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.common.Constants;
import net.shortninja.staffplus.core.common.bungee.BungeeClient;
import net.shortninja.staffplusplus.staffmode.chat.StaffChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@IocListener(conditionalOnProperty = "staff-chat-module.bungee=true")
public class StaffChatBungeeSender implements Listener {

    private final BungeeClient bungeeClient;

    public StaffChatBungeeSender(BungeeClient bungeeClient) {
        this.bungeeClient = bungeeClient;
    }

    @EventHandler
    public void onChat(StaffChatEvent staffChatEvent) {
        if(Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        Player player = Bukkit.getOnlinePlayers().iterator().next();
        bungeeClient.sendMessage(player, Constants.BUNGEE_STAFFCHAT_CHANNEL, new StaffChatBungeeMessage(staffChatEvent.getServerName(), staffChatEvent.getChannel(), staffChatEvent.getMessage(), staffChatEvent.getPlayer().getName()));
    }

}
