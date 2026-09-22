package org.classes;

import org.classes.enums.Statut;

import java.time.LocalDateTime;
import java.util.Objects;

public record Inscription(
        Long id,
        Session session,
        Utilisateur utilisateur,
        Statut statut,
        LocalDateTime dateInscription
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscription that = (Inscription) o;
        return Objects.equals(session, that.session)
                && Objects.equals(utilisateur, that.utilisateur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, utilisateur);
    }
}