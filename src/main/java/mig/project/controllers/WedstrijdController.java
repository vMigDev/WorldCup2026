package mig.project.controllers;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Wedstrijd;
import mig.project.service.WedstrijdService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


//controller voor het detailscherm van een wedstrijd en het opslaan van prognoses
@Controller
@RequiredArgsConstructor
@RequestMapping("/wedstrijd")
public class WedstrijdController {

    private final WedstrijdService wedstrijdService;

    //toont gedetaileerde info van 1 wedstrijd met capaciteit en eigen prognose
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication authentication, Model model) {
        Wedstrijd wedstrijd = wedstrijdService.getWedstrijdById(id);
        model.addAttribute("wedstrijd", wedstrijd);
        model.addAttribute("capaciteit", wedstrijdService.getCapaciteit(wedstrijd.getStadiumcode()));

        if (authentication != null) {
            model.addAttribute("eigenPrognose",
                    wedstrijdService.getPrognoseVanGebruikerVoorWedstrijd(authentication.getName(), wedstrijd).orElse(null));
            model.addAttribute("magWijzigen", wedstrijdService.magPrognoseWijzigen(wedstrijd.getDatumTijd()));
        }

        return "wedstrijd-detail";
    }

    //slaat de prognose van de ingelogde gebruiker op
    @PostMapping("/{id}/prognose")
    public String slaPrognoseOp(
            @PathVariable Long id,
            @RequestParam Integer doelpuntenA,
            @RequestParam Integer doelpuntenB,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            wedstrijdService.slaPrognoseOp(id, authentication.getName(), doelpuntenA, doelpuntenB);
            redirectAttributes.addFlashAttribute("succes", "msg.prognose.opgeslagen");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("foutmelding", ex.getMessage());
        }
        return "redirect:/wedstrijd/" + id;
    }
}
