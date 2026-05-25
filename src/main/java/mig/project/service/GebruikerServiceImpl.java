package mig.project.service;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Gebruiker;
import mig.project.repository.GebruikerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GebruikerServiceImpl implements GebruikerService {

    private final GebruikerRepository gebruikerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Gebruiker registreerGebruiker(String gebruikersnaam, String wachtwoord) {
        if (gebruikerRepository.findByGebruikersnaam(gebruikersnaam).isPresent()) {
            throw new IllegalArgumentException("error.gebruiker.bestaat");
        }
        if (wachtwoord == null || wachtwoord.length() < 8) {
            throw new IllegalArgumentException("validation.wachtwoord.lengte");
        }

        Gebruiker gebruiker = new Gebruiker();
        gebruiker.setGebruikersnaam(gebruikersnaam);
        gebruiker.setWachtwoord(passwordEncoder.encode(wachtwoord));
        gebruiker.setRol("USER");

        return gebruikerRepository.save(gebruiker);
    }

    @Override
    @Transactional(readOnly = true)
    public Gebruiker getByGebruikersnaam(String gebruikersnaam) {
        return gebruikerRepository.findByGebruikersnaam(gebruikersnaam)
                .orElseThrow(() -> new IllegalArgumentException("error.gebruiker.nietgevonden"));
    }
}
