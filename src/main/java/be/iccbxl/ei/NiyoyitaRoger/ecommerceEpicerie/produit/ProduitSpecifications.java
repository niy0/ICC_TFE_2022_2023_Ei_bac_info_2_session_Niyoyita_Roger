package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

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
}
