package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProduitRepository  extends JpaRepository<Produit, Long> {
    @Query("SELECT produit FROM Produit produit WHERE CONCAT(produit.id, '', produit.nom, '', produit.prix) LIKE %?1%")
    public List<Produit> getAllProduct(String mot);

    @Query("SELECT p FROM Produit p WHERE LOWER(p.nom) LIKE %:nom%")
    List<Produit> findByNomContainingIgnoreCase(@Param("nom") String nom);

    @Query("SELECT p FROM Produit p WHERE p.prix BETWEEN :prixMin AND :prixMax")
    List<Produit> findByPrixBetween(@Param("prixMin") double prixMin, @Param("prixMax") double prixMax);
}
