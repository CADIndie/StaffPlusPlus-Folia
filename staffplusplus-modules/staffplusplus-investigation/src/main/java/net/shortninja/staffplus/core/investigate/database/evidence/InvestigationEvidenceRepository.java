package net.shortninja.staffplus.core.investigate.database.evidence;

import net.shortninja.staffplus.core.investigate.EvidenceEntity;
import net.shortninja.staffplus.core.investigate.Investigation;
import net.shortninja.staffplusplus.investigate.evidence.Evidence;

import java.util.List;
import java.util.Optional;

public interface InvestigationEvidenceRepository {

    void addEvidence(EvidenceEntity evidenceEntity);

    List<EvidenceEntity> getAllEvidence(int investigationId);

    List<EvidenceEntity> getAllEvidence(int investigationId, int offset, int amount);

    void removeEvidence(int id);

    Optional<EvidenceEntity> findLinkedEvidence(Investigation investigation, Evidence evidence);

    Optional<EvidenceEntity> find(int id);
}
