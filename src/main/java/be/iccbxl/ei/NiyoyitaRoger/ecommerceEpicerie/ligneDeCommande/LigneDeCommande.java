package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name="lignedecommande")
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

    private double prixUnitaire; // Prix unitaire du produit au moment de la commande

    private double montantTotal; // Montant total de la ligne de commande


    protected LigneDeCommande() {}

    public LigneDeCommande(Produit produit, Panier panier, int quantite, double prixUnitaire, double montantTotal) {
        this.produit = produit;
        this.panier = panier;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantTotal = montantTotal;
    }

    // Constructeurs, getters, setters et autres méthodes d'accès


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
            this.prixUnitaire = produit.getPrix();
        } else {
            this.prixUnitaire = 1.0; // Mettez la valeur par défaut que vous souhaitez ici
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

    public double getPrixUnitaire() {
        return prixUnitaire;
    } //binder avec Produit.getPrix()m

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerMontantTotal(); // Appel de la méthode pour recalculer le montant total
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void calculerMontantTotal() {
        montantTotal = quantite * prixUnitaire;
    }
}

