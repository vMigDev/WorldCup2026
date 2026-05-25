package mig.project.service;

import mig.project.domein.Gebruiker;
import mig.project.domein.Prognose;
import mig.project.domein.Team;
import mig.project.domein.Wedstrijd;
import mig.project.repository.PrognoseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceImplTest {

    @Mock
    private PrognoseRepository prognoseRepository;

    @InjectMocks
    private ScoreServiceImpl scoreService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scoreService, "puntenExact", 5);
        ReflectionTestUtils.setField(scoreService, "puntenWinnaar", 2);
        ReflectionTestUtils.setField(scoreService, "bonusExact", 3);
        ReflectionTestUtils.setField(scoreService, "bonusWinnaar", 1);
    }

    @Test
    void berekenEnSlaScoresOpGeeftExactBonusEnSlaatOp() {
        Wedstrijd wedstrijd = new Wedstrijd();
        wedstrijd.setDoelpuntenA(1);
        wedstrijd.setDoelpuntenB(0);

        Team team = new Team();
        team.setId(10L);

        Gebruiker g1 = new Gebruiker();
        g1.setId(1L);
        g1.setTeam(team);
        Gebruiker g2 = new Gebruiker();
        g2.setId(2L);
        g2.setTeam(team);

        Prognose p1 = new Prognose();
        p1.setGebruiker(g1);
        p1.setVoorspeldeDoelpuntenA(1);
        p1.setVoorspeldeDoelpuntenB(0);

        Prognose p2 = new Prognose();
        p2.setGebruiker(g2);
        p2.setVoorspeldeDoelpuntenA(2);
        p2.setVoorspeldeDoelpuntenB(0);

        when(prognoseRepository.findByWedstrijd(wedstrijd)).thenReturn(List.of(p1, p2));

        scoreService.berekenEnSlaScoresOp(wedstrijd);

        assertEquals(10, p1.getBehaaldePunten());
        assertEquals(2, p2.getBehaaldePunten());
        verify(prognoseRepository).saveAll(List.of(p1, p2));
    }

    @Test
    void berekenEnSlaScoresOpDoetNietsZonderOfficieleUitslag() {
        Wedstrijd wedstrijd = new Wedstrijd();
        wedstrijd.setDoelpuntenA(null);
        wedstrijd.setDoelpuntenB(1);

        scoreService.berekenEnSlaScoresOp(wedstrijd);

        verify(prognoseRepository, never()).findByWedstrijd(wedstrijd);
        verify(prognoseRepository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
    }
}
