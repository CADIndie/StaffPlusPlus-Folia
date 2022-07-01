package net.shortninja.staffplus.core.application.database.migrations.common;

import be.garagepoort.mcsqlmigrations.Migration;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

@IocBean
@IocMultiProvider(Migration.class)
public class V57_AlterSessionTableAddMutedStaffChatChannelsColumnMigration implements Migration {

    @Override
    public String getStatement() {
        return "ALTER TABLE sp_sessions ADD COLUMN muted_staff_chat_channels VARCHAR(255) null;";
    }

    @Override
    public int getVersion() {
        return 57;
    }
}
