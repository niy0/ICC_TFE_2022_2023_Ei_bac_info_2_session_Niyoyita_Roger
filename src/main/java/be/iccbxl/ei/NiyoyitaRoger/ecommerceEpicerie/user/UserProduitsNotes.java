package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import jakarta.persistence.*;

@Entity
@Table(name = "user_produits_notes")
public class UserProduitsNotes {

    @EmbeddedId
    private UserProduitsNotesId id = new UserProduitsNotesId();

    @ManyToOne
    @MapsId("userId") // Utiliser l'ID utilisateur de la clé composite
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("produitId") // Utiliser l'ID produit de la clé composite
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @Column(name = "note", nullable = false)
    private Integer note;

    // Constructeurs
    public UserProduitsNotes() {
    }

    public UserProduitsNotes(User user, Produit produit, Integer note) {
        this.user = user;
        this.produit = produit;
        this.note = note;
        this.id = new UserProduitsNotesId(user.getId(), produit.getId()); // Crée l'ID composite
    }

    // Getters et setters
    public UserProduitsNotesId getId() {
        return id;
    }

    public void setId(UserProduitsNotesId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "UserProduitsNotes{" +
                "user=" + user.getId() +
                ", produit=" + produit.getId() +
                ", note=" + note +
                '}';
    }
}
