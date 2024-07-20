package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private  final PanierRepository panierRepository;

    @Autowired
    public CommandeService(CommandeRepository commandeRepository, PanierRepository panierRepository) {
        this.commandeRepository = commandeRepository;
        this.panierRepository = panierRepository;
    }

    public List<Commande> getCommandesByUser(User user) {
        return commandeRepository.findByUtilisateurId(user.getId());
    }

    public Optional<Commande> getCommandeById(Long id) {
        return commandeRepository.findById(id);
    }

    public Commande createCommande(Commande nouvelleCommande) {
        return commandeRepository.save(nouvelleCommande);
    }
}
