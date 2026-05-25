package mig.project.service;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Prognose;
import mig.project.domein.Wedstrijd;
import mig.project.repository.PrognoseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final PrognoseRepository prognoseRepository;

    @Value("${score.punten.exact}")
    private int puntenExact;

    @Value("${score.punten.winnaar}")
    private int puntenWinnaar;

    @Value("${score.bonus.uniek.exact}")
    private int bonusExact;

    @Value("${score.bonus.uniek.winnaar}")
    private int bonusWinnaar;

    @Override
    @Transactional
    public void berekenEnSlaScoresOp(Wedstrijd wedstrijd) {
        if (wedstrijd.getDoelpuntenA() == null || wedstrijd.getDoelpuntenB() == null) {
            return;
        }

        List<Prognose> prognoses = prognoseRepository.findByWedstrijd(wedstrijd);
        Map<Long, List<Prognose>> prognosesPerTeam = prognoses.stream()
                .filter(prognose -> prognose.getGebruiker() != null && prognose.getGebruiker().getTeam() != null)
                .collect(Collectors.groupingBy(prognose -> prognose.getGebruiker().getTeam().getId()));

        for (Prognose prognose : prognoses) {
            int punten = berekenBasispunten(prognose, wedstrijd);
            punten += berekenBonusVoorTeam(prognose, wedstrijd, prognosesPerTeam);
            prognose.setBehaaldePunten(punten);
        }

        prognoseRepository.saveAll(prognoses);
    }

    private int berekenBasispunten(Prognose prognose, Wedstrijd wedstrijd) {
        int punten = 0;

        boolean exact = Objects.equals(prognose.getVoorspeldeDoelpuntenA(), wedstrijd.getDoelpuntenA())
                && Objects.equals(prognose.getVoorspeldeDoelpuntenB(), wedstrijd.getDoelpuntenB());

        if (exact) {
            punten += puntenExact;
        }

        if (resultaatTeken(prognose.getVoorspeldeDoelpuntenA(), prognose.getVoorspeldeDoelpuntenB())
                == resultaatTeken(wedstrijd.getDoelpuntenA(), wedstrijd.getDoelpuntenB())) {
            punten += puntenWinnaar;
        }

        return punten;
    }

    private int berekenBonusVoorTeam(Prognose huidige, Wedstrijd wedstrijd, Map<Long, List<Prognose>> prognosesPerTeam) {
        if (huidige.getGebruiker() == null || huidige.getGebruiker().getTeam() == null) {
            return 0;
        }

        List<Prognose> teamPrognoses = prognosesPerTeam.getOrDefault(huidige.getGebruiker().getTeam().getId(), List.of());
        if (teamPrognoses.isEmpty()) {
            return 0;
        }

        long exactCorrect = telCorrecte(teamPrognoses, p ->
                Objects.equals(p.getVoorspeldeDoelpuntenA(), wedstrijd.getDoelpuntenA())
                        && Objects.equals(p.getVoorspeldeDoelpuntenB(), wedstrijd.getDoelpuntenB()));

        long winnaarCorrect = telCorrecte(teamPrognoses, p ->
                resultaatTeken(p.getVoorspeldeDoelpuntenA(), p.getVoorspeldeDoelpuntenB())
                        == resultaatTeken(wedstrijd.getDoelpuntenA(), wedstrijd.getDoelpuntenB()));

        int bonus = 0;
        boolean huidigeIsExact = Objects.equals(huidige.getVoorspeldeDoelpuntenA(), wedstrijd.getDoelpuntenA())
                && Objects.equals(huidige.getVoorspeldeDoelpuntenB(), wedstrijd.getDoelpuntenB());

        if (huidigeIsExact && exactCorrect == 1) {
            bonus += bonusExact;
        }

        boolean huidigeHeeftWinnaar = resultaatTeken(huidige.getVoorspeldeDoelpuntenA(), huidige.getVoorspeldeDoelpuntenB())
                == resultaatTeken(wedstrijd.getDoelpuntenA(), wedstrijd.getDoelpuntenB());

        if (huidigeHeeftWinnaar && winnaarCorrect == 1) {
            bonus += bonusWinnaar;
        }

        return bonus;
    }

    private long telCorrecte(List<Prognose> prognoses, Function<Prognose, Boolean> correctChecker) {
        return prognoses.stream().filter(p -> Boolean.TRUE.equals(correctChecker.apply(p))).count();
    }

    private int resultaatTeken(int doelpuntenA, int doelpuntenB) {
        return Integer.compare(doelpuntenA, doelpuntenB);
    }
}
