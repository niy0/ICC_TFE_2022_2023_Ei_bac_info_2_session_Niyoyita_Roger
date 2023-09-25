package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import jakarta.persistence.*;

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
    private List<Produit> produits;

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

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +

                '}';
    }
}
