package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IAzureAdConfigDAO;
import com.logicalis.apisolver.model.AzureAdConfig;
import com.logicalis.apisolver.services.IAzureAdConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AzureAdConfigServiceImpl implements IAzureAdConfigService {

    private final IAzureAdConfigDAO azureAdConfigDAO;

    @Autowired
    public AzureAdConfigServiceImpl(IAzureAdConfigDAO azureAdConfigDAO) {
        this.azureAdConfigDAO = azureAdConfigDAO;
    }

    @Override
    public AzureAdConfig findBySuffix(String suffix) {
        // Llamar al DAO para buscar en la base de datos
        return azureAdConfigDAO.findBySuffix(suffix)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró configuración para el suffix: " + suffix));
    }
}
