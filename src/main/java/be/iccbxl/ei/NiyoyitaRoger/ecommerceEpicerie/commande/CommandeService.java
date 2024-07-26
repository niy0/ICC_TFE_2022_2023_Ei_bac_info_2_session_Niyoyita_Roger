package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private  final PanierRepository panierRepository;

    private final Validator validator;

    @Autowired
    public CommandeService(CommandeRepository commandeRepository, PanierRepository panierRepository, Validator validator) {
        this.commandeRepository = commandeRepository;
        this.panierRepository = panierRepository;
        this.validator = validator;
    }

    public List<Commande> getCommandesByUser(User user) {
        return commandeRepository.findByUtilisateurId(user.getId());
    }

    public Optional<Commande> getCommandeById(Long id) {
        return commandeRepository.findById(id);
    }

    public Commande createCommande(Commande nouvelleCommande) {
        // Valider la commande avant de la sauvegarder
        Set<ConstraintViolation<Commande>> violations = validator.validate(nouvelleCommande);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return commandeRepository.save(nouvelleCommande);
    }
}
