package mig.project.config;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Gebruiker;
import mig.project.domein.Prognose;
import mig.project.domein.Team;
import mig.project.domein.Wedstrijd;
import mig.project.repository.GebruikerRepository;
import mig.project.repository.PrognoseRepository;
import mig.project.repository.TeamRepository;
import mig.project.repository.WedstrijdRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


//vult de database met startdata wanneer de applicatie voor het eerst draait
@Configuration
@RequiredArgsConstructor
public class DataInitialisatieConfig {

    private final GebruikerRepository gebruikerRepository;
    private final TeamRepository teamRepository;
    private final WedstrijdRepository wedstrijdRepository;
    private final PrognoseRepository prognoseRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            if (gebruikerRepository.count() > 0 || wedstrijdRepository.count() > 0) {
                return;
            }

            Gebruiker admin = new Gebruiker();
            admin.setGebruikersnaam("admin");
            admin.setWachtwoord(passwordEncoder.encode("Admin1234"));
            admin.setRol("ADMIN");
            gebruikerRepository.save(admin);

            Gebruiker eigenaar = new Gebruiker();
            eigenaar.setGebruikersnaam("captain");
            eigenaar.setWachtwoord(passwordEncoder.encode("Captain1234"));
            eigenaar.setRol("USER");
            eigenaar = gebruikerRepository.save(eigenaar);

            Team team = new Team();
            team.setTeamNaam("Oranje Fans");
            team.setUitnodigingscode("WKCUP202");
            team.setEigenaar(eigenaar);
            team = teamRepository.save(team);

            eigenaar.setTeam(team);
            eigenaar = gebruikerRepository.save(eigenaar);

            Gebruiker lid = new Gebruiker();
            lid.setGebruikersnaam("fan1");
            lid.setWachtwoord(passwordEncoder.encode("Fan12345"));
            lid.setRol("USER");
            lid.setTeam(team);
            lid = gebruikerRepository.save(lid);

            Wedstrijd wedstrijd1 = new Wedstrijd();
            wedstrijd1.setLandA("België");
            wedstrijd1.setLandB("Nederland");
            wedstrijd1.setDatumTijd(LocalDateTime.of(2026, 6, 15, 20, 0));
            wedstrijd1.setLocatie("Brussel");
            wedstrijd1.setStadiumcode(1234);
            wedstrijd1.setChecksum(1234 % 97);
            wedstrijd1.setDoelpuntenA(1);
            wedstrijd1.setDoelpuntenB(2);
            wedstrijd1 = wedstrijdRepository.save(wedstrijd1);

            Wedstrijd wedstrijd2 = new Wedstrijd();
            wedstrijd2.setLandA("Frankrijk");
            wedstrijd2.setLandB("Duitsland");
            wedstrijd2.setDatumTijd(LocalDateTime.of(2026, 6, 18, 18, 0));
            wedstrijd2.setLocatie("Parijs");
            wedstrijd2.setStadiumcode(5678);
            wedstrijd2.setChecksum(5678 % 97);
            wedstrijd2 = wedstrijdRepository.save(wedstrijd2);

            Prognose prognose1 = new Prognose();
            prognose1.setGebruiker(eigenaar);
            prognose1.setWedstrijd(wedstrijd1);
            prognose1.setVoorspeldeDoelpuntenA(1);
            prognose1.setVoorspeldeDoelpuntenB(2);
            prognose1.setBehaaldePunten(8);
            prognoseRepository.save(prognose1);

            Prognose prognose2 = new Prognose();
            prognose2.setGebruiker(lid);
            prognose2.setWedstrijd(wedstrijd1);
            prognose2.setVoorspeldeDoelpuntenA(0);
            prognose2.setVoorspeldeDoelpuntenB(1);
            prognose2.setBehaaldePunten(2);
            prognoseRepository.save(prognose2);
        };
    }
}
