package net.shortninja.staffplus.core.domain.staff.investigate;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.investigate.config.InvestigationConfiguration;
import net.shortninja.staffplus.core.domain.staff.investigate.database.evidence.InvestigationEvidenceRepository;
import net.shortninja.staffplusplus.investigate.InvestigationEvidenceLinkedEvent;
import net.shortninja.staffplusplus.investigate.InvestigationEvidenceUnlinkedEvent;
import net.shortninja.staffplusplus.investigate.evidence.Evidence;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;

@IocBean
public class InvestigationEvidenceService {

    private final InvestigationEvidenceRepository investigationEvidenceRepository;
    private final PermissionHandler permissionHandler;
    private final InvestigationConfiguration investigationConfiguration;

    public InvestigationEvidenceService(InvestigationEvidenceRepository investigationEvidenceRepository,
                                        PermissionHandler permissionHandler,
                                        InvestigationConfiguration investigationConfiguration) {
        this.investigationEvidenceRepository = investigationEvidenceRepository;
        this.permissionHandler = permissionHandler;
        this.investigationConfiguration = investigationConfiguration;
    }

    public void linkEvidence(Player linker, Investigation investigation, Evidence evidence) {
        permissionHandler.validate(linker, investigationConfiguration.getLinkEvidencePermission());
        Optional<EvidenceEntity> linkedEvidence = investigationEvidenceRepository.findLinkedEvidence(investigation, evidence);
        if (linkedEvidence.isPresent()) {
            throw new BusinessException("&CCannot link evidence. This evidence piece is already linked to this investigation");
        }

        EvidenceEntity evidenceEntity = new EvidenceEntity(
            investigation.getId(),
            evidence.getId(),
            evidence.getEvidenceType(),
            linker.getUniqueId(),
            linker.getName(),
            evidence.getDescription());

        investigationEvidenceRepository.addEvidence(evidenceEntity);
        sendEvent(new InvestigationEvidenceLinkedEvent(investigation, evidenceEntity));
    }

    public void unlinkEvidence(Player unlinker, Investigation investigation, int id) {
        permissionHandler.validate(unlinker, investigationConfiguration.getLinkEvidencePermission());
        Optional<EvidenceEntity> evidenceEntity = investigationEvidenceRepository.find(id);
        if (evidenceEntity.isPresent()) {
            investigationEvidenceRepository.removeEvidence(id);
            sendEvent(new InvestigationEvidenceUnlinkedEvent(investigation, evidenceEntity.get()));
        }
    }

    public List<EvidenceEntity> getEvidenceForInvestigation(Investigation investigation) {
        return investigationEvidenceRepository.getAllEvidence(investigation.getId());
    }

    public List<EvidenceEntity> getEvidenceForInvestigation(Investigation investigation, int offset, int amount) {
        return investigationEvidenceRepository.getAllEvidence(investigation.getId(), offset, amount);
    }
}
