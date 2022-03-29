package net.shortninja.staffplus.core.domain.staff.investigate.bungee.receive;

import be.garagepoort.mcioc.IocMessageListener;
import net.shortninja.staffplus.core.common.Constants;
import net.shortninja.staffplus.core.common.bungee.BungeeClient;
import net.shortninja.staffplus.core.domain.staff.investigate.bungee.InvestigationBungee;
import net.shortninja.staffplus.core.domain.staff.investigate.bungee.events.InvestigationPausedBungeeEvent;
import net.shortninja.staffplus.core.domain.synchronization.ServerSyncConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Optional;

@IocMessageListener(
    channel = Constants.BUNGEE_CORD_CHANNEL,
    conditionalOnProperty = "isNotEmpty(server-sync-module.investigation-sync)")
public class InvestigationPausedBungeeReceiver implements PluginMessageListener {

    private final BungeeClient bungeeClient;
    private final ServerSyncConfiguration serverSyncConfiguration;

    public InvestigationPausedBungeeReceiver(BungeeClient bungeeClient, ServerSyncConfiguration serverSyncConfiguration) {
        this.bungeeClient = bungeeClient;
        this.serverSyncConfiguration = serverSyncConfiguration;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        Optional<InvestigationBungee> bungeeMessage = bungeeClient.handleReceived(channel, Constants.BUNGEE_INVESTIGATION_PAUSED_CHANNEL, message, InvestigationBungee.class);

        if (bungeeMessage.isPresent() && serverSyncConfiguration.investigationSyncServers.matchesServer(bungeeMessage.get().getServerName())) {
            Bukkit.getPluginManager().callEvent(new InvestigationPausedBungeeEvent(bungeeMessage.get()));
        }
    }
}
