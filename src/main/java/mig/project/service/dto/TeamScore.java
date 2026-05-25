package mig.project.service.dto;

import mig.project.domein.Team;

public record TeamScore(Team team, int totaleScore, int aantalLeden) {
}
