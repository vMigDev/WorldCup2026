package mig.project.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mig.project.domein.Wedstrijd;
import mig.project.service.ScoreService;
import mig.project.service.WedstrijdService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/wedstrijden")
public class AdminWedstrijdController {

    private final WedstrijdService wedstrijdService;
    private final ScoreService scoreService;

    //toont het overzicht met alle wedstrijden voor beheren
    @GetMapping
    public String overzicht(Model model) {
        model.addAttribute("wedstrijden", wedstrijdService.getAlleWedstrijdenGesorteerd());
        return "admin-wedstrijden";
    }

    //toont het formulier om een nieuwe wedstrijd aan te maken
    @GetMapping("/nieuw")
    public String nieuw(Model model) {
        model.addAttribute("wedstrijd", new Wedstrijd());
        return "admin-wedstrijd-form";
    }

    //verwerkt het formulier voor het aanmaken van een wedstrijd
    @PostMapping("/nieuw")
    public String maak(@Valid @ModelAttribute("wedstrijd") Wedstrijd wedstrijd,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin-wedstrijd-form";
        }

        try {
            wedstrijdService.maakWedstrijdAan(wedstrijd);
            redirectAttributes.addFlashAttribute("succes", "msg.wedstrijd.toegevoegd");
            return "redirect:/admin/wedstrijden";
        } catch (IllegalArgumentException ex) {
            voegBusinessFoutToeAanJuisteVeld(bindingResult, ex.getMessage());
            return "admin-wedstrijd-form";
        }
    }

    //toont het formulier om een bestaande wedstrijd te wijzigen
    @GetMapping("/{id}/bewerk")
    public String bewerk(@PathVariable Long id, Model model) {
        model.addAttribute("wedstrijd", wedstrijdService.getWedstrijdById(id));
        return "admin-wedstrijd-form";
    }

    //slaat wijzigingen van een bestaande wedstrijd op
    @PostMapping("/{id}/bewerk")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("wedstrijd") Wedstrijd wedstrijd,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin-wedstrijd-form";
        }

        try {
            wedstrijdService.wijzigWedstrijd(id, wedstrijd);
            redirectAttributes.addFlashAttribute("succes", "msg.wedstrijd.gewijzigd");
            return "redirect:/admin/wedstrijden";
        } catch (IllegalArgumentException ex) {
            voegBusinessFoutToeAanJuisteVeld(bindingResult, ex.getMessage());
            return "admin-wedstrijd-form";
        }
    }

    //slaat de official uitslag op en triggert scoreberekening
    @PostMapping("/{id}/uitslag")
    public String uitslag(@PathVariable Long id,
                          @RequestParam(required = false) Integer doelpuntenA,
                          @RequestParam(required = false) Integer doelpuntenB,
                          RedirectAttributes redirectAttributes) {

        if (doelpuntenA == null || doelpuntenB == null) {
            redirectAttributes.addFlashAttribute("foutmelding", "validation.doelpunten.verplicht");
            return "redirect:/admin/wedstrijden";
        }

        Wedstrijd wedstrijd = wedstrijdService.slaUitslagOp(id, doelpuntenA, doelpuntenB);
        scoreService.berekenEnSlaScoresOp(wedstrijd);
        redirectAttributes.addFlashAttribute("succes", "msg.wedstrijd.uitslag.opgeslagen");
        return "redirect:/admin/wedstrijden";
    }

    private void voegBusinessFoutToeAanJuisteVeld(BindingResult bindingResult, String foutCode) {
        switch (foutCode) {
            case "error.wedstrijd.land.gelijk" -> bindingResult.rejectValue("landB", foutCode);
            case "error.wedstrijd.datum.periode", "error.wedstrijd.datum.toekomst" ->
                    bindingResult.rejectValue("datumTijd", foutCode);
            case "error.wedstrijd.uniek.locatie.tijd" -> bindingResult.rejectValue("locatie", foutCode);
            default -> bindingResult.reject("business", foutCode);
        }
    }
}
