package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void updateLigneDeCommande(LigneDeCommande ligneDeCommande) {
        // Assurez-vous que la ligne de commande existe en base de données
        LigneDeCommande existingLigneDeCommande = ligneDeCommandeRepository.findById(ligneDeCommande.getId()).orElse(null);

        if (existingLigneDeCommande != null) {
            // Mettez à jour les champs de la ligne de commande existante avec les valeurs de la nouvelle ligne de commande
            //existingLigneDeCommande.setProduit(ligneDeCommande.getProduit());
            existingLigneDeCommande.setQuantite(ligneDeCommande.getQuantite());

            // Enregistrez la mise à jour dans la base de données
            ligneDeCommandeRepository.save(existingLigneDeCommande);
        } else {
            // Gérer le cas où la ligne de commande n'existe pas
            throw new EntityNotFoundException("Ligne de commande non trouvée");
        }
    }

    //Methode supprimer une ligne de commande (dans un panier)
    public void deleteLigneDeCommande(Long id) {
        ligneDeCommandeRepository.deleteById(id);
    }
}
