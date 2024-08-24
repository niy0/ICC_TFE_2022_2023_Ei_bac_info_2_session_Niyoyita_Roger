package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;


public interface ProduitRepository extends JpaRepository<Produit, Long>, JpaSpecificationExecutor<Produit> {
    @Query("SELECT produit FROM Produit produit WHERE CONCAT(produit.id, '', produit.nom, '', produit.prix) LIKE %?1%")
    public List<Produit> getAllProduct(String mot);

    @Query("SELECT p FROM Produit p WHERE p.id = :id")
    Produit getPro(@Param("id") long id);

    @Query("SELECT p FROM Produit p WHERE LOWER(p.nom) LIKE %:nom%")
    List<Produit> findByNomContainingIgnoreCase(@Param("nom") String nom);

    @Query("SELECT p FROM Produit p WHERE p.prix BETWEEN :prixMin AND :prixMax")
    List<Produit> findByPrixBetween(@Param("prixMin") double prixMin, @Param("prixMax") double prixMax);

    @Query("SELECT p FROM Produit p WHERE p.quantite >= 1 AND p.actif = true")
    List<Produit> findProduitsAvecQuantiteMin();

    @Query("SELECT p FROM Produit p WHERE p.quantite >= 1 AND p.actif = true")
    Page<Produit> findProduitsAvecQuantiteMin(Pageable pageable);

}
