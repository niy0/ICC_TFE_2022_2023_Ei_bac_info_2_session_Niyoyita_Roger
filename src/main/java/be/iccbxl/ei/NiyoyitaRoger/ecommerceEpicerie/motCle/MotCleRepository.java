package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MotCleRepository extends JpaRepository <MotCle, Long> {
    boolean existsByNom(String name);
    MotCle findById(long id);
}
