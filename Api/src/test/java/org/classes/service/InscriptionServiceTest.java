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
import org.classes.exception.SessionPasseeException;
import org.classes.repository.InscriptionRepository;
import org.classes.repository.SessionRepository;
import org.classes.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InscriptionServiceTest {

    private InscriptionRepository inscriptionRepository;
    private SessionRepository sessionRepository;
    private UtilisateurRepository utilisateurRepository;
    private InscriptionService service;

    private final Formation formation = new Formation(1L, "Java", "Cours de base", 40, Niveau.DEBUTANT, true);
    private final Session session = new Session(1L, formation,
            LocalDateTime.of(2026, 10, 1, 9, 0),
            LocalDateTime.of(2026, 10, 5, 17, 0), "Salle 12", 30);
    private final Utilisateur utilisateur = new Utilisateur(1L, "alice@x.fr", "hash", "Doe", "Alice", Role.APPRENANT);

    @BeforeEach
    void setUp() {
        inscriptionRepository = mock(InscriptionRepository.class);
        sessionRepository = mock(SessionRepository.class);
        utilisateurRepository = mock(UtilisateurRepository.class);
        service = new InscriptionService(inscriptionRepository, sessionRepository, utilisateurRepository);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(inscriptionRepository.findBySessionId(1L)).thenReturn(List.of());
        when(inscriptionRepository.save(any(Inscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void confirmeUneInscriptionQuandLaSessionEstDisponible() {
        Inscription resultat = service.inscrire(1L, 1L);

        Assertions.assertThat(resultat.session()).isEqualTo(session);
        Assertions.assertThat(resultat.utilisateur()).isEqualTo(utilisateur);
        Assertions.assertThat(resultat.statut()).isEqualTo(Statut.CONFIRMEE);
        Assertions.assertThat(resultat.dateInscription()).isNotNull();
        verify(inscriptionRepository).save(any(Inscription.class));
    }

    @Test
    void refuseUneDoubleInscription() {
        when(inscriptionRepository.existsBySessionIdAndUtilisateurId(1L, 1L)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.inscrire(1L, 1L))
                .isInstanceOf(InscriptionDupliqueeException.class)
                .hasMessageContaining("déjà inscrit");
    }

    @Test
    void refuseUneInscriptionQuandLaSessionEstPleine() {
        Session pleine = new Session(2L, formation,
                session.dateDebut(), session.dateFin(), session.lieu(), 1);
        Inscription existante = new Inscription(1L, pleine, utilisateur, Statut.CONFIRMEE, LocalDateTime.now());
        when(sessionRepository.findById(2L)).thenReturn(Optional.of(pleine));
        when(inscriptionRepository.findBySessionId(2L)).thenReturn(List.of(existante));

        Assertions.assertThatThrownBy(() -> service.inscrire(1L, 2L))
                .isInstanceOf(SessionCompletteException.class)
                .hasMessageContaining("capacité maximale");
    }

    @Test
    void refuseUneInscriptionQuandLaSessionEstPassee() {
        Clock horlogeFixe = Clock.fixed(
                LocalDateTime.of(2026, 12, 1, 10, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new InscriptionService(inscriptionRepository, sessionRepository,
                utilisateurRepository, horlogeFixe);

        Session passee = new Session(3L, formation,
                LocalDateTime.of(2026, 9, 1, 9, 0),
                LocalDateTime.of(2026, 9, 5, 17, 0), "Salle 3", 30);
        when(sessionRepository.findById(3L)).thenReturn(Optional.of(passee));

        Assertions.assertThatThrownBy(() -> service.inscrire(1L, 3L))
                .isInstanceOf(SessionPasseeException.class)
                .hasMessageContaining("terminée");
    }

    @Test
    void annuleUneInscriptionConfirmee() {
        Inscription inscription = new Inscription(7L, session, utilisateur, Statut.CONFIRMEE, LocalDateTime.now());
        when(inscriptionRepository.findById(7L)).thenReturn(Optional.of(inscription));
        when(inscriptionRepository.save(any(Inscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.annuler(7L);

        verify(inscriptionRepository).save(eq(new Inscription(7L, session, utilisateur, Statut.ANNULEE, inscription.dateInscription())));
    }
}