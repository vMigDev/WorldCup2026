package mig.project.service;

import mig.project.rest.dto.WedstrijdRestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class StadionWebClientService {

    private final WebClient webClient;

    public StadionWebClientService(WebClient.Builder webClientBuilder,
                                   @Value("${app.rest.base-url:http://localhost:8080}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<List<WedstrijdRestDto>> haalWedstrijdenOpDatum(LocalDate datum) {
        return this.webClient.get()
                .uri("/api/wedstrijden/datum/{datum}", datum)
                .retrieve()
                .bodyToFlux(WedstrijdRestDto.class)
                .collectList();
    }

    public Mono<Integer> haalCapaciteitOp(Integer stadiumcode) {
        return this.webClient.get()
                .uri("/api/wedstrijden/capaciteit/{stadiumcode}", stadiumcode)
                .retrieve()
                .bodyToMono(Integer.class);
    }
}