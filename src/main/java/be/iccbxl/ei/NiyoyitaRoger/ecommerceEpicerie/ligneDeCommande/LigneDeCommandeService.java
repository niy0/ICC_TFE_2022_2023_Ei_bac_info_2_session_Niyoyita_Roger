package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LigneDeCommandeService {

    private final LigneDeCommandeRepository ligneDeCommandeRepository;

    @Autowired
    public LigneDeCommandeService(LigneDeCommandeRepository ligneDeCommandeRepository) {
        this.ligneDeCommandeRepository = ligneDeCommandeRepository;
    }

    public LigneDeCommande save(LigneDeCommande ligneDeCommande) {
        return ligneDeCommandeRepository.save(ligneDeCommande);
    }

    public List<LigneDeCommande> getAllLignesDeCommande() {
        return ligneDeCommandeRepository.findAll();
    }

    public LigneDeCommande getLigneDeCommandeById(Long id) {
        return ligneDeCommandeRepository.findById(id).orElse(null);
    }

    @Transactional
    public LigneDeCommande updateLigneDeCommandeQuantite(Long ligneDeCommandeId, int nouvelleQuantite) {
        // Rechercher la ligne de commande
        Optional<LigneDeCommande> ligneOpt = ligneDeCommandeRepository.findById(ligneDeCommandeId);
        if (!ligneOpt.isPresent()) {
            throw new RuntimeException("Ligne de commande non trouvée.");
        }

        LigneDeCommande ligneDeCommande = ligneOpt.get();
        Produit produit = ligneDeCommande.getProduit();

        // Vérifier que la quantité demandée n'excède pas le stock disponible
        if (nouvelleQuantite > produit.getQuantite()) {
            throw new RuntimeException("Quantité demandée non disponible en stock. Maximum : " + produit.getQuantite());
        }

        // Mettre à jour la quantité
        ligneDeCommande.setQuantite(nouvelleQuantite);

        // Recalculer le montant total pour la ligne de commande
        ligneDeCommande.calculerMontantTotal();

        // Sauvegarder la ligne de commande mise à jour
        return ligneDeCommandeRepository.save(ligneDeCommande);
    }


    //Methode supprimer une ligne de commande (dans un panier)
    public void deleteLigneDeCommande(Long id) {
        ligneDeCommandeRepository.deleteById(id);
    }
}
