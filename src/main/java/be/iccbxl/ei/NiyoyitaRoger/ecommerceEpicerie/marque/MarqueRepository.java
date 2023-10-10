package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarqueRepository extends JpaRepository<Marque, Long> {
    boolean existsByNom(String name);
    Marque findById(long id);
}
