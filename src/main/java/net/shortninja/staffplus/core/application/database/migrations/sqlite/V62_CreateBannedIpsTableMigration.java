package net.shortninja.staffplus.core.application.database.migrations.sqlite;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

@IocBean(conditionalOnProperty = "storage.type=sqlite")
@IocMultiProvider(Migration.class)
public class V62_CreateBannedIpsTableMigration implements Migration {
    @Override
    public String getStatement() {
        return "CREATE TABLE IF NOT EXISTS sp_banned_ips (  " +
            "ID integer PRIMARY KEY,  " +
            "ip VARCHAR(15) NOT NULL,  " +
            "issuer_name VARCHAR(36) NOT NULL,  " +
            "issuer_uuid VARCHAR(36) NOT NULL,  " +
            "unbanned_by_uuid VARCHAR(36) NULL,  " +
            "unbanned_by_name VARCHAR(36) NULL,  " +
            "creation_timestamp BIGINT NOT NULL, " +
            "server_name VARCHAR(255) NULL, " +
            "silent_ban boolean NOT NULL, " +
            "silent_unban boolean NULL, " +
            "end_timestamp BIGINT NULL);";
    }

    @Override
    public int getVersion() {
        return 62;
    }
}
