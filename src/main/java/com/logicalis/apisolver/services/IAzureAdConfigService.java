package com.logicalis.apisolver.services;


import com.logicalis.apisolver.model.AzureAdConfig;

public interface IAzureAdConfigService {

    AzureAdConfig findBySuffix(String suffix);

    // Puedes añadir más métodos si son necesarios
}

