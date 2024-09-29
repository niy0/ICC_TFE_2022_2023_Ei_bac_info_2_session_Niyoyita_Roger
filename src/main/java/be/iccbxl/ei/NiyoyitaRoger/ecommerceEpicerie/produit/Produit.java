package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque.Marque;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StreamUtils;

@Entity
@Table(name = "produit")
public class Produit implements Serializable {

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
    private BigDecimal prix;

    @NotNull
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marque_id")
    private Marque marque;

    @JsonIgnore
    @Lob
    @Column(name = "image_principale", nullable = false)
    private Blob imagePrincipale;

    private transient byte[] imagePrincipaleJson;

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

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @Column(name = "disponibilite", nullable = false)
    private Boolean disponibilite = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "taux_tva", nullable = false)
    private BigDecimal tauxTVA; // Taux de TVA pour le produit (5.5%, 20%, etc.)

    @NotNull
    @Column(name = "vues", nullable = false)
    private Integer vues = 0;  // Initialisation à 0 pour le nombre de vues

    @PrePersist
    @PreUpdate
    public void beforeSave() {
        // Appliquer le taux de TVA avant la persistance ou la mise à jour
        if (this.categorie != null) {
            this.tauxTVA = this.categorie.getTauxTVA();
        }

        // Définir la date de création si elle n'est pas encore définie
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }

        // Mettre à jour la date de modification
        this.dateModification = LocalDateTime.now();
    }

    @ManyToMany
    @JoinTable(
            name = "produit_mots_cles",
            joinColumns = @JoinColumn(name = "produit_id"),
            inverseJoinColumns = @JoinColumn(name = "mot_cle_id")
    )
    private Set<MotCle> motsCles = new HashSet<>();

    @ManyToMany(mappedBy = "produitsFavoris")
    private Set<User> utilisateursFavoris = new HashSet<>();

    protected Produit() {
    }

    // Les getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Marque getMarque() {
        return marque;
    }

    public void setMarque(Marque marque) {
        this.marque = marque;
    }

    @JsonIgnore
    public Blob getImagePrincipale() {
        return imagePrincipale;
    }

    public void setImagePrincipale(Blob imagePrincipale) {
        this.imagePrincipale = imagePrincipale;
    }

    @JsonIgnore
    public byte[] getImagePrincipaleJson() {
        if (this.imagePrincipaleJson == null) {
            this.imagePrincipaleJson = this.getImagePrincipaleBytes();
        }
        return this.imagePrincipaleJson;
    }

    public void setImagePrincipaleJson(byte[] imagePrincipaleJson) {
        this.imagePrincipaleJson = imagePrincipaleJson;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Boolean getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(Boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Set<MotCle> getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(Set<MotCle> motsCles) {
        this.motsCles = motsCles;
    }

    public BigDecimal getTauxTVA() {
        return tauxTVA;
    }

    public void setTauxTVA(BigDecimal tauxTVA) {
        this.tauxTVA = tauxTVA;
    }

    // Méthode pour convertir Blob en byte[]
    private byte[] getImagePrincipaleBytes() {
        if (this.imagePrincipale != null) {
            try (InputStream is = this.imagePrincipale.getBinaryStream()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } catch (SQLException | IOException e) {
                throw new RuntimeException("Erreur lors de la conversion de l'image en byte[]", e);
            }
        }
        return null;
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

    public void updateCote(double nouvelleCote) {
        this.cote = nouvelleCote;
    }

    public Boolean getActif() {
        return actif;
    }

    public String getTypePrix() {
        return typePrix;
    }

    // Getter et Setter pour 'vues'
    public Integer getVues() {
        return vues;
    }

    public void setVues(Integer vues) {
        this.vues = vues;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", marque=" + (marque != null ? marque.getNom() : "null") +  // Utiliser un attribut simple ou afficher null
                ", minStock=" + minStock +
                ", middleStock=" + middleStock +
                ", maxStock=" + maxStock +
                ", cote=" + cote +
                ", actif=" + actif +
                ", typePrix='" + typePrix + '\'' +
                ", categorie=" + (categorie != null ? categorie.getNom() : "null") + // Pareil ici
                ", disponibilite=" + disponibilite +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", vues=" + vues +
                ", motsCles=" + (motsCles != null ? motsCles.size() + " mots clés" : "null") + // Afficher la taille au lieu des détails
                ", utilisateursFavoris=" + (utilisateursFavoris != null ? utilisateursFavoris.size() + " utilisateurs favoris" : "null") + // Afficher la taille
                '}';
    }

}
