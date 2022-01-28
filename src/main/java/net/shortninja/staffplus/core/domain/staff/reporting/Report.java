package net.shortninja.staffplus.core.domain.staff.reporting;

import net.shortninja.staffplus.core.common.SppLocation;
import net.shortninja.staffplus.core.domain.staff.infractions.Infraction;
import net.shortninja.staffplus.core.domain.staff.infractions.InfractionType;
import net.shortninja.staffplusplus.ILocation;
import net.shortninja.staffplusplus.investigate.evidence.Evidence;
import net.shortninja.staffplusplus.reports.IReport;
import net.shortninja.staffplusplus.reports.ReportStatus;
import org.bukkit.Location;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public class Report implements IReport, Infraction, Evidence {
    private final UUID culpritUuid;
    private final String culpritName;
    private final String reason;
    private final ZonedDateTime timestamp;
    private final UUID reporterUuid;
    private String reporterName;
    private String staffName;
    private UUID staffUuid;
    private ReportStatus reportStatus;
    private int id;
    private String closeReason;
    private String serverName;
    private transient Location location;
    private SppLocation sppLocation;
    private String type;

    public Report(UUID culpritUuid, String culpritName, int id, String reason, String reporterName, UUID reporterUuid, long time,
                  ReportStatus reportStatus,
                  String staffName,
                  UUID staffUuid,
                  String closeReason,
                  String serverName,
                  Location location,
                  SppLocation sppLocation,
                  String type) {
        this.culpritUuid = culpritUuid;
        this.culpritName = culpritName;
        this.reason = reason;
        this.reporterName = reporterName;
        this.reporterUuid = reporterUuid;
        this.id = id;
        this.timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        this.reportStatus = reportStatus;
        this.staffName = staffName;
        this.staffUuid = staffUuid;
        this.closeReason = closeReason;
        this.serverName = serverName;
        this.location = location;
        this.sppLocation = sppLocation;
        this.type = type;
    }

    public Report(UUID culpritUuid, String culpritName, String reason, String reporterName, UUID reporterUuid, ReportStatus reportStatus, ZonedDateTime timestamp, Location location, String type, String serverName) {
        this.culpritUuid = culpritUuid;
        this.culpritName = culpritName;
        this.reason = reason;
        this.reporterName = reporterName;
        this.reporterUuid = reporterUuid;
        this.reportStatus = reportStatus;
        this.timestamp = timestamp;
        this.location = location;
        this.type = type;
        this.serverName = serverName;
        if (location != null) {
            this.sppLocation = new SppLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), serverName);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getEvidenceType() {
        return "REPORT";
    }

    @Override
    public String getDescription() {
        return reason;
    }

    public UUID getUuid() {
        return culpritUuid;
    }

    public UUID getCulpritUuid() {
        return culpritUuid;
    }

    public String getCulpritName() {
        return culpritName;
    }

    public String getReason() {
        return reason;
    }

    public String getReporterName() {
        return reporterName;
    }

    /*
     * This is only required in order to keep report names up to date when the
     * reporter changes his or her name.
     */
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public UUID getReporterUuid() {
        return reporterUuid;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public UUID getStaffUuid() {
        return staffUuid;
    }

    public void setStaffUuid(UUID staffUuid) {
        this.staffUuid = staffUuid;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    @Override
    public ZonedDateTime getCreationDate() {
        return timestamp;
    }

    @Override
    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    @Override
    public InfractionType getInfractionType() {
        return InfractionType.REPORTED;
    }

    @Override
    public Long getCreationTimestamp() {
        return Timestamp.valueOf(timestamp.toLocalDateTime()).getTime();
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public Optional<Location> getLocation() {
        return Optional.ofNullable(location);
    }

    @Override
    public Optional<ILocation> getSppLocation() {
        return Optional.ofNullable(sppLocation);
    }

    @Override
    public Optional<String> getReportType() {
        return Optional.ofNullable(type);
    }
}