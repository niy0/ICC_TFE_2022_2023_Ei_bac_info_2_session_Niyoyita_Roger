package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignedecommande")
public class LigneDeCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    @JsonIgnore
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    private int quantite;

    private BigDecimal prixUnitaire; // Prix unitaire du produit au moment de la commande

    private BigDecimal montantTotal; // Montant total de la ligne de commande

    public LigneDeCommande() {
    }

    // Constructeurs, getters, setters et autres méthodes d'accès

    public LigneDeCommande(Produit produit, Panier panier, int quantite, BigDecimal prixUnitaire) {
        this.produit = produit;
        this.panier = panier;
        this.quantite = quantite;
        setPrixUnitaire(prixUnitaire); // Utilisation de la méthode setter pour initialiser le prix unitaire
        calculerMontantTotal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        // Mettre à jour automatiquement le prix unitaire lorsque le produit est défini
        if (produit != null) {
            setPrixUnitaire(produit.getPrix());
        } else {
            setPrixUnitaire(BigDecimal.valueOf(1.0)); // Mettez la valeur par défaut que vous souhaitez ici
        }
        calculerMontantTotal(); // Appel de la méthode pour recalculer le montant total
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
        calculerMontantTotal(); // Appel de la méthode pour recalculer le montant total
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerMontantTotal(); // Appel de la méthode pour recalculer le montant total
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void calculerMontantTotal() {
        montantTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    @Override
    public String toString() {
        return "LigneDeCommande{" +
                "id=" + id +
                ", produit=" + produit.getId() +
                ", panier=" + panier.getId() +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", montantTotal=" + montantTotal +
                '}';
    }
}
