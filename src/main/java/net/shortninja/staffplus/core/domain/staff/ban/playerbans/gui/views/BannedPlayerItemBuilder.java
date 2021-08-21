package net.shortninja.staffplus.core.domain.staff.ban.playerbans.gui.views;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.common.gui.LoreBuilder;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.Ban;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionType;
import net.shortninja.staffplus.core.domain.staff.infractions.gui.views.InfractionGuiProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@IocBean
@IocMultiProvider(InfractionGuiProvider.class)
public class BannedPlayerItemBuilder implements InfractionGuiProvider<Ban> {

    private final IProtocolService protocolService;
    private final Options options;

    public BannedPlayerItemBuilder(IProtocolService protocolService, Options options) {
        this.protocolService = protocolService;
        this.options = options;
    }


    public ItemStack build(Ban ban) {
        String time = getTimeString(ban.getCreationDate());

        List<String> lore = LoreBuilder.builder("&b", "&6")
            .addItem("Id", String.valueOf(ban.getId()))
            .addItem("Server", ban.getServerName(), options.serverSyncConfiguration.banSyncEnabled)
            .addItem("Banned player", ban.getTargetName())
            .addItem("Issuer", ban.getIssuerName())
            .addItem("Issued on", time)
            .addItem("Ended on", () -> getTimeString(ban.getEndDate()), ban.hasEnded())
            .addIndented("Reason", ban.getReason())
            .addDuration("Time Left", ban.getHumanReadableDuration(), ban.getEndTimestamp() != null)
            .addItem("Permanent ban", ban.getEndTimestamp() == null)
            .build();

        ItemStack item = Items.builder()
            .setMaterial(Material.PLAYER_HEAD)
            .setName("&cBan")
            .addLore(lore)
            .build();

        return protocolService.getVersionProtocol().addNbtString(item, String.valueOf(ban.getId()));
    }

    @NotNull
    private String getTimeString(ZonedDateTime date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
        return localDateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofPattern(options.timestampFormat));
    }

    @Override
    public InfractionType getType() {
        return InfractionType.BAN;
    }

    @Override
    public ItemStack getMenuItem(Ban ban) {
        ItemStack itemStack = build(ban);
        itemStack.setType(options.infractionsConfiguration.getBansGuiItem());
        return itemStack;
    }
}
