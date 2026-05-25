package mig.project.repository;

import mig.project.domein.Gebruiker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GebruikerRepository extends JpaRepository<Gebruiker, Long> {
    Optional<Gebruiker> findByGebruikersnaam(String gebruikersnaam);
}
