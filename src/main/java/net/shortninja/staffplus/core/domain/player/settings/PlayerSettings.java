package net.shortninja.staffplus.core.domain.player.settings;

import net.shortninja.staffplus.core.domain.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplusplus.alerts.AlertType;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.Material;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PlayerSettings {

    private final UUID uuid;
    private final Set<AlertType> alertOptions;
    private final Set<String> mutedStaffChatChannels;
    private final Set<String> soundDisabledStaffChatChannels;
    private String name;
    private Material glassColor;
    private VanishType vanishType;
    private boolean inStaffMode;
    private final Map<String, Boolean> nightVision;
    private String modeName;

    public PlayerSettings(UUID uuid,
                          String name,
                          Material glassColor,
                          Set<AlertType> alertOptions,
                          VanishType vanishType,
                          boolean inStaffMode,
                          String modeName,
                          Set<String> mutedStaffChatChannels,
                          Set<String> soundDisabledStaffChatChannels,
                          Map<String, Boolean> nightVision) {
        this.uuid = uuid;
        this.name = name;
        this.glassColor = glassColor;
        this.alertOptions = alertOptions;
        this.vanishType = vanishType;
        this.inStaffMode = inStaffMode;
        this.modeName = modeName;
        this.mutedStaffChatChannels = mutedStaffChatChannels;
        this.soundDisabledStaffChatChannels = soundDisabledStaffChatChannels;
        this.nightVision = nightVision;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Material getGlassColor() {
        return glassColor;
    }

    public Set<AlertType> getAlertOptions() {
        return alertOptions;
    }

    public VanishType getVanishType() {
        return vanishType;
    }

    public boolean isInStaffMode() {
        return inStaffMode;
    }

    public Optional<String> getModeName() {
        return Optional.ofNullable(modeName);
    }

    public Set<String> getMutedStaffChatChannels() {
        return mutedStaffChatChannels;
    }

    public Set<String> getSoundDisabledStaffChatChannels() {
        return soundDisabledStaffChatChannels;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVanished() {
        return vanishType == VanishType.TOTAL || vanishType == VanishType.PLAYER;
    }

    public void setVanishType(VanishType vanishType) {
        this.vanishType = vanishType;
    }

    public void setGlassColor(Material glassColor) {
        this.glassColor = glassColor;
    }

    public boolean isStaffChatMuted(String channelName) {
        return mutedStaffChatChannels.contains(channelName);
    }

    public boolean isStaffChatSoundEnabled(String channelName) {
        return !soundDisabledStaffChatChannels.contains(channelName);
    }

    public void setModeConfiguration(GeneralModeConfiguration modeConfiguration) {
        this.modeName = modeConfiguration.getName();
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public void setInStaffMode(boolean staffMode) {
        this.inStaffMode = staffMode;
    }

    public void setStaffChatMuted(String name, boolean muted) {
        if (muted) {
            mutedStaffChatChannels.add(name);
        } else {
            mutedStaffChatChannels.remove(name);
        }
    }

    public void setStaffChatNotificationSound(String name, boolean enabled) {
        if (enabled) {
            soundDisabledStaffChatChannels.remove(name);
        } else {
            soundDisabledStaffChatChannels.add(name);
        }
    }

    public void setAlertOption(AlertType alertType, boolean isEnabled) {
        if (isEnabled) {
            alertOptions.add(alertType);
        } else {
            alertOptions.remove(alertType);
        }
    }

    public boolean isNightVisionEnabled() {
        return nightVision.values().stream().anyMatch(n -> n);
    }

    public Map<String, Boolean> getNightVision() {
        return nightVision;
    }

    public void setNightVision(String initiator, boolean nightVision) {
        this.nightVision.put(initiator, nightVision);
    }
}
