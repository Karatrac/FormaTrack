package org.classes.repository;

import org.classes.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);

    Optional<Session> findById(Long id);

    List<Session> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);
}