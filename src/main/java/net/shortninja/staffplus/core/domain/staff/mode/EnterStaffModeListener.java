package net.shortninja.staffplus.core.domain.staff.mode;

import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplusplus.staffmode.EnterStaffModeEvent;
import net.shortninja.staffplusplus.staffmode.IModeData;
import net.shortninja.staffplusplus.staffmode.SwitchStaffModeEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;

@IocListener
public class EnterStaffModeListener implements Listener {

    private final PlayerManager playerManager;
    private final Messages messages;
    private final StaffModeItemsService staffModeItemsService;
    private final ModeProvider modeProvider;

    public EnterStaffModeListener(PlayerManager playerManager, Messages messages, StaffModeItemsService staffModeItemsService, ModeProvider modeProvider) {
        this.playerManager = playerManager;
        this.messages = messages;
        this.staffModeItemsService = staffModeItemsService;
        this.modeProvider = modeProvider;
    }

    @EventHandler
    public void onEnterStaffMode(EnterStaffModeEvent event) {
        Optional<SppPlayer> sppPlayer = playerManager.getOnlinePlayer(event.getPlayerUuid());
        if (!sppPlayer.isPresent()) {
            return;
        }

        Player player = sppPlayer.get().getPlayer();
        GeneralModeConfiguration modeConfiguration = modeProvider.getMode(player, event.getMode());
        setPlayerStaffState(sppPlayer.get(), modeConfiguration);

        messages.send(player, messages.modeStatus.replace("%status%", messages.enabled), messages.prefixGeneral);
    }

    @EventHandler
    public void onSwitchStaffMode(SwitchStaffModeEvent event) {
        Optional<SppPlayer> sppPlayer = playerManager.getOnlinePlayer(event.getPlayerUuid());
        if (!sppPlayer.isPresent()) {
            return;
        }

        Player player = sppPlayer.get().getPlayer();
        GeneralModeConfiguration modeConfiguration = modeProvider.getMode(player, event.getToMode());
        resetPlayer(player, event.getModeData());
        setPlayerStaffState(sppPlayer.get(), modeConfiguration);

        messages.send(player, "&eYou switched to staff mode &6" + modeConfiguration.getName(), messages.prefixGeneral);
    }

    private void setPlayerStaffState(SppPlayer sppPlayer, GeneralModeConfiguration modeConfiguration) {
        Player player = sppPlayer.getPlayer();
        staffModeItemsService.setStaffModeItems(player, modeConfiguration);
        player.setAllowFlight(modeConfiguration.isModeFlight());
        if (modeConfiguration.isModeCreative()) {
            sppPlayer.setGameMode(GameMode.CREATIVE);
        }
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        if (modeConfiguration.isNightVision()) {
            sppPlayer.turnNightVisionOn();
        }
    }

    private void resetPlayer(Player player, IModeData modeData) {
        JavaUtils.clearInventory(player);
        player.getInventory().setContents(modeData.getPlayerInventory());
        player.updateInventory();
        player.setExp(modeData.getXp());
        player.setAllowFlight(modeData.hasFlight());
        player.setGameMode(modeData.getGameMode());
        player.setFireTicks(modeData.getFireTicks());
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        modeData.getPotionEffects().forEach(player::addPotionEffect);
    }

}
