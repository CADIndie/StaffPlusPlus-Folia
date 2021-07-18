package net.shortninja.staffplus.core.domain.staff.altaccountdetect;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.altaccountdetect.config.AltDetectConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@IocBean
public class AltDetectionListener implements Listener {

    private final AltDetectConfiguration altDetectConfiguration;
    private final AltDetectionService altDetectionService;
    private final PlayerManager playerManager;

    public AltDetectionListener(AltDetectConfiguration altDetectConfiguration, AltDetectionService altDetectionService, PlayerManager playerManager) {
        this.altDetectionService = altDetectionService;
        this.altDetectConfiguration = altDetectConfiguration;
        this.playerManager = playerManager;
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!altDetectConfiguration.enabled) {
            return;
        }

        playerManager.getOnlinePlayer(event.getPlayer().getUniqueId()).ifPresent(onlinePlayer -> {
            Bukkit.getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
                altDetectionService.detectAltAccount(onlinePlayer);
            });
        });
    }
}
