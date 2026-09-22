package org.classes;

import java.time.LocalDateTime;

public class Session {
    private Long id;
    private Formation formation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private int capacite;

    public Session() {
    }

    public Session(Long id, Formation formation, LocalDateTime dateDebut, LocalDateTime dateFin, String lieu, int capacite) {
        this.id = id;
        this.formation = formation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capacite = capacite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
}