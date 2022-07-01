package net.shortninja.staffplus.core.domain.staff.mode.config.modeitems.cps;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigurationLoader;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.domain.staff.mode.config.ModeItemLoader;

@IocBean
public class CpsModeItemLoader extends ModeItemLoader<CpsModeConfiguration> {
    public CpsModeItemLoader(IProtocolService protocolService, ConfigurationLoader configurationLoader) {
        super(protocolService, configurationLoader);
    }

    @Override
    protected String getModuleName() {
        return "cps-module";
    }

    @Override
    protected CpsModeConfiguration load() {
        CpsModeConfiguration modeItemConfiguration = new CpsModeConfiguration(getModuleName(),
            staffModeModulesConfig.getInt("modules.cps-module.time") * 20,
            staffModeModulesConfig.getInt("modules.cps-module.max")
        );
        return super.loadGeneralConfig(modeItemConfiguration);
    }
}
