package org.classes;

import org.classes.enums.Niveau;

public class Formation {
    private Long id;
    private String titre;
    private String description;
    private int dureeHeures;
    private Niveau niveau;
    private boolean active;

    public Formation() {
    }

    public Formation(Long id, String titre, String description, int dureeHeures, Niveau niveau, boolean active) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dureeHeures = dureeHeures;
        this.niveau = niveau;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDureeHeures() {
        return dureeHeures;
    }

    public void setDureeHeures(int dureeHeures) {
        this.dureeHeures = dureeHeures;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
