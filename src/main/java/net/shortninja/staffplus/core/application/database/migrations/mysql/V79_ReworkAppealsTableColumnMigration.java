package net.shortninja.staffplus.core.application.database.migrations.mysql;

import be.garagepoort.mcsqlmigrations.Migration;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

@IocBean(conditionalOnProperty = "storage.type=mysql")
@IocMultiProvider(Migration.class)
public class V79_ReworkAppealsTableColumnMigration implements Migration {
    @Override
    public List<String> getStatements() {
        return Arrays.asList(
            createNewTable(),
            "INSERT INTO sp_appeals SELECT ID, warning_id, appealer_uuid, resolver_uuid, reason, resolve_reason, status, timestamp, 'WARNING' FROM sp_warning_appeals;",
            "DROP TABLE sp_warning_appeals;");
    }

    @NotNull
    private String createNewTable() {
        return "CREATE TABLE IF NOT EXISTS sp_appeals (  " +
            "ID INT NOT NULL AUTO_INCREMENT,  " +
            "appealable_id INT NOT NULL,  " +
            "appealer_uuid VARCHAR(36) NOT NULL,  " +
            "resolver_uuid VARCHAR(36) NULL,  " +
            "reason TEXT NOT NULL,  " +
            "resolve_reason TEXT NULL,  " +
            "status VARCHAR(36) NOT NULL DEFAULT 'OPEN',  " +
            "timestamp BIGINT NOT NULL, " +
            "type VARCHAR(36) NOT NULL, " +
            "PRIMARY KEY (ID)) ENGINE = InnoDB;";
    }

    @Override
    public int getVersion() {
        return 79;
    }
}
