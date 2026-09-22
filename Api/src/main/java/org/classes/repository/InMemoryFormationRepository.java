package org.classes.repository;

import org.classes.Formation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryFormationRepository implements FormationRepository {
    private final Map<Long, Formation> formations = new HashMap<>();
    private long nextId = 1;

    @Override
    public Formation save(Formation formation) {
        Formation aEnregistrer = formation.id() == null
                ? new Formation(nextId++, formation.titre(), formation.description(),
                        formation.dureeHeures(), formation.niveau(), formation.active())
                : formation;
        formations.put(aEnregistrer.id(), aEnregistrer);
        return aEnregistrer;
    }

    @Override
    public Optional<Formation> findById(Long id) {
        return Optional.ofNullable(formations.get(id));
    }

    @Override
    public List<Formation> findAll() {
        return new ArrayList<>(formations.values());
    }

    @Override
    public boolean existsById(Long id) {
        return formations.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        formations.remove(id);
    }
}