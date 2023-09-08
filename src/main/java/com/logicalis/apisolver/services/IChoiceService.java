
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Choice;
import com.logicalis.apisolver.model.ChoiceFields;

import java.util.List;

public interface IChoiceService {

    public List<Choice> findAll();

    public Choice save(Choice choice);

    public Choice findById(Long id);

    public void delete(Long id);

    List<Choice> findByInactive(boolean inactive);

    Choice findTopByInactive(boolean inactive);

    public List<ChoiceFields> choicesByIncident();

    public List<ChoiceFields> choicesByScRequestItem();

    public List<ChoiceFields> choicesByScTask();

    public Choice findByIntegrationId(String integrationId);

    public Choice findByValueAndElementAndName(String value,String element,String name);

    public ChoiceFields findByFilters(String name, String element, String value);
}
