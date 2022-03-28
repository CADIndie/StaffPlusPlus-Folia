package net.shortninja.staffplus.core.domain.staff.investigate.gui.views;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMulti;
import be.garagepoort.mcioc.gui.GuiActionBuilder;
import be.garagepoort.mcioc.gui.model.TubingGui;
import be.garagepoort.mcioc.gui.model.TubingGuiActions;
import net.shortninja.staffplus.core.common.gui.PagedGuiBuilder;
import net.shortninja.staffplus.core.domain.staff.investigate.EvidenceEntity;
import net.shortninja.staffplus.core.domain.staff.investigate.Investigation;
import net.shortninja.staffplus.core.domain.staff.investigate.InvestigationEvidenceService;
import net.shortninja.staffplusplus.investigate.evidence.EvidenceGuiClick;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@IocBean
public class EvidenceOverviewViewBuilder {

    private static final int PAGE_SIZE = 45;
    private final InvestigationEvidenceService investigationEvidenceService;
    private final InvestigationEvidenceItemBuilder investigationEvidenceItemBuilder;
    private final List<EvidenceGuiClick> evidenceGuiClicks;

    public EvidenceOverviewViewBuilder(InvestigationEvidenceService investigationEvidenceService, InvestigationEvidenceItemBuilder investigationEvidenceItemBuilder, @IocMulti(EvidenceGuiClick.class) List<EvidenceGuiClick> evidenceGuiClicks) {
        this.investigationEvidenceService = investigationEvidenceService;
        this.investigationEvidenceItemBuilder = investigationEvidenceItemBuilder;
        this.evidenceGuiClicks = evidenceGuiClicks;
    }

    public TubingGui buildGui(Player player, Investigation investigation, int page, String currentAction) {
        return new PagedGuiBuilder.Builder("Evidence")
            .addPagedItems(currentAction,
                getItems(investigation, page * PAGE_SIZE, PAGE_SIZE),
                investigationEvidenceItemBuilder::build,
                getLeftAction(player),
                getDeleteAction(),
                page)
            .backAction()
            .build();
    }

    @NotNull
    private Function<EvidenceEntity, String> getLeftAction(Player player) {
        return evidence -> evidenceGuiClicks.stream()
            .filter(e -> e.getType().equals(evidence.getEvidenceType()))
            .findFirst()
            .map(e -> e.getAction(player, evidence.getEvidenceId(), "$$back"))
            .orElse(TubingGuiActions.NOOP);
    }

    @NotNull
    private Function<EvidenceEntity, String> getDeleteAction() {
        return evidence -> GuiActionBuilder.builder()
            .action("manage-investigation-evidence/view/unlink")
            .param("evidenceId", String.valueOf(evidence.getId()))
            .param("investigationId", String.valueOf(evidence.getInvestigationId()))
            .build();
    }

    public List<EvidenceEntity> getItems(Investigation investigation, int offset, int amount) {
        return investigationEvidenceService.getEvidenceForInvestigation(investigation, offset, amount);
    }
}