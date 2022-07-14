package net.shortninja.staffplus.core.application.database.migrations.mysql;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

import java.sql.Connection;

@IocBean(conditionalOnProperty = "storage.type=mysql")
@IocMultiProvider(Migration.class)
public class V36_CreateWarningAppealsTableMigration implements Migration {
    @Override
    public String getStatement(Connection connection) {
        return "CREATE TABLE IF NOT EXISTS sp_warning_appeals (  " +
            "ID INT NOT NULL AUTO_INCREMENT,  " +
            "warning_id INT NOT NULL,  " +
            "appealer_uuid VARCHAR(36) NOT NULL,  " +
            "resolver_uuid VARCHAR(36) NULL,  " +
            "reason TEXT NOT NULL,  " +
            "resolve_reason TEXT NULL,  " +
            "status VARCHAR(36) NOT NULL DEFAULT 'OPEN',  " +
            "timestamp BIGINT NOT NULL, " +
            "PRIMARY KEY (ID), " +
            "FOREIGN KEY (warning_id) REFERENCES sp_warnings(id)) ENGINE = InnoDB;";
    }

    @Override
    public int getVersion() {
        return 36;
    }
}
