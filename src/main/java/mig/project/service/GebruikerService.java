package mig.project.service;

import mig.project.domein.Gebruiker;

public interface GebruikerService {

    Gebruiker registreerGebruiker(String gebruikersnaam, String wachtwoord);

    Gebruiker getByGebruikersnaam(String gebruikersnaam);
}
