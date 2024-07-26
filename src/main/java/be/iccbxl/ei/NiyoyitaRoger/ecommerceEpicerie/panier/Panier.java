package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Panier implements Serializable {
    private static final long serialVersionUID = 1333887242125065122L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LigneDeCommande> lignesDeCommande = new LinkedList<>();

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User utilisateur;

    private Boolean actif = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Transient
    private BigDecimal montantTotalPanier;

    public Panier() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        this.montantTotalPanier = BigDecimal.ZERO;
    }

    private BigDecimal calculerMontantTotal() {
        if (lignesDeCommande == null || lignesDeCommande.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return lignesDeCommande.stream()
                .map(LigneDeCommande::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addLigneDeCommande(LigneDeCommande ligne) {
        if (ligne != null) {
            lignesDeCommande.add(ligne);
        }
        recalculerMontantTotalPanier();
    }

    public void removeLigneDeCommande(LigneDeCommande ligneDeCommande) {
        if (lignesDeCommande.remove(ligneDeCommande)) { // Vérifie si la ligne a bien été supprimée
            ligneDeCommande.setPanier(null); // Retire le panier de la ligne de commande
        }
        recalculerMontantTotalPanier();
    }

    public void removeFirstLigneDeCommande() {
        if (!lignesDeCommande.isEmpty()) {
            LigneDeCommande firstLigne = (LigneDeCommande) ((LinkedList<LigneDeCommande>) lignesDeCommande).removeFirst();
            if (firstLigne != null) {
                firstLigne.setPanier(null);
            }
            recalculerMontantTotalPanier();
        }
    }

    public void recalculerMontantTotalPanier() {
        montantTotalPanier = calculerMontantTotal();
    }

    public void mergeWith(Panier panierTemporaire) {
        for (LigneDeCommande ligneTemp : panierTemporaire.getLignesDeCommande()) {
            boolean found = false;
            for (LigneDeCommande ligneCourante : this.getLignesDeCommande()) {
                if (ligneCourante.getId().equals(ligneTemp.getId())) {
                    int nouvelleQuantite = ligneCourante.getQuantite() + ligneTemp.getQuantite();
                    ligneCourante.setQuantite(nouvelleQuantite);
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.addLigneDeCommande(ligneTemp);
            }
        }
        recalculerMontantTotalPanier();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LigneDeCommande> getLignesDeCommande() {
        return lignesDeCommande;
    }

    public void setLignesDeCommande(List<LigneDeCommande> lignesDeCommande) {
        this.lignesDeCommande = lignesDeCommande;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public BigDecimal getMontantTotalPanier() {
        recalculerMontantTotalPanier();
        return montantTotalPanier.setScale(2, RoundingMode.HALF_UP);
    }

    public void setMontantTotalPanier(BigDecimal montantTotalPanier) {
        this.montantTotalPanier = montantTotalPanier;
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id=" + id +
                ", actif=" + (actif != null ? actif : "null") +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", montantTotalPanier=" + calculerMontantTotal() +
                ", utilisateur=" + (utilisateur != null ? utilisateur.getId() : "null") +
                '}';
    }
}
