package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    private String localite;

    @NotNull
    @Size(max = 100)
    private String rue;

    @NotNull
    @Size(max = 10)
    private String numero;

    @NotNull
    @Size(max = 10)
    private String codePostal;

    @NotNull
    @Size(max = 50)
    private String departement;

    // Autres propriétés de l'adresse

    @ManyToOne
    private User utilisateur;

    // Getters, setters, constructeurs, etc.
}

