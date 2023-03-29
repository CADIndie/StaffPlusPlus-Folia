package net.shortninja.staffplus.core.domain.staff.mode.config.modeitems.examine;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperties;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import be.garagepoort.mcioc.configuration.ConfigTransformer;
import net.shortninja.staffplus.core.domain.staff.mode.config.ModeItemConfiguration;

@IocBean
@ConfigProperties("staffmode-modules:modules.examine-module")
public class ExamineModeConfiguration extends ModeItemConfiguration {

    @ConfigProperty("item.name")
    private String modeExamineTitle;

    @ConfigProperty("info-line.food")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineFood;

    @ConfigProperty("info-line.food")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineIp;

    @ConfigProperty("info-line.gamemode")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineGamemode;

    @ConfigProperty("info-line.infractions")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineInfractions;

    @ConfigProperty("info-line.location")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineLocation;

    @ConfigProperty("info-line.notes")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineNotes;

    @ConfigProperty("info-line.freeze")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineFreeze;

    @ConfigProperty("info-line.warn")
    @ConfigTransformer(ExamineModeItemLocationConfigTransformer.class)
    private int modeExamineWarn;

    public String getModeExamineTitle() {
        return modeExamineTitle;
    }

    public int getModeExamineFood() {
        return modeExamineFood;
    }

    public int getModeExamineIp() {
        return modeExamineIp;
    }

    public int getModeExamineGamemode() {
        return modeExamineGamemode;
    }

    public int getModeExamineInfractions() {
        return modeExamineInfractions;
    }

    public int getModeExamineLocation() {
        return modeExamineLocation;
    }

    public int getModeExamineNotes() {
        return modeExamineNotes;
    }

    public int getModeExamineFreeze() {
        return modeExamineFreeze;
    }

    public int getModeExamineWarn() {
        return modeExamineWarn;
    }

    @Override
    public String getIdentifier() {
        return "examine-module";
    }
}
