package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long>, JpaSpecificationExecutor<Commande> {
    List<Commande> findByUtilisateurId(Long userId);

    Page<Commande> findByUtilisateur(User utilisateur, Pageable pageable);

    //statistiques
    // Requête pour les utilisateurs avec le nombre de commandes entre un minimum et un maximum
    // Repository
    @Query("SELECT c.userId, COUNT(c) AS nombreDeCommandes FROM Commande c WHERE c.userId IS NOT NULL GROUP BY c.userId HAVING COUNT(c) BETWEEN :minCommandes AND :maxCommandes ORDER BY nombreDeCommandes ASC")
    Page<Object[]> findByUserIdNotNullAndNombreDeCommandesBetweenOrderByNombreDeCommandesAsc(@Param("minCommandes") int minCommandes, @Param("maxCommandes") int maxCommandes, Pageable pageable);

    @Query("SELECT c.userId, COUNT(c) AS nombreDeCommandes FROM Commande c WHERE c.userId IS NOT NULL GROUP BY c.userId HAVING COUNT(c) BETWEEN :minCommandes AND :maxCommandes ORDER BY nombreDeCommandes DESC")
    Page<Object[]> findByUserIdNotNullAndNombreDeCommandesBetweenOrderByNombreDeCommandesDesc(@Param("minCommandes") int minCommandes, @Param("maxCommandes") int maxCommandes, Pageable pageable);

    @Query("SELECT c.userId, COUNT(c) AS nombreDeCommandes FROM Commande c WHERE c.userId IS NOT NULL GROUP BY c.userId HAVING COUNT(c) >= :minCommandes ORDER BY nombreDeCommandes ASC")
    Page<Object[]> findByUserIdNotNullAndNombreDeCommandesGreaterThanEqualOrderByNombreDeCommandesAsc(@Param("minCommandes") int minCommandes, Pageable pageable);

    @Query("SELECT c.userId, COUNT(c) AS nombreDeCommandes FROM Commande c WHERE c.userId IS NOT NULL GROUP BY c.userId HAVING COUNT(c) >= :minCommandes ORDER BY nombreDeCommandes DESC")
    Page<Object[]> findByUserIdNotNullAndNombreDeCommandesGreaterThanEqualOrderByNombreDeCommandesDesc(@Param("minCommandes") int minCommandes, Pageable pageable);


    // Chiffre d'affaires mensuel pour une année donnée avec pagination
    @Query(value = "SELECT MONTH(c.date_commande) AS mois, YEAR(c.date_commande) AS annee, " +
            "COALESCE(SUM(c.montant_commande), 0) AS chiffre_affaire_mensuel " +
            "FROM commande c " +
            "WHERE YEAR(c.date_commande) = :annee " +
            "GROUP BY mois, annee " +
            "ORDER BY mois ASC",
            countQuery = "SELECT COUNT(DISTINCT MONTH(c.date_commande)) " +
                    "FROM commande c " +
                    "WHERE YEAR(c.date_commande) = :annee",
            nativeQuery = true)
    Page<Object[]> findChiffreAffaireMensuelParAnnee(@Param("annee") int annee, Pageable pageable);


}