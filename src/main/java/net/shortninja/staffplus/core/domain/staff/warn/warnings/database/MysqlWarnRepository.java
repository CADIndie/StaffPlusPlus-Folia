package net.shortninja.staffplus.core.domain.staff.warn.warnings.database;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.exceptions.DatabaseException;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.appeals.database.AppealRepository;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.Warning;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@IocBean(conditionalOnProperty = "storage.type=mysql")
public class MysqlWarnRepository extends AbstractSqlWarnRepository {

    public MysqlWarnRepository(PlayerManager playerManager, AppealRepository appealRepository, SqlConnectionProvider sqlConnectionProvider, Options options) {
        super(playerManager, appealRepository, sqlConnectionProvider, options);
    }

    @Override
    public int addWarning(Warning warning) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("INSERT INTO sp_warnings(Reason, Warner_UUID, warner_name, Player_UUID, player_name, score, severity, timestamp, server_name) " +
                 "VALUES(? ,?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, warning.getReason());
            insert.setString(2, warning.getIssuerUuid().toString());
            insert.setString(3, warning.getIssuerName());
            insert.setString(4, warning.getTargetUuid().toString());
            insert.setString(5, warning.getTargetName());
            insert.setInt(6, warning.getScore());
            insert.setString(7, warning.getSeverity());
            insert.setLong(8, warning.getCreationTimestamp());
            insert.setString(9, options.serverName);
            insert.executeUpdate();

            ResultSet generatedKeys = insert.getGeneratedKeys();
            int generatedKey = -1;
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }

            return generatedKey;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
