package org.classes.service;

import org.classes.Formation;
import org.classes.Session;

public record Rapport(String titre, long effectif, double tauxRemplissage) {
    public static Rapport of(String titre, long effectif, double tauxRemplissage) {
        return new Rapport(titre, effectif, tauxRemplissage);
    }
}