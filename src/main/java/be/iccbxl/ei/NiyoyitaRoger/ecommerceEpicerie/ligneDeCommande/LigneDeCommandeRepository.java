package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneDeCommandeRepository extends JpaRepository<LigneDeCommande, Long> {
    LigneDeCommande findByProduitIdAndPanierId(Long produitId, Long panierId);
}