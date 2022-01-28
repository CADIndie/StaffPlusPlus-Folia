package net.shortninja.staffplus.core.domain.staff.reporting.gui.cmd;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.staff.reporting.ReportService;
import net.shortninja.staffplus.core.domain.staff.reporting.config.CulpritFilterPredicate;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportReasonConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportTypeConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.ReportReasonSelectGui;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.ReportTypeSelectGui;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.shortninja.staffplus.core.application.SppInteractorBuilder.fromSender;

@Command(
    command = "commands:report",
    permissions = "permissions:report",
    description = "Sends a report without a specific player.",
    usage = "[reason]"
)
@IocBean(conditionalOnProperty = "reports-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class ReportCmd extends AbstractCmd {
    private static final CulpritFilterPredicate culpritFilterPredicate = new CulpritFilterPredicate(false);
    private final Options options;
    private final ReportService reportService;
    private final List<ReportTypeConfiguration> reportTypeConfigurations;
    private final List<ReportReasonConfiguration> reportReasonConfigurations;
    private final BukkitUtils bukkitUtils;

    public ReportCmd(Messages messages,
                     Options options,
                     ReportService reportService,
                     CommandService commandService,
                     PermissionHandler permissionHandler, BukkitUtils bukkitUtils) {
        super(messages, permissionHandler, commandService);
        this.options = options;
        this.reportService = reportService;
        reportTypeConfigurations = options.reportConfiguration.getReportTypeConfigurations(culpritFilterPredicate);
        reportReasonConfigurations = options.reportConfiguration.getReportReasonConfigurations(culpritFilterPredicate);
        this.bukkitUtils = bukkitUtils;
    }


    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        String reason = options.reportConfiguration.isFixedReason() ? null : JavaUtils.compileWords(args, 0);

        if (reportTypeConfigurations.isEmpty() && reportReasonConfigurations.isEmpty()) {
            bukkitUtils.runTaskAsync(sender, () -> reportService.sendReport(fromSender(sender), reason));
            return true;
        }

        if (!reportTypeConfigurations.isEmpty()) {
            new ReportTypeSelectGui((Player) sender, reason, reportTypeConfigurations, reportReasonConfigurations).show((Player) sender);
            return true;
        }

        new ReportReasonSelectGui((Player) sender, null, reportReasonConfigurations).show((Player) sender);
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        if (reportReasonConfigurations.isEmpty()) {
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return false;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}