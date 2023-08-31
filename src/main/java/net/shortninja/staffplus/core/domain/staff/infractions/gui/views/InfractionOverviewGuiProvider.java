package net.shortninja.staffplus.core.domain.staff.infractions.gui.views;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionOverview;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@IocBean
public class InfractionOverviewGuiProvider {

    public ItemStack build(InfractionOverview infractionOverview) {
        List<String> lore = new ArrayList<>();
        lore.add("&bTotal: &6" + infractionOverview.getTotal());
        infractionOverview.getInfractions().forEach((type, count) -> lore.add("&b" + type.getGuiTitle() + ": &6" + count));
        lore.add("");
        lore.addAll(infractionOverview.getAdditionalInfo());

        return Items.editor(Items.createSkull(infractionOverview.getSppPlayer().getUsername())).setAmount(1)
            .setName(infractionOverview.getSppPlayer().getUsername())
            .addLore(lore)
            .build();
    }
}
