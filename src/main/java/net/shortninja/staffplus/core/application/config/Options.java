package net.shortninja.staffplus.core.application.config;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.authentication.AuthenticationConfiguration;
import net.shortninja.staffplus.core.authentication.AuthenticationConfigurationLoader;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.utils.Materials;
import net.shortninja.staffplus.core.domain.chat.blacklist.BlackListConfiguration;
import net.shortninja.staffplus.core.domain.chat.blacklist.BlackListConfigurationLoader;
import net.shortninja.staffplus.core.domain.staff.broadcast.config.BroadcastConfiguration;
import net.shortninja.staffplus.core.domain.staff.broadcast.config.BroadcastConfigurationLoader;
import net.shortninja.staffplus.core.domain.staff.examine.config.ExamineConfiguration;
import net.shortninja.staffplus.core.domain.staff.examine.config.ExamineModuleLoader;
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
import net.shortninja.staffplus.core.domain.staff.protect.config.ProtectConfiguration;
import net.shortninja.staffplus.core.domain.staff.protect.config.ProtectModuleLoader;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportingModuleLoader;
import net.shortninja.staffplus.core.domain.staff.teleport.config.LocationLoader;
import net.shortninja.staffplus.core.domain.staff.tracing.config.TraceConfiguration;
import net.shortninja.staffplus.core.domain.staff.tracing.config.TraceModuleLoader;
import net.shortninja.staffplus.core.domain.staff.warn.appeals.config.AppealConfiguration;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.config.WarningConfiguration;
import net.shortninja.staffplus.core.domain.synchronization.ServerSyncConfiguration;
import net.shortninja.staffplus.core.domain.synchronization.ServerSyncModuleLoader;
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
    public AuthenticationConfiguration authenticationConfiguration;
    public InfractionsConfiguration infractionsConfiguration;
    public InvestigationConfiguration investigationConfiguration;
    public ReportConfiguration reportConfiguration;
    public WarningConfiguration warningConfiguration;
    public AppealConfiguration appealConfiguration;
    public BlackListConfiguration blackListConfiguration;
    public TraceConfiguration traceConfiguration;
    public BroadcastConfiguration broadcastConfiguration;
    public ProtectConfiguration protectConfiguration;
    public KickConfiguration kickConfiguration;
    public ExamineConfiguration examineConfiguration;
    public Map<String, GeneralModeConfiguration> modeConfigurations;
    public ServerSyncConfiguration serverSyncConfiguration;
    public StaffItemsConfiguration staffItemsConfiguration;

    /*
     * Custom
     */
    public List<CustomModuleConfiguration> customModuleConfigurations;
    /*
     * Permissions
     */
    public String permissionFreeze;
    public String permissionFreezeBypass;
    public String permissionMember;
    public String ipHidePerm;
    public String permissionCounterGuiShowVanish;

    private final AuthenticationConfigurationLoader authenticationConfigurationLoader;
    private final InfractionsModuleLoader infractionsModuleLoader;
    private final ReportingModuleLoader reportingModuleLoader;
    private final BlackListConfigurationLoader blackListConfigurationLoader;
    private final TraceModuleLoader traceModuleLoader;
    private final BroadcastConfigurationLoader broadcastConfigurationLoader;
    private final ProtectModuleLoader protectModuleLoader;
    private final KickModuleLoader kickModuleLoader;
    private final ExamineModuleLoader examineModuleLoader;
    private final StaffModesLoader staffModesLoader;
    private final ServerSyncModuleLoader serverSyncModuleLoader;
    private final InvestigationModuleLoader investigationModuleLoader;
    private final StaffCustomItemsLoader staffCustomItemsLoader;
    private final StaffItemsLoader staffItemsLoader;

    public Options(AuthenticationConfigurationLoader authenticationConfigurationLoader,
                   InfractionsModuleLoader infractionsModuleLoader,
                   ReportingModuleLoader reportingModuleLoader,
                   BlackListConfigurationLoader blackListConfigurationLoader,
                   TraceModuleLoader traceModuleLoader,
                   BroadcastConfigurationLoader broadcastConfigurationLoader,
                   ProtectModuleLoader protectModuleLoader,
                   KickModuleLoader kickModuleLoader,
                   ExamineModuleLoader examineModuleLoader,
                   StaffModesLoader staffModesLoader,
                   ServerSyncModuleLoader serverSyncModuleLoader,
                   InvestigationModuleLoader investigationModuleLoader,
                   StaffCustomItemsLoader staffCustomItemsLoader,
                   StaffItemsLoader staffItemsLoader,
                   WarningConfiguration warningConfiguration,
                   AppealConfiguration appealConfiguration) {
        this.authenticationConfigurationLoader = authenticationConfigurationLoader;
        this.infractionsModuleLoader = infractionsModuleLoader;
        this.reportingModuleLoader = reportingModuleLoader;
        this.blackListConfigurationLoader = blackListConfigurationLoader;
        this.traceModuleLoader = traceModuleLoader;
        this.broadcastConfigurationLoader = broadcastConfigurationLoader;
        this.protectModuleLoader = protectModuleLoader;
        this.kickModuleLoader = kickModuleLoader;
        this.examineModuleLoader = examineModuleLoader;
        this.staffModesLoader = staffModesLoader;
        this.serverSyncModuleLoader = serverSyncModuleLoader;
        this.investigationModuleLoader = investigationModuleLoader;
        this.staffCustomItemsLoader = staffCustomItemsLoader;
        this.staffItemsLoader = staffItemsLoader;
        this.warningConfiguration = warningConfiguration;
        this.appealConfiguration = appealConfiguration;
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
        authenticationConfiguration = this.authenticationConfigurationLoader.loadConfig();
        infractionsConfiguration = this.infractionsModuleLoader.loadConfig();
        reportConfiguration = this.reportingModuleLoader.loadConfig();
        blackListConfiguration = this.blackListConfigurationLoader.loadConfig();
        traceConfiguration = this.traceModuleLoader.loadConfig();
        broadcastConfiguration = this.broadcastConfigurationLoader.loadConfig();
        protectConfiguration = this.protectModuleLoader.loadConfig();
        kickConfiguration = this.kickModuleLoader.loadConfig();
        examineConfiguration = this.examineModuleLoader.loadConfig();
        modeConfigurations = this.staffModesLoader.loadConfig();
        serverSyncConfiguration = this.serverSyncModuleLoader.loadConfig();
        investigationConfiguration = this.investigationModuleLoader.loadConfig();
        customModuleConfigurations = this.staffCustomItemsLoader.loadConfig();
        staffItemsConfiguration = this.staffItemsLoader.loadConfig();

        /*
         * Permissions
         */
        permissionFreeze = permissionsConfig.getString("freeze");
        permissionFreezeBypass = permissionsConfig.getString("freeze-bypass");
        permissionMember = permissionsConfig.getString("member");
        ipHidePerm = permissionsConfig.getString("ipPerm");
        permissionCounterGuiShowVanish = permissionsConfig.getString("counter-show-vanished");
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