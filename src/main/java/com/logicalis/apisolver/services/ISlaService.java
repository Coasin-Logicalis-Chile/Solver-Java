
package com.logicalis.apisolver.services;

import com.logicalis.apisolver.model.Sla;

import java.util.List;

public interface ISlaService {

    public List<Sla> findAll();

    public Sla save(Sla sla);

    public Sla findById(Long id);

    public void delete(Long id);

    public List<Sla> findByActive(boolean active);

    public Sla findTopByActive(boolean active);
}
