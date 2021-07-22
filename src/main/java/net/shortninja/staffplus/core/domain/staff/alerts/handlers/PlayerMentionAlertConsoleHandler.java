package net.shortninja.staffplus.core.domain.staff.alerts.handlers;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.alerts.config.AlertsConfiguration;
import net.shortninja.staffplusplus.chat.PlayerMentionedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@IocBean(conditionalOnProperty = "alerts-module.mention-notify-console=true")
@IocListener
public class PlayerMentionAlertConsoleHandler implements Listener {

    private final PermissionHandler permission;
    private final AlertsConfiguration alertsConfiguration;

    public PlayerMentionAlertConsoleHandler(PermissionHandler permission, AlertsConfiguration alertsConfiguration) {
        this.permission = permission;
        this.alertsConfiguration = alertsConfiguration;
    }

    @EventHandler
    public void handle(PlayerMentionedEvent event) {
        if (permission.has(event.getPlayer(), alertsConfiguration.permissionMentionBypass)) {
            return;
        }

        String message = "&7%target% &bhas mentioned %mentioned% in chat!".replace("%target%", event.getPlayer().getName()).replace("%mentioned%", event.getMentionedPlayer().getName());
        StaffPlus.get().getLogger().info(message);
    }

}
