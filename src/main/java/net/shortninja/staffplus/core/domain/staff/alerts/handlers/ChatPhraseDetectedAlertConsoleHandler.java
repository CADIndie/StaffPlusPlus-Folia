package net.shortninja.staffplus.core.domain.staff.alerts.handlers;

import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplusplus.chat.PhrasesDetectedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@IocListener(conditionalOnProperty = "alerts-module.chat-phrase-detection-console=true")
public class ChatPhraseDetectedAlertConsoleHandler implements Listener {

    private final Messages messages;

    public ChatPhraseDetectedAlertConsoleHandler(Messages messages) {
        this.messages = messages;
    }

    @EventHandler
    public void handle(PhrasesDetectedEvent phrasesDetectedEvent) {
        String message = messages.alertsChatPhraseDetected
            .replace("%target%", phrasesDetectedEvent.getPlayer().getName())
            .replace("%originalMessage%", phrasesDetectedEvent.getOriginalMessage())
            .replace("%detectedPhrases%", String.join(" | ", phrasesDetectedEvent.getDetectedPhrases()));

        StaffPlus.get().getLogger().info(message);
    }

}
