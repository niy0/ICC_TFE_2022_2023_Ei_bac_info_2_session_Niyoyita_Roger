package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.livraison.LivraisonService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true, insertable = false, updatable = false)
    private User utilisateur;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @NotBlank
    private String prenom;

    @NotBlank
    private String nom;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String rue;

    @NotNull
    @Size(max = 10)
    private String numero;

    private String localite;

    @NotBlank
    private String ville;

    @NotBlank
    private String codePostal;

    private String departement;

    @NotBlank
    private String pays;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCommande;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatutCommande statut;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MethodCommande methodCommande;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDerniereMajStatut;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<ChangementStatutCommande> historiqueStatuts;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LigneDeCommande> lignesDeCommande = new LinkedList<>();

    @NotNull
    private BigDecimal montantCommande;

    @NotNull
    private BigDecimal fraisCommande;

    // Constructeur par défaut
    protected Commande() {
    }

    // Constructeur avec utilisateur connecté
    public Commande(User utilisateur, MethodCommande methodCommande) {
        this.utilisateur = utilisateur;
        this.userId = utilisateur.getId();
        this.prenom = utilisateur.getPrenom();
        this.nom = utilisateur.getNom();
        this.email = utilisateur.getEmail();
        this.rue = utilisateur.getAdresse().getRue();
        this.numero = utilisateur.getAdresse().getNumero();
        this.localite = utilisateur.getAdresse().getLocalite();
        this.ville = utilisateur.getAdresse().getVille();
        this.codePostal = utilisateur.getAdresse().getCodePostal();
        this.departement = utilisateur.getAdresse().getDepartement();
        this.pays = utilisateur.getAdresse().getPays();

        this.iniNouvelleCommande(methodCommande);
    }

    //Constructeur sans utilisateur connecté
    public Commande(String prenom, String nom, String email, String rue, String numero, String localite, String ville, String codePostal, String departement, String pays, MethodCommande methodCommande) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.rue = rue;
        this.numero = numero;
        this.localite = localite;
        this.ville = ville;
        this.codePostal = codePostal;
        this.departement = departement;
        this.pays = pays;

        this.iniNouvelleCommande(methodCommande);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
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

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public MethodCommande getMethodCommande() {
        return methodCommande;
    }

    public void setMethodCommande(MethodCommande methodCommande) {
        this.methodCommande = methodCommande;
    }

    public Date getDateDerniereMajStatut() {
        return dateDerniereMajStatut;
    }

    public void setDateDerniereMajStatut(Date dateDerniereMajStatut) {
        this.dateDerniereMajStatut = dateDerniereMajStatut;
    }

    public List<ChangementStatutCommande> getHistoriqueStatuts() {
        return historiqueStatuts;
    }

    public void setHistoriqueStatuts(List<ChangementStatutCommande> historiqueStatuts) {
        this.historiqueStatuts = historiqueStatuts;
    }

    public BigDecimal getMontantCommande() {
        return montantCommande;
    }

    public void setMontantCommande(BigDecimal montantCommande) {
        this.montantCommande = montantCommande;
    }

    public List<LigneDeCommande> getLignesDeCommande() {
        return lignesDeCommande;
    }

    public void setLignesDeCommande(List<LigneDeCommande> lignesDeCommande) {
        this.lignesDeCommande = lignesDeCommande;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Getter et Setter pour fraisCommande
    public BigDecimal getFraisCommande() {
        return fraisCommande;
    }

    public void setFraisCommande(BigDecimal fraisCommande) {
        this.fraisCommande = fraisCommande;
    }

    public void calculerFraisDeCommande(LivraisonService livraisonService) {
        if (this.methodCommande == MethodCommande.PICKUP) {
            // Si c'est un retrait en magasin, les frais sont de 0
            this.fraisCommande = BigDecimal.ZERO;
        } else {
            // Si c'est une livraison, on calcule les frais via le service
            BigDecimal fraisLivraison = livraisonService.calculerFraisLivraison(this);
            this.fraisCommande = fraisLivraison;
        }
    }

    // Méthode de validation pour vérifier que la liste des lignes de commande n'est pas vide
    @AssertTrue(message = "La commande doit avoir au moins une ligne de commande.")
    public boolean isLignesDeCommandeNotEmpty() {
        return lignesDeCommande != null && !lignesDeCommande.isEmpty();
    }

    // Méthode pour initialiser une nouvelle commande
    private void iniNouvelleCommande(MethodCommande methodCommande) {
        this.dateCommande = new Date();
        this.statut = StatutCommande.EN_COURS;
        this.methodCommande = methodCommande;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", userId=" + userId +
                ", prenom='" + prenom + '\'' +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", rue='" + rue + '\'' +
                ", numero='" + numero + '\'' +
                ", localite='" + localite + '\'' +
                ", ville='" + ville + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", departement='" + departement + '\'' +
                ", pays='" + pays + '\'' +
                ", dateCommande=" + dateCommande +
                ", statut=" + statut +
                ", methodCommande=" + methodCommande.name() +
                ", dateDerniereMajStatut=" + dateDerniereMajStatut +
                ", historiqueStatuts=" + historiqueStatuts +
                ", montantCommande=" + montantCommande +
                ", fraisCommande=" + fraisCommande +
                ", lignesDeCommande=" + lignesDeCommande +
                '}';
    }
}
