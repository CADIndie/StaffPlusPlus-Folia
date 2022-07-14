package net.shortninja.staffplus.core.application.session.synchronizers;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.mode.ModeProvider;
import net.shortninja.staffplusplus.staffmode.EnterStaffModeEvent;
import net.shortninja.staffplusplus.staffmode.ExitStaffModeEvent;
import net.shortninja.staffplusplus.staffmode.SwitchStaffModeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@IocBukkitListener
public class StaffModeSessionSynchronizer implements Listener {

    private final OnlineSessionsManager onlineSessionsManager;
    private final PlayerManager playerManager;
    private final ModeProvider modeProvider;

    public StaffModeSessionSynchronizer(OnlineSessionsManager onlineSessionsManager, PlayerManager playerManager, ModeProvider modeProvider) {
        this.onlineSessionsManager = onlineSessionsManager;
        this.playerManager = playerManager;
        this.modeProvider = modeProvider;
    }

    @EventHandler
    public void onModeEnter(EnterStaffModeEvent event) {
        playerManager.getOnlinePlayer(event.getPlayerUuid())
            .ifPresent(p -> {
                OnlinePlayerSession onlinePlayerSession = onlineSessionsManager.get(p.getPlayer());
                onlinePlayerSession.setInStaffMode(true);
                onlinePlayerSession.setModeConfig(modeProvider.getConfiguration(event.getMode()).orElse(null));
            });
    }

    @EventHandler
    public void onModeSwitch(SwitchStaffModeEvent event) {
        playerManager.getOnlinePlayer(event.getPlayerUuid())
            .ifPresent(p -> {
                OnlinePlayerSession onlinePlayerSession = onlineSessionsManager.get(p.getPlayer());
                onlinePlayerSession.setInStaffMode(true);
                onlinePlayerSession.setModeConfig(modeProvider.getConfiguration(event.getToMode()).orElse(null));
            });
    }

    @EventHandler
    public void onModeExit(ExitStaffModeEvent event) {
        playerManager.getOnlinePlayer(event.getPlayerUuid())
            .ifPresent(p -> {
                OnlinePlayerSession onlinePlayerSession = onlineSessionsManager.get(p.getPlayer());
                onlinePlayerSession.setInStaffMode(false);
                onlinePlayerSession.setModeConfig(null);
            });
    }
}
