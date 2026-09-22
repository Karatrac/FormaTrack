package org.classes;

import java.time.LocalDateTime;

public record Session(
        Long id,
        Formation formation,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        String lieu,
        int capacite
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session that = (Session) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}