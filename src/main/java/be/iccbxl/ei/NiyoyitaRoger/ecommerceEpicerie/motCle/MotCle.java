package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="mot_cle")
public class MotCle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", length = 40, nullable = false)
    private String nom;

    protected MotCle() {}

    public MotCle(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    @ManyToMany(mappedBy = "motsCles")
    @JsonIgnore
    private Set<Produit> produits = new HashSet<>();

    // Ajoutez des méthodes get et set pour produits si nécessaire
    public Set<Produit> getProduits() {
        return produits;
    }

    public void setProduits(Set<Produit> produits) {
        this.produits = produits;
    }

    public String getNom() {
        return nom;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MotCle motCle = (MotCle) o;
        return Objects.equals(id, motCle.id) && Objects.equals(nom, motCle.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }


    @Override
    public String toString() {
        return "MotCle{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
