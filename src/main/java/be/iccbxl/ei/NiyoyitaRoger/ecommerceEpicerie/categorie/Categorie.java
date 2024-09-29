package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "categorie")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cat_nom", length = 40, nullable = false)
    private String nom;

    @OneToMany(mappedBy = "categorie")
    @JsonIgnore
    private List<Produit> produits;

    @Column(name = "taux_tva", nullable = false)
    private BigDecimal tauxTVA;

    public Categorie() {
    }

    public Categorie(String nom) {
        this.nom = nom;
    }
    
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    public BigDecimal getTauxTVA() {
        return tauxTVA;
    }

    public void setTauxTVA(BigDecimal tauxTVA) {
        this.tauxTVA = tauxTVA;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
