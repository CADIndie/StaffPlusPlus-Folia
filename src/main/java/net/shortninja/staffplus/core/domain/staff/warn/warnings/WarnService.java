package net.shortninja.staffplus.core.domain.staff.warn.warnings;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.appeals.Appeal;
import net.shortninja.staffplus.core.domain.staff.appeals.AppealService;
import net.shortninja.staffplus.core.domain.staff.appeals.database.AppealRepository;
import net.shortninja.staffplus.core.domain.staff.infractions.Infraction;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionInfo;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionProvider;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionType;
import net.shortninja.staffplus.core.domain.staff.infractions.config.InfractionsConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.config.WarningConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.config.WarningSeverityConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.database.WarnRepository;
import net.shortninja.staffplusplus.appeals.AppealStatus;
import net.shortninja.staffplusplus.appeals.AppealableType;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplusplus.warnings.WarningCreatedEvent;
import net.shortninja.staffplusplus.warnings.WarningExpiredEvent;
import net.shortninja.staffplusplus.warnings.WarningFilters;
import net.shortninja.staffplusplus.warnings.WarningRemovedEvent;
import net.shortninja.staffplusplus.warnings.WarningService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.Constants.CONSOLE_UUID;
import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;

@IocBean
@IocMultiProvider(InfractionProvider.class)
public class WarnService implements InfractionProvider, WarningService {

    @ConfigProperty("permissions:warn-bypass")
    private String permissionWarnBypass;

    private final PermissionHandler permission;
    private final Options options;
    private final Messages messages;
    private final WarnRepository warnRepository;
    private final AppealRepository appealRepository;
    private final WarningConfiguration warningConfiguration;
    private final AppealService appealService;
    private final InfractionsConfiguration infractionsConfiguration;

    public WarnService(PermissionHandler permission,
                       Options options,
                       Messages messages,
                       WarnRepository warnRepository,
                       AppealRepository appealRepository,
                       WarningConfiguration warningConfiguration,
                       AppealService appealService,
                       InfractionsConfiguration infractionsConfiguration) {
        this.permission = permission;
        this.options = options;
        this.messages = messages;
        this.warnRepository = warnRepository;
        this.appealRepository = appealRepository;
        this.warningConfiguration = warningConfiguration;
        this.appealService = appealService;
        this.infractionsConfiguration = infractionsConfiguration;
    }

    public void sendWarning(CommandSender sender, SppPlayer culprit, String reason, WarningSeverityConfiguration severityConfig) {
        if (StringUtils.isEmpty(reason) && !severityConfig.hasDefaultReason()) {
            throw new BusinessException("&CReason must provided");
        }
        if (severityConfig.hasDefaultReason() && (!severityConfig.isReasonSettable() || StringUtils.isEmpty(reason))) {
            reason = severityConfig.getReason().get();
        }

        String issuerName = sender instanceof Player ? sender.getName() : "Console";
        UUID issuerUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : CONSOLE_UUID;
        Warning warning = new Warning(culprit.getId(), culprit.getUsername(), reason, issuerName, issuerUuid, System.currentTimeMillis(), severityConfig);
        createWarning(culprit, warning);
    }

    @Deprecated
    // This is only used when severity levels are empty, in the new system this is not recommended and it will be removed.
    public void sendWarning(CommandSender sender, SppPlayer user, String reason) {
        String issuerName = sender instanceof Player ? sender.getName() : "Console";
        UUID issuerUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : CONSOLE_UUID;
        Warning warning = new Warning(user.getId(), user.getUsername(), reason, issuerName, issuerUuid, System.currentTimeMillis());
        createWarning(user, warning);
    }

    private void createWarning(SppPlayer user, Warning warning) {
        // Offline users cannot bypass being warned this way. Permissions are taken away upon logging out
        if (user.isOnline() && permission.has(user.getPlayer(), permissionWarnBypass)) {
            throw new BusinessException(messages.bypassed);
        }

        int warningId = warnRepository.addWarning(warning);
        warning.setId(warningId);
        sendEvent(new WarningCreatedEvent(warning));
    }

    public Warning getWarning(int warningId) {
        return warnRepository.findWarning(warningId)
            .orElseThrow(() -> new BusinessException("Warning with id [" + warningId + "] not found", messages.prefixWarnings));
    }

    public List<Warning> getWarnings(UUID uuid, boolean includeAppealed) {
        return warnRepository.getWarnings(uuid).stream()
            .filter(w -> includeAppealed || (w.getAppeal().map(a -> a.getStatus() != AppealStatus.APPROVED).orElse(true)))
            .collect(Collectors.toList());
    }

    public List<Warning> getWarnings() {
        return warnRepository.getWarnings();
    }

    public void removeWarning(int id) {
        Warning warning = getWarning(id);
        if (warning.getServerName() != null && !warning.getServerName().equals(options.serverName)) {
            throw new BusinessException("For consistency reasons a warning must be removed on the same server it was created. Please try removing the warning while connected to server " + warning.getServerName());
        }

        appealRepository.deleteAppeals(id, AppealableType.WARNING);
        warnRepository.removeWarning(id);
        sendEvent(new WarningRemovedEvent(warning));
    }

    public void approveAppeal(SppPlayer resolver, int appealId) {
        this.approveAppeal(resolver, appealId, null);
    }

    public void approveAppeal(SppPlayer resolver, int appealId, String appealReason) {
        Appeal appeal = appealService.getAppeal(appealId);
        Warning warning = warnRepository.findWarning(appeal.getAppealableId()).orElseThrow(() -> new BusinessException("No warning found."));

        if (warning.getServerName() != null && !warning.getServerName().equals(options.serverName)) {
            throw new BusinessException("For consistency reasons an appeal must accepted on the same server the warning was created. Please try accepting the appeal while connected to server " + warning.getServerName());
        }
        appealService.approveAppeal(resolver, appealId, appealReason, AppealableType.WARNING);
    }

    public void expireWarning(int id) {
        Warning warning = getWarning(id);
        warnRepository.expireWarning(id);
        sendEvent(new WarningExpiredEvent(warning));
    }

    public List<Warning> getWarnings(UUID uniqueId, int offset, int amount, boolean includeExpired) {
        return warnRepository.getWarnings(uniqueId, offset, amount)
            .stream().filter(w -> includeExpired || !w.isExpired())
            .collect(Collectors.toList());
    }

    public List<Warning> getAllWarnings(int offset, int amount, boolean includeExpired) {
        return warnRepository.getAllWarnings(offset, amount)
            .stream().filter(w -> includeExpired || !w.isExpired())
            .collect(Collectors.toList());
    }

    @Override
    public long getWarnCount(WarningFilters warningFilters) {
        return warnRepository.getWarnCount(warningFilters);
    }

    @Override
    public int getTotalScore(String playerName) {
        return warnRepository.getTotalScore(playerName);
    }

    @Override
    public List<Warning> findWarnings(WarningFilters warningFilters, int offset, int amount) {
        return warnRepository.findWarnings(warningFilters, offset, amount);
    }

    public List<Warning> getAppealedWarnings(int offset, int amount) {
        return warnRepository.getAppealedWarnings(offset, amount);
    }

    public void markWarningsRead(UUID uniqueId) {
        warnRepository.markWarningsRead(uniqueId);
    }

    public void expireWarnings() {
        long now = System.currentTimeMillis();
        warningConfiguration.getSeverityLevels().stream()
            .filter(s -> s.getExpirationDuration() > 0)
            .forEach(s -> warnRepository.expireWarnings(s.getName(), now - s.getExpirationDuration()));
    }

    @Override
    public List<? extends Infraction> getInfractions(Player executor, UUID playerUUID) {
        if (!infractionsConfiguration.isShowWarnings()) {
            return Collections.emptyList();
        }
        return getWarnings(playerUUID, false).stream()
            .filter(w -> !w.isExpired())
            .collect(Collectors.toList());
    }

    @Override
    public Optional<InfractionInfo> getInfractionsInfo() {
        if (!infractionsConfiguration.isShowWarnings()) {
            return Optional.empty();
        }
        return Optional.of(new InfractionInfo(InfractionType.WARNING, warnRepository.getCountByPlayer()));
    }

    @Override
    public InfractionType getType() {
        return InfractionType.WARNING;
    }

    public void rejectAppeal(SppPlayer player, int appealId) {
        appealService.rejectAppeal(player, appealId, AppealableType.WARNING);
    }
    public void rejectAppeal(SppPlayer player, int appealId, String reason) {
        appealService.rejectAppeal(player, appealId, reason, AppealableType.WARNING);
    }
}
