package net.shortninja.staffplus.core.domain.staff.protect.gui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.gui.AsyncGui;
import be.garagepoort.mcioc.gui.CurrentAction;
import be.garagepoort.mcioc.gui.GuiAction;
import be.garagepoort.mcioc.gui.GuiController;
import be.garagepoort.mcioc.gui.GuiParam;
import be.garagepoort.mcioc.gui.TubingGui;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.staff.protect.ProtectService;
import net.shortninja.staffplus.core.domain.staff.protect.ProtectedArea;
import net.shortninja.staffplus.core.domain.staff.protect.gui.views.ManageProtectedAreaViewBuilder;
import net.shortninja.staffplus.core.domain.staff.protect.gui.views.ProtectedAreasViewBuilder;
import net.shortninja.staffplus.core.domain.staff.teleport.TeleportService;
import org.bukkit.entity.Player;

import static be.garagepoort.mcioc.gui.AsyncGui.async;

@IocBean
@GuiController
public class ProtectedAreasGuiController {

    private final ProtectedAreasViewBuilder protectedAreasViewBuilder;
    private final ProtectService protectService;
    private final ManageProtectedAreaViewBuilder manageProtectedAreaViewBuilder;
    private final TeleportService teleportService;
    private final BukkitUtils bukkitUtils;

    public ProtectedAreasGuiController(ProtectedAreasViewBuilder protectedAreasViewBuilder, ProtectService protectService, ManageProtectedAreaViewBuilder manageProtectedAreaViewBuilder, TeleportService teleportService, BukkitUtils bukkitUtils) {
        this.protectedAreasViewBuilder = protectedAreasViewBuilder;
        this.protectService = protectService;
        this.manageProtectedAreaViewBuilder = manageProtectedAreaViewBuilder;
        this.teleportService = teleportService;
        this.bukkitUtils = bukkitUtils;
    }


    @GuiAction("protected-areas/view")
    public AsyncGui<TubingGui> getOverview(@GuiParam(value = "page", defaultValue = "0") int page,
                                           @CurrentAction String currentAction,
                                           @GuiParam("backAction") String backAction) {
        return async(() -> protectedAreasViewBuilder.buildGui(page, currentAction, backAction));
    }

    @GuiAction("protected-areas/view/detail")
    public AsyncGui<TubingGui> getAreaDetail(@GuiParam("areaId") int areaId, @GuiParam("backAction") String backAction) {
        return async(() -> {
            ProtectedArea protectedArea = protectService.getById(areaId);
            return manageProtectedAreaViewBuilder.buildGui(protectedArea, backAction);
        });
    }

    @GuiAction("protected-areas/delete")
    public void delete(Player player, @GuiParam("areaId") int areaId) {
        bukkitUtils.runTaskAsync(player, () -> protectService.deleteProtectedArea(player, areaId));
    }

    @GuiAction("protected-areas/teleport")
    public void teleport(Player player, @GuiParam("areaId") int areaId) {
        ProtectedArea protectedArea = protectService.getById(areaId);
        teleportService.teleportSelf(player, protectedArea.getCornerPoint1());
    }

}