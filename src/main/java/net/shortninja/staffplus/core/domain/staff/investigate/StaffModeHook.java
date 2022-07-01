package net.shortninja.staffplus.core.domain.staff.investigate;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.mode.StaffModeService;
import net.shortninja.staffplusplus.investigate.InvestigationStartedEvent;
import net.shortninja.staffplusplus.staffmode.ExitStaffModeEvent;
import net.shortninja.staffplusplus.staffmode.SwitchStaffModeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

@IocBukkitListener
public class StaffModeHook implements Listener {

    private final StaffModeService staffModeService;
    private final PlayerManager playerManager;
    private final Options options;
    private final InvestigationService investigationService;
    private final BukkitUtils bukkitUtils;

    public StaffModeHook(StaffModeService staffModeService, PlayerManager playerManager, Options options, InvestigationService investigationService, BukkitUtils bukkitUtils) {
        this.staffModeService = staffModeService;
        this.playerManager = playerManager;
        this.options = options;
        this.investigationService = investigationService;
        this.bukkitUtils = bukkitUtils;
    }

    @EventHandler
    public void handleInvestigationStarted(InvestigationStartedEvent investigationStartedEvent) {
        if (options.investigationConfiguration.isEnforceStaffMode()) {
            playerManager.getOnlinePlayer(investigationStartedEvent.getInvestigation().getInvestigatorUuid())
                .ifPresent(p -> {
                    bukkitUtils.runTaskAsync(() -> {
                        if (options.investigationConfiguration.getStaffMode().isPresent()) {
                            staffModeService.turnStaffModeOn(p.getPlayer(), options.investigationConfiguration.getStaffMode().get());
                        } else {
                            staffModeService.turnStaffModeOn(p.getPlayer());
                        }
                    });
                });
        }
    }

    @EventHandler
    public void handleExitStaffMode(ExitStaffModeEvent exitStaffModeEvent) {
        if (options.investigationConfiguration.isEnforceStaffMode()) {
            playerManager.getOnlinePlayer(exitStaffModeEvent.getPlayerUuid()).ifPresent(p -> investigationService.tryPausingInvestigation(p.getPlayer()));
        }
    }

    @EventHandler
    public void handleExitStaffMode(SwitchStaffModeEvent switchStaffModeEvent) {
        Optional<String> staffModeConfig = options.investigationConfiguration.getStaffMode();
        if (options.investigationConfiguration.isEnforceStaffMode() && staffModeConfig.isPresent() && !staffModeConfig.get().equalsIgnoreCase(switchStaffModeEvent.getToMode())) {
            playerManager.getOnlinePlayer(switchStaffModeEvent.getPlayerUuid()).ifPresent(p -> investigationService.tryPausingInvestigation(p.getPlayer()));
        }
    }
}
