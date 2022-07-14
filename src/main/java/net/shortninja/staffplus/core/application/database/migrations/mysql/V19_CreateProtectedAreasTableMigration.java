package net.shortninja.staffplus.core.application.database.migrations.mysql;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

import java.sql.Connection;

@IocBean(conditionalOnProperty = "storage.type=mysql")
@IocMultiProvider(Migration.class)
public class V19_CreateProtectedAreasTableMigration implements Migration {
    @Override
    public String getStatement(Connection connection) {
        return "CREATE TABLE IF NOT EXISTS sp_protected_areas (  " +
            "ID INT NOT NULL AUTO_INCREMENT,  " +
            "name VARCHAR(128) NOT NULL,  " +
            "corner_location_1_id integer NOT NULL,  " +
            "corner_location_2_id integer NOT NULL,  " +
            "protected_by VARCHAR(36) NOT NULL,  " +
            "PRIMARY KEY (ID), " +
            "FOREIGN KEY (corner_location_1_id) REFERENCES sp_locations(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (corner_location_2_id) REFERENCES sp_locations(id) ON DELETE CASCADE) ENGINE = InnoDB;";
    }

    @Override
    public int getVersion() {
        return 19;
    }
}
