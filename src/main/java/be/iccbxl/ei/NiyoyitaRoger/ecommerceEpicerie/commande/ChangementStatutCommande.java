package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ChangementStatutCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @Enumerated(EnumType.STRING)
    private StatutCommande ancienStatut;

    @Enumerated(EnumType.STRING)
    private StatutCommande nouveauStatut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateChangement;

    private String commentaire;

    // Getters, setters, constructeurs, etc.
}

