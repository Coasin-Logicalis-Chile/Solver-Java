
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.ScRequest;
import com.logicalis.apisolver.model.ScRequestItem;
import com.logicalis.apisolver.model.ScRequestItemInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IScRequestItemService {

    public List<ScRequestItem> findAll();

    public ScRequestItem save(ScRequestItem scRequestItem);

    public ScRequestItem findById(Long id);

    public   List<ScRequestItem> findByScRequest(ScRequest scRequest);

    public void delete(Long id);

    public List<ScRequestItem> findByActive(boolean active);

    public ScRequestItem findTopByActive(boolean active);

    public Page<ScRequestItemInfo> findPaginatedScRequestItemsByFilters(Pageable pageRequest, String filter, Long assignedTo, Long company, String state, boolean openUnassigned, boolean solved);

    public Long countScRequestItemsByFilters(Long assignedTo, Long company, String state);

    public ScRequestItem findByIntegrationId(String integrationId);

    public List<ScRequestItem> findByCompany(String integrationId);
}
