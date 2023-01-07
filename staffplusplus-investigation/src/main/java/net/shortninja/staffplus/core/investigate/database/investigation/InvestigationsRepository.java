package net.shortninja.staffplus.core.investigate.database.investigation;

import net.shortninja.staffplus.core.investigate.Investigation;
import net.shortninja.staffplusplus.investigate.InvestigationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvestigationsRepository {

    int addInvestigation(Investigation investigation);

    void updateInvestigation(Investigation investigation);

    Optional<Investigation> findInvestigationForInvestigated(UUID investigatorUuid, UUID investigatedUuid, List<InvestigationStatus> investigationStatuses);

    List<Investigation> findAllInvestigationForInvestigated(UUID investigatedUuid, List<InvestigationStatus> investigationStatuses);

    List<Investigation> findAllInvestigationsForInvestigator(UUID investigatedUuid, List<InvestigationStatus> investigationStatuses);

    List<Investigation> findAllInvestigationsForInvestigator(UUID uniqueId, List<InvestigationStatus> singletonList, int offset, int amount);

    Optional<Investigation> getInvestigationForInvestigator(UUID investigatorUuid, List<InvestigationStatus> investigationStatuses);

    List<Investigation> getInvestigationsForInvestigated(UUID investigatorUuid, List<InvestigationStatus> investigationStatuses);

    List<Investigation> getAllInvestigations(int offset, int amount);

    List<Investigation> getInvestigationsForInvestigated(UUID id, int offset, int amount);

    Optional<Investigation> findInvestigation(int investigationId);

    void pauseAllInvestigations();

}
