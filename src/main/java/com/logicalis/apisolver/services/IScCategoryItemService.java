
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.ScCategoryItem;

import java.util.List;

public interface IScCategoryItemService {

    public List<ScCategoryItem> findAll();

    public ScCategoryItem save(ScCategoryItem scCategoryItem);

    public ScCategoryItem findById(Long id);

    public void delete(Long id);

    public List<ScCategoryItem> findByActive(boolean active);

    public ScCategoryItem findTopByActive(boolean active);

    public ScCategoryItem findByIntegrationId(String integrationId);
}
