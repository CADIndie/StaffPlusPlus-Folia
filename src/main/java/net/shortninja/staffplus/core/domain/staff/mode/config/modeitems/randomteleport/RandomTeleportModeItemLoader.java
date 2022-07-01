package net.shortninja.staffplus.core.domain.staff.mode.config.modeitems.randomteleport;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigurationLoader;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.domain.staff.mode.config.ModeItemLoader;

@IocBean
public class RandomTeleportModeItemLoader extends ModeItemLoader<RandomTeleportModeConfiguration> {
    public RandomTeleportModeItemLoader(IProtocolService protocolService, ConfigurationLoader configurationLoader) {
        super(protocolService, configurationLoader);
    }

    @Override
    protected String getModuleName() {
        return "random-teleport-module";
    }

    @Override
    protected RandomTeleportModeConfiguration load() {
        RandomTeleportModeConfiguration modeItemConfiguration = new RandomTeleportModeConfiguration(getModuleName(), staffModeModulesConfig.getBoolean("modules.random-teleport-module.random"));
        return super.loadGeneralConfig(modeItemConfiguration);
    }
}
