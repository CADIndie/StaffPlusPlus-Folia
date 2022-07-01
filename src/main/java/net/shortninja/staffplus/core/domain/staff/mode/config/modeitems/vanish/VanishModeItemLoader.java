package net.shortninja.staffplus.core.domain.staff.mode.config.modeitems.vanish;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigurationLoader;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.domain.staff.mode.config.ModeItemLoader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@IocBean
public class VanishModeItemLoader extends ModeItemLoader<VanishModeConfiguration> {
    public VanishModeItemLoader(IProtocolService protocolService, ConfigurationLoader configurationLoader) {
        super(protocolService, configurationLoader);
    }

    @Override
    protected String getModuleName() {
        return "vanish-module";
    }

    @Override
    protected VanishModeConfiguration load() {

        Material modeVanishTypeOff = Options.stringToMaterial(sanitize(staffModeModulesConfig.getString("modules.vanish-module.item-off")));
        short modeVanishDataOff = getMaterialData(staffModeModulesConfig.getString("modules.vanish-module.item-off"));
        String modeVanishName = staffModeModulesConfig.getString("modules.vanish-module.name");
        String commas = staffModeModulesConfig.getString("modules.vanish-module.lore");
        if (commas == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        List<String> modeVanishLore = new ArrayList<>(Arrays.asList(commas.split("\\s*,\\s*")));

        ItemStack modeVanishItemOff = Items.builder().setMaterial(modeVanishTypeOff).setData(modeVanishDataOff).setName(modeVanishName).setLore(modeVanishLore).build();
        modeVanishItemOff = protocolService.getVersionProtocol().addNbtString(modeVanishItemOff, getModuleName());

        VanishModeConfiguration modeItemConfiguration = new VanishModeConfiguration(protocolService, getModuleName(), modeVanishItemOff);
        return super.loadGeneralConfig(modeItemConfiguration);
    }
}
