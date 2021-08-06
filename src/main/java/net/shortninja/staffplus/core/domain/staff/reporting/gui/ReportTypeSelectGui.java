package net.shortninja.staffplus.core.domain.staff.reporting.gui;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.gui.SimpleItemBuilder;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.staff.reporting.ReportService;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportReasonConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportTypeConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.getInventorySize;

public class ReportTypeSelectGui extends AbstractGui {

    private final Player staff;
    private final String reason;
    private final SppPlayer targetPlayer;
    private final List<ReportTypeConfiguration> reportTypeConfigurations;
    private final List<ReportReasonConfiguration> reportReasonConfigurations;
    private final ReportService reportService = StaffPlus.get().getIocContainer().get(ReportService.class);
    private final BukkitUtils bukkitUtils = StaffPlus.get().getIocContainer().get(BukkitUtils.class);

    public ReportTypeSelectGui(Player staff, String reason, SppPlayer targetPlayer, List<ReportTypeConfiguration> reportTypeConfigurations, List<ReportReasonConfiguration> reportReasonConfigurations) {
        super(getInventorySize(reportTypeConfigurations.size()), "Select the type of report");
        this.staff = staff;
        this.reason = reason;
        this.targetPlayer = targetPlayer;
        this.reportTypeConfigurations = reportTypeConfigurations;
        this.reportReasonConfigurations = reportReasonConfigurations;
    }

    public ReportTypeSelectGui(Player staff, String reason, List<ReportTypeConfiguration> reportTypeConfigurations, List<ReportReasonConfiguration> reportReasonConfigurations) {
        super(getInventorySize(reportTypeConfigurations.size()), "Select the type of report");
        this.staff = staff;
        this.reason = reason;
        this.reportTypeConfigurations = reportTypeConfigurations;
        this.reportReasonConfigurations = reportReasonConfigurations;
        this.targetPlayer = null;
    }

    @Override
    public void buildGui() {
        IAction selectAction = getSelectAction();
        int count = 0;
        for (ReportTypeConfiguration r : reportTypeConfigurations) {
            setItem(count, SimpleItemBuilder.build(r.getType(), r.getLore(), r.getMaterial()), selectAction);
            count++;
        }
    }

    private IAction getSelectAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                String reportType = StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().getNbtString(item);
                if (StringUtils.isEmpty(reason)) {
                    new ReportReasonSelectGui(staff, targetPlayer, reportType, reportReasonConfigurations).show(staff);
                } else {
                    if (targetPlayer == null) {
                        bukkitUtils.runTaskAsync(player, () -> reportService.sendReport(staff, reason, reportType));
                    } else {
                        bukkitUtils.runTaskAsync(player, () -> reportService.sendReport(staff, targetPlayer, reason, reportType));
                    }
                }
            }

            @Override
            public boolean shouldClose(Player player) {
                return !StringUtils.isEmpty(reason);
            }
        };
    }
}