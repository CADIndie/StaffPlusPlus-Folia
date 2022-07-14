package net.shortninja.staffplus.core.domain.staff.investigate.bungee;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.common.Constants;
import net.shortninja.staffplus.core.common.bungee.BungeeClient;
import net.shortninja.staffplusplus.investigate.InvestigationConcludedEvent;
import net.shortninja.staffplusplus.investigate.InvestigationStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@IocBukkitListener
public class InvestigationBungeeSender implements Listener {

    private final BungeeClient bungeeClient;

    public InvestigationBungeeSender(BungeeClient bungeeClient) {
        this.bungeeClient = bungeeClient;
    }

    @EventHandler
    public void onInvestigationStarted(InvestigationStartedEvent investigationStartedEvent) {
        if(Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        Player player = Bukkit.getOnlinePlayers().iterator().next();
        bungeeClient.sendMessage(player, Constants.BUNGEE_INVESTIGATION_STARTED_CHANNEL, new InvestigationBungee(investigationStartedEvent.getInvestigation()));
    }

    @EventHandler
    public void onInvestigationConcluded(InvestigationConcludedEvent investigationConcludedEvent) {
        if(Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        Player player = Bukkit.getOnlinePlayers().iterator().next();
        bungeeClient.sendMessage(player, Constants.BUNGEE_INVESTIGATION_CONCLUDED_CHANNEL, new InvestigationBungee(investigationConcludedEvent.getInvestigation()));
    }
}
