package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;


@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User utilisateur;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCommande;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDerniereMajStatut;

    @OneToMany(mappedBy = "commande")
    private List<ChangementStatutCommande> historiqueStatuts;


    /**
     * <dependency>
     *     <groupId>org.springframework.boot</groupId>
     *     <artifactId>spring-boot-starter-mail</artifactId>
     * </dependency>
     *
     * spring.mail.host=your-smtp-server.com
     * spring.mail.port=587
     * spring.mail.username=your-username
     * spring.mail.password=your-password
     * spring.mail.properties.mail.smtp.auth=true
     * spring.mail.properties.mail.smtp.starttls.enable=true
     */
}

