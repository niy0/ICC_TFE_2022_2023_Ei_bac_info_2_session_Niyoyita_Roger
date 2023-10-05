package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotBlank
    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @NotNull
    @Column(name = "prix", nullable = false)
    private Double prix;

    @NotNull
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "marque", length = 40)
    private String marque;

    @Lob
    @Column(name = "image_principale")
    private Blob imagePrincipale;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @Column(name = "disponibilite", nullable = false)
    private Boolean disponibilite = true;


    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ManyToMany
    @JoinTable(
            name = "produit_mots_cles",
            joinColumns = @JoinColumn(name = "produit_id"),
            inverseJoinColumns = @JoinColumn(name = "mot_cle_id")
    )
    private Set<MotCle> motsCles = new HashSet<>();  // Assuming you have an entity MotCle

    // Constructors
    public Produit() {
        this.dateCreation = LocalDateTime.now();
    }

    public Produit(String nom, String description, Double prix, Integer quantite, Blob imagePrincipale) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantite = quantite;
        this.imagePrincipale = imagePrincipale;
        this.dateCreation = LocalDateTime.now();
        updateDisponibilite();
    }

    // Getters
    public Long getId() { return id; }

    public String getNom() { return nom; }

    public String getDescription() { return description; }

    public Double getPrix() { return prix; }

    public Integer getQuantite() { return quantite; }

    public String getMarque() { return marque; }

    public Blob getImagePrincipale() { return imagePrincipale; }

    public Categorie getCategorie() { return categorie; }

    public Boolean getDisponibilite() { return disponibilite; }

    public LocalDateTime getDateCreation() { return dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }

    public Set<MotCle> getMotsCles() { return motsCles; }

    // Setters
    public void setId(Long id) { this.id = id; }

    public void setNom(String nom) { this.nom = nom; }

    public void setDescription(String description) { this.description = description; }

    public void setPrix(Double prix) { this.prix = prix; }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
        updateDisponibilite();
    }

    public void setMarque(String marque) { this.marque = marque; }

    public void setImagePrincipale(Blob imagePrincipale) { this.imagePrincipale = imagePrincipale; }

    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public void setDisponibilite(Boolean disponibilite) { this.disponibilite = disponibilite; }

    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public void setMotsCles(Set<MotCle> motsCles) { this.motsCles = motsCles; }

    // Additional Methods
    public void updateDisponibilite() {
        this.disponibilite = this.quantite > 0;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", marque='" + marque + '\'' +
                ", imagePrincipale=" + imagePrincipale +
                ", categorie=" + categorie +
                ", disponibilite=" + disponibilite +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", motsCles=" + motsCles +
                '}';
    }

    public List<String> listMotsCle(){
        List<String> res = new ArrayList<>();

        this.getMotsCles().forEach(mot -> res.add(mot+ " "));

        return res;
    }

    //permet de mettre en maj la première lettre du mot
    public String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
