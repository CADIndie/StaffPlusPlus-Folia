package net.shortninja.staffplus.core.common.gui;

import net.shortninja.staffplus.core.StaffPlusPlus;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.common.Items;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractGui implements IGui {
    protected final Messages messages = StaffPlusPlus.get().getIocContainer().get(Messages.class);
    protected final OnlineSessionsManager sessionManager = StaffPlusPlus.get().getIocContainer().get(OnlineSessionsManager.class);
    protected final Options options = StaffPlusPlus.get().getIocContainer().get(Options.class);

    private final String title;
    protected Supplier<AbstractGui> previousGuiSupplier;
    private final Inventory inventory;
    private final Map<Integer, IAction> actions = new HashMap<>();

    public AbstractGui(String title, InventoryType inventoryType) {
        this.title = title;
        inventory = Bukkit.createInventory(null, inventoryType);
    }

    public AbstractGui(int size, String title) {
        this.title = title;
        inventory = Bukkit.createInventory(null, size, messages.colorize(title));
    }

    public AbstractGui(int size, String title, Supplier<AbstractGui> previousGuiSupplier) {
        this.title = title;
        inventory = Bukkit.createInventory(null, size, messages.colorize(title));
        this.previousGuiSupplier = previousGuiSupplier;
    }

    public abstract void buildGui();

    public void show(Player player) {
        buildGui();
        if (previousGuiSupplier != null) {
            ItemStack item = Items.editor(Items.createDoor("Back", "Go back"))
                .setAmount(1)
                .build();
            setItem(getBackButtonSlot(), item, new IAction() {
                @Override
                public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                    previousGuiSupplier.get().show(player);
                }

                @Override
                public boolean shouldClose(Player player) {
                    return false;
                }
            });
        }

        player.closeInventory();
        player.openInventory(getInventory());
        sessionManager.get(player).setCurrentGui(this);
    }

    public String getTitle() {
        return title;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public IAction getAction(int slot) {
        return actions.get(slot);
    }

    public void setItem(int slot, ItemStack item, IAction action) {
        inventory.setItem(slot, item);

        if (action != null) {
            actions.put(slot, action);
        }
    }

    protected int getBackButtonSlot() {
        return 49;
    }
}