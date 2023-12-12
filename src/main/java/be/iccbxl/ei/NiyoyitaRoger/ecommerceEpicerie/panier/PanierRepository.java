package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PanierRepository extends JpaRepository<Panier,Long> {

    @Query("SELECT p FROM Panier p WHERE p.id = :id")
    Panier getPanier(@Param("id") long id);

    // Méthode pour trouver les paniers modifiés avant un certain LocalDateTime
    List<Panier> findByDateModificationBefore(LocalDateTime seuil);

    // Méthode pour récupérer un panier par son identifiant avec une requête personnalisée
    @Query("SELECT p FROM Panier p WHERE p.utilisateur.id = :idUser")
    Panier getPanierUser(@Param("idUser") long id);
}
