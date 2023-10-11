package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque.Marque;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marque_id")
    private Marque marque;

    @Lob
    @Column(name = "image_principale", nullable = false)
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

    //*************admin/manager manage*************
    //min,middle,max,cote,actif, type de prix

    @Column()
    private Integer minStock = 1;

    @Column()
    private Integer middleStock = null;

    @Column()
    private Integer maxStock = null;

    @Column()
    private Double cote = 5.;

    @Column()
    private Boolean actif = true;

    @Column(name = "type_prix", nullable = false)
    private String typePrix;
    //******************************

    @ManyToMany
    @JoinTable(
            name = "produit_mots_cles",
            joinColumns = @JoinColumn(name = "produit_id"),
            inverseJoinColumns = @JoinColumn(name = "mot_cle_id")
    )
    private Set<MotCle> motsCles = new HashSet<>();

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

    public Marque getMarque() {
        return marque;
    }

    public void setMarque(Marque marque) {
        this.marque = marque;
    }



    public Integer getMinStock() {
        return minStock;
    }

    public Integer getMiddleStock() {
        return middleStock;
    }

    public Integer getMaxStock() {
        return maxStock;
    }

    public Double getCote() {
        return cote;
    }

    public Boolean getActif() {
        return actif;
    }

    public String getTypePrix() {
        return typePrix;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    public void setMiddleStock(Integer middleStock) {
        this.middleStock = middleStock;
    }

    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }

    public void setCote(Double cote) {
        this.cote = cote;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public void setTypePrix(String typePrix) {
        this.typePrix = typePrix;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", marque=" + marque +
                ", imagePrincipale=" + imagePrincipale +
                ", categorie=" + categorie +
                ", disponibilite=" + disponibilite +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", minStock=" + minStock +
                ", middleStock=" + middleStock +
                ", maxStock=" + maxStock +
                ", cote=" + cote +
                ", actif=" + actif +
                ", typePrix='" + typePrix + '\'' +
                ", motsCles=" + motsCles +
                '}';
    }
}
