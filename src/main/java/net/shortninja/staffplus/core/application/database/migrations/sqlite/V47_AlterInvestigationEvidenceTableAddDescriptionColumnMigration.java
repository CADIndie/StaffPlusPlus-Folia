package net.shortninja.staffplus.core.application.database.migrations.sqlite;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcsqlmigrations.Migration;

@IocBean(conditionalOnProperty = "storage.type=sqlite")
@IocMultiProvider(Migration.class)
public class V47_AlterInvestigationEvidenceTableAddDescriptionColumnMigration implements Migration {

    @Override
    public String getStatement() {
        return "ALTER TABLE sp_investigation_evidence ADD COLUMN description TEXT NOT NULL DEFAULT '';";
    }

    @Override
    public int getVersion() {
        return 47;
    }
}