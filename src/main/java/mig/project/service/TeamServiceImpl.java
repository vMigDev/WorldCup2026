package mig.project.service;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Gebruiker;
import mig.project.domein.Team;
import mig.project.repository.GebruikerRepository;
import mig.project.repository.PrognoseRepository;
import mig.project.repository.TeamRepository;
import mig.project.service.dto.TeamLidScore;
import mig.project.service.dto.TeamScore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TeamRepository teamRepository;
    private final PrognoseRepository prognoseRepository;
    private final GebruikerRepository gebruikerRepository;

    @Override
    public Team maakTeamAan(String teamNaam, Gebruiker eigenaar) {
        if (teamRepository.findByTeamNaam(teamNaam).isPresent()) {
            throw new IllegalArgumentException("error.team.naam.bestaat");
        }

        Team team = new Team();
        team.setTeamNaam(teamNaam);
        team.setUitnodigingscode(genereerUniekeCode());
        team.setEigenaar(eigenaar);

        Team opgeslagen = teamRepository.save(team);
        eigenaar.setTeam(opgeslagen);
        return opgeslagen;
    }

    @Override
    public Team sluitAanBijTeam(String uitnodigingscode, Gebruiker gebruiker) {
        Team team = teamRepository.findByUitnodigingscode(uitnodigingscode)
                .orElseThrow(() -> new IllegalArgumentException("error.team.code.ongeldig"));
        gebruiker.setTeam(team);
        return team;
    }

    @Override
    @Transactional(readOnly = true)
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("error.team.nietgevonden"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamScore> getTop10Teams() {
        // Door op score en daarna alfabetisch te sorteren blijft de ranking stabiel bij gelijke punten.
        return teamRepository.findAllMetLeden().stream()
                .map(team -> new TeamScore(team, berekenTotaalScore(team), team.getLeden().size()))
                .sorted(Comparator.comparingInt(TeamScore::totaleScore).reversed()
                        .thenComparing(score -> score.team().getTeamNaam()))
                .limit(10)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int berekenTotaalScore(Team team) {
        return team.getLeden().stream()
                .mapToInt(gebruiker -> prognoseRepository.somPuntenVanGebruiker(gebruiker.getId()))
                .sum();
    }

    @Override
    public String genereerNieuweUitnodigingscode(Long teamId, Gebruiker eigenaar) {
        Team team = getTeamById(teamId);
        if (team.getEigenaar() == null || !team.getEigenaar().getId().equals(eigenaar.getId())) {
            throw new IllegalArgumentException("error.team.eigenaar.vereist");
        }

        team.setUitnodigingscode(genereerUniekeCode());
        teamRepository.save(team);
        return team.getUitnodigingscode();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamLidScore> getLidScores(Team team) {
        return team.getLeden().stream()
                .map(lid -> new TeamLidScore(
                        lid.getId(),
                        lid.getGebruikersnaam(),
                        prognoseRepository.somPuntenVanGebruiker(lid.getId()),
                        team.getEigenaar() != null && team.getEigenaar().getId().equals(lid.getId())))
                .sorted(Comparator.comparingInt(TeamLidScore::score).reversed()
                        .thenComparing(TeamLidScore::gebruikersnaam))
                .toList();
    }

    @Override
    public void verwijderLid(Long teamId, Long lidId, Gebruiker eigenaar) {
        Team team = getTeamById(teamId);
        if (team.getEigenaar() == null || !team.getEigenaar().getId().equals(eigenaar.getId())) {
            throw new IllegalArgumentException("error.team.eigenaar.vereist");
        }
        if (team.getEigenaar().getId().equals(lidId)) {
            throw new IllegalArgumentException("error.team.eigenaar.niet.verwijderen");
        }

        Gebruiker lid = team.getLeden().stream()
                .filter(gebruiker -> gebruiker.getId().equals(lidId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("error.gebruiker.nietgevonden"));

        lid.setTeam(null);
        gebruikerRepository.save(lid);
    }

    private String genereerUniekeCode() {
        String code;
        do {
            code = genereerCode();
        } while (teamRepository.findByUitnodigingscode(code).isPresent());
        return code;
    }

    private String genereerCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
