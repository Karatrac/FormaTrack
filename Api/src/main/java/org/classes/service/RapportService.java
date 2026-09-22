package org.classes.service;

import org.classes.Formation;
import org.classes.Inscription;
import org.classes.Session;
import org.classes.enums.Statut;
import org.classes.repository.InscriptionRepository;
import org.classes.repository.SessionRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RapportService {
    private final SessionRepository sessionRepository;
    private final InscriptionRepository inscriptionRepository;

    public RapportService(SessionRepository sessionRepository, InscriptionRepository inscriptionRepository) {
        this.sessionRepository = sessionRepository;
        this.inscriptionRepository = inscriptionRepository;
    }

    public Map<Formation, Long> inscritsParFormation() {
        return parFormation().entrySet().stream()
                .sorted(Map.Entry.<Formation, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public List<Formation> formationsLesPlusDemandees(int limite) {
        return parFormation().entrySet().stream()
                .sorted(Map.Entry.<Formation, Long>comparingByValue().reversed())
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Map<Session, Double> tauxDeRemplissage() {
        return sessionRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s, s -> {
                    long inscrits = inscriptions(s).size();
                    return s.capacite() > 0
                            ? (double) inscrits / s.capacite() * 100.0
                            : 0.0;
                }));
    }

    public List<Rapport> rapportGlobal() {
        return parFormation().entrySet().stream()
                .sorted(Map.Entry.<Formation, Long>comparingByValue().reversed())
                .map(e -> {
                    double taux = tauxFormation(e.getKey());
                    return Rapport.of(e.getKey().titre(), e.getValue(), taux);
                })
                .collect(Collectors.toList());
    }

    private Map<Formation, Long> parFormation() {
        Map<Long, Long> inscritsParSession = inscriptionRepository.findAll().stream()
                .filter(i -> i.statut() == Statut.CONFIRMEE)
                .map(Inscription::session)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Session::id, Collectors.counting()));

        return sessionRepository.findAll().stream()
                .filter(s -> s.formation() != null)
                .collect(Collectors.groupingBy(Session::formation,
                        Collectors.summingLong(s -> inscritsParSession.getOrDefault(s.id(), 0L))));
    }

    private double tauxFormation(Formation formation) {
        List<Session> sessions = sessionRepository.findAll().stream()
                .filter(s -> formation.equals(s.formation()))
                .collect(Collectors.toList());
        long inscrits = sessions.stream()
                .mapToLong(s -> inscriptions(s).size())
                .sum();
        long capacite = sessions.stream()
                .mapToLong(Session::capacite)
                .sum();
        return capacite > 0 ? (double) inscrits / capacite * 100.0 : 0.0;
    }

    private List<Inscription> inscriptions(Session session) {
        return inscriptionRepository.findAll().stream()
                .filter(i -> i.statut() == Statut.CONFIRMEE)
                .filter(i -> session.equals(i.session()))
                .collect(Collectors.toList());
    }
}