package net.shortninja.staffplus.core.domain.staff.alerts.handlers;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.alerts.config.AlertsConfiguration;
import net.shortninja.staffplusplus.alerts.AlertType;
import net.shortninja.staffplusplus.chat.PhrasesDetectedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@IocBean
public class ChatPhraseDetectedAlertHandler extends AlertsHandler implements Listener {

    public ChatPhraseDetectedAlertHandler(AlertsConfiguration alertsConfiguration, SessionManagerImpl sessionManager, PermissionHandler permission, Messages messages) {
        super(alertsConfiguration, sessionManager, permission, messages);
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler
    public void handle(PhrasesDetectedEvent phrasesDetectedEvent) {
        if (!alertsConfiguration.alertsChatPhraseDetectionEnabled) {
            return;
        }

        if (permission.has(phrasesDetectedEvent.getPlayer(), alertsConfiguration.permissionChatPhraseDetectionBypass)) {
            return;
        }

        for (Player player : getPlayersToNotify()) {
            messages.send(player, messages.alertsChatPhraseDetected
                .replace("%target%", phrasesDetectedEvent.getPlayer().getName())
                .replace("%originalMessage%", phrasesDetectedEvent.getOriginalMessage())
                .replace("%detectedPhrases%", String.join(" | ", phrasesDetectedEvent.getDetectedPhrases())), messages.prefixGeneral, getPermission());
        }
    }

    @Override
    protected AlertType getType() {
        return AlertType.CHAT_PHRASE_DETECTION;
    }

    @Override
    protected String getPermission() {
        return alertsConfiguration.permissionChatPhraseDetection;
    }
}
