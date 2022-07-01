package net.shortninja.staffplus.core.application.database.migrations.common;

import be.garagepoort.mcsqlmigrations.Migration;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

@IocBean
@IocMultiProvider(Migration.class)
public class V35_AlterWarningsTableAddServerColumnMigration implements Migration {

    @Override
    public String getStatement() {
        return "ALTER TABLE sp_warnings ADD COLUMN server_name VARCHAR(255) null;";
    }

    @Override
    public int getVersion() {
        return 35;
    }
}
