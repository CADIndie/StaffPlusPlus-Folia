package net.shortninja.staffplus.core.domain.player.listeners;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.domain.staff.alerts.xray.XrayService;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceService;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static net.shortninja.staffplus.core.domain.staff.tracing.TraceType.BLOCK_BREAK;

@IocBukkitListener
public class BlockBreak implements Listener {
    private final XrayService xrayService;
    private final TraceService traceService;
    private final OnlineSessionsManager sessionManager;

    public BlockBreak(XrayService xrayService, TraceService traceService, OnlineSessionsManager sessionManager) {
        this.xrayService = xrayService;
        this.traceService = traceService;
        this.sessionManager = sessionManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        OnlinePlayerSession session = sessionManager.get(player);

        if (session.isFrozen()) {
            event.setCancelled(true);
            return;
        }

        if (!session.isInStaffMode() || session.getModeConfig().get().isModeBlockManipulation()) {
            Block block = event.getBlock();
            xrayService.handleBlockBreak(block, player);
            traceService.sendTraceMessage(BLOCK_BREAK, event.getPlayer().getUniqueId(),
                String.format("Block [%s] broken at [%s,%s,%s]", block.getType(), block.getX(), block.getY(), block.getZ()));
            return;
        }

        event.setCancelled(true);
    }
}