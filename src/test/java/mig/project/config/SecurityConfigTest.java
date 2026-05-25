package mig.project.config;

import mig.project.controllers.AdminWedstrijdController;
import mig.project.controllers.TeamController;
import mig.project.repository.GebruikerRepository;
import mig.project.service.ScoreService;
import mig.project.service.TeamService;
import mig.project.service.WedstrijdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({TeamController.class, AdminWedstrijdController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @MockBean
    private GebruikerRepository gebruikerRepository;

    @MockBean
    private WedstrijdService wedstrijdService;

    @MockBean
    private ScoreService scoreService;

    @Test
    void teamPaginaVraagtLogin() throws Exception {
        mockMvc.perform(get("/team"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminPadIsVerbodenVoorNietAdmin() throws Exception {
        mockMvc.perform(get("/admin/wedstrijden"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPadIsToegankelijkVoorAdmin() throws Exception {
        when(wedstrijdService.getAlleWedstrijdenGesorteerd()).thenReturn(List.of());

        mockMvc.perform(get("/admin/wedstrijden"))
                .andExpect(status().isOk());
    }
}
