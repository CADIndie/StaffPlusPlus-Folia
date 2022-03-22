package net.shortninja.staffplus.core.application.database;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.Migrations;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import be.garagepoort.mcsqlmigrations.helpers.QueryBuilderFactory;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.domain.actions.database.StoredCommandRepository;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.player.ip.database.PlayerIpRepository;

@IocBean(priority = true)
public class DatabaseInitializer {

    // The parameters injected here are to ensure the migrations can use them, do not remove them
    public DatabaseInitializer(SqlConnectionProvider sqlConnectionProvider,
                               QueryBuilderFactory queryBuilderFactory,
                               PlayerIpRepository playerIpRepository,
                               Options options,
                               StoredCommandRepository storedCommandRepository,
                               PlayerManager playerManager) {
        new Migrations(sqlConnectionProvider).run("migrations");
    }

}
