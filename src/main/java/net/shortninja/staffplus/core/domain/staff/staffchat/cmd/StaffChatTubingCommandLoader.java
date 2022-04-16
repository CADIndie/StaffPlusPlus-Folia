package net.shortninja.staffplus.core.domain.staff.staffchat.cmd;

import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcioc.TubingConfiguration;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.domain.player.settings.PlayerSettingsRepository;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.chat.ChatInterceptor;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChannelConfiguration;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChatInterceptor;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatServiceImpl;
import net.shortninja.staffplus.core.domain.staff.staffchat.config.StaffChatConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@TubingConfiguration
public class StaffChatTubingCommandLoader {

    @IocMultiProvider(SppCommand.class)
    public static List<SppCommand> loadCommands(Messages messages,
                                                StaffChatConfiguration staffChatConfiguration,
                                                OnlineSessionsManager sessionManager,
                                                StaffChatServiceImpl staffChatService,
                                                CommandService commandService,
                                                PermissionHandler permissionHandler,
                                                PlayerSettingsRepository playerSettingsRepository,
                                                BukkitUtils bukkitUtils) {
        List<SppCommand> commands = new ArrayList<>();
        List<StaffChatChannelConfiguration> channelConfigurations = staffChatConfiguration.getChannelConfigurations();
        for (StaffChatChannelConfiguration channelConfiguration : channelConfigurations) {
            StaffChatChannelCmd staffChatChannelCmd = new StaffChatChannelCmd(messages, sessionManager, staffChatService, commandService, channelConfiguration, permissionHandler, bukkitUtils);
            staffChatChannelCmd.setReplaceDoubleQoutesEnabled(false);
            commands.add(staffChatChannelCmd);
            commands.add(new StaffChatMuteChannelCmd(messages, commandService, playerSettingsRepository, channelConfiguration, permissionHandler, bukkitUtils));
            commands.add(new StaffChatSoundChannelCmd(messages, commandService, playerSettingsRepository, channelConfiguration, permissionHandler, bukkitUtils));
        }
        return commands;
    }

    @IocMultiProvider(ChatInterceptor.class)
    public static List<ChatInterceptor> loadChatInterceptors(StaffChatConfiguration staffChatConfiguration, OnlineSessionsManager sessionManager, PermissionHandler permissionHandler, StaffChatServiceImpl staffChatService) {
        return staffChatConfiguration.getChannelConfigurations().stream()
            .map(c -> new StaffChatChatInterceptor(staffChatService, permissionHandler, sessionManager, c, staffChatConfiguration))
            .collect(Collectors.toList());
    }
}