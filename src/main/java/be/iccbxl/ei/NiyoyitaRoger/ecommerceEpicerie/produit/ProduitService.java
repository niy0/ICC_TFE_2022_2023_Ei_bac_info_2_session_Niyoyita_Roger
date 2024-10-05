package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserProduitsNotesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private UserProduitsNotesRepository userProduitsNotesRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Produit> getAllProduct() {
        return (List<Produit>) produitRepository.findAll();
    }


    public List<Produit> getProduitsAvecQuantiteMinEtActif() {
        return produitRepository.findProduitsAvecQuantiteMin();
    }

    public int getQuantiteProduit(Long produitId) {
        Produit produit = produitRepository.findById(produitId).orElse(null);
        if (produit != null) {
            return produit.getQuantite();
        }
        return 0; // ou une autre valeur par défaut si le produit n'est pas trouvé
    }

    public void updateProduit(Produit produit) {
        produitRepository.save(produit);
    }

    public Optional<Produit> getProduitByIdOp(Long id) {
        return produitRepository.findById(id);
    }

    public Produit getProduitById(Long id) throws ProduitNotFoundException {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException("Produit non trouvé avec l'ID : " + id));
    }

    public void deleteProductById(Long id) {
        produitRepository.deleteById(id);
    }

    //page admin tous les produits actif et non actif
    public Page<Produit> getAllProduits(Pageable pageable, String sortBy, String searchId, String searchNom, String sortPrice, String sortDate, String filterCategorie, String filterMarque, String filterMotCle) {
        Specification<Produit> spec = Specification.where(null);

        if (searchId != null && !searchId.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasId(searchId));
        }
        if (searchNom != null && !searchNom.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasNom(searchNom));
        }
        if (filterCategorie != null && !filterCategorie.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasCategorie(filterCategorie));
        }
        if (filterMarque != null && !filterMarque.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasMarque(filterMarque));
        }
        if (filterMotCle != null && !filterMotCle.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasMotCle(filterMotCle));
        }

        // Appliquez le tri en fonction des valeurs de sortPrice et sortDate
        if (sortPrice != null && !sortPrice.isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortPrice.equals("priceAsc") ? Sort.Order.asc("prix") : Sort.Order.desc("prix")));
        } else if (sortDate != null && !sortDate.isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDate.equals("dateAsc") ? Sort.Order.asc("dateCreation") : Sort.Order.desc("dateCreation")));
        }

        return produitRepository.findAll(spec, pageable);
    }

    //page produit produit actif=true pour tout user
    public Page<Produit> getApiAllProduits(Pageable pageable, String searchQuery, String sortPrice, String sortDate, String filterCategorie, String filterMarque, String filterMotCle) {
        Specification<Produit> spec = Specification.where(null);

        // Filtre pour les produits actifs avec quantité minimale
        spec = spec.and(ProduitSpecifications.isActiveAndHasMinQuantity());

        // Recherche par ID, nom, ou mot-clé
        if (searchQuery != null && !searchQuery.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasIdOrNomOrMotCle(searchQuery));
        }

        // Filtre par catégorie
        if (filterCategorie != null && !filterCategorie.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasCategorie(filterCategorie));
        }

        // Filtre par marque
        if (filterMarque != null && !filterMarque.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasMarque(filterMarque));
        }

        // Filtre par mot-clé
        if (filterMotCle != null && !filterMotCle.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasMotCle(filterMotCle));
        }

        // Applique le tri en fonction de la requête
        if (sortPrice != null && !sortPrice.isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortPrice.equals("priceAsc") ? Sort.Order.asc("prix") : Sort.Order.desc("prix")));
        } else if (sortDate != null && !sortDate.isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDate.equals("dateAsc") ? Sort.Order.asc("dateCreation") : Sort.Order.desc("dateCreation")));
        }

        return produitRepository.findAll(spec, pageable);
    }

    public Page<Produit> getUserFavoris(Long userId, Pageable pageable, String searchNom, String sortPrice, String sortDate, String filterCategorie, String filterMarque, String filterMotCle) {
        Specification<Produit> spec = ProduitSpecifications.isFavoriForUser(userId);

        if (searchNom != null && !searchNom.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasNom(searchNom));
        }

        if (filterCategorie != null && !filterCategorie.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasCategorie(filterCategorie));
        }

        if (filterMarque != null && !filterMarque.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasMarque(filterMarque));
        }

        if (filterMotCle != null && !filterMotCle.isEmpty()) {
            spec = spec.and(ProduitSpecifications.hasMotCleFav(filterMotCle));
        }

        if (sortPrice != null && !sortPrice.isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortPrice.equals("priceAsc") ? Sort.Order.asc("prix") : Sort.Order.desc("prix")));
        } else if (sortDate != null && !sortDate.isEmpty()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDate.equals("dateAsc") ? Sort.Order.asc("dateCreation") : Sort.Order.desc("dateCreation")));
        }

        return produitRepository.findAll(spec, pageable);
    }

    // Sauvegarder ou mettre à jour la note de l'utilisateur pour un produit
    public void noterProduit(Long produitId, Long userId, Integer note) {
        userProduitsNotesRepository.saveOrUpdateNote(userId, produitId, note);
        mettreAJourCoteProduit(produitId);
    }

    // Calculer la moyenne des notes et mettre à jour la cote du produit
    public void mettreAJourCoteProduit(Long produitId) {
        Double moyenne = userProduitsNotesRepository.calculerMoyenneNotes(produitId);
        if (moyenne != null) {
            Produit produit = produitRepository.findById(produitId)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            produit.setCote(moyenne);
            produitRepository.save(produit);
        }
    }

    //statistiques

    //Nombre de vues des produits
    public Page<Produit> getProduitsLesPlusVus(int page, int limit, String order, int vuesMin, Integer vuesMax) {
        Pageable pageable = PageRequest.of(page, limit, order.equals("asc") ? Sort.by("vues").ascending() : Sort.by("vues").descending());

        // Si vuesMax est fourni, utilisez la méthode pour trouver les produits entre deux valeurs de vues
        if (vuesMax != null) {
            return produitRepository.findByVuesBetween(vuesMin, vuesMax, pageable);
        } else {
            // Sinon, récupérez les produits dont le nombre de vues est supérieur ou égal à vuesMin
            return produitRepository.findByVuesGreaterThanEqual(vuesMin, pageable);
        }
    }


    //Notes moyennes des produits
    public Page<Produit> getProduitsAvecCote(int page, int limit, String order, int noteMin, Integer noteMax) {
        // Définir le Pageable avec tri croissant ou décroissant basé sur la cote
        Pageable pageable = PageRequest.of(page, limit, order.equals("asc") ? Sort.by("cote").ascending() : Sort.by("cote").descending());

        // Si une valeur maximale pour la cote est fournie et qu'elle est valide (supérieure ou égale à noteMin)
        if (noteMax != null && noteMax >= noteMin) {
            return produitRepository.findByCoteBetween(noteMin, noteMax, pageable);
        } else {
            // Si aucune cote maximale valide n'est fournie, récupérer les produits avec une cote supérieure ou égale à noteMin
            return produitRepository.findByCoteGreaterThanEqual(noteMin, pageable);
        }
    }

    public Page<Produit> getProduitsLesPlusVendusAvecInterval(int page, int limit, String order, int minAchats, Integer maxAchats) {
        Pageable pageable = PageRequest.of(page, limit, order.equals("asc") ? Sort.by("compteurAchats").ascending() : Sort.by("compteurAchats").descending());

        // Si le maxAchats n'est pas fourni, on ignore cette limite dans la requête
        if (maxAchats != null) {
            return produitRepository.findByCompteurAchatsBetween(minAchats, maxAchats, pageable);
        } else {
            return produitRepository.findByCompteurAchatsGreaterThanEqual(minAchats, pageable);
        }
    }


}

