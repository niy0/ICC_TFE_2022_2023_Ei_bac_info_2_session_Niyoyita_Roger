package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import jakarta.persistence.*;

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

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @Column(name = "prix", nullable = false)
    private double prix;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "marque", length = 40)
    private String marque; // rendre en objet


    @Column( nullable = false)
    private String imagePrincipale;


    @Column(name = "disponibilite")
    private boolean disponibilite = true; //vérifier binder avec quantité

    //*************admin/manager manage*************

    @Column(name = "min")
    private Integer minProd = 1;

    @Column(name = "middle")
    private Integer middleProd = null;

    @Column(name = "max")
    private Integer maxProd = null;

    private Integer cote = null;

    //***************************

    @Column(name = "date_creation",  nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ElementCollection
    @CollectionTable(name = "produit_mots_cles", joinColumns = @JoinColumn(name = "produit_id"))
    @Column(name = "mot_cle",length = 40)
    private Set<String> motsCles = new HashSet<>(); //modifier pour une entité
    //*******************

    public Produit() {
    }

    public Produit(String nom, String description, String prix, String quantite, String bytes) {
        this.nom = nom;
        this.description = description;
        this.prix = Double.parseDouble(prix);
        this.quantite = Integer.parseInt(quantite);
        this.imagePrincipale = bytes;
        this.dateCreation = LocalDateTime.now();
    }

    public Produit(String nom, String description, String prix, String quantite, String bytes, String mot) {
        this.nom = nom;
        this.description = description;
        this.prix = Double.parseDouble(prix);
        this.quantite = Integer.parseInt(quantite);
        this.imagePrincipale = bytes;
        this.motsCles.add(mot);
        this.dateCreation = LocalDateTime.now();

    }

    //faire un nouveau costruct

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public double getPrix() {
        return prix;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public String getMarque() {
        return marque;
    }

    public String getImagePrincipale() {
        return imagePrincipale;
    }
    public boolean isDisponibilite() {
        return disponibilite;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }



    public Set<String> getMotsCles() {
        return motsCles;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public void setImagePrincipale(String imagePrincipale) {
        this.imagePrincipale = imagePrincipale;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }


    public void setMotsCles(Set<String> motsCles) {
        this.motsCles = motsCles;
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


    public Integer getMinProd() {
        return minProd;
    }

    public Integer getMiddleProd() {
        return middleProd;
    }

    public Integer getMaxProd() {
        return maxProd;
    }

    public Integer getCote() {
        return cote;
    }

    public void setMinProd(Integer minProd) {
        this.minProd = minProd;
    }

    public void setMiddleProd(Integer middleProd) {
        this.middleProd = middleProd;
    }

    public void setMaxProd(Integer maxProd) {
        this.maxProd = maxProd;
    }

    public void setCote(Integer cote) {
        this.cote = cote;
    }

}
