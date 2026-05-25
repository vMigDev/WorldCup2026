package mig.project.domein;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gebruikers")
@Getter
@Setter
@NoArgsConstructor
public class Gebruiker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validation.gebruikersnaam.nietleeg}")
    @Column(nullable = false, unique = true)
    private String gebruikersnaam;

    @NotBlank(message = "{validation.wachtwoord.nietleeg}")
    @Column(nullable = false)
    private String wachtwoord;

    @NotBlank(message = "{validation.rol.nietleeg}")
    @Column(nullable = false)
    private String rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "gebruiker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prognose> prognoses = new ArrayList<>();
}
