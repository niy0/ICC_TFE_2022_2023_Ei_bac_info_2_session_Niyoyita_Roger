package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Size(max = 50)
    private String departement;

    @NotNull
    @Size(max = 50)
    private String ville;

    @NotNull
    @Size(max = 50)
    private String pays;

    @NotNull
    @NotBlank
    @Size(max = 50)
    private String nom;

    @NotNull
    @NotBlank
    @Size(max = 50)
    private String prenom;

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

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
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
                ", ville='" + ville + '\'' +
                ", pays='" + pays + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}
