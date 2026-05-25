package mig.project.controllers;

import lombok.RequiredArgsConstructor;
import mig.project.domein.Gebruiker;
import mig.project.domein.Team;
import mig.project.repository.GebruikerRepository;
import mig.project.service.TeamService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final GebruikerRepository gebruikerRepository;

    @GetMapping("/top10")
    public String top10(Model model) {
        model.addAttribute("teams", teamService.getTop10Teams());
        return "top10";
    }

    @GetMapping("/team")
    public String teamVanGebruiker(Authentication authentication, RedirectAttributes redirectAttributes) {
        Gebruiker gebruiker = getHuidigeGebruiker(authentication);
        if (gebruiker.getTeam() == null) {
            redirectAttributes.addFlashAttribute("foutmelding", "error.team.geen.team");
            return "redirect:/team/nieuw";
        }
        return "redirect:/team/" + gebruiker.getTeam().getId();
    }

    @GetMapping("/team/nieuw")
    public String nieuwTeamScherm() {
        return "team-nieuw";
    }

    @PostMapping("/team/nieuw")
    public String maakTeam(@RequestParam String teamNaam, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Gebruiker gebruiker = getHuidigeGebruiker(authentication);
            Team team = teamService.maakTeamAan(teamNaam, gebruiker);
            gebruikerRepository.save(gebruiker);
            redirectAttributes.addFlashAttribute("succes", "msg.team.aangemaakt");
            return "redirect:/team/" + team.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("foutmelding", ex.getMessage());
            return "redirect:/team/nieuw";
        }
    }

    @PostMapping("/team/join")
    public String joinTeam(@RequestParam String uitnodigingscode, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Gebruiker gebruiker = getHuidigeGebruiker(authentication);
            Team team = teamService.sluitAanBijTeam(uitnodigingscode, gebruiker);
            gebruikerRepository.save(gebruiker);
            redirectAttributes.addFlashAttribute("succes", "msg.team.join.succes");
            return "redirect:/team/" + team.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("foutmelding", ex.getMessage());
            return "redirect:/team/nieuw";
        }
    }

    @GetMapping("/team/{id}")
    public String teamDetail(@PathVariable Long id, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        Team team = teamService.getTeamById(id);
        Gebruiker gebruiker = getHuidigeGebruiker(authentication);
        if (gebruiker.getTeam() == null || !gebruiker.getTeam().getId().equals(id)) {
            redirectAttributes.addFlashAttribute("foutmelding", "error.team.geen.toegang");
            return "redirect:/home";
        }

        model.addAttribute("team", team);
        model.addAttribute("totaleScore", teamService.berekenTotaalScore(team));
        model.addAttribute("lidScores", teamService.getLidScores(team));
        model.addAttribute("isEigenaar", team.getEigenaar() != null && team.getEigenaar().getId().equals(gebruiker.getId()));
        return "team-detail";
    }

    @PostMapping("/team/{id}/regenerate")
    public String regenerateCode(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Gebruiker gebruiker = getHuidigeGebruiker(authentication);
            teamService.genereerNieuweUitnodigingscode(id, gebruiker);
            redirectAttributes.addFlashAttribute("succes", "msg.team.code.vernieuwd");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("foutmelding", ex.getMessage());
        }
        return "redirect:/team/" + id;
    }

    @PostMapping("/team/{id}/leden/{lidId}/verwijder")
    public String verwijderLid(@PathVariable Long id,
                               @PathVariable Long lidId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            Gebruiker gebruiker = getHuidigeGebruiker(authentication);
            teamService.verwijderLid(id, lidId, gebruiker);
            redirectAttributes.addFlashAttribute("succes", "msg.team.lid.verwijderd");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("foutmelding", ex.getMessage());
        }
        return "redirect:/team/" + id;
    }

    private Gebruiker getHuidigeGebruiker(Authentication authentication) {
        return gebruikerRepository.findByGebruikersnaam(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("error.gebruiker.nietgevonden"));
    }
}
