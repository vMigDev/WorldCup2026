package mig.project.domein;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validation.teamnaam.nietleeg}")
    @Column(nullable = false, unique = true)
    private String teamNaam;

    @NotBlank(message = "{validation.uitnodigingscode.nietleeg}")
    @Size(min = 8, message = "{validation.uitnodigingscode.lengte}")
    @Column(nullable = false, unique = true)
    private String uitnodigingscode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eigenaar_id")
    private Gebruiker eigenaar;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Gebruiker> leden = new ArrayList<>();
}
