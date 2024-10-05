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


    //statistiques

    // Requête pour obtenir les produits dont le nombre de vues est compris entre deux valeurs
    Page<Produit> findByVuesBetween(int vuesMin, int vuesMax, Pageable pageable);

    // Requête pour obtenir les produits dont le nombre de vues est supérieur ou égal à une valeur
    Page<Produit> findByVuesGreaterThanEqual(int vuesMin, Pageable pageable);

    Page<Produit> findByCompteurAchatsBetween(int minAchats, int maxAchats, Pageable pageable);

    Page<Produit> findByCompteurAchatsGreaterThanEqual(int minAchats, Pageable pageable);


    @Query("SELECT p FROM Produit p WHERE p.cote BETWEEN :noteMin AND :noteMax")
    Page<Produit> findProduitsAvecCote(
            @Param("noteMin") Double noteMin,
            @Param("noteMax") Double noteMax,
            Pageable pageable
    );

    // Trouver les produits avec une cote entre deux valeurs
    @Query("SELECT p FROM Produit p WHERE p.cote BETWEEN :noteMin AND :noteMax")
    Page<Produit> findByCoteBetween(@Param("noteMin") int noteMin, @Param("noteMax") int noteMax, Pageable pageable);

    // Trouver les produits avec une cote supérieure ou égale à une valeur minimale
    @Query("SELECT p FROM Produit p WHERE p.cote >= :noteMin")
    Page<Produit> findByCoteGreaterThanEqual(@Param("noteMin") int noteMin, Pageable pageable);


}
