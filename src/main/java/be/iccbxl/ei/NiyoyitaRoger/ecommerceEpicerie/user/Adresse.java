package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    private String localite;

    @NotNull
    @Size(max = 100)
    private String rue;

    @NotNull
    @Size(max = 10)
    private String numero;

    @NotNull
    @Size(max = 10)
    private String codePostal;

    @NotNull
    @Size(max = 50)
    private String departement;

    // Autres propriétés de l'adresse

    @JsonIgnore
    @ManyToOne
    private User utilisateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public String toString() {
        return "Adresse{" +
                "id=" + id +
                ", localite='" + localite + '\'' +
                ", rue='" + rue + '\'' +
                ", numero='" + numero + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", departement='" + departement + '\'' +
                '}';
    }
}

