package net.shortninja.staffplus.core.investigate;

import net.shortninja.staffplusplus.investigate.evidence.Evidence;
import net.shortninja.staffplusplus.investigate.evidence.IInvestigationEvidence;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class EvidenceEntity implements IInvestigationEvidence, Evidence {

    private int id;
    private int investigationId;
    private int evidenceId;
    private String evidenceType;
    private UUID linkedByUuid;
    private String linkedByName;
    private String description;
    private long timestamp;

    public EvidenceEntity(int investigationId, int evidenceId, String evidenceType, UUID linkedByUuid, String linkedByName, String description) {
        this.investigationId = investigationId;
        this.evidenceId = evidenceId;
        this.evidenceType = evidenceType;
        this.linkedByUuid = linkedByUuid;
        this.linkedByName = linkedByName;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    public EvidenceEntity(int id, int investigationId, int evidenceId, String evidenceType, UUID linkedByUuid, String linkedByName, String description, long timestamp) {
        this.id = id;
        this.investigationId = investigationId;
        this.evidenceId = evidenceId;
        this.evidenceType = evidenceType;
        this.linkedByUuid = linkedByUuid;
        this.linkedByName = linkedByName;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getInvestigationId() {
        return investigationId;
    }

    public int getEvidenceId() {
        return evidenceId;
    }

    public String getEvidenceType() {
        return evidenceType;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public UUID getLinkedByUuid() {
        return linkedByUuid;
    }

    public Long getCreationTimestamp() {
        return timestamp;
    }

    @Override
    public ZonedDateTime getCreationDate() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public String getLinkedByName() {
        return linkedByName;
    }
}
