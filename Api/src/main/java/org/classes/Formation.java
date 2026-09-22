package org.classes;

import org.classes.enums.Niveau;

public record Formation(
        Long id,
        String titre,
        String description,
        int dureeHeures,
        Niveau niveau,
        boolean active
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Formation that = (Formation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}