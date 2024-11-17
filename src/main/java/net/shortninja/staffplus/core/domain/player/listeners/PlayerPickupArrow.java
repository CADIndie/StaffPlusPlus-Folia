package net.shortninja.staffplus.core.domain.player.listeners;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

import static net.shortninja.staffplus.core.domain.staff.tracing.TraceType.PICKUP_ITEM;

@IocBukkitListener
public class PlayerPickupArrow implements Listener {
    private final TraceService traceService;
    private final OnlineSessionsManager sessionManager;

    public PlayerPickupArrow(TraceService traceService, OnlineSessionsManager sessionManager) {
        this.traceService = traceService;
        this.sessionManager = sessionManager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        
        OnlinePlayerSession session = sessionManager.get(player);
        if (!session.isInStaffMode() || session.getModeConfig().get().isModeItemPickup()) {
                traceService.sendTraceMessage(PICKUP_ITEM, player.getUniqueId(), "Picked up arrow");
                return;
        }
        event.setCancelled(true);
    }
}