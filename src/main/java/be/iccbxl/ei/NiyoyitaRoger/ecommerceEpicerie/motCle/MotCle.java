package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle;

import jakarta.persistence.*;

@Entity
@Table(name="motCle")
public class MotCle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    protected MotCle() {}

    public MotCle(String nom) {
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
        return "MotCle{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
