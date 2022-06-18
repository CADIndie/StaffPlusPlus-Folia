package net.shortninja.staffplus.core.domain.staff.alerts.config;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import be.garagepoort.mcioc.configuration.ConfigTransformer;
import net.shortninja.staffplus.core.application.config.SoundsConfigTransformer;
import net.shortninja.staffplus.core.common.Sounds;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplusplus.alerts.AlertType;
import net.shortninja.staffplusplus.altdetect.AltDetectTrustLevel;

import java.util.List;

@IocBean
public class AlertsConfiguration {

    @ConfigProperty("alerts-module.name-notify")
    public boolean alertsNameNotify;
    @ConfigProperty("alerts-module.alt-detect-notify.enabled")
    public boolean alertsAltDetectEnabled;
    @ConfigProperty("alerts-module.alt-detect-notify.trust-levels")
    @ConfigTransformer(AltDetectTrustLevelConfigTransformer.class)
    public List<AltDetectTrustLevel> alertsAltDetectTrustLevels;

    @ConfigProperty("permissions:alerts.notifications.alt-detect")
    public String permissionAlertsAltDetect;
    @ConfigProperty("permissions:alerts.notifications.mention")
    public String permissionMention;
    @ConfigProperty("permissions:alerts.notifications.name-change")
    public String permissionNameChange;
    @ConfigProperty("permissions:alerts.notifications.chat-phrase-detection")
    public String permissionChatPhraseDetection;
    @ConfigProperty("permissions:alerts.notifications.blacklist-detection")
    public String permissionBlacklistDetection;
    @ConfigProperty("permissions:alerts.notifications.command-detection")
    public String permissionCommandDetection;

    @ConfigProperty("alerts-module.sound")
    @ConfigTransformer(SoundsConfigTransformer.class)
    public Sounds alertsSound;

    private final XrayConfiguration xrayConfiguration;

    public AlertsConfiguration(XrayConfiguration xrayConfiguration) {
        this.xrayConfiguration = xrayConfiguration;
    }

    public String getPermissionForType(AlertType alertType) {
        switch (alertType) {
            case XRAY:
                return xrayConfiguration.permissionXray;
            case MENTION:
                return permissionMention;
            case ALT_DETECT:
                return permissionAlertsAltDetect;
            case NAME_CHANGE:
                return permissionNameChange;
            case CHAT_PHRASE_DETECTION:
                return permissionChatPhraseDetection;
            case COMMAND_DETECTION:
                return permissionCommandDetection;
            case BLACKLIST:
                return permissionBlacklistDetection;
            default:
                throw new BusinessException("&CUnsupported alertType [" + alertType + "]");
        }
    }
}
