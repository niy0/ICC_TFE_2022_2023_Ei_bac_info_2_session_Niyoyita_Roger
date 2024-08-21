package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    //api de recherche page /produit
    public Page<Produit> getApiAllProduits(Pageable pageable, String searchQuery, String sortPrice, String sortDate, String filterCategorie, String filterMarque, String filterMotCle) {
        Specification<Produit> spec = Specification.where(null);

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

}

