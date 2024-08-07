package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private  final PanierRepository panierRepository;
    private final UserService userService;

    private final Validator validator;

    @Autowired
    public CommandeService(CommandeRepository commandeRepository,
                           PanierRepository panierRepository,
                           UserService userService,
                           Validator validator) {
        this.commandeRepository = commandeRepository;
        this.panierRepository = panierRepository;
        this.userService = userService;
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

    public Page<Commande> getCommandesByUser(User user, Pageable pageable) {
        return commandeRepository.findByUtilisateur(user, pageable);
    }

    public Page<Commande> getAllCommandes(Pageable pageable, String sortBy) {
        return commandeRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy)));
    }

    @Transactional
    public void updateStatutCommande(Long commandeId, String statut) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée pour cet ID :: " + commandeId));

        try {
            StatutCommande statutCommande = StatutCommande.valueOf(statut);
            commande.setStatut(statutCommande);
            commandeRepository.save(commande);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Statut de commande invalide : " + statut);
        }
    }

    public Page<Commande> getAllCommandes(Pageable pageable, String sortBy, String searchId, String searchVille, String searchCodePostal, String filterStatut, String filterMethodCommande) {
        Specification<Commande> spec = Specification.where(null);

        if (searchId != null && !searchId.isEmpty()) {
            spec = spec.and(CommandeSpecification.hasIdLike(searchId));
        }

        if (searchVille != null && !searchVille.isEmpty()) {
            spec = spec.and(CommandeSpecification.hasVilleLike(searchVille));
        }

        if (searchCodePostal != null && !searchCodePostal.isEmpty()) {
            spec = spec.and(CommandeSpecification.hasCodePostalLike(searchCodePostal));
        }

        if (filterStatut != null && !filterStatut.isEmpty()) {
            spec = spec.and(CommandeSpecification.hasStatut(StatutCommande.valueOf(filterStatut)));
        }

        if (filterMethodCommande != null && !filterMethodCommande.isEmpty()) {
            spec = spec.and(CommandeSpecification.hasMethodCommande(MethodCommande.valueOf(filterMethodCommande)));
        }

        return commandeRepository.findAll(spec, pageable);
    }

}
