package net.shortninja.staffplus.core.domain.chat;

import org.bukkit.entity.Player;

public interface ChatAction {

	void execute(Player player, String message);

}