package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PanierService {
    private final PanierRepository panierRepository;

    public PanierService(PanierRepository panierRepository) {
        this.panierRepository = panierRepository;
    }

    // Ajoutez des méthodes pour gérer le panier, par exemple, ajouter, mettre à jour, supprimer, etc.

    public List<Panier> getAllPaniers() {
        return panierRepository.findAll();
    }

    public Panier getPanierById(Long id) {
        return panierRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Panier createPanier(Panier panier) {
        return panierRepository.save(panier);
    }

    public Panier updatePanier(Panier panier) {
        return panierRepository.save(panier);
    }

    public void deletePanier(Long id) {
        panierRepository.deleteById(id);
    }
}
