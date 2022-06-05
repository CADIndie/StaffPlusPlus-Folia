package net.shortninja.staffplus.core.domain.staff.reporting;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.bungee.ServerSwitcher;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.actions.ActionRunStrategy;
import net.shortninja.staffplus.core.domain.actions.ActionService;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.infractions.Infraction;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionInfo;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionProvider;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionType;
import net.shortninja.staffplus.core.domain.staff.reporting.database.ReportRepository;
import net.shortninja.staffplusplus.reports.CreateReportEvent;
import net.shortninja.staffplusplus.reports.ReportFilters;
import net.shortninja.staffplusplus.reports.ReportStatus;
import net.shortninja.staffplusplus.session.SppInteractor;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;
import static net.shortninja.staffplus.core.domain.actions.CreateStoredCommandRequest.CreateStoredCommandRequestBuilder.commandBuilder;

@IocBean
@IocMultiProvider(InfractionProvider.class)
public class ReportService implements InfractionProvider, net.shortninja.staffplusplus.reports.ReportService {

    @ConfigProperty("permissions:report-bypass")
    private String permissionReportBypass;

    private final PermissionHandler permission;
    private final Options options;
    private final Messages messages;
    private final PlayerManager playerManager;
    private final ReportRepository reportRepository;
    private final ActionService actionService;

    public ReportService(PermissionHandler permission,
                         Options options,
                         ReportRepository reportRepository,
                         Messages messages,
                         PlayerManager playerManager,
                         ActionService actionService) {
        this.permission = permission;
        this.options = options;
        this.reportRepository = reportRepository;
        this.messages = messages;
        this.playerManager = playerManager;
        this.actionService = actionService;
    }

    public List<Report> getReported(SppPlayer player, int offset, int amount) {
        return reportRepository.getReports(player.getId(), offset, amount);
    }

    public List<Report> findReports(ReportFilters reportFilters, int offset, int amount) {
        return reportRepository.findReports(reportFilters, offset, amount);
    }

    public List<Report> getReported(UUID playerUuid, int offset, int amount) {
        SppPlayer user = getUser(playerUuid);
        return reportRepository.getReports(user.getId(), offset, amount);
    }

    public void sendReport(SppInteractor player, SppPlayer user, String reason) {
        sendReport(player, user, reason, null);
    }

    public void sendReport(SppInteractor sppInteractor, SppPlayer user, String reason, String type) {
        if(StringUtils.isEmpty(reason)) {
            throw new BusinessException("Report cannot be created with the reason");
        }
        // Offline users cannot bypass being reported this way. Permissions are taken away upon logging out
        if (user.isOnline() && permission.has(user.getPlayer(), permissionReportBypass)) {
            messages.send(sppInteractor, messages.bypassed, messages.prefixGeneral);
            return;
        }

        Report report = new Report(
            user.getId(),
            user.getUsername(),
            reason,
            sppInteractor.getUsername(),
            sppInteractor.getId(),
            ReportStatus.OPEN,
            ZonedDateTime.now(),
            sppInteractor.isBukkitPlayer() ? ((Player)sppInteractor.getCommandSender().get()).getLocation() : null,
            type,
            options.serverName);

        int id = reportRepository.addReport(report);
        report.setId(id);
        sendEvent(new CreateReportEvent(report));
    }

    public void sendReport(SppInteractor player, String reason) {
        sendReport(player, reason, null);
    }

    public void sendReport(SppInteractor sppInteractor, String reason, String type) {
        Report report = new Report(
            null,
            null,
            reason,
            sppInteractor.getUsername(),
            sppInteractor.getId(),
            ReportStatus.OPEN,
            ZonedDateTime.now(),
            sppInteractor.isBukkitPlayer() ? ((Player)sppInteractor.getCommandSender().get()).getLocation() : null,
            type,
            options.serverName);

        int id = reportRepository.addReport(report);
        report.setId(id);
        sendEvent(new CreateReportEvent(report));
    }

    public Collection<Report> getUnresolvedReports(int offset, int amount) {
        return reportRepository.getUnresolvedReports(offset, amount);
    }

    public Collection<Report> getAllAssignedReports(int offset, int amount) {
        return reportRepository.getAssignedReports(offset, amount);
    }

    public Collection<Report> getAssignedReports(UUID staffUuid, int offset, int amount) {
        return reportRepository.getAssignedReports(staffUuid, offset, amount);
    }

    public Collection<Report> getMyReports(UUID reporterUuid, int offset, int amount) {
        return reportRepository.getMyReports(reporterUuid, offset, amount);
    }

    public List<Report> getMyReports(UUID reporterUuid) {
        return reportRepository.getMyReports(reporterUuid);
    }

    private SppPlayer getUser(UUID playerUuid) {
        Optional<SppPlayer> player = playerManager.getOnOrOfflinePlayer(playerUuid);
        if (!player.isPresent()) {
            throw new BusinessException(messages.playerNotRegistered, messages.prefixGeneral);
        }
        return player.get();
    }

    public Report getReport(int reportId) {
        return reportRepository.findReport(reportId).orElseThrow(() -> new BusinessException("Report with id [" + reportId + "] not found", messages.prefixReports));
    }

    public void goToReportLocation(Player player, int reportId) {
        Report report = getReport(reportId);
        Location location = report.getLocation().orElseThrow(() -> new BusinessException("Cannot teleport to report, report has no known location"));
        if (report.getServerName().equalsIgnoreCase(options.serverName)) {
            player.teleport(location);
            messages.send(player, "You have been teleported to the location where this report was created", messages.prefixReports);
        } else {
            actionService.createCommand(
                commandBuilder()
                    .command("staffplus:teleport-to-report " + reportId)
                    .executor(player.getUniqueId())
                    .executorRunStrategy(ActionRunStrategy.DELAY)
                    .serverName(report.getServerName())
                    .build());
            ServerSwitcher.switchServer(player, report.getServerName());
        }
    }

    @Override
    public long getReportCount(ReportFilters reportFilter) {
        return this.reportRepository.getReportCount(reportFilter);
    }

    @Override
    public List<? extends Infraction> getInfractions(Player executor, UUID playerUUID) {
        if (!options.infractionsConfiguration.isShowReported()) {
            return Collections.emptyList();
        }
        return reportRepository.getReportsByOffender(playerUUID);
    }

    @Override
    public Optional<InfractionInfo> getInfractionsInfo() {
        if (!options.infractionsConfiguration.isShowReported()) {
            return Optional.empty();
        }
        return Optional.of(new InfractionInfo(InfractionType.REPORTED, reportRepository.getReportedCount()));
    }

    @Override
    public InfractionType getType() {
        return InfractionType.REPORTED;
    }


}
