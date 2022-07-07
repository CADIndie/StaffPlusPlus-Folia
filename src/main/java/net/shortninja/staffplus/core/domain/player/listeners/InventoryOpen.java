package net.shortninja.staffplus.core.domain.player.listeners;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.StaffPlusPlus;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceService;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@IocBean
public class InventoryOpen implements Listener {

    private final TraceService traceService;

    public InventoryOpen(TraceService traceService) {
        this.traceService = traceService;
        Bukkit.getServer().getPluginManager().registerEvents(this, StaffPlusPlus.get());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        Inventory inventory = event.getInventory();
        String holder = inventory.getHolder() != null ? inventory.getHolder().getClass().getSimpleName() : "No holder";

        Location location = inventory.getLocation() == null ? player.getLocation() : inventory.getLocation();
        if (location == null) {
            traceService.sendTraceMessage(TraceType.INVENTORY, uniqueId, String.format("Opened inventory with holder [%s] no location found", holder));
        } else {
            traceService.sendTraceMessage(TraceType.INVENTORY, uniqueId,
                String.format("Opened inventory with holder [%s] at location [%s,%s,%s] with content %s",
                    holder,
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    getInventoryContent(inventory))
            );
        }
    }

    private String getInventoryContent(Inventory inventory) {
        return Arrays.stream(inventory.getContents())
            .filter(Objects::nonNull)
            .map(i -> i.getAmount() + " " + i.getType())
            .collect(Collectors.joining(" - "));
    }
}
