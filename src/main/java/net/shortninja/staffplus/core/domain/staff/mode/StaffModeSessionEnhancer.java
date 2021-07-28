package net.shortninja.staffplus.core.domain.staff.mode;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.session.DataFile;
import net.shortninja.staffplus.core.application.session.PlayerSession;
import net.shortninja.staffplus.core.application.session.SessionEnhancer;
import net.shortninja.staffplus.core.application.session.database.SessionEntity;
import net.shortninja.staffplus.core.application.session.database.SessionsRepository;
import net.shortninja.staffplus.core.domain.staff.mode.config.GeneralModeConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;

@IocBean
@IocMultiProvider(SessionEnhancer.class)
public class StaffModeSessionEnhancer implements SessionEnhancer {
    private final SessionsRepository sessionsRepository;
    private final Options options;
    private final FileConfiguration dataFileConfiguration;
    private final ModeProvider modeProvider;

    public StaffModeSessionEnhancer(SessionsRepository sessionsRepository, Options options, DataFile dataFile, ModeProvider modeProvider) {
        this.sessionsRepository = sessionsRepository;
        this.options = options;
        this.dataFileConfiguration = dataFile.getConfiguration();
        this.modeProvider = modeProvider;
    }

    @Override
    public void enhance(PlayerSession playerSession) {
        boolean staffMode = dataFileConfiguration.getBoolean(playerSession.getUuid() + ".staff-mode", false);
        String staffModeName = dataFileConfiguration.getString(playerSession.getUuid() + ".staff-mode-name", null);
        playerSession.setInStaffMode(staffMode);

        if (options.serverSyncConfiguration.staffModeSyncEnabled) {
            Optional<SessionEntity> sessionEntity = sessionsRepository.findSession(playerSession.getUuid());
            if (sessionEntity.isPresent()) {
                playerSession.setInStaffMode(sessionEntity.get().getStaffMode());
                staffModeName = sessionEntity.get().getStaffModeName();
            }
        }

        if (playerSession.getPlayer().isPresent()) {
            Optional<GeneralModeConfiguration> modeConfig = modeProvider.findMode(playerSession.getPlayer().get(), staffModeName);
            playerSession.setModeConfiguration(modeConfig.orElse(null));

        }
    }
}
