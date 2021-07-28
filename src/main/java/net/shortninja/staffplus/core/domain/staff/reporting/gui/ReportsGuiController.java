package net.shortninja.staffplus.core.domain.staff.reporting.gui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.gui.CurrentAction;
import be.garagepoort.mcioc.gui.GuiAction;
import be.garagepoort.mcioc.gui.GuiActionBuilder;
import be.garagepoort.mcioc.gui.GuiController;
import be.garagepoort.mcioc.gui.GuiParam;
import be.garagepoort.mcioc.gui.GuiParams;
import be.garagepoort.mcioc.gui.TubingGui;
import be.garagepoort.mcioc.gui.TubingGuiActions;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.session.PlayerSession;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.gui.GuiItemConfig;
import net.shortninja.staffplus.core.common.gui.PagedGuiBuilder;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.reporting.CloseReportRequest;
import net.shortninja.staffplus.core.domain.staff.reporting.ManageReportService;
import net.shortninja.staffplus.core.domain.staff.reporting.Report;
import net.shortninja.staffplus.core.domain.staff.reporting.ReportService;
import net.shortninja.staffplus.core.domain.staff.reporting.cmd.ReportFiltersMapper;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ManageReportConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.views.FindReportsViewBuilder;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.views.ManageReportsViewBuilder;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.views.ReportDetailViewBuilder;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.views.ReportItemBuilder;
import net.shortninja.staffplusplus.reports.ReportFilters;
import net.shortninja.staffplusplus.reports.ReportStatus;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@IocBean
@GuiController
public class ReportsGuiController {

    private static final int PAGE_SIZE = 45;
    private static final String CANCEL = "cancel";

    private final PermissionHandler permissionHandler;
    private final ManageReportConfiguration manageReportConfiguration;
    private final ReportItemBuilder reportItemBuilder;
    private final ReportService reportService;
    private final Options options;
    private final Messages messages;
    private final ManageReportService manageReportService;
    private final ReportDetailViewBuilder reportDetailViewBuilder;
    private final ManageReportsViewBuilder manageReportsViewBuilder;
    private final FindReportsViewBuilder findReportsViewBuilder;
    private final SessionManagerImpl sessionManager;
    private final ReportFiltersMapper reportFiltersMapper;

    private final GuiItemConfig closedReportsGui;
    private final GuiItemConfig myAssignedReportsGui;
    private final GuiItemConfig assignedReportsGui;
    private final GuiItemConfig openReportsGui;

    public ReportsGuiController(PermissionHandler permissionHandler,
                                ManageReportConfiguration manageReportConfiguration,
                                ReportItemBuilder reportItemBuilder,
                                ReportService reportService,
                                Options options,
                                Messages messages,
                                ManageReportService manageReportService,
                                ReportDetailViewBuilder reportDetailViewBuilder,
                                ManageReportsViewBuilder manageReportsViewBuilder,
                                FindReportsViewBuilder findReportsViewBuilder, SessionManagerImpl sessionManager, ReportFiltersMapper reportFiltersMapper) {
        this.permissionHandler = permissionHandler;
        this.manageReportConfiguration = manageReportConfiguration;
        this.reportItemBuilder = reportItemBuilder;
        this.reportService = reportService;
        this.options = options;
        this.messages = messages;
        this.manageReportService = manageReportService;

        openReportsGui = options.reportConfiguration.getOpenReportsGui();
        closedReportsGui = options.reportConfiguration.getClosedReportsGui();
        myAssignedReportsGui = options.reportConfiguration.getMyAssignedReportsGui();
        assignedReportsGui = options.reportConfiguration.getAssignedReportsGui();

        this.reportDetailViewBuilder = reportDetailViewBuilder;
        this.manageReportsViewBuilder = manageReportsViewBuilder;
        this.findReportsViewBuilder = findReportsViewBuilder;
        this.sessionManager = sessionManager;
        this.reportFiltersMapper = reportFiltersMapper;
    }

    @GuiAction("manage-reports/view/overview")
    public TubingGui manageReportsOverview(Player player) {
        permissionHandler.validate(player, manageReportConfiguration.permissionView);
        return manageReportsViewBuilder.buildGui();
    }

    @GuiAction("manage-reports/view/find-reports")
    public TubingGui allAssignedReportsGui(@GuiParam(value = "page", defaultValue = "0") int page,
                                           @CurrentAction String currentAction,
                                           @GuiParams Map<String, String> allParams) {
        ReportFilters.ReportFiltersBuilder reportFiltersBuilder = new ReportFilters.ReportFiltersBuilder();
        allParams.forEach((k, v) -> reportFiltersMapper.map(k, v, reportFiltersBuilder));
        return findReportsViewBuilder.buildGui(reportFiltersBuilder.build(), currentAction, page);
    }

    @GuiAction("manage-reports/accept")
    public void acceptReport(Player player,
                             @GuiParam("reportId") int reportId) {
        permissionHandler.validate(player, manageReportConfiguration.permissionAccept);
        manageReportService.acceptReport(player, reportId);
    }

    @GuiAction("manage-reports/delete")
    public void deleteReport(Player player,
                             @GuiParam("reportId") int reportId) {
        permissionHandler.validate(player, manageReportConfiguration.permissionDelete);
        manageReportService.deleteReport(player, reportId);
    }

    @GuiAction("manage-reports/reopen")
    public void reopenReport(Player player,
                             @GuiParam("reportId") int reportId) {
        manageReportService.reopenReport(player, reportId);
    }

    @GuiAction("manage-reports/reject")
    public void rejectReport(Player player,
                             @GuiParam("reportId") int reportId) {
        if (options.reportConfiguration.isClosingReasonEnabled()) {
            messages.send(player, "&1==================================================", messages.prefixReports);
            messages.send(player, "&6        You have chosen to reject this report", messages.prefixReports);
            messages.send(player, "&6Type your closing reason in chat to reject the report", messages.prefixReports);
            messages.send(player, "&6        Type \"cancel\" to cancel closing the report ", messages.prefixReports);
            messages.send(player, "&1==================================================", messages.prefixReports);
            PlayerSession playerSession = sessionManager.get(player.getUniqueId());
            playerSession.setChatAction((player1, message) -> {
                if (message.equalsIgnoreCase(CANCEL)) {
                    messages.send(player, "&CYou have cancelled rejecting this report", messages.prefixReports);
                    return;
                }
                manageReportService.closeReport(player, new CloseReportRequest(reportId, ReportStatus.REJECTED, message));
            });
        } else {
            manageReportService.closeReport(player, new CloseReportRequest(reportId, ReportStatus.REJECTED, null));
        }
    }

    @GuiAction("manage-reports/teleport")
    public void teleportToReport(Player player,
                                 @GuiParam("reportId") int reportId) {
        Report report = reportService.getReport(reportId);
        if (report.getLocation().isPresent()) {
            reportService.goToReportLocation(player, report.getId());
        } else {
            messages.send(player, "&cLocation not known for this report.", messages.prefixReports);
        }
    }

    @GuiAction("manage-reports/resolve")
    public void resolveReport(Player player,
                              @GuiParam("reportId") int reportId) {
        permissionHandler.validate(player, manageReportConfiguration.permissionResolve);
        if (options.reportConfiguration.isClosingReasonEnabled()) {
            messages.send(player, "&1===================================================", messages.prefixReports);
            messages.send(player, "&6       You have chosen to resolve this report", messages.prefixReports);
            messages.send(player, "&6Type your closing reason in chat to resolve the report", messages.prefixReports);
            messages.send(player, "&6      Type \"cancel\" to cancel closing the report ", messages.prefixReports);
            messages.send(player, "&1===================================================", messages.prefixReports);
            PlayerSession playerSession = sessionManager.get(player.getUniqueId());
            playerSession.setChatAction((player1, message) -> {
                if (message.equalsIgnoreCase(CANCEL)) {
                    messages.send(player, "&CYou have cancelled rejecting this report", messages.prefixReports);
                    return;
                }
                manageReportService.closeReport(player, new CloseReportRequest(reportId, ReportStatus.RESOLVED, message));
            });
        } else {
            manageReportService.closeReport(player, new CloseReportRequest(reportId, ReportStatus.RESOLVED, null));
        }
    }

    @GuiAction("manage-reports/view/open")
    public TubingGui openReportsGui(@GuiParam(value = "page", defaultValue = "0") int page,
                                    @CurrentAction String currentAction,
                                    @GuiParam("backAction") String backAction) {
        Collection<Report> reports = reportService.getUnresolvedReports(PAGE_SIZE * page, PAGE_SIZE);
        return getReportsView(page, reports, currentAction, openReportsGui.getTitle(), r -> "manage-reports/accept?reportId=" + r.getId(), backAction);
    }

    @GuiAction("manage-reports/view/detail")
    public TubingGui goToManageReportView(Player player,
                                          @GuiParam("reportId") int reportId,
                                          @GuiParam("backAction") String backAction,
                                          @CurrentAction String currentAction) {
        Report report = reportService.getReport(reportId);
        return reportDetailViewBuilder.buildGui(player, report, backAction, currentAction);
    }

    @GuiAction("manage-reports/view/assigned")
    public TubingGui allAssignedReportsGui(@GuiParam(value = "page", defaultValue = "0") int page,
                                           @CurrentAction String currentAction,
                                           @GuiParam("backAction") String backAction) {
        Collection<Report> reports = reportService.getAllAssignedReports(PAGE_SIZE * page, PAGE_SIZE);
        return getReportsView(page, reports, currentAction, assignedReportsGui.getTitle(), backAction);
    }

    @GuiAction("manage-reports/view/closed")
    public TubingGui closedReportsGui(@GuiParam(value = "page", defaultValue = "0") int page,
                                      @CurrentAction String currentAction,
                                      @GuiParam("backAction") String backAction) {
        List<Report> reports = manageReportService.getClosedReports(PAGE_SIZE * page, PAGE_SIZE);
        return getReportsView(page, reports, currentAction, closedReportsGui.getTitle(), backAction);
    }


    @GuiAction("manage-reports/view/my-assigned")
    public TubingGui myAssignedReportsGui(Player player,
                                          @GuiParam(value = "page", defaultValue = "0") int page,
                                          @CurrentAction String currentAction,
                                          @GuiParam("backAction") String backAction) {
        Collection<Report> reports = reportService.getAssignedReports(player.getUniqueId(), PAGE_SIZE * page, PAGE_SIZE);
        return getReportsView(page, reports, currentAction, myAssignedReportsGui.getTitle(), backAction);
    }

    @GuiAction("my-reports/view")
    public TubingGui myReportsGui(Player player,
                                  @GuiParam(value = "page", defaultValue = "0") int page,
                                  @CurrentAction String currentAction) {
        Collection<Report> reports = reportService.getMyReports(player.getUniqueId(), PAGE_SIZE * page, PAGE_SIZE);
        return getReportsView(page, reports, currentAction, "My Reports", r -> TubingGuiActions.NOOP, null);
    }

    private TubingGui getReportsView(int page, Collection<Report> reports, String action, String title, String backAction) {
        return getReportsView(page, reports, action, title, r -> getDetailAction(action, r), backAction);
    }

    private TubingGui getReportsView(int page, Collection<Report> assignedReports, String action, String title, Function<Report, String> clickAction, String backAction) {
        return new PagedGuiBuilder.Builder(messages.colorize(title))
            .addPagedItems(action, assignedReports, reportItemBuilder::build, clickAction, page)
            .backAction(backAction)
            .build();
    }

    private String getDetailAction(String currentAction, Report report) {

        return GuiActionBuilder.builder()
            .action("manage-reports/view/detail")
            .param("reportId", String.valueOf(report.getId()))
            .param("backAction", currentAction)
            .build();
    }
}