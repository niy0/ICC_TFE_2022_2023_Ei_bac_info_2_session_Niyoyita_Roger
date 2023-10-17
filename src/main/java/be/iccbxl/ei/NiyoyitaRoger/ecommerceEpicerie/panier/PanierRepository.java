package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanierRepository extends JpaRepository<Panier,Long> {
}
