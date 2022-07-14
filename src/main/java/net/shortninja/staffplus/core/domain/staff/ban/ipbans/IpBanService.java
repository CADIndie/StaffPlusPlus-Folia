package net.shortninja.staffplus.core.domain.staff.ban.ipbans;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.domain.staff.ban.ipbans.database.IpBanRepository;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.BanType;
import net.shortninja.staffplusplus.ban.IpBanEvent;
import net.shortninja.staffplusplus.ban.IpUnbanEvent;
import org.apache.commons.net.util.SubnetUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.Constants.CONSOLE_UUID;
import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;

@IocBean
public class IpBanService {

    private final IpBanRepository ipBanRepository;
    private final Options options;
    private final Messages messages;
    private final IpBanTemplateResolver ipBanTemplateResolver;

    public IpBanService(IpBanRepository ipBanRepository, Options options, Messages messages, IpBanTemplateResolver ipBanTemplateResolver) {
        this.ipBanRepository = ipBanRepository;
        this.options = options;
        this.messages = messages;
        this.ipBanTemplateResolver = ipBanTemplateResolver;
    }

    public void banIp(CommandSender issuer, String ipAddress, String template, boolean isSilent) {
        ban(issuer, ipAddress, template, null, isSilent);
    }

    public void tempBanIp(CommandSender issuer, String ipAddress, String template, Long durationInMillis, boolean isSilent) {
        ban(issuer, ipAddress, template, durationInMillis, isSilent);
    }

    private void ban(CommandSender issuer, String ipAddress, String template, Long durationInMillis, boolean isSilent) {
        List<IpBan> matchingIpBans = findMatchingIpBans(ipAddress);
        if (!matchingIpBans.isEmpty()) {
            throw new BusinessException("&cThis ip is already banned by the following rules: " + matchingIpBans.stream().map(IpBan::getIp).collect(Collectors.joining(" | ")), messages.prefixBans);
        }

        String issuerName = issuer instanceof Player ? issuer.getName() : "Console";
        UUID issuerUuid = issuer instanceof Player ? ((Player) issuer).getUniqueId() : CONSOLE_UUID;
        Long endDate = durationInMillis == null ? null : System.currentTimeMillis() + durationInMillis;
        String templateName = ipBanTemplateResolver.getTemplate(template, endDate == null ? BanType.PERM_BAN : BanType.TEMP_BAN).orElse(null);

        IpBan ipBan = new IpBan(ipAddress, endDate, issuerName, issuerUuid, options.serverName, isSilent, templateName);
        ipBan.setId(ipBanRepository.saveBan(ipBan));
        sendEvent(new IpBanEvent(ipBan, template));
    }

    public List<IpBan> findMatchingIpBans(String ipAddress) {
        List<IpBan> bannedIps = ipBanRepository.getBannedIps();
        Optional<IpBan> first = bannedIps.stream().filter(b -> b.getIp().equalsIgnoreCase(ipAddress)).findFirst();
        if (first.isPresent()) {
            return Collections.singletonList(first.get());
        }

        if (isCidr(ipAddress)) {
            return Collections.emptyList();
        }

        return bannedIps.stream()
            .filter(IpBan::isSubnet)
            .filter(i -> new SubnetUtils(i.getIp()).getInfo().isInRange(ipAddress))
            .collect(Collectors.toList());
    }

    private boolean isCidr(String ipAddress) {
        return ipAddress.contains("/");
    }

    public void unbanIp(CommandSender sender, String ipAddress, boolean silent) {
        IpBan ipBan = ipBanRepository.getActiveBannedRule(ipAddress).orElseThrow(() -> new BusinessException("No ipban found with rule: " + ipAddress, messages.prefixBans));
        ipBan.setSilentUnban(silent);

        ipBan.setUnbannedByName(sender instanceof Player ? sender.getName() : "CONSOLE");
        ipBan.setUnbannedByUuid(sender instanceof Player ? ((Player) sender).getUniqueId() : CONSOLE_UUID);
        ipBanRepository.deleteBan(ipBan);
        sendEvent(new IpUnbanEvent(ipBan));
    }

    public List<IpBan> getAllActiveBans() {
        return ipBanRepository.getBannedIps();
    }
}
