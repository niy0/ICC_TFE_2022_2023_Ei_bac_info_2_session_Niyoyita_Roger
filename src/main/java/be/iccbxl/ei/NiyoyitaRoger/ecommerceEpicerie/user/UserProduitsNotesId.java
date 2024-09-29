package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserProduitsNotesId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "produit_id")
    private Long produitId;

    // Constructeurs
    public UserProduitsNotesId() {
    }

    public UserProduitsNotesId(Long userId, Long produitId) {
        this.userId = userId;
        this.produitId = produitId;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    // equals() et hashCode() sont nécessaires pour que JPA gère correctement la clé composite
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProduitsNotesId that = (UserProduitsNotesId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(produitId, that.produitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, produitId);
    }

    @Override
    public String toString() {
        return "UserProduitsNotesId{" +
                "userId=" + userId +
                ", produitId=" + produitId +
                '}';
    }
}
