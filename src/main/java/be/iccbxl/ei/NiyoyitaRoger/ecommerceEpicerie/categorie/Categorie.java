package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

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


    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +

                '}';
    }
}
