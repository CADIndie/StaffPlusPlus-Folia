package net.shortninja.staffplus.core.domain.staff.chests.config;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;

@IocBean
public class EnderchestsConfiguration {

    @ConfigProperty("permissions:enderchests.view.online")
    public String permissionViewOnline;
    @ConfigProperty("permissions:enderchests.view.offline")
    public String permissionViewOffline;
    @ConfigProperty("permissions:enderchests.interact")
    public String permissionInteract;

}
