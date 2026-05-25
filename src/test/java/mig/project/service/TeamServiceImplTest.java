package mig.project.service;

import mig.project.domein.Gebruiker;
import mig.project.domein.Team;
import mig.project.repository.GebruikerRepository;
import mig.project.repository.PrognoseRepository;
import mig.project.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PrognoseRepository prognoseRepository;
    @Mock
    private GebruikerRepository gebruikerRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @Test
    void berekenTotaalScoreSomtLedenOp() {
        Team team = new Team();
        Gebruiker u1 = new Gebruiker();
        u1.setId(1L);
        Gebruiker u2 = new Gebruiker();
        u2.setId(2L);
        team.setLeden(List.of(u1, u2));

        when(prognoseRepository.somPuntenVanGebruiker(1L)).thenReturn(7);
        when(prognoseRepository.somPuntenVanGebruiker(2L)).thenReturn(5);

        int score = teamService.berekenTotaalScore(team);

        assertEquals(12, score);
    }

    @Test
    void verwijderLidDoorEigenaarWerkt() {
        Gebruiker eigenaar = new Gebruiker();
        eigenaar.setId(1L);

        Gebruiker lid = new Gebruiker();
        lid.setId(2L);

        Team team = new Team();
        team.setId(10L);
        team.setEigenaar(eigenaar);
        team.setLeden(List.of(eigenaar, lid));
        lid.setTeam(team);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));

        teamService.verwijderLid(10L, 2L, eigenaar);

        assertNull(lid.getTeam());
        verify(gebruikerRepository).save(lid);
    }

    @Test
    void verwijderLidDoorNietEigenaarFaalt() {
        Gebruiker eigenaar = new Gebruiker();
        eigenaar.setId(1L);

        Gebruiker nietEigenaar = new Gebruiker();
        nietEigenaar.setId(3L);

        Team team = new Team();
        team.setId(10L);
        team.setEigenaar(eigenaar);
        team.setLeden(List.of(eigenaar));

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));

        assertThrows(IllegalArgumentException.class,
                () -> teamService.verwijderLid(10L, 1L, nietEigenaar));
        verify(gebruikerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
