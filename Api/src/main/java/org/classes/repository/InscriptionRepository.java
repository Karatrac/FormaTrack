package org.classes.repository;

import org.classes.Inscription;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository {
    Inscription save(Inscription inscription);

    Optional<Inscription> findById(Long id);

    List<Inscription> findAll();

    List<Inscription> findBySessionId(Long sessionId);

    List<Inscription> findByUtilisateurId(Long utilisateurId);

    boolean existsBySessionIdAndUtilisateurId(Long sessionId, Long utilisateurId);
}