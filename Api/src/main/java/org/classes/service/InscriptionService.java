package org.classes.service;

import org.classes.Inscription;
import org.classes.Session;
import org.classes.Utilisateur;
import org.classes.enums.Statut;
import org.classes.exception.ElementIntrouvableException;
import org.classes.exception.InscriptionDupliqueeException;
import org.classes.exception.SessionCompletteException;
import org.classes.repository.InscriptionRepository;
import org.classes.repository.SessionRepository;
import org.classes.repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class InscriptionService {
    private final InscriptionRepository inscriptionRepository;
    private final SessionRepository sessionRepository;
    private final UtilisateurRepository utilisateurRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository,
                              SessionRepository sessionRepository,
                              UtilisateurRepository utilisateurRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.sessionRepository = sessionRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Inscription inscrire(Long utilisateurId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ElementIntrouvableException(
                        "Session introuvable avec l'id " + sessionId));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ElementIntrouvableException(
                        "Utilisateur introuvable avec l'id " + utilisateurId));

        if (inscriptionRepository.existsBySessionIdAndUtilisateurId(session.id(), utilisateur.id())) {
            throw new InscriptionDupliqueeException(
                    "L'utilisateur " + utilisateur.email()
                            + " est déjà inscrit à la session " + session.id());
        }

        if (compteConfirmations(session) >= session.capacite()) {
            throw new SessionCompletteException(
                    "La session " + session.id() + " a atteint sa capacité maximale de "
                            + session.capacite() + " participants");
        }

        return inscriptionRepository.save(new Inscription(
                null, session, utilisateur, Statut.CONFIRMEE, LocalDateTime.now()));
    }

    public void annuler(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ElementIntrouvableException(
                        "Inscription introuvable avec l'id " + inscriptionId));
        inscriptionRepository.save(new Inscription(
                inscription.id(), inscription.session(), inscription.utilisateur(),
                Statut.ANNULEE, inscription.dateInscription()));
    }

    public List<Inscription> inscriptionsConfirmees(Long sessionId) {
        return inscriptionRepository.findBySessionId(sessionId).stream()
                .filter(i -> i.statut() == Statut.CONFIRMEE)
                .collect(Collectors.toList());
    }

    public int placesRestantes(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ElementIntrouvableException(
                        "Session introuvable avec l'id " + sessionId));
        return session.capacite() - compteConfirmations(session);
    }

    private int compteConfirmations(Session session) {
        return (int) inscriptionRepository.findBySessionId(session.id()).stream()
                .filter(i -> i.statut() == Statut.CONFIRMEE)
                .count();
    }
}