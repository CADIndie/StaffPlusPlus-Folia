package net.shortninja.staffplus.core.domain.staff.ban.playerbans.gui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.bungee.dto.BanBungeeDto;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.bungee.events.BanBungeeEvent;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.bungee.events.UnbanBungeeEvent;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.config.BanConfiguration;
import net.shortninja.staffplusplus.ban.BanEvent;
import net.shortninja.staffplusplus.ban.IBan;
import net.shortninja.staffplusplus.ban.UnbanEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.shortninja.staffplus.core.domain.staff.ban.playerbans.BanMessageStringUtil.replaceBanPlaceholders;

@IocBean
@IocListener
public class BanChatNotifier implements Listener {

    private final Messages messages;
    private final BanConfiguration banConfiguration;

    public BanChatNotifier(Messages messages, BanConfiguration banConfiguration) {
        this.messages = messages;
        this.banConfiguration = banConfiguration;
    }

    @EventHandler
    public void notifyPlayerBanned(BanEvent event) {
        IBan ban = event.getBan();
        if(ban.isSilentBan()) {
            return;
        }
        String banMessage = ban.getEndDate() == null ? messages.permanentBanned : messages.tempBanned;
        String message = replaceBanPlaceholders(banMessage, ban);

        this.messages.sendGroupMessage(message, banConfiguration.staffNotificationPermission, messages.prefixGeneral);
    }

    @EventHandler
    public void notifyPlayerBannedBungee(BanBungeeEvent event) {
        BanBungeeDto ban = event.getBan();
        if(ban.isSilentBan()) {
            return;
        }
        String banMessage = ban.getEndTimestamp() == null ? messages.permanentBanned : messages.tempBanned;
        String message = replaceBanPlaceholders(banMessage, ban);

        this.messages.sendGroupMessage(message, banConfiguration.staffNotificationPermission, messages.prefixGeneral);
    }

    @EventHandler
    public void notifyUnban(UnbanEvent event) {
        IBan ban = event.getBan();
        if(ban.isSilentUnban()) {
            return;
        }

        String unbanMessage = replaceBanPlaceholders(messages.unbanned, ban);
        this.messages.sendGroupMessage(unbanMessage, banConfiguration.staffNotificationPermission, messages.prefixGeneral);
    }

    @EventHandler
    public void notifyUnbanBungee(UnbanBungeeEvent event) {
        BanBungeeDto ban = event.getBan();
        if(ban.isSilentUnban()) {
            return;
        }

        String unbanMessage = replaceBanPlaceholders(messages.unbanned, ban);
        this.messages.sendGroupMessage(unbanMessage, banConfiguration.staffNotificationPermission, messages.prefixGeneral);
    }

}
