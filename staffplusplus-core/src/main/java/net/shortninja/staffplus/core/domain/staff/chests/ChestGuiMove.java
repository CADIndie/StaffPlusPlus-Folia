package net.shortninja.staffplus.core.domain.staff.chests;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

@IocBukkitListener
public class ChestGuiMove implements Listener {
    private final OnlineSessionsManager sessionManager;

    public ChestGuiMove(OnlineSessionsManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        OnlinePlayerSession playerSession = sessionManager.get(player);
        if (!playerSession.getCurrentGui().isPresent() || !(playerSession.getCurrentGui().get() instanceof ChestGUI)) {
            return;
        }

        ChestGUI chestGUI = (ChestGUI) playerSession.getCurrentGui().get();
        if (!chestGUI.isInteractionEnabled()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() != ClickType.LEFT) {
            event.setCancelled(true);
            return;
        }

        if (!isEmptyStack(event.getCursor()) && !isEmptyStack(event.getCurrentItem())) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(chestGUI.getInventory())) {
            handleChestInventoryClick(event, chestGUI);
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory())) {
            handleStaffInventoryClick(event, chestGUI);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        OnlinePlayerSession playerSession = sessionManager.get(player);
        if (!playerSession.getCurrentGui().isPresent() || !(playerSession.getCurrentGui().get() instanceof ChestGUI)) {
            return;
        }

        ChestGUI chestGUI = (ChestGUI) playerSession.getCurrentGui().get();
        if (!chestGUI.isInteractionEnabled()) {
            return;
        }

        if (!isEmptyStack(player.getItemOnCursor())) {
            if ("player".equalsIgnoreCase(chestGUI.getItemSelectedFrom())) {
                player.setItemOnCursor(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void dragItem(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        OnlinePlayerSession playerSession = sessionManager.get(player);
        if (!playerSession.getCurrentGui().isPresent() || !(playerSession.getCurrentGui().get() instanceof ChestGUI)) {
            return;
        }
        event.setCancelled(true);
    }

    private void handleChestInventoryClick(InventoryClickEvent event, ChestGUI chestGUI) {
        if (!isEmptyStack(event.getCursor()) && isEmptyStack(event.getCurrentItem()) && "staff".equalsIgnoreCase(chestGUI.getItemSelectedFrom())) {
            chestGUI.getTargetInventory().setItem(event.getSlot(), event.getCursor());
        }

        chestGUI.setItemSelectedFrom(null);

        if (isEmptyStack(event.getCursor()) && !isEmptyStack(event.getCurrentItem())) {
            chestGUI.setItemSelectedFrom("player");
            chestGUI.setItemSelectedSlot(event.getSlot());
        }
    }

    private void handleStaffInventoryClick(InventoryClickEvent event, ChestGUI chestGUI) {
        if (!isEmptyStack(event.getCursor()) && isEmptyStack(event.getCurrentItem()) && "player".equalsIgnoreCase(chestGUI.getItemSelectedFrom())) {
            chestGUI.getTargetInventory().setItem(chestGUI.getItemSelectedSlot(), null);
        }

        chestGUI.setItemSelectedFrom(null);

        if (isEmptyStack(event.getCursor()) && !isEmptyStack(event.getCurrentItem())) {
            chestGUI.setItemSelectedFrom("staff");
        }
    }

    private boolean isEmptyStack(ItemStack cursor) {
        return cursor == null || cursor.getType() == Material.AIR;
    }
}