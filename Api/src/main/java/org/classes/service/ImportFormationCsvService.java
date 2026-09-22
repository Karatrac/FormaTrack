package org.classes.service;

import org.classes.Formation;
import org.classes.enums.Niveau;
import org.classes.exception.ImportFichierException;
import org.classes.exception.LigneCsvInvalideException;
import org.classes.repository.FormationRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImportFormationCsvService {
    private final FormationRepository formationRepository;

    public ImportFormationCsvService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public int importer(String cheminFichier) throws ImportFichierException {
        int compteur = 0;
        try (BufferedReader lecteur = Files.newBufferedReader(Path.of(cheminFichier), StandardCharsets.UTF_8)) {
            String ligne;
            int numero = 0;
            while ((ligne = lecteur.readLine()) != null) {
                numero++;
                if (numero == 1 || ligne.isBlank()) {
                    continue;
                }
                try {
                    Formation formation = parser(ligne);
                    formationRepository.save(formation);
                    compteur++;
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    throw new LigneCsvInvalideException(
                            "Ligne " + numero + " invalide : " + ligne, e);
                }
            }
        } catch (IOException e) {
            throw new ImportFichierException("Impossible de lire le fichier " + cheminFichier
                    + " (" + e.getMessage() + ")", e);
        }
        return compteur;
    }

    private Formation parser(String ligne) {
        String[] champs = ligne.split(";");
        String titre = champs[0].trim();
        String description = champs[1].trim();
        int dureeHeures = Integer.parseInt(champs[2].trim());
        Niveau niveau = Niveau.valueOf(champs[3].trim().toUpperCase());
        boolean active = Boolean.parseBoolean(champs[4].trim());
        return new Formation(null, titre, description, dureeHeures, niveau, active);
    }
}