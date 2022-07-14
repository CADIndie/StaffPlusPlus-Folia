package net.shortninja.staffplus.core.domain.staff.infractions.gui;

import be.garagepoort.mcioc.tubinggui.AsyncGui;
import be.garagepoort.mcioc.tubinggui.CurrentAction;
import be.garagepoort.mcioc.tubinggui.GuiAction;
import be.garagepoort.mcioc.tubinggui.GuiController;
import be.garagepoort.mcioc.tubinggui.GuiParam;
import be.garagepoort.mcioc.tubinggui.model.TubingGui;
import net.shortninja.staffplus.core.common.exceptions.PlayerNotFoundException;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionType;
import net.shortninja.staffplus.core.domain.staff.infractions.gui.views.InfractionsOverviewViewBuilder;
import net.shortninja.staffplus.core.domain.staff.infractions.gui.views.InfractionsTopViewBuilder;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.List;

import static be.garagepoort.mcioc.tubinggui.AsyncGui.async;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@GuiController
public class InfractionsGuiController {

    private final InfractionsOverviewViewBuilder infractionsOverviewViewBuilder;
    private final InfractionsTopViewBuilder infractionsTopViewBuilder;
    private final PlayerManager playerManager;

    public InfractionsGuiController(InfractionsOverviewViewBuilder infractionsOverviewViewBuilder, InfractionsTopViewBuilder infractionsTopViewBuilder, PlayerManager playerManager) {
        this.infractionsOverviewViewBuilder = infractionsOverviewViewBuilder;
        this.infractionsTopViewBuilder = infractionsTopViewBuilder;
        this.playerManager = playerManager;
    }

    @GuiAction("manage-infractions/view/overview")
    public AsyncGui<TubingGui> getOverview(Player player,
                                           @GuiParam("targetPlayerName") String targetPlayerName,
                                           @GuiParam(value = "page", defaultValue = "0") int page,
                                           @CurrentAction String currentAction) {
        SppPlayer sppPlayer = playerManager.getOnOrOfflinePlayer(targetPlayerName).orElseThrow(() -> new PlayerNotFoundException(targetPlayerName));
        return async(() -> infractionsOverviewViewBuilder.buildGui(player, sppPlayer, page, currentAction));
    }

    @GuiAction("manage-infractions/view/top")
    public AsyncGui<TubingGui> getTop(@GuiParam("infractionType") String type,
                                      @GuiParam(value = "page", defaultValue = "0") int page,
                                      @CurrentAction String currentAction) {
        return async(() -> {
            List<InfractionType> infractionTypes = StringUtils.isBlank(type) ? emptyList() : singletonList(InfractionType.valueOf(type));
            return infractionsTopViewBuilder.buildGui(page, infractionTypes, currentAction);
        });
    }
}
