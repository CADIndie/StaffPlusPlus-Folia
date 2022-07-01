package net.shortninja.staffplus.core.application.database.migrations.common;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

@IocBean
@IocMultiProvider(Migration.class)
public class V10_AlterReportTableAddStaffNameMigration implements Migration {
    @Override
    public String getStatement() {
        return "ALTER TABLE sp_reports ADD COLUMN staff_name varchar(16);";
    }

    @Override
    public int getVersion() {
        return 10;
    }
}
