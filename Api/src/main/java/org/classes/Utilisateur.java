package org.classes;

import org.classes.enums.Role;

public record Utilisateur(
        Long id,
        String email,
        String motDePasseHash,
        String nom,
        String prenom,
        Role role
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", email='" + email + "', nom='" + nom
                + "', prenom='" + prenom + "', role=" + role + "}";
    }
}