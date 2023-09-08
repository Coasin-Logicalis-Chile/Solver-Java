
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.ICatalogLineDAO;
import com.logicalis.apisolver.model.CatalogLine;
import com.logicalis.apisolver.services.ICatalogLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogLineServiceImpl implements ICatalogLineService {

    @Autowired
    private ICatalogLineDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<CatalogLine> findAll() {
        return (List<CatalogLine>) dao.findAll();
    }

    @Override
    public CatalogLine save(CatalogLine catalogLine) {
        return dao.save(catalogLine);
    }

    @Override
    public CatalogLine findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public List<CatalogLine> findByActive(boolean active) {
        return null;
    }

    @Override
    public CatalogLine findTopByActive(boolean active) {
        return (CatalogLine) dao.findTopByActive(active);
    }

    @Override
    public CatalogLine findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

}
