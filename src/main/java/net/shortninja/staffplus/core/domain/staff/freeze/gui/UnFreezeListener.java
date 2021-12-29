package net.shortninja.staffplus.core.domain.staff.freeze.gui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.domain.staff.freeze.FreezeGui;
import net.shortninja.staffplus.core.domain.staff.freeze.config.FreezeConfiguration;
import net.shortninja.staffplusplus.freeze.PlayerUnFrozenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

@IocBean
@IocListener
public class UnFreezeListener implements Listener {

    private final FreezeConfiguration freezeConfiguration;
    private final Messages messages;
    private final OnlineSessionsManager onlineSessionsManager;

    public UnFreezeListener(FreezeConfiguration freezeConfiguration, Messages messages, OnlineSessionsManager onlineSessionsManager) {
        this.freezeConfiguration = freezeConfiguration;
        this.messages = messages;
        this.onlineSessionsManager = onlineSessionsManager;
    }

    @EventHandler
    public void onUnFreeze(PlayerUnFrozenEvent event) {
        Player player = event.getTarget();

        messages.send(event.getIssuer(), messages.staffUnfroze.replace("%target%", player.getName()), messages.prefixGeneral);
        messages.send(player, messages.unfrozen, messages.prefixGeneral);

        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.BLINDNESS);

        OnlinePlayerSession session = onlineSessionsManager.get(player);
        if (freezeConfiguration.prompt && session.getCurrentGui().isPresent() && session.getCurrentGui().get() instanceof FreezeGui) {
            player.closeInventory();
        }
    }
}
