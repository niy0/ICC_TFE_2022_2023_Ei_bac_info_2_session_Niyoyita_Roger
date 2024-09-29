package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProduitsNotesRepository extends JpaRepository<UserProduitsNotes, UserProduitsNotesId> {

    // Méthode pour enregistrer ou mettre à jour une note
    @Modifying
    @Query(value = "INSERT INTO user_produits_notes (user_id, produit_id, note) VALUES (:userId, :produitId, :note) " +
            "ON DUPLICATE KEY UPDATE note = :note", nativeQuery = true)
    void saveOrUpdateNote(@Param("userId") Long userId, @Param("produitId") Long produitId, @Param("note") Integer note);

    // Méthode pour calculer la moyenne des notes d'un produit
    @Query("SELECT AVG(upn.note) FROM UserProduitsNotes upn WHERE upn.produit.id = :produitId")
    Double calculerMoyenneNotes(@Param("produitId") Long produitId);
}