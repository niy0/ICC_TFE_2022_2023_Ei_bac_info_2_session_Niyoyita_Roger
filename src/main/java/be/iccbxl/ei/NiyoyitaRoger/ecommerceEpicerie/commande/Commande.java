package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.Adresse;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User utilisateur;

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

    @ManyToOne
    @JoinColumn(name = "panier_id", nullable = false)
    @NotNull
    private Panier panier;

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

    protected Commande() {}

    // Constructors for different scenarios
    public Commande(Panier panier, MethodCommande methodCommande) {
        this.panier = panier;
        this.dateCommande = new Date();
        this.statut = StatutCommande.EN_COURS;
        this.methodCommande = methodCommande;
    }

    public Commande(User utilisateur, Panier panier, MethodCommande methodCommande) {
        this(panier, methodCommande);
        this.utilisateur = utilisateur;
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
    }

    public Commande(String prenom, String nom, String email, String rue, String numero, String localite, String ville, String codePostal, String departement, String pays, Panier panier, MethodCommande methodCommande) {
        this(panier, methodCommande);
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
    }

    public Commande(Adresse adresse, String email, Panier panier, MethodCommande methodCommande) {
        this(panier, methodCommande);
        this.prenom = adresse.getPrenom();
        this.nom = adresse.getNom();
        this.email = email;
        this.rue = adresse.getRue();
        this.numero = adresse.getNumero();
        this.localite = adresse.getLocalite();
        this.ville = adresse.getVille();
        this.codePostal = adresse.getCodePostal();
        this.departement = adresse.getDepartement();
        this.pays = adresse.getPays();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
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

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
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
}
