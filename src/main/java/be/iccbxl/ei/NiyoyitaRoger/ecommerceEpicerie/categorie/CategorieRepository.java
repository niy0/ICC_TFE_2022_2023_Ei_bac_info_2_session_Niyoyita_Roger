package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie,Long> {
    boolean existsByNom(String nom);
}
