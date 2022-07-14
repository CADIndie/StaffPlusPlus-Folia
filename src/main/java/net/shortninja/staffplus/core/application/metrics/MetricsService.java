package net.shortninja.staffplus.core.application.metrics;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.StaffPlusPlus;
import net.shortninja.staffplus.core.domain.staff.altaccountdetect.config.AltDetectConfiguration;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.config.BanConfiguration;
import net.shortninja.staffplus.core.domain.staff.freeze.config.FreezeConfiguration;
import net.shortninja.staffplus.core.domain.staff.investigate.config.InvestigationConfiguration;
import net.shortninja.staffplus.core.domain.staff.kick.config.KickConfiguration;
import net.shortninja.staffplus.core.domain.staff.mute.config.MuteConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportConfiguration;
import net.shortninja.staffplus.core.domain.staff.staffchat.config.StaffChatConfiguration;
import net.shortninja.staffplus.core.domain.staff.vanish.VanishConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.appeals.WarningAppealConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.config.WarningConfiguration;

@IocBean(conditionalOnProperty = "metrics=true")
public class MetricsService {

    private static final int PLUGIN_ID = 9351;

    public MetricsService(@ConfigProperty("storage.type") String storageType,
                          @ConfigProperty("chat-module.enabled") String chatModuleEnabled,
                          @ConfigProperty("blacklist-module.enabled") String blacklistModuleEnabled,
                          @ConfigProperty("broadcast-module.enabled") String broadcastModuleEnabled,
                          @ConfigProperty("infractions-module.enabled") String infractionsModuleEnabled,
                          @ConfigProperty("webui-module.enabled") String webUiModuleEnabled,
                          WarningConfiguration warningConfiguration,
                          BanConfiguration banConfiguration,
                          AltDetectConfiguration altDetectConfiguration,
                          MuteConfiguration muteConfiguration,
                          ReportConfiguration reportConfiguration,
                          KickConfiguration kickConfiguration,
                          WarningAppealConfiguration warningAppealConfiguration,
                          InvestigationConfiguration investigationConfiguration,
                          VanishConfiguration vanishConfiguration,
                          StaffChatConfiguration staffChatConfiguration,
                          FreezeConfiguration freezeConfiguration) {
        Metrics metrics = new Metrics(StaffPlusPlus.get(), PLUGIN_ID);

        boolean warningConfigurationEnabled = warningConfiguration.isEnabled();
        boolean appealConfigurationEnabled = warningAppealConfiguration.enabled;
        boolean reportConfigurationEnabled = reportConfiguration.isEnabled();
        boolean banConfigurationEnabled = banConfiguration.enabled;
        boolean muteConfigurationEnabled = muteConfiguration.muteEnabled;
        boolean kickConfigurationEnabled = kickConfiguration.kickEnabled;

        metrics.addCustomChart(new Metrics.SimplePie("alt_detection_module", () -> String.valueOf(altDetectConfiguration.enabled)));
        metrics.addCustomChart(new Metrics.SimplePie("ban_module", () -> String.valueOf(banConfigurationEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("blacklist_module", () -> String.valueOf(blacklistModuleEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("broadcast_module", () -> String.valueOf(broadcastModuleEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("chat_module", () -> String.valueOf(chatModuleEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("freeze_module", () -> String.valueOf(freezeConfiguration.enabled)));
        metrics.addCustomChart(new Metrics.SimplePie("infractions_module", () -> String.valueOf(infractionsModuleEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("investigations_module", () -> String.valueOf(investigationConfiguration.isEnabled())));
        metrics.addCustomChart(new Metrics.SimplePie("kick_module", () -> String.valueOf(kickConfigurationEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("mute_module", () -> String.valueOf(muteConfigurationEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("reports_module", () -> String.valueOf(reportConfigurationEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("staff_chat_module", () -> String.valueOf(staffChatConfiguration.isEnabled())));
        metrics.addCustomChart(new Metrics.SimplePie("storage_type", () -> storageType));
        metrics.addCustomChart(new Metrics.SimplePie("vanish_module", () -> String.valueOf(vanishConfiguration.enabled)));
        metrics.addCustomChart(new Metrics.SimplePie("warning_appeals_module", () -> String.valueOf(appealConfigurationEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("warnings_module", () -> String.valueOf(warningConfigurationEnabled)));
        metrics.addCustomChart(new Metrics.SimplePie("web_ui_module", () -> String.valueOf(webUiModuleEnabled)));
    }
}
