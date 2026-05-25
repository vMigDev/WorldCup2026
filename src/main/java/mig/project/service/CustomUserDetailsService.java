package mig.project.service;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Gebruiker;
import mig.project.repository.GebruikerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final GebruikerRepository gebruikerRepository;

    @Override
    public UserDetails loadUserByUsername(String gebruikersnaam) throws UsernameNotFoundException {
        Gebruiker gebruiker = gebruikerRepository.findByGebruikersnaam(gebruikersnaam)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden: " + gebruikersnaam));

        return User.builder()
                .username(gebruiker.getGebruikersnaam())
                .password(gebruiker.getWachtwoord())
                .roles(gebruiker.getRol())
                .build();
    }
}
