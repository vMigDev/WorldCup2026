package mig.project.controllers;

import lombok.RequiredArgsConstructor;
import mig.project.service.TeamService;
import mig.project.service.WedstrijdService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final WedstrijdService wedstrijdService;
    private final TeamService teamService;

    @GetMapping({"/", "/home"})
    public String toonHome(Model model) {
        model.addAttribute("wedstrijden", wedstrijdService.getAlleWedstrijdenGesorteerd());
        model.addAttribute("topTeams", teamService.getTop10Teams());
        return "home";
    }

    @GetMapping("/login")
    public String toonLogin() {
        return "login";
    }
}
