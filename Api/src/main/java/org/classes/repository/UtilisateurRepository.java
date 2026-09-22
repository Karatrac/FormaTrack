package org.classes.repository;

import org.classes.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository {
    Utilisateur save(Utilisateur utilisateur);

    Optional<Utilisateur> findById(Long id);

    List<Utilisateur> findAll();

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    void deleteById(Long id);
}