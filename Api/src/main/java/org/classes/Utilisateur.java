package org.classes;

import org.classes.enums.Role;

public class Utilisateur {
    private Long id;
    private String email;
    private String motDePasseHash;
    private String nom;
    private String prenom;
    private Role role;

    public Utilisateur() {
    }

    public Utilisateur(Long id, String email, String motDePasseHash, String nom, String prenom, Role role) {
        this.id = id;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
