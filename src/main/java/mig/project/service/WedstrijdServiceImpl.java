package mig.project.service;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Gebruiker;
import mig.project.domein.Prognose;
import mig.project.domein.Wedstrijd;
import mig.project.exceptions.WedstrijdNietGevondenException;
import mig.project.repository.GebruikerRepository;
import mig.project.repository.PrognoseRepository;
import mig.project.repository.WedstrijdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WedstrijdServiceImpl implements WedstrijdService {

    private static final LocalDateTime WORLD_CUP_START = LocalDateTime.of(2026, 6, 1, 0, 0);
    private static final LocalDateTime WORLD_CUP_END = LocalDateTime.of(2026, 8, 1, 0, 0);

    private final WedstrijdRepository wedstrijdRepository;
    private final GebruikerRepository gebruikerRepository;
    private final PrognoseRepository prognoseRepository;
    private final StadionWebClientService webClientService;

    @Override
    @Transactional(readOnly = true)
    public List<Wedstrijd> getAlleWedstrijdenGesorteerd() {
        return wedstrijdRepository.findAllByOrderByDatumTijdAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wedstrijd> getWedstrijdenOpDatum(LocalDate datum) {
        LocalDateTime startVanDag = datum.atStartOfDay();
        LocalDateTime startVolgendeDag = datum.plusDays(1).atStartOfDay();
        return wedstrijdRepository.findByDatumTijdBetween(startVanDag, startVolgendeDag).stream()
                .sorted(Comparator.comparing(Wedstrijd::getDatumTijd))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Wedstrijd getWedstrijdById(Long id) {
        return wedstrijdRepository.findById(id)
                .orElseThrow(() -> new WedstrijdNietGevondenException("error.wedstrijd.nietgevonden"));
    }

    @Override
    public Wedstrijd maakWedstrijdAan(Wedstrijd wedstrijd) {
        valideerWedstrijd(wedstrijd, null);
        return wedstrijdRepository.save(wedstrijd);
    }

    @Override
    public Wedstrijd wijzigWedstrijd(Long id, Wedstrijd wedstrijd) {
        Wedstrijd bestaand = getWedstrijdById(id);
        bestaand.setLandA(wedstrijd.getLandA());
        bestaand.setLandB(wedstrijd.getLandB());
        bestaand.setDatumTijd(wedstrijd.getDatumTijd());
        bestaand.setLocatie(wedstrijd.getLocatie());
        bestaand.setStadiumcode(wedstrijd.getStadiumcode());
        bestaand.setChecksum(wedstrijd.getChecksum());

        valideerWedstrijd(bestaand, id);
        return wedstrijdRepository.save(bestaand);
    }

    @Override
    public Wedstrijd slaUitslagOp(Long id, Integer doelpuntenA, Integer doelpuntenB) {
        Wedstrijd wedstrijd = getWedstrijdById(id);
        wedstrijd.setDoelpuntenA(doelpuntenA);
        wedstrijd.setDoelpuntenB(doelpuntenB);
        return wedstrijdRepository.save(wedstrijd);
    }

    @Override
    public boolean magPrognoseWijzigen(LocalDateTime aftrap) {
        return LocalDateTime.now().isBefore(aftrap.minusHours(1));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Prognose> getPrognoseVanGebruikerVoorWedstrijd(String gebruikersnaam, Wedstrijd wedstrijd) {
        return gebruikerRepository.findByGebruikersnaam(gebruikersnaam)
                .flatMap(gebruiker -> prognoseRepository.findByGebruikerAndWedstrijd(gebruiker, wedstrijd));
    }

    @Override
    public void slaPrognoseOp(Long wedstrijdId, String gebruikersnaam, Integer doelpuntenA, Integer doelpuntenB) {
        Wedstrijd wedstrijd = getWedstrijdById(wedstrijdId);
        if (!magPrognoseWijzigen(wedstrijd.getDatumTijd())) {
            throw new IllegalArgumentException("error.prognose.te.laat");
        }

        Gebruiker gebruiker = gebruikerRepository.findByGebruikersnaam(gebruikersnaam)
                .orElseThrow(() -> new IllegalArgumentException("error.gebruiker.nietgevonden"));

        Prognose prognose = prognoseRepository.findByGebruikerAndWedstrijd(gebruiker, wedstrijd)
                .orElseGet(Prognose::new);
        prognose.setGebruiker(gebruiker);
        prognose.setWedstrijd(wedstrijd);
        prognose.setVoorspeldeDoelpuntenA(doelpuntenA);
        prognose.setVoorspeldeDoelpuntenB(doelpuntenB);
        prognoseRepository.save(prognose);
    }

    @Override
    public Integer getCapaciteit(Integer stadiumcode) {
        return webClientService.haalCapaciteitOp(stadiumcode).blockOptional().orElse(0);
    }

    private void valideerWedstrijd(Wedstrijd wedstrijd, Long huidigId) {
        if (wedstrijd.getLandA() != null && wedstrijd.getLandA().equalsIgnoreCase(wedstrijd.getLandB())) {
            throw new IllegalArgumentException("error.wedstrijd.land.gelijk");
        }

        if (wedstrijd.getDatumTijd() == null
                || wedstrijd.getDatumTijd().isBefore(WORLD_CUP_START)
                || wedstrijd.getDatumTijd().isAfter(WORLD_CUP_END)) {
            throw new IllegalArgumentException("error.wedstrijd.datum.periode");
        }
        if (wedstrijd.getDatumTijd().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("error.wedstrijd.datum.toekomst");
        }

        boolean bestaat = huidigId == null
                ? wedstrijdRepository.existsByDatumTijdAndLocatie(wedstrijd.getDatumTijd(), wedstrijd.getLocatie())
                : wedstrijdRepository.existsByDatumTijdAndLocatieAndIdNot(wedstrijd.getDatumTijd(), wedstrijd.getLocatie(), huidigId);

        if (bestaat) {
            throw new IllegalArgumentException("error.wedstrijd.uniek.locatie.tijd");
        }
    }
}
