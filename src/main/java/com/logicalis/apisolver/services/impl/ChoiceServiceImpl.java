
package com.logicalis.apisolver.services.impl;

import com.logicalis.apisolver.dao.IChoiceDAO;
import com.logicalis.apisolver.model.Choice;
import com.logicalis.apisolver.model.ChoiceFields;
import com.logicalis.apisolver.services.IChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChoiceServiceImpl implements IChoiceService {

    @Autowired
    private IChoiceDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<Choice> findAll() {
        return (List<Choice>) dao.findAll();
    }

    @Override
    public Choice save(Choice choice) {
        return dao.save(choice);
    }

    @Override
    public Choice findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }


    @Override
    public List<Choice> findByInactive(boolean inactive) {
        return null;
    }

    @Override
    public Choice findTopByInactive(boolean inactive) {
        return (Choice) dao.findTopByInactive(inactive);
    }

    @Override
    public List<ChoiceFields> choicesByIncident() {
        return (List<ChoiceFields>) dao.choicesByIncident();
    }

    @Override
    public List<ChoiceFields> choicesByScTask() {
        return (List<ChoiceFields>) dao.choicesByScTask();
    }

    @Override
    public List<ChoiceFields> choicesByScRequestItem() {
        return (List<ChoiceFields>) dao.choicesByScRequestItem();
    }

    @Override
    public Choice findByIntegrationId(String integrationId) {
        return dao.findByIntegrationId(integrationId);
    }

    @Override
    public Choice findByValueAndElementAndName(String value, String element, String name) {
        return dao.findByValueAndElementAndName(value, element, name);
    }

    @Override
    public ChoiceFields findByFilters(String name, String element, String value) {
        return dao.findByFilters(name, element, value);
    }
}