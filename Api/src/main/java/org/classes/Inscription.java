package org.classes;

import org.classes.enums.Statut;

import java.time.LocalDateTime;
import java.util.Objects;

public class Inscription {
    private Long id;
    private Session session;
    private Utilisateur utilisateur;
    private Statut statut;
    private LocalDateTime dateInscription;

    public Inscription() {
    }

    public Inscription(Long id, Session session, Utilisateur utilisateur, Statut statut, LocalDateTime dateInscription) {
        this.id = id;
        this.session = session;
        this.utilisateur = utilisateur;
        this.statut = statut;
        this.dateInscription = dateInscription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

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

    @Override
    public String toString() {
        return "Inscription{id=" + id
                + ", sessionId=" + (session != null ? session.getId() : null)
                + ", utilisateur=" + (utilisateur != null ? utilisateur.getEmail() : null)
                + ", statut=" + statut + ", dateInscription=" + dateInscription + "}";
    }
}