package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneDeCommande> lignesDeCommande = new ArrayList<>();

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public Panier() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    public void addLigneDeCommande(LigneDeCommande ligneDeCommande) {
        lignesDeCommande.add(ligneDeCommande);
        ligneDeCommande.setPanier(this);
    }

    public void removeLigneDeCommande(LigneDeCommande ligneDeCommande) {
        lignesDeCommande.remove(ligneDeCommande);
        ligneDeCommande.setPanier(null);
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
}
