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
import net.shortninja.staffplus.core.domain.player.PlayerManager;
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
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.application.SppInteractorBuilder.fromSender;
import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.BOTH;

@Command(
    command = "commands:reportPlayer",
    permissions = "permissions:report",
    description = "Sends a report with the given player and reason.",
    usage = "[player] [reason]",
    playerRetrievalStrategy = BOTH
)
@IocBean(conditionalOnProperty = "reports-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class ReportPlayerCmd extends AbstractCmd {
    private static final CulpritFilterPredicate culpritFilterPredicate = new CulpritFilterPredicate(true);
    private final List<ReportTypeConfiguration> reportTypeConfigurations;
    private final List<ReportReasonConfiguration> reportReasonConfigurations;

    private final Options options;
    private final ReportService reportService;
    private final PlayerManager playerManager;
    private final BukkitUtils bukkitUtils;

    public ReportPlayerCmd(Messages messages, Options options, ReportService reportService, PlayerManager playerManager, CommandService commandService, PermissionHandler permissionHandler, BukkitUtils bukkitUtils) {
        super(messages, permissionHandler, commandService);
        this.options = options;
        this.reportService = reportService;
        this.playerManager = playerManager;
        reportTypeConfigurations = options.reportConfiguration.getReportTypeConfigurations(culpritFilterPredicate);
        reportReasonConfigurations = options.reportConfiguration.getReportReasonConfigurations(culpritFilterPredicate);
        this.bukkitUtils = bukkitUtils;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        String reason = options.reportConfiguration.isFixedReasonCulprit() ? null : JavaUtils.compileWords(args, 1);

        if (reportTypeConfigurations.isEmpty() && reportReasonConfigurations.isEmpty()) {
            bukkitUtils.runTaskAsync(sender, () -> reportService.sendReport(fromSender(sender), player, reason));
            return true;
        }

        if (!reportTypeConfigurations.isEmpty()) {
            new ReportTypeSelectGui((Player) sender, reason, player, reportTypeConfigurations, reportReasonConfigurations).show((Player) sender);
            return true;
        }

        new ReportReasonSelectGui((Player) sender, player, null, reportReasonConfigurations).show((Player) sender);
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        if (reportReasonConfigurations.isEmpty()) {
            return 2;
        }
        return 1;
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return false;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.of(args[0]);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return playerManager.getAllPlayerNames().stream()
                .filter(s -> args[0].isEmpty() || s.contains(args[0]))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}