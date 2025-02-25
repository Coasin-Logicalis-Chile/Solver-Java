package com.logicalis.apisolver.dao;

import com.logicalis.apisolver.model.AzureAdConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IAzureAdConfigDAO extends CrudRepository<AzureAdConfig, Long> {

    @Query(value = "SELECT * FROM azure_ad_config WHERE suffix = ?1", nativeQuery = true)
    Optional<AzureAdConfig> findBySuffix(String suffix);
}
