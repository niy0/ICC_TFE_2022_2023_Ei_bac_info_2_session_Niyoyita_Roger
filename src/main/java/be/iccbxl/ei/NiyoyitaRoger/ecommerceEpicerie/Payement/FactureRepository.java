package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
}
