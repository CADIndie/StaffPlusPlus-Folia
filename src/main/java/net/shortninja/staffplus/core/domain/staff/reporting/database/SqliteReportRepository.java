package net.shortninja.staffplus.core.domain.staff.reporting.database;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.exceptions.DatabaseException;
import net.shortninja.staffplus.core.domain.location.LocationRepository;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.reporting.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

@IocBean(conditionalOnProperty = "storage.type=sqlite")
public class SqliteReportRepository extends AbstractSqlReportRepository {

    private final LocationRepository locationRepository;

    public SqliteReportRepository(PlayerManager playerManager, SqlConnectionProvider sqlConnectionProvider, Options options, LocationRepository locationRepository) {
        super(playerManager, sqlConnectionProvider, options);
        this.locationRepository = locationRepository;
    }

    @Override
    public int addReport(Report report) {
        Integer locationId = report.getLocation().isPresent() ? locationRepository.addLocation(report.getLocation().get()) : null;
        try (Connection connection = getConnection();
             PreparedStatement insert = connection.prepareStatement("INSERT INTO sp_reports(Reason, Reporter_UUID, reporter_name, Player_UUID, player_name, status, timestamp, server_name, location_id, type, deleted) " +
                 "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            insert.setString(1, report.getReason());
            insert.setString(2, report.getReporterUuid().toString());
            insert.setString(3, report.getReporterName());
            insert.setString(4, report.getCulpritUuid() == null ? null : report.getCulpritUuid().toString());
            insert.setString(5, report.getCulpritName());
            insert.setString(6, report.getReportStatus().toString());
            insert.setLong(7, report.getCreationDate().toInstant().toEpochMilli());
            insert.setString(8, options.serverName);
            if (locationId != null) {
                insert.setInt(9, locationId);
            } else {
                insert.setNull(9, Types.INTEGER);
            }

            if (report.getReportType().isPresent()) {
                insert.setString(10, report.getReportType().get());
            } else {
                insert.setNull(10, Types.VARCHAR);
            }
            // need to set this explicitly in older version because the deleted reports column has a wrong default "false" value.
            // This fix is easier than actually migrating the table.
            insert.setBoolean(11, false);
            insert.executeUpdate();

            Statement statement = connection.createStatement();
            ResultSet generatedKeys = statement.executeQuery("SELECT last_insert_rowid()");
            int generatedKey = -1;
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }
            connection.commit(); // Commits transaction.

            return generatedKey;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
