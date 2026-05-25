package mig.project.repository;

import mig.project.domein.Gebruiker;
import mig.project.domein.Prognose;
import mig.project.domein.Wedstrijd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrognoseRepository extends JpaRepository<Prognose, Long> {

    List<Prognose> findByWedstrijd(Wedstrijd wedstrijd);

    List<Prognose> findByGebruiker(Gebruiker gebruiker);

    Optional<Prognose> findByGebruikerAndWedstrijd(Gebruiker gebruiker, Wedstrijd wedstrijd);

    @Query("select coalesce(sum(p.behaaldePunten), 0) from Prognose p where p.gebruiker.id = :gebruikerId")
    Integer somPuntenVanGebruiker(Long gebruikerId);
}
