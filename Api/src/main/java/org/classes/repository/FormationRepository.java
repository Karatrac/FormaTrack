package org.classes.repository;

import org.classes.Formation;

import java.util.List;
import java.util.Optional;

public interface FormationRepository {
    Formation save(Formation formation);

    Optional<Formation> findById(Long id);

    List<Formation> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);
}