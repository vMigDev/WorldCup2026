package mig.project.service;

import mig.project.domein.Prognose;
import mig.project.domein.Wedstrijd;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WedstrijdService {

    List<Wedstrijd> getAlleWedstrijdenGesorteerd();

    List<Wedstrijd> getWedstrijdenOpDatum(LocalDate datum);

    Wedstrijd getWedstrijdById(Long id);

    Wedstrijd maakWedstrijdAan(Wedstrijd wedstrijd);

    Wedstrijd wijzigWedstrijd(Long id, Wedstrijd wedstrijd);

    Wedstrijd slaUitslagOp(Long id, Integer doelpuntenA, Integer doelpuntenB);

    boolean magPrognoseWijzigen(LocalDateTime aftrap);

    Optional<Prognose> getPrognoseVanGebruikerVoorWedstrijd(String gebruikersnaam, Wedstrijd wedstrijd);

    void slaPrognoseOp(Long wedstrijdId, String gebruikersnaam, Integer doelpuntenA, Integer doelpuntenB);

    Integer getCapaciteit(Integer stadiumcode);
}
