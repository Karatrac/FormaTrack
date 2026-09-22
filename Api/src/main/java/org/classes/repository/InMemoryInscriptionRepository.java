package org.classes.repository;

import org.classes.Inscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryInscriptionRepository implements InscriptionRepository {
    private final Map<Long, Inscription> inscriptions = new HashMap<>();
    private long nextId = 1;

    @Override
    public Inscription save(Inscription inscription) {
        Inscription aEnregistrer = inscription.id() == null
                ? new Inscription(nextId++, inscription.session(), inscription.utilisateur(),
                        inscription.statut(), inscription.dateInscription())
                : inscription;
        inscriptions.put(aEnregistrer.id(), aEnregistrer);
        return aEnregistrer;
    }

    @Override
    public Optional<Inscription> findById(Long id) {
        return Optional.ofNullable(inscriptions.get(id));
    }

    @Override
    public List<Inscription> findAll() {
        return new ArrayList<>(inscriptions.values());
    }

    @Override
    public List<Inscription> findBySessionId(Long sessionId) {
        return inscriptions.values().stream()
                .filter(i -> i.session() != null && i.session().id() != null
                        && i.session().id().equals(sessionId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Inscription> findByUtilisateurId(Long utilisateurId) {
        return inscriptions.values().stream()
                .filter(i -> i.utilisateur() != null && i.utilisateur().id() != null
                        && i.utilisateur().id().equals(utilisateurId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySessionIdAndUtilisateurId(Long sessionId, Long utilisateurId) {
        return inscriptions.values().stream()
                .anyMatch(i -> i.session() != null && i.session().id() != null
                        && i.session().id().equals(sessionId)
                        && i.utilisateur() != null && i.utilisateur().id() != null
                        && i.utilisateur().id().equals(utilisateurId));
    }
}