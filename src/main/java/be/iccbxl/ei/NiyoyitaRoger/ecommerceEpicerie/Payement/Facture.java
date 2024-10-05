package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.Adresse;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroFacture;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateFacture;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;  // Référence à la commande associée

    @Column(nullable = false)
    private BigDecimal montantHT;

    @Column(nullable = false)
    private BigDecimal montantTVA;

    @Column(nullable = false)
    private BigDecimal montantTTC;

    @ManyToOne(optional = false)
    @JoinColumn(name = "adresse_facturation_id", nullable = false)
    private Adresse adresseFacturation;  // Objet Adresse pour la facturation

    @ManyToOne(optional = false)
    @JoinColumn(name = "adresse_livraison_id", nullable = false)
    private Adresse adresseLivraison;  // Objet Adresse pour la livraison

    @PrePersist
    protected void onCreate() {
        dateFacture = new Date();
    }

    protected Facture() {
    }

    // Constructeur avec tous les paramètres
    public Facture(String numeroFacture, Date dateFacture, BigDecimal montantHT, BigDecimal montantTVA, BigDecimal montantTTC,
                   Commande commande, Adresse adresseFacturation, Adresse adresseLivraison) {
        this.numeroFacture = numeroFacture;
        this.dateFacture = dateFacture;
        this.montantHT = montantHT;
        this.montantTVA = montantTVA;
        this.montantTTC = montantTTC;
        this.commande = commande;
        this.adresseFacturation = adresseFacturation;
        this.adresseLivraison = adresseLivraison;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public Date getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(Date dateFacture) {
        this.dateFacture = dateFacture;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public BigDecimal getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(BigDecimal montantHT) {
        this.montantHT = montantHT;
    }

    public BigDecimal getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(BigDecimal montantTVA) {
        this.montantTVA = montantTVA;
    }

    public BigDecimal getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(BigDecimal montantTTC) {
        this.montantTTC = montantTTC;
    }

    public Adresse getAdresseFacturation() {
        return adresseFacturation;
    }

    public void setAdresseFacturation(Adresse adresseFacturation) {
        this.adresseFacturation = adresseFacturation;
    }

    public Adresse getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(Adresse adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    // Méthode pour calculer le montant HT, TVA et TTC
    public void calculerMontantFacture() {
        BigDecimal totalHT = BigDecimal.ZERO;
        BigDecimal totalTVA = BigDecimal.ZERO;

        // Itérer sur les lignes de commande pour calculer les montants par catégorie de TVA
        for (LigneDeCommande ligne : commande.getLignesDeCommande()) {
            BigDecimal montantHTLigne = ligne.getPrixUnitaire().multiply(new BigDecimal(ligne.getQuantite()));
            BigDecimal tauxTVA = ligne.getProduit().getTauxTVA(); // Supposons que chaque produit a un taux de TVA

            BigDecimal montantTVALigne = montantHTLigne.multiply(tauxTVA.divide(BigDecimal.valueOf(100)));

            totalHT = totalHT.add(montantHTLigne);
            totalTVA = totalTVA.add(montantTVALigne);
        }

        // Mettre à jour les champs de la facture
        this.montantHT = totalHT;
        this.montantTVA = totalTVA;
        this.montantTTC = totalHT.add(totalTVA);
    }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", numeroFacture='" + numeroFacture + '\'' +
                ", dateFacture=" + dateFacture +
                ", commande=" + commande +
                ", montantHT=" + montantHT +
                ", montantTVA=" + montantTVA +
                ", montantTTC=" + montantTTC +
                ", adresseFacturation=" + adresseFacturation +
                ", adresseLivraison=" + adresseLivraison +
                '}';
    }
}