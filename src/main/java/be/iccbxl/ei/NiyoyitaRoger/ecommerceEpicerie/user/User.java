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
    private Long Id;

    @NotEmpty(message = "Le nom doit contenir minimum 2 lettre.")
    @NotBlank
    @Size(min = 2, max = 50, message = "La taille du nom doit être entre 2 et 50 caractères maximum.")
    private String Nom;

    @NotEmpty(message = "Le prenom doit contenir minimum 2 lettre.")
    @NotBlank
    @Size(min = 2, max = 50)
    private String Prenom;

    @NotEmpty(message = "Email obligatoire.")
    @NotBlank
    @Email
    @Column(unique = true, length = 50)
    private String Email;

    @NotEmpty(message = "Mot de passe obligatoire")
    @NotBlank
    @Size(min = 8, max = 200, message = "La taille du mot de passe doit être entre 8 et 50 caractères maximum.")
    private String Password;

    private boolean isLoggedIn;

    @Size(max = 15 , message = "La taille du numéro de téléphone doit être de 15 caractères maximum.")
    private String Telephone;

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
        while(it.hasNext()) {
            Role role = it.next();
            if(role.getNom().equals(roleName)) {
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
    private Set<Produit> produitsFavoris = new HashSet<>();

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Panier panier;

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    @NotNull
    @Enumerated(EnumType.STRING) // Vous pouvez utiliser EnumType.ORDINAL si vous préférez stocker sous forme d'entier
    private Sexe sexe;

    protected User(){}

    public User(String nom, String prenom, String email, String password, Sexe sexe) {
        Nom = nom;
        Prenom = prenom;
        Email = email;
        Password = password;
        this.sexe = sexe;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public void setPrenom(String prenom) {
        Prenom = prenom;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresses) {
        this.adresse = adresses;
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

    public Set<Produit> getProduitsFavoris() {
        return produitsFavoris;
    }

    public void setProduitsFavoris(Set<Produit> produitsFavoris) {
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

    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", Nom='" + Nom + '\'' +
                ", Prenom='" + Prenom + '\'' +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                ", Telephone='" + Telephone + '\'' +
                ", adresse=" + adresse +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", roles=" + roles +
                ", commandes=" + commandes +
                ", produitsFavoris=" + produitsFavoris +
                ", panier=" + panier.getId() +
                ", sexe=" + sexe +
                '}';
    }
}

