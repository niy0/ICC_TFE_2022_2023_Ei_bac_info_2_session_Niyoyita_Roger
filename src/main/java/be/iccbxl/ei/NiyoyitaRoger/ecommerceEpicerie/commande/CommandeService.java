package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.livraison.LivraisonService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final PanierRepository panierRepository;
    private final UserService userService;
    private final Validator validator;
    private final LivraisonService livraisonService;

    @Autowired
    public CommandeService(CommandeRepository commandeRepository,
                           PanierRepository panierRepository,
                           UserService userService,
                           Validator validator,
                           LivraisonService livraisonService) {
        this.commandeRepository = commandeRepository;
        this.panierRepository = panierRepository;
        this.userService = userService;
        this.validator = validator;
        this.livraisonService = livraisonService;
    }

    public void save(Commande commande) {
        commandeRepository.save(commande);
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

    public void creerOuMettreAJourCommande(Commande commande, LivraisonService livraisonService) {
        // Calculer les frais de commande en fonction de la méthode (Pickup ou Delivery)
        commande.calculerFraisDeCommande(livraisonService);

        // Associer chaque ligne de commande à la commande
        commande.getLignesDeCommande().forEach(ligne -> ligne.setCommande(commande));

        // Enregistrez ou mettez à jour la commande dans la base de données
        commandeRepository.save(commande);
    }

    //stat
    //Nombre de commandes par utilisateur
    // Service
    // Service
    public Page<Object[]> getCommandesParUtilisateur(int minCommandes, Integer maxCommandes, String order, Pageable pageable) {
        if (maxCommandes != null) {
            // Utiliser l'ordre défini (asc ou desc) pour les commandes entre minCommandes et maxCommandes
            return "asc".equals(order)
                    ? commandeRepository.findByUserIdNotNullAndNombreDeCommandesBetweenOrderByNombreDeCommandesAsc(minCommandes, maxCommandes, pageable)
                    : commandeRepository.findByUserIdNotNullAndNombreDeCommandesBetweenOrderByNombreDeCommandesDesc(minCommandes, maxCommandes, pageable);
        } else {
            // Utiliser l'ordre défini (asc ou desc) pour les commandes à partir de minCommandes
            return "asc".equals(order)
                    ? commandeRepository.findByUserIdNotNullAndNombreDeCommandesGreaterThanEqualOrderByNombreDeCommandesAsc(minCommandes, pageable)
                    : commandeRepository.findByUserIdNotNullAndNombreDeCommandesGreaterThanEqualOrderByNombreDeCommandesDesc(minCommandes, pageable);
        }
    }

    // Chiffre d'affaires mensuel par année avec pagination
    public Page<Object[]> getChiffreAffaireMensuelParAnnee(int annee, int page, int limit) {
        // Le tri se fait toujours en ordre croissant par mois
        Sort sort = Sort.by("mois").ascending();
        Pageable pageable = PageRequest.of(page, limit, sort);

        // Récupérer les données depuis le repository
        Page<Object[]> rawData = commandeRepository.findChiffreAffaireMensuelParAnnee(annee, pageable);

        // Formater chaque ligne de données
        List<Object[]> formattedData = rawData.getContent().stream()
                .map(data -> formatChiffreAffaire(data, null, annee, annee))  // Formater les données
                .collect(Collectors.toList());

        // Retourner une nouvelle page avec les données formatées
        return new PageImpl<>(formattedData, pageable, rawData.getTotalElements());
    }


    public Object[] formatChiffreAffaire(Object[] data, String mois, Integer anneeMin, Integer anneeMax) {
        Integer monthNumber = (Integer) data[0]; // Mois
        Integer year = (Integer) data[1]; // Année
        BigDecimal totalAmount = null;

        // Vérifier si le montant est de type BigDecimal ou Integer
        if (data[2] instanceof BigDecimal) {
            totalAmount = (BigDecimal) data[2];
        } else if (data[2] instanceof Integer) {
            totalAmount = BigDecimal.valueOf((Integer) data[2]);
        } else if (data[2] instanceof Long) {
            totalAmount = BigDecimal.valueOf((Long) data[2]);
        }

        String moisFormatted = getMonthNameInFrench(monthNumber); // Convertir le numéro de mois en nom du mois
        String montantFormatted = String.format(Locale.FRANCE, "%.2f €", totalAmount); // Formater le montant

        return new Object[]{moisFormatted, year, montantFormatted};
    }

    // Méthode pour traduire les numéros de mois en noms français
    public String getMonthNameInFrench(int monthNumber) {
        return Stream.of("Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre")
                .collect(Collectors.toList()).get(monthNumber - 1);
    }

}
