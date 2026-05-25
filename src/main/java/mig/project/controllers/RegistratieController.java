package mig.project.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import mig.project.service.GebruikerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Validated
public class RegistratieController {

    private final GebruikerService gebruikerService;

    @GetMapping("/registreer")
    public String toonFormulier() {
        return "registreer";
    }

    @PostMapping("/registreer")
    public String registreer(
            @RequestParam @NotBlank String gebruikersnaam,
            @RequestParam @NotBlank String wachtwoord,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            gebruikerService.registreerGebruiker(gebruikersnaam, wachtwoord);
            redirectAttributes.addFlashAttribute("succes", "msg.registratie.succes");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("foutmelding", ex.getMessage());
            return "registreer";
        }
    }
}
