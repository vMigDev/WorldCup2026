package mig.project.rest.dto;

import mig.project.domein.Wedstrijd;

import java.time.LocalDateTime;

public record WedstrijdRestDto(
        Long id,
        String landA,
        String landB,
        LocalDateTime datumTijd,
        String locatie,
        Integer doelpuntenA,
        Integer doelpuntenB
) {
    public static WedstrijdRestDto fromEntity(Wedstrijd wedstrijd) {
        return new WedstrijdRestDto(
                wedstrijd.getId(),
                wedstrijd.getLandA(),
                wedstrijd.getLandB(),
                wedstrijd.getDatumTijd(),
                wedstrijd.getLocatie(),
                wedstrijd.getDoelpuntenA(),
                wedstrijd.getDoelpuntenB()
        );
    }
}
