package net.shortninja.staffplus.core.application.database.migrations.sqlite;

import be.garagepoort.mcsqlmigrations.Migration;

import java.sql.Connection;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

import java.sql.Connection;

@IocBean(conditionalOnProperty = "storage.type=sqlite")
@IocMultiProvider(Migration.class)
public class V71_CreateActionableActionsTableMigration implements Migration {
    @Override
    public String getStatement(Connection connection) {
        return "CREATE TABLE IF NOT EXISTS sp_commands (  " +
            "ID integer PRIMARY KEY,  " +

            "actionable_id integer NULL,  " +
            "actionable_type VARCHAR(36) NULL,  " +
            "rollback_timestamp BIGINT NULL, " +
            "rollback_command_id INT NULL, " +

            "command VARCHAR(255) NOT NULL,  " +
            "executor_uuid VARCHAR(36) NOT NULL, " +
            "target_uuid VARCHAR(36) NULL, " +
            "executor_run_strategy VARCHAR(255) NOT NULL,  " +
            "target_run_strategy VARCHAR(255) NULL,  " +
            "creation_timestamp BIGINT NOT NULL, " +
            "execution_timestamp BIGINT NULL," +
            "server_name VARCHAR(255) NULL, " +

            "is_delayed BOOLEAN default 0" +
            ");";
    }

    @Override
    public int getVersion() {
        return 71;
    }
}
