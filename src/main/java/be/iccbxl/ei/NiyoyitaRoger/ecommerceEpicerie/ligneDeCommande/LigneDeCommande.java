package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import jakarta.persistence.*;

@Entity
public class LigneDeCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    private int quantite;

    private double prixUnitaire; // Prix unitaire du produit au moment de la commande

    private double montantTotal; // Montant total de la ligne de commande

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
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerMontantTotal(); // Appel de la méthode pour recalculer le montant total
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public void calculerMontantTotal() {
        montantTotal = quantite * prixUnitaire;
    }
}

