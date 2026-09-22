package org.classes.repository;

import org.classes.Utilisateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUtilisateurRepository implements UtilisateurRepository {
    private final Map<Long, Utilisateur> utilisateurs = new HashMap<>();
    private long nextId = 1;

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        Utilisateur aEnregistrer = utilisateur.id() == null
                ? new Utilisateur(nextId++, utilisateur.email(), utilisateur.motDePasseHash(),
                        utilisateur.nom(), utilisateur.prenom(), utilisateur.role())
                : utilisateur;
        utilisateurs.put(aEnregistrer.id(), aEnregistrer);
        return aEnregistrer;
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return Optional.ofNullable(utilisateurs.get(id));
    }

    @Override
    public List<Utilisateur> findAll() {
        return new ArrayList<>(utilisateurs.values());
    }

    @Override
    public boolean existsById(Long id) {
        return utilisateurs.containsKey(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return utilisateurs.values().stream()
                .anyMatch(u -> email != null && email.equals(u.email()));
    }

    @Override
    public void deleteById(Long id) {
        utilisateurs.remove(id);
    }
}