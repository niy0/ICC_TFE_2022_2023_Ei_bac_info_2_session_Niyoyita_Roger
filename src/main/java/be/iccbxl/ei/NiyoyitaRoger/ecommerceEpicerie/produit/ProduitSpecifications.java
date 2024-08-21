package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ProduitSpecifications {
    public static Specification<Produit> hasId(String id) {
        return (produit, query, builder) -> builder.equal(produit.get("id"), id);
    }

    public static Specification<Produit> hasNom(String nom) {
        return (produit, query, builder) -> builder.like(builder.lower(produit.get("nom")), "%" + nom.toLowerCase() + "%");
    }

    public static Specification<Produit> hasCategorie(String categorieId) {
        return (produit, query, builder) -> builder.equal(produit.get("categorie").get("id"), Long.valueOf(categorieId));
    }


    public static Specification<Produit> hasMarque(String marque) {
        return (produit, query, builder) -> builder.equal(produit.get("marque").get("nom"), marque);
    }

    public static Specification<Produit> hasMotCle(String motCle) {
        return (produit, query, builder) -> builder.isMember(motCle, produit.get("motsCles"));
    }

    public static Specification<Produit> hasMotCleFav(String motCleId) {
        return (produit, query, builder) -> {
            Join<Produit, MotCle> motCleJoin = produit.join("motsCles", JoinType.INNER);
            return builder.equal(motCleJoin.get("id"), motCleId);
        };
    }


    // Filtrer les produits favoris pour un utilisateur spécifique
    public static Specification<Produit> isFavoriForUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            Join<Produit, User> favorisJoin = root.join("utilisateursFavoris", JoinType.LEFT);
            return criteriaBuilder.equal(favorisJoin.get("id"), userId);
        };
    }

    // Recherche par ID, nom, ou mot-clé
    public static Specification<Produit> hasIdOrNomOrMotCle(String searchQuery) {
        return (root, query, criteriaBuilder) -> {
            // Comparaison par ID (exacte), nom (contient), ou mot-clé (contient)
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("id").as(String.class), searchQuery),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), "%" + searchQuery.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.join("motsCles").get("nom")), "%" + searchQuery.toLowerCase() + "%")
            );
        };
    }
}
