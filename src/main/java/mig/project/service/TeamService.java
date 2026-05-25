package mig.project.service;

import mig.project.domein.Gebruiker;
import mig.project.domein.Team;
import mig.project.service.dto.TeamScore;
import mig.project.service.dto.TeamLidScore;

import java.util.List;

public interface TeamService {

    Team maakTeamAan(String teamNaam, Gebruiker eigenaar);

    Team sluitAanBijTeam(String uitnodigingscode, Gebruiker gebruiker);

    Team getTeamById(Long id);

    List<TeamScore> getTop10Teams();

    int berekenTotaalScore(Team team);

    String genereerNieuweUitnodigingscode(Long teamId, Gebruiker eigenaar);

    List<TeamLidScore> getLidScores(Team team);

    void verwijderLid(Long teamId, Long lidId, Gebruiker eigenaar);
}
