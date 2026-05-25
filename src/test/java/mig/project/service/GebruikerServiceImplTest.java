package mig.project.service;

import mig.project.domein.Gebruiker;
import mig.project.repository.GebruikerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GebruikerServiceImplTest {

    @Mock
    private GebruikerRepository gebruikerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private GebruikerServiceImpl gebruikerService;

    @Test
    void registreerGebruikerEncodeertWachtwoordEnZetUserRol() {
        when(gebruikerRepository.findByGebruikersnaam("mike")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1")).thenReturn("ENC");
        when(gebruikerRepository.save(org.mockito.ArgumentMatchers.any(Gebruiker.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Gebruiker opgeslagen = gebruikerService.registreerGebruiker("mike", "Password1");

        ArgumentCaptor<Gebruiker> captor = ArgumentCaptor.forClass(Gebruiker.class);
        verify(gebruikerRepository).save(captor.capture());
        assertEquals("ENC", captor.getValue().getWachtwoord());
        assertEquals("USER", captor.getValue().getRol());
        assertEquals("mike", opgeslagen.getGebruikersnaam());
    }

    @Test
    void registreerGebruikerMetDubbeleNaamFaalt() {
        when(gebruikerRepository.findByGebruikersnaam("bestaat"))
                .thenReturn(Optional.of(new Gebruiker()));

        assertThrows(IllegalArgumentException.class,
                () -> gebruikerService.registreerGebruiker("bestaat", "Password1"));
    }

    @Test
    void registreerGebruikerMetTeKortWachtwoordFaalt() {
        when(gebruikerRepository.findByGebruikersnaam("nieuw")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> gebruikerService.registreerGebruiker("nieuw", "kort"));
    }
}
