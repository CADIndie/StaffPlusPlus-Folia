package net.shortninja.staffplus.core.application.session.synchronizers;

import be.garagepoort.mcioc.tubingbukkit.annotations.IocBukkitListener;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplusplus.chat.NameChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@IocBukkitListener
public class NameChangeSessionSynchronizer implements Listener {

    private final OnlineSessionsManager onlineSessionsManager;

    public NameChangeSessionSynchronizer(OnlineSessionsManager onlineSessionsManager) {
        this.onlineSessionsManager = onlineSessionsManager;
    }

    @EventHandler
    public void onNameChange(NameChangeEvent nameChangeEvent) {
        OnlinePlayerSession onlinePlayerSession = onlineSessionsManager.get(nameChangeEvent.getPlayer());
        onlinePlayerSession.setName(nameChangeEvent.getNewName());
    }
}
