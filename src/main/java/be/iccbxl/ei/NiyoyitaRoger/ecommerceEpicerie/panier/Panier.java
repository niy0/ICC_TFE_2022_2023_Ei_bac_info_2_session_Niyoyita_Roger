package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Panier implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LigneDeCommande> lignesDeCommande = new ArrayList<>();

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // champ pour stocker le montant total du panier
    @Transient // Pour indiquer à JPA de ne pas persister cet attribut en base de données
    private double montantTotalPanier;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User utilisateur;

    public Panier() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        // Initialisez le montant total du panier avec 0.0 par défaut
        this.montantTotalPanier = 0.0;
    }

    public void addLigneDeCommande(LigneDeCommande ligneDeCommande) {
        lignesDeCommande.add(ligneDeCommande);
        ligneDeCommande.setPanier(this);
        recalculerMontantTotalPanier(); // Mettez à jour le montant total du panier
    }

    public void removeLigneDeCommande(LigneDeCommande ligneDeCommande) {
        lignesDeCommande.remove(ligneDeCommande);
        ligneDeCommande.setPanier(null);
        recalculerMontantTotalPanier(); // Mettez à jour le montant total du panier
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
        recalculerMontantTotalPanier(); // Mettez à jour le montant total du panier lorsque la liste change
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
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

    // Ajoutez cette méthode pour recalculer et mettre à jour le montant total du panier
    public void recalculerMontantTotalPanier() {
        double montantTotalPanier = 0.0;
        for (LigneDeCommande ligneDeCommande : lignesDeCommande) {
            montantTotalPanier += ligneDeCommande.getMontantTotal();
        }
        this.montantTotalPanier = montantTotalPanier;
    }

    // Ajoutez un getter pour récupérer le montant total du panier
    public double getMontantTotalPanier() {
        return montantTotalPanier;
    }

    public void mergeWith(Panier panierTemporaire) {
    }

    public void setMontantTotalPanier(double montantTotalPanier) {
        this.montantTotalPanier = montantTotalPanier;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id=" + id +
                ", lignesDeCommande=" + lignesDeCommande +
                ", actif=" + actif +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", montantTotalPanier=" + montantTotalPanier +
                ", utilisateur=" + utilisateur +
                '}';
    }
}