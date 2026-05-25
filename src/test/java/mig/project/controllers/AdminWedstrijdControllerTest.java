package mig.project.controllers;

import mig.project.config.SecurityConfig;
import mig.project.service.ScoreService;
import mig.project.service.WedstrijdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminWedstrijdController.class)
@Import(SecurityConfig.class)
class AdminWedstrijdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WedstrijdService wedstrijdService;

    @MockBean
    private ScoreService scoreService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void nieuwWedstrijdFormToontVeldfoutBijLegeVerplichteVelden() throws Exception {
        mockMvc.perform(post("/admin/wedstrijden/nieuw")
                        .with(csrf())
                        .param("landA", "")
                        .param("landB", "Brazilië")
                        .param("datumTijd", "2026-06-20T18:00")
                        .param("locatie", "MetLife Stadium")
                        .param("stadiumcode", "1234")
                        .param("checksum", "70"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-wedstrijd-form"))
                .andExpect(model().attributeHasFieldErrors("wedstrijd", "landA"));

        verify(wedstrijdService, never()).maakWedstrijdAan(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void businessValidatieLandenGelijkWordtAlsVeldfoutGetoond() throws Exception {
        doThrow(new IllegalArgumentException("error.wedstrijd.land.gelijk"))
                .when(wedstrijdService).maakWedstrijdAan(any());

        mockMvc.perform(post("/admin/wedstrijden/nieuw")
                        .with(csrf())
                        .param("landA", "België")
                        .param("landB", "België")
                        .param("datumTijd", "2026-06-20T18:00")
                        .param("locatie", "MetLife Stadium")
                        .param("stadiumcode", "1234")
                        .param("checksum", "70"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-wedstrijd-form"))
                .andExpect(model().attributeHasFieldErrors("wedstrijd", "landB"));
    }
}
