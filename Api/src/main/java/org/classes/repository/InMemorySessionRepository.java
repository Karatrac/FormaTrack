package org.classes.repository;

import org.classes.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemorySessionRepository implements SessionRepository {
    private final Map<Long, Session> sessions = new HashMap<>();
    private long nextId = 1;

    @Override
    public Session save(Session session) {
        Session aEnregistrer = session.id() == null
                ? new Session(nextId++, session.formation(), session.dateDebut(),
                        session.dateFin(), session.lieu(), session.capacite())
                : session;
        sessions.put(aEnregistrer.id(), aEnregistrer);
        return aEnregistrer;
    }

    @Override
    public Optional<Session> findById(Long id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<Session> findAll() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public boolean existsById(Long id) {
        return sessions.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        sessions.remove(id);
    }
}