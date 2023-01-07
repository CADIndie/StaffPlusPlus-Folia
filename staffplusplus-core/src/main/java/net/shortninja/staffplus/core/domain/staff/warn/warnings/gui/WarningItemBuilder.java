package net.shortninja.staffplus.core.domain.staff.warn.warnings.gui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.common.gui.LoreBuilder;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionType;
import net.shortninja.staffplus.core.domain.staff.infractions.config.InfractionsConfiguration;
import net.shortninja.staffplus.core.domain.staff.infractions.gui.views.InfractionGuiProvider;
import net.shortninja.staffplus.core.domain.staff.warn.appeals.WarningAppealConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.Warning;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.config.WarningConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.config.WarningSeverityConfiguration;
import net.shortninja.staffplusplus.appeals.AppealStatus;
import net.shortninja.staffplusplus.appeals.IAppeal;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static net.shortninja.staffplus.core.common.JavaUtils.toHumanReadableDuration;

@IocBean
@IocMultiProvider(InfractionGuiProvider.class)
public class WarningItemBuilder implements InfractionGuiProvider<Warning> {

    private final Options options;
    private final WarningAppealConfiguration warningAppealConfiguration;
    private final WarningConfiguration warningConfiguration;
    private final IProtocolService protocolService;
    private final InfractionsConfiguration infractionsConfiguration;

    public WarningItemBuilder(Options options,
                              WarningAppealConfiguration warningAppealConfiguration, WarningConfiguration warningConfiguration,
                              IProtocolService protocolService,
                              InfractionsConfiguration infractionsConfiguration) {
        this.options = options;
        this.warningAppealConfiguration = warningAppealConfiguration;
        this.protocolService = protocolService;
        this.warningConfiguration = warningConfiguration;
        this.infractionsConfiguration = infractionsConfiguration;
    }


    public ItemStack build(Warning warning) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(warning.getCreationDate().toInstant(), ZoneOffset.UTC);
        String time = localDateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofPattern(options.timestampFormat));
        Optional<IAppeal> appeal = warning.getAppeal();

        LoreBuilder loreBuilder = LoreBuilder.builder("&b", "&6")
            .addItem("Id", String.valueOf(warning.getId()))
            .addItem("Server", warning.getServerName(), options.serverSyncConfiguration.warningSyncServers.isEnabled())
            .addItem("Severity", warning.getSeverity())
            .addItem("Issuer", warning.getIssuerName(), warningConfiguration.isShowIssuer())
            .addItem("Culprit", warning.getTargetName())
            .addItem("Issued on", time)
            .addIndented("Reason", warning.getReason())
            .addItem("Appeal &2approved", appealApproved(appeal))
            .addItem("&cExpired", !appealApproved(appeal) && warning.isExpired());

        if (!warning.isExpired() && !appealApproved(appeal) && expirationEnabled(warning.getSeverity())) {
            addExpiresAt(warning, loreBuilder);
        }

        ItemStack item = Items.editor(Items.createSkull(warning.getTargetName())).setAmount(1)
            .setName("&6Warning")
            .setLore(loreBuilder.build())
            .build();

        return protocolService.getVersionProtocol().addNbtString(item, String.valueOf(warning.getId()));
    }

    private boolean appealApproved(Optional<IAppeal> appeal) {
        return warningAppealConfiguration.enabled && appeal.isPresent() && appeal.get().getStatus() == AppealStatus.APPROVED;
    }

    private void addExpiresAt(Warning warning, LoreBuilder lore) {
        warningConfiguration.getSeverityConfiguration(warning.getSeverity())
            .ifPresent(warningSeverityConfiguration -> {
                long now = System.currentTimeMillis();
                long expireTimestamp = warning.getCreationTimestamp() + warningSeverityConfiguration.getExpirationDuration();
                long expirationDuration = expireTimestamp - now;
                lore.addItem("Expires after", "Less than a minute", expirationDuration <= 0);
                lore.addDuration("Expires after", toHumanReadableDuration(expirationDuration), expirationDuration > 0);
            });
    }

    private boolean expirationEnabled(String severityLevel) {
        return warningConfiguration.getSeverityConfiguration(severityLevel)
            .map(WarningSeverityConfiguration::isExpirationEnabled)
            .orElse(false);
    }

    @Override
    public InfractionType getType() {
        return InfractionType.WARNING;
    }

    @Override
    public ItemStack getMenuItem(Warning warning) {
        ItemStack itemStack = build(warning);
        itemStack.setType(infractionsConfiguration.getWarningsGuiItem());
        return itemStack;
    }
}
