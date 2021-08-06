package net.shortninja.staffplus.core.domain.staff.investigate.bungee;

import net.shortninja.staffplus.core.common.bungee.BungeeMessage;
import net.shortninja.staffplusplus.investigate.IInvestigation;
import net.shortninja.staffplusplus.investigate.InvestigationStatus;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public class InvestigationBungee extends BungeeMessage implements IInvestigation {

    private int id;
    private Long creationTimestamp;
    private Long conclusionTimestamp;
    private ZonedDateTime creationDate;
    private ZonedDateTime conclusionDate;
    private String investigatorName;
    private UUID investigatorUuid;
    private String investigatedName;
    private UUID investigatedUuid;
    private InvestigationStatus status;

    public InvestigationBungee(IInvestigation investigation) {
        super(investigation.getServerName());
        id = investigation.getId();
        creationDate = investigation.getCreationDate();
        conclusionDate = investigation.getConclusionDate().orElse(null);
        creationTimestamp = investigation.getCreationTimestamp();
        conclusionTimestamp = investigation.getConclusionTimestamp().orElse(null);
        investigatorName = investigation.getInvestigatorName();
        investigatorUuid = investigation.getInvestigatorUuid();
        investigatedName = investigation.getInvestigatedName().orElse(null);
        investigatedUuid = investigation.getInvestigatedUuid().orElse(null);
        status = investigation.getStatus();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Long getCreationTimestamp() {
        return creationTimestamp;
    }

    @Override
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public Optional<Long> getConclusionTimestamp() {
        return Optional.ofNullable(conclusionTimestamp);
    }

    @Override
    public Optional<ZonedDateTime> getConclusionDate() {
        return Optional.ofNullable(conclusionDate);
    }

    @Override
    public String getInvestigatorName() {
        return investigatorName;
    }

    @Override
    public UUID getInvestigatorUuid() {
        return investigatorUuid;
    }

    @Override
    public Optional<String> getInvestigatedName() {
        return Optional.ofNullable(investigatedName);
    }

    @Override
    public Optional<UUID> getInvestigatedUuid() {
        return Optional.ofNullable(investigatedUuid);
    }

    @Override
    public InvestigationStatus getStatus() {
        return status;
    }
}
