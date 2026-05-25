package mig.project.controllers;

import mig.project.config.SecurityConfig;
import mig.project.service.TeamService;
import mig.project.service.WedstrijdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WedstrijdService wedstrijdService;

    @MockBean
    private TeamService teamService;

    @Test
    void homePaginaToontWedstrijdenEnTopTeams() throws Exception {
        when(wedstrijdService.getAlleWedstrijdenGesorteerd()).thenReturn(List.of());
        when(teamService.getTop10Teams()).thenReturn(List.of());

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("wedstrijden"))
                .andExpect(model().attributeExists("topTeams"));
    }
}
