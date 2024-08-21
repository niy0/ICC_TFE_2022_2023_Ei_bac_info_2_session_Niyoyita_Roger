package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

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

    @Size(max = 50)
    private String nom;

    @Size(max = 50)
    private String prenom;

    @JsonIgnore
    @ManyToOne
    private User utilisateur;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Date dateCreation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modification", nullable = false)
    private Date dateModification;

    protected Adresse() {
    }

    public Adresse(String localite, String rue, String numero, String codePostal, String departement, String ville, String pays) {
        this.localite = localite;
        this.rue = rue;
        this.numero = numero;
        this.codePostal = codePostal;
        this.departement = departement;
        this.ville = ville;
        this.pays = pays;
    }

    public Adresse(String localite, String rue, String numero, String codePostal, String departement, String ville, String pays, String nom, String prenom) {
        this.localite = localite;
        this.rue = rue;
        this.numero = numero;
        this.codePostal = codePostal;
        this.departement = departement;
        this.ville = ville;
        this.pays = pays;
        this.nom = nom;
        this.prenom = prenom;
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = new Date();
        dateModification = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = new Date();
    }

    // Getters and setters


    public Long getId() {
        return id;
    }

    public String getLocalite() {
        return localite;
    }

    public String getRue() {
        return rue;
    }

    public String getNumero() {
        return numero;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getDepartement() {
        return departement;
    }

    public String getVille() {
        return ville;
    }

    public String getPays() {
        return pays;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
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
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                '}';
    }
}
