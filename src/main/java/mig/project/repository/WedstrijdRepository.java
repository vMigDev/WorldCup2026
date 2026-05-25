package mig.project.repository;

import mig.project.domein.Wedstrijd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WedstrijdRepository extends JpaRepository<Wedstrijd, Long> {

    List<Wedstrijd> findAllByOrderByDatumTijdAsc();

    List<Wedstrijd> findByDatumTijdBetween(LocalDateTime start, LocalDateTime eind);

    boolean existsByDatumTijdAndLocatie(LocalDateTime datumTijd, String locatie);

    boolean existsByDatumTijdAndLocatieAndIdNot(LocalDateTime datumTijd, String locatie, Long id);
}
