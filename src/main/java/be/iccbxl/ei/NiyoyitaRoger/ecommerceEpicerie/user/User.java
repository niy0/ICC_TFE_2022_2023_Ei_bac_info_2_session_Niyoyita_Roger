package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.context.annotation.Scope;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Scope("session")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Le nom doit contenir minimum 2 lettres.")
    @NotBlank
    @Size(min = 2, max = 50, message = "La taille du nom doit être entre 2 et 50 caractères maximum.")
    private String nom;

    @NotEmpty(message = "Le prénom doit contenir minimum 2 lettres.")
    @NotBlank
    @Size(min = 2, max = 50)
    private String prenom;

    @NotEmpty(message = "Email obligatoire.")
    @NotBlank
    @Email
    @Column(unique = true, length = 50)
    private String email;

    @NotEmpty(message = "Mot de passe obligatoire.")
    @NotBlank
    @Size(min = 8, max = 200, message = "La taille du mot de passe doit être entre 8 et 200 caractères maximum.")
    private String password;

    private boolean isLoggedIn;

    @Size(max = 15, message = "La taille du numéro de téléphone doit être de 15 caractères maximum.")
    private String telephone;

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Adresse adresse;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public boolean hasRole(String roleName) {
        Iterator<Role> it = roles.iterator();
        while (it.hasNext()) {
            Role role = it.next();
            if (role.getNom().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    @OneToMany(mappedBy = "utilisateur")
    private List<Commande> commandes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_produits_favoris",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private List<Produit> produitsFavoris = new ArrayList<>();

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Panier panier;

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    protected User() {}

    public User(String nom, String prenom, String email, String password, Sexe sexe) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.sexe = sexe;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }



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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }

    public List<Produit> getProduitsFavoris() {
        return produitsFavoris;
    }

    public void setProduitsFavoris(List<Produit> produitsFavoris) {
        this.produitsFavoris = produitsFavoris;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
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

    public void ajouterProduitFavori(Produit produit) {
        this.produitsFavoris.add(produit);
    }

    public void supprimerProduitFavori(Produit produit) {
        this.produitsFavoris.remove(produit);
    }

    public boolean estProduitFavori(Produit produit) {
        return this.produitsFavoris.contains(produit);
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                ", telephone='" + telephone + '\'' +
                ", adresse=" + adresse +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", roles=" + roles +
                ", commandes=" + commandes +
                ", produitsFavoris=" + produitsFavoris +
                ", panier=" + (panier != null ? panier.getId() : null) +
                ", sexe=" + sexe +
                '}';
    }
}
