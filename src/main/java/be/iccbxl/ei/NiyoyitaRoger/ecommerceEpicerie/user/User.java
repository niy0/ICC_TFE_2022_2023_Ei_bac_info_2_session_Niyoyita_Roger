package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String Nom;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 50)
    private String Prenom;

    @NotNull
    @NotBlank
    @Email
    private String Email;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 50)
    private String Password;

    private boolean isLoggedIn;

    @Size(max = 15)
    private String Telephone;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Adresse> adresses = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur")
    private List<Commande> commandes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_produits_favoris",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private Set<Produit> produitsFavoris = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING) // Vous pouvez utiliser EnumType.ORDINAL si vous préférez stocker sous forme d'entier
    private Sexe sexe;

    protected User(){}

    public User(String nom, String prenom, String email, String password) {
        Nom = nom;
        Prenom = prenom;
        Email = email;
        Password = password;
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

    public List<Adresse> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<Adresse> adresses) {
        this.adresses = adresses;
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
                ", adresses=" + adresses +
                ", roles=" + roles +
                ", commandes=" + commandes +
                ", produitsFavoris=" + produitsFavoris +
                ", sexe=" + sexe +
                '}';
    }
}

