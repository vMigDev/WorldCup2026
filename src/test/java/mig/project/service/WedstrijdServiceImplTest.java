package mig.project.service;

import mig.project.domein.Wedstrijd;
import mig.project.repository.WedstrijdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WedstrijdServiceImplTest {

    @Mock
    private WedstrijdRepository wedstrijdRepository;

    @Mock
    private StadionWebClientService webClientService;

    @InjectMocks
    private WedstrijdServiceImpl wedstrijdService;

    @Test
    void maakWedstrijdAanWerptFoutAlsLandAGelijkAanLandB() {
        Wedstrijd foutieveWedstrijd = new Wedstrijd();
        foutieveWedstrijd.setLandA("Nederland");
        foutieveWedstrijd.setLandB("Nederland");
        foutieveWedstrijd.setDatumTijd(LocalDateTime.of(2026, 6, 15, 20, 0));
        foutieveWedstrijd.setLocatie("Amsterdam");

        assertThrows(IllegalArgumentException.class,
                () -> wedstrijdService.maakWedstrijdAan(foutieveWedstrijd),
                "error.wedstrijd.land.gelijk");

        verify(wedstrijdRepository, never()).save(any());
    }

    @Test
    void maakWedstrijdAanWerptFoutAlsDatumBuitenWKValt() {
        Wedstrijd foutieveWedstrijd = new Wedstrijd();
        foutieveWedstrijd.setLandA("België");
        foutieveWedstrijd.setLandB("Spanje");
        foutieveWedstrijd.setDatumTijd(LocalDateTime.of(2025, 6, 15, 20, 0));
        foutieveWedstrijd.setLocatie("Brussel");

        assertThrows(IllegalArgumentException.class,
                () -> wedstrijdService.maakWedstrijdAan(foutieveWedstrijd),
                "error.wedstrijd.datum.periode");

        verify(wedstrijdRepository, never()).save(any());
    }

    @Test
    void magPrognoseWijzigenGeeftFalseAlsHetTeLaatIs() {
        LocalDateTime aftrapInDeNabijeToekomst = LocalDateTime.now().plusMinutes(30);

        boolean magNog = wedstrijdService.magPrognoseWijzigen(aftrapInDeNabijeToekomst);

        org.junit.jupiter.api.Assertions.assertFalse(magNog);
    }
}