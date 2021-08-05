package net.shortninja.staffplus.core.domain.player.listeners;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.application.session.PlayerSession;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.staff.mode.StaffModeService;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceService;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import static net.shortninja.staffplus.core.domain.staff.tracing.TraceType.WORLD_CHANGE;

@IocBean
@IocListener
public class PlayerWorldChange implements Listener {
    private final StaffModeService staffModeService;
    private final TraceService traceService;
    private final SessionManagerImpl sessionManager;
    private final BukkitUtils bukkitUtils;

    public PlayerWorldChange(StaffModeService staffModeService, TraceService traceService, SessionManagerImpl sessionManager, BukkitUtils bukkitUtils) {
        this.staffModeService = staffModeService;
        this.traceService = traceService;
        this.sessionManager = sessionManager;
        this.bukkitUtils = bukkitUtils;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        PlayerSession session = sessionManager.get(event.getPlayer().getUniqueId());
        World currentWorld = event.getPlayer().getWorld();

        bukkitUtils.runTaskAsync(event.getPlayer(), () -> {
            if (session.isInStaffMode() && (session.getModeConfiguration().get().isDisableOnWorldChange() || !session.getModeConfiguration().get().isModeValidInWorld(currentWorld))) {
                staffModeService.turnStaffModeOff(event.getPlayer());
            }
        });

        traceService.sendTraceMessage(WORLD_CHANGE, event.getPlayer().getUniqueId(), String.format("World changed from [%s] to [%s]", event.getFrom().getName(), currentWorld.getName()));
    }
}
