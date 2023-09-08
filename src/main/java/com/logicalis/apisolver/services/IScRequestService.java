
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IScRequestService {

    public List<ScRequest> findAll();

    public ScRequest save(ScRequest scRequest);

    public ScRequest findById(Long id);

    public void delete(Long id);

    public List<ScRequest> findByActive(boolean active);

    public ScRequest findTopByActive(boolean active);

    public ScRequest findByIntegrationId(String integrationId);

    public Page<ScRequestFields> findPaginatedScRequestsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state);

    public List<ScRequest> findByCompany(String integrationId);
}
