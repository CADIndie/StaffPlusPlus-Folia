package net.shortninja.staffplus.core.application.config;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.utils.Materials;
import net.shortninja.staffplus.core.domain.staff.broadcast.config.BroadcastConfiguration;
import net.shortninja.staffplus.core.domain.staff.broadcast.config.BroadcastConfigurationLoader;
import net.shortninja.staffplus.core.domain.staff.infractions.config.InfractionsConfiguration;
import net.shortninja.staffplus.core.domain.staff.infractions.config.InfractionsModuleLoader;
import net.shortninja.staffplus.core.domain.staff.investigate.config.InvestigationConfiguration;
import net.shortninja.staffplus.core.domain.staff.investigate.config.InvestigationModuleLoader;
import net.shortninja.staffplus.core.domain.staff.kick.config.KickConfiguration;
import net.shortninja.staffplus.core.domain.staff.kick.config.KickModuleLoader;
import net.shortninja.staffplus.core.domain.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplus.core.domain.staff.mode.config.StaffCustomItemsLoader;
import net.shortninja.staffplus.core.domain.staff.mode.config.StaffItemsConfiguration;
import net.shortninja.staffplus.core.domain.staff.mode.config.StaffItemsLoader;
import net.shortninja.staffplus.core.domain.staff.mode.config.StaffModesLoader;
import net.shortninja.staffplus.core.domain.staff.mode.item.CustomModuleConfiguration;
import net.shortninja.staffplus.core.domain.staff.teleport.config.LocationLoader;
import net.shortninja.staffplus.core.domain.staff.tracing.config.TraceConfiguration;
import net.shortninja.staffplus.core.domain.staff.tracing.config.TraceModuleLoader;
import net.shortninja.staffplus.core.domain.staff.warn.appeals.WarningAppealConfiguration;
import net.shortninja.staffplus.core.domain.synchronization.ServerSyncConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//TODO: replace this with something that isn't horribly coupled...
@IocBean
public class Options {

    public List<String> blockedCommands;
    public List<String> blockedModeCommands;
    public String glassTitle;

    public String serverName;
    public String mainWorld;
    public String timestampFormat;
    public int autoSave;
    public boolean offlinePlayersModeEnabled;

    public Map<String, Location> locations;
    public InfractionsConfiguration infractionsConfiguration;
    public InvestigationConfiguration investigationConfiguration;
    public WarningAppealConfiguration warningAppealConfiguration;
    public TraceConfiguration traceConfiguration;
    public BroadcastConfiguration broadcastConfiguration;
    public KickConfiguration kickConfiguration;
    public Map<String, GeneralModeConfiguration> modeConfigurations;
    public final ServerSyncConfiguration serverSyncConfiguration;
    public StaffItemsConfiguration staffItemsConfiguration;

    /*
     * Custom
     */
    public List<CustomModuleConfiguration> customModuleConfigurations;
    /*
     * Permissions
     */
    public String permissionMember;

    private final InfractionsModuleLoader infractionsModuleLoader;
    private final TraceModuleLoader traceModuleLoader;
    private final BroadcastConfigurationLoader broadcastConfigurationLoader;
    private final KickModuleLoader kickModuleLoader;
    private final StaffModesLoader staffModesLoader;
    private final InvestigationModuleLoader investigationModuleLoader;
    private final StaffCustomItemsLoader staffCustomItemsLoader;
    private final StaffItemsLoader staffItemsLoader;

    @ConfigProperty("offline-player-cache")
    public boolean offlinePlayerCache;

    public Options(InfractionsModuleLoader infractionsModuleLoader,
                   TraceModuleLoader traceModuleLoader,
                   BroadcastConfigurationLoader broadcastConfigurationLoader,
                   KickModuleLoader kickModuleLoader,
                   StaffModesLoader staffModesLoader,
                   InvestigationModuleLoader investigationModuleLoader,
                   StaffCustomItemsLoader staffCustomItemsLoader,
                   StaffItemsLoader staffItemsLoader,
                   WarningAppealConfiguration warningAppealConfiguration,
                   ServerSyncConfiguration serverSyncConfiguration) {
        this.infractionsModuleLoader = infractionsModuleLoader;
        this.traceModuleLoader = traceModuleLoader;
        this.broadcastConfigurationLoader = broadcastConfigurationLoader;
        this.kickModuleLoader = kickModuleLoader;
        this.staffModesLoader = staffModesLoader;
        this.investigationModuleLoader = investigationModuleLoader;
        this.staffCustomItemsLoader = staffCustomItemsLoader;
        this.staffItemsLoader = staffItemsLoader;
        this.warningAppealConfiguration = warningAppealConfiguration;
        this.serverSyncConfiguration = serverSyncConfiguration;
        reload();
    }

    public void reload() {
        FileConfiguration defaultConfig = StaffPlus.get().getFileConfigurations().get("config");
        FileConfiguration permissionsConfig = StaffPlus.get().getFileConfigurations().get("permissions");

        String commas1 = defaultConfig.getString("blocked-commands", "");
        if (commas1 == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        blockedCommands = new ArrayList<>(Arrays.asList(commas1.split("\\s*,\\s*")));
        String commas = defaultConfig.getString("blocked-mode-commands", "");
        if (commas == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        blockedModeCommands = new ArrayList<>(Arrays.asList(commas.split("\\s*,\\s*")));
        glassTitle = defaultConfig.getString("glass-title");

        serverName = defaultConfig.getString("server-name");
        mainWorld = defaultConfig.getString("main-world");
        timestampFormat = defaultConfig.getString("timestamp-format");
        autoSave = defaultConfig.getInt("auto-save");
        offlinePlayersModeEnabled = defaultConfig.getBoolean("offline-players-mode");

        locations = new LocationLoader().loadConfig();
        infractionsConfiguration = this.infractionsModuleLoader.loadConfig();
        traceConfiguration = this.traceModuleLoader.loadConfig();
        broadcastConfiguration = this.broadcastConfigurationLoader.loadConfig();
        kickConfiguration = this.kickModuleLoader.loadConfig();
        modeConfigurations = this.staffModesLoader.loadConfig();
        investigationConfiguration = this.investigationModuleLoader.loadConfig();
        customModuleConfigurations = this.staffCustomItemsLoader.loadConfig();
        staffItemsConfiguration = this.staffItemsLoader.loadConfig();

        /*
         * Permissions
         */
        permissionMember = permissionsConfig.getString("member");
    }


    public static String getMaterial(String current) {
        switch (current) {
            case "HEAD":
                return Materials.valueOf("HEAD").getName();
            case "SPAWNER":
                return Materials.valueOf("SPAWNER").getName();
            case "ENDEREYE":
                return Materials.valueOf("ENDEREYE").getName();
            case "CLOCK":
                return Materials.valueOf("CLOCK").getName();
            case "LEAD":
                return Materials.valueOf("LEAD").getName();
            case "INK":
                return Materials.valueOf("INK").getName();
            default:
                return current;

        }

    }

    public static Material stringToMaterial(String string) {
        Material sound = Material.STONE;

        boolean isValid = JavaUtils.isValidEnum(Material.class, getMaterial(string));
        if (!isValid) {
            Bukkit.getLogger().severe("Invalid material type '" + string + "'!");
        } else
            sound = Material.valueOf(getMaterial(string));

        return sound;
    }
}