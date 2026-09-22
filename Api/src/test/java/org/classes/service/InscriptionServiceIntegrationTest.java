package org.classes.service;

import org.assertj.core.api.Assertions;
import org.classes.Formation;
import org.classes.Inscription;
import org.classes.Session;
import org.classes.Utilisateur;
import org.classes.enums.Niveau;
import org.classes.enums.Role;
import org.classes.enums.Statut;
import org.classes.exception.InscriptionDupliqueeException;
import org.classes.exception.SessionCompletteException;
import org.classes.repository.InMemoryFormationRepository;
import org.classes.repository.InMemoryInscriptionRepository;
import org.classes.repository.InMemorySessionRepository;
import org.classes.repository.InMemoryUtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class InscriptionServiceIntegrationTest {

    private InMemoryInscriptionRepository inscriptionRepository;
    private InMemorySessionRepository sessionRepository;
    private InMemoryUtilisateurRepository utilisateurRepository;
    private InscriptionService service;

    private Long sessionId;
    private Long aliceId;
    private Long bobId;

    @BeforeEach
    void setUp() {
        inscriptionRepository = new InMemoryInscriptionRepository();
        sessionRepository = new InMemorySessionRepository();
        utilisateurRepository = new InMemoryUtilisateurRepository();
        var formationRepository = new InMemoryFormationRepository();
        service = new InscriptionService(inscriptionRepository, sessionRepository, utilisateurRepository);

        Formation formation = formationRepository.save(new Formation(null,
                "Java", "Cours de base", 40, Niveau.DEBUTANT, true));
        Session session = sessionRepository.save(new Session(null, formation,
                LocalDateTime.of(2026, 10, 1, 9, 0),
                LocalDateTime.of(2026, 10, 5, 17, 0), "Salle 12", 2));
        sessionId = session.id();

        aliceId = utilisateurRepository.save(new Utilisateur(null, "alice@x.fr", "h", "Doe", "Alice", Role.APPRENANT)).id();
        bobId = utilisateurRepository.save(new Utilisateur(null, "bob@x.fr", "h", "Smith", "Bob", Role.APPRENANT)).id();
    }

    @Test
    void inscritUnUtilisateurEtLePersisteDansLeRepository() {
        Inscription inscription = service.inscrire(aliceId, sessionId);

        Assertions.assertThat(inscription.id()).isNotNull();
        Assertions.assertThat(inscriptionRepository.findById(inscription.id())).contains(inscription);
        Assertions.assertThat(inscriptionRepository.findAll()).hasSize(1);
    }

    @Test
    void neCreePasDeDoublonDansLeRepository() {
        service.inscrire(aliceId, sessionId);

        Assertions.assertThatThrownBy(() -> service.inscrire(aliceId, sessionId))
                .isInstanceOf(InscriptionDupliqueeException.class);

        Assertions.assertThat(inscriptionRepository.findAll()).hasSize(1);
    }

    @Test
    void autoriseDeuxInscriptionsDistinctesDansUneSessionDeCapaciteDeux() {
        Inscription alice = service.inscrire(aliceId, sessionId);
        Inscription bob = service.inscrire(bobId, sessionId);

        Assertions.assertThat(inscriptionRepository.findBySessionId(sessionId))
                .containsExactlyInAnyOrder(alice, bob);
        Assertions.assertThat(service.placesRestantes(sessionId)).isZero();
    }

    @Test
    void refuseLaTroisiemeInscriptionQuandLaCapaciteEstAtteinte() {
        Session pleine = sessionRepository.save(new Session(null,
                sessionRepository.findById(sessionId).orElseThrow().formation(),
                LocalDateTime.of(2026, 11, 1, 9, 0),
                LocalDateTime.of(2026, 11, 5, 17, 0), "Salle 3", 1));
        Long aliceSurPleine = service.inscrire(aliceId, pleine.id()).id();

        Assertions.assertThatThrownBy(() -> service.inscrire(bobId, pleine.id()))
                .isInstanceOf(SessionCompletteException.class)
                .hasMessageContaining("capacité maximale");

        Assertions.assertThat(inscriptionRepository.findAll()).extracting(Inscription::id)
                .containsExactly(aliceSurPleine);
    }

    @Test
    void libereUnePlaceQuandUneInscriptionEstAnnulee() {
        service.inscrire(aliceId, sessionId);
        Inscription bob = service.inscrire(bobId, sessionId);

        service.annuler(bob.id());

        Assertions.assertThat(inscriptionRepository.findBySessionId(sessionId))
                .extracting(Inscription::statut)
                .containsExactlyInAnyOrder(Statut.CONFIRMEE, Statut.ANNULEE);
        Assertions.assertThat(service.placesRestantes(sessionId)).isPositive();
    }

    @Test
    void compteUniquementLesInscriptionsConfirmees() {
        Inscription alice = service.inscrire(aliceId, sessionId);
        Inscription bob = service.inscrire(bobId, sessionId);
        service.annuler(bob.id());

        List<Inscription> confirmees = service.inscriptionsConfirmees(sessionId);

        Assertions.assertThat(confirmees).extracting(Inscription::id).containsExactly(alice.id());
    }
}