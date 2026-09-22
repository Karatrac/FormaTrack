package org.console;

import org.classes.Formation;
import org.classes.Inscription;
import org.classes.Session;
import org.classes.Utilisateur;
import org.classes.enums.Niveau;
import org.classes.enums.Role;
import org.classes.exception.ImportFichierException;
import org.classes.repository.InMemoryFormationRepository;
import org.classes.repository.InMemoryInscriptionRepository;
import org.classes.repository.InMemorySessionRepository;
import org.classes.repository.InMemoryUtilisateurRepository;
import org.classes.service.ImportFormationCsvService;
import org.classes.service.InscriptionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final InMemoryFormationRepository formationRepository = new InMemoryFormationRepository();
    private final InMemorySessionRepository sessionRepository = new InMemorySessionRepository();
    private final InMemoryUtilisateurRepository utilisateurRepository = new InMemoryUtilisateurRepository();
    private final InscriptionService inscriptionService = new InscriptionService(
            new InMemoryInscriptionRepository(), sessionRepository, utilisateurRepository);
    private final ImportFormationCsvService importFormationCsvService = new ImportFormationCsvService(formationRepository);
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        var main = new Main();
        main.run();
    }

    private void run() {
        while (true) {
            try {
                afficherMenu();
                int choix = lireChoix();
                if (choix == 0) {
                    System.out.println("Au revoir !");
                    return;
                }
                traiterChoix(choix);
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void afficherMenu() {
        System.out.print("""
                === Gestion des formations ===
                1. Créer une formation
                2. Lister les formations
                3. Créer une session
                4. Lister les sessions
                5. Créer un utilisateur
                6. Lister les utilisateurs
                7. Inscrire un utilisateur à une session
                8. Importer des formations depuis un CSV
                0. Quitter
                Choix : """);
    }

    private int lireChoix() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Veuillez entrer un nombre : ");
        }
        return scanner.nextInt();
    }

    private void traiterChoix(int choix) {
        Runnable action = switch (choix) {
            case 1 -> this::creerFormation;
            case 2 -> this::listerFormations;
            case 3 -> this::creerSession;
            case 4 -> this::listerSessions;
            case 5 -> this::creerUtilisateur;
            case 6 -> this::listerUtilisateurs;
            case 7 -> this::inscrire;
            case 8 -> this::importerFormations;
            default -> () -> System.out.println("Choix invalide.");
        };
        action.run();
    }

    private void creerFormation() {
        scanner.nextLine();
        System.out.print("Titre : ");
        var titre = scanner.nextLine();
        System.out.print("Description : ");
        var description = scanner.nextLine();
        System.out.print("Durée (heures) : ");
        var duree = lireEntier();
        System.out.print("Niveau (DEBUTANT, INTERMEDIAIRE, AVANCE) : ");
        var niveau = Niveau.valueOf(scanner.next().toUpperCase());

        var formation = new Formation(null, titre, description, duree, niveau, true);
        Formation sauvegardee = formationRepository.save(formation);
        System.out.println("Formation créée avec l'id " + sauvegardee.id());
    }

    private void listerFormations() {
        List<Formation> formations = formationRepository.findAll();
        if (formations.isEmpty()) {
            System.out.println("Aucune formation.");
            return;
        }
        for (var f : formations) {
            System.out.printf("%d. %s (%d h, %s, %s)%n",
                    f.id(), f.titre(), f.dureeHeures(), f.niveau(),
                    f.active() ? "active" : "inactive");
        }
    }

    private void creerSession() {
        listerFormations();
        System.out.print("Id de la formation : ");
        var formationId = scanner.nextLong();
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new IllegalArgumentException("Formation introuvable"));
        scanner.nextLine();
        System.out.print("Date de début (jj/MM/aaaa HH:mm) : ");
        var debut = lireDate();
        System.out.print("Date de fin (jj/MM/aaaa HH:mm) : ");
        var fin = lireDate();
        System.out.print("Lieu : ");
        var lieu = scanner.nextLine();
        System.out.print("Capacité : ");
        var capacite = lireEntier();

        var session = new Session(null, formation, debut, fin, lieu, capacite);
        Session sauvegardee = sessionRepository.save(session);
        System.out.println("Session créée avec l'id " + sauvegardee.id());
    }

    private void listerSessions() {
        List<Session> sessions = sessionRepository.findAll();
        if (sessions.isEmpty()) {
            System.out.println("Aucune session.");
            return;
        }
        for (var s : sessions) {
            System.out.printf("%d. %s — %s au %s (%s)%n",
                    s.id(),
                    s.formation() != null ? s.formation().titre() : "?",
                    s.dateDebut().format(DATE_FORMAT),
                    s.dateFin().format(DATE_FORMAT),
                    s.lieu());
        }
    }

    private void creerUtilisateur() {
        scanner.nextLine();
        System.out.print("Email : ");
        var email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        var motDePasse = scanner.nextLine();
        System.out.print("Nom : ");
        var nom = scanner.nextLine();
        System.out.print("Prénom : ");
        var prenom = scanner.nextLine();
        System.out.print("Rôle (ADMIN, FORMATEUR, APPRENANT) : ");
        var role = Role.valueOf(scanner.next().toUpperCase());

        var utilisateur = new Utilisateur(null, email, motDePasse, nom, prenom, role);
        Utilisateur sauvegarde = utilisateurRepository.save(utilisateur);
        System.out.println("Utilisateur créé avec l'id " + sauvegarde.id());
    }

    private void listerUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        if (utilisateurs.isEmpty()) {
            System.out.println("Aucun utilisateur.");
            return;
        }
        for (var u : utilisateurs) {
            System.out.printf("%d. %s %s (%s, %s)%n",
                    u.id(), u.prenom(), u.nom(), u.email(), u.role());
        }
    }

    private void inscrire() {
        listerUtilisateurs();
        System.out.print("Id de l'utilisateur : ");
        var utilisateurId = scanner.nextLong();
        listerSessions();
        System.out.print("Id de la session : ");
        var sessionId = scanner.nextLong();

        Inscription inscription = inscriptionService.inscrire(utilisateurId, sessionId);
        System.out.println("Inscription confirmée (id " + inscription.id() + ", statut "
                + inscription.statut() + "), places restantes : "
                + inscriptionService.placesRestantes(sessionId));
    }

    private void importerFormations() {
        scanner.nextLine();
        System.out.print("Chemin du fichier CSV : ");
        var chemin = scanner.nextLine().trim();
        try {
            int compteur = importFormationCsvService.importer(chemin);
            System.out.println(compteur + " formation(s) importée(s).");
        } catch (ImportFichierException e) {
            System.out.println("Erreur d'import : " + e.getMessage());
        }
    }

    private int lireEntier() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Veuillez entrer un entier : ");
        }
        return scanner.nextInt();
    }

    private LocalDateTime lireDate() {
        while (true) {
            try {
                String ligne = scanner.nextLine();
                return LocalDateTime.parse(ligne, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.print("Format invalide (jj/MM/aaaa HH:mm), réessayez : ");
            }
        }
    }
}