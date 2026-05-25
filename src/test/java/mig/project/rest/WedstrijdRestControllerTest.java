package mig.project.rest;

import mig.project.config.SecurityConfig;
import mig.project.domein.Wedstrijd;
import mig.project.service.WedstrijdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WedstrijdRestController.class)
@Import(SecurityConfig.class)
class WedstrijdRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WedstrijdService wedstrijdService;

    @Test
    void filtertWedstrijdenOpDatum() throws Exception {
        Wedstrijd match = new Wedstrijd();
        match.setId(1L);
        match.setLandA("Belgie");
        match.setLandB("Brazilie");
        match.setDatumTijd(LocalDateTime.of(2026, 6, 12, 18, 0));
        match.setLocatie("NY - MetLife");

        when(wedstrijdService.getWedstrijdenOpDatum(java.time.LocalDate.of(2026, 6, 12))).thenReturn(List.of(match));

        mockMvc.perform(get("/api/wedstrijden/datum/2026-06-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].landA").value("Belgie"));
    }

    @Test
    void capaciteitEndpointWerkt() throws Exception {
        mockMvc.perform(get("/api/wedstrijden/capaciteit/1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(12340));
    }
}
