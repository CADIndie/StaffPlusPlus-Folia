package net.shortninja.staffplus.core.domain.confirmation;

import be.garagepoort.mcioc.IocBean;
import org.bukkit.entity.Player;

import java.util.Map;

@IocBean
public class ConfirmationService {

    private final ChoiceChatService choiceChatService;

    public ConfirmationService(ChoiceChatService choiceChatService) {
        this.choiceChatService = choiceChatService;
    }

    public void showConfirmation(Player player, ConfirmationConfig confirmationConfig, Map<String, String> placeholders, ChoiceAction confirmAction, ChoiceAction cancelAction) {
        String confirmationMessage = confirmationConfig.getConfirmationMessage();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            confirmationMessage = confirmationMessage.replace(entry.getKey(), entry.getValue());
        }

        if (confirmationConfig.getConfirmationType() == ConfirmationType.GUI) {
            new ConfirmationGui("Confirm?", confirmationMessage, confirmAction, cancelAction).show(player);
        } else {
            choiceChatService.sendConfirmationChoiceMessage(player, confirmationMessage, confirmAction, cancelAction);
        }
    }
}
