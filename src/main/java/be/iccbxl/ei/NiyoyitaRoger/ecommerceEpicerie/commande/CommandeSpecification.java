package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import org.springframework.data.jpa.domain.Specification;

public class CommandeSpecification {

    public static Specification<Commande> hasIdLike(String id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("id").as(String.class), "%" + id + "%");
    }

    public static Specification<Commande> hasVilleLike(String ville) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("ville"), "%" + ville + "%");
    }

    public static Specification<Commande> hasCodePostalLike(String codePostal) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("codePostal"), "%" + codePostal + "%");
    }

    public static Specification<Commande> hasStatut(StatutCommande statut) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("statut"), statut);
    }

    public static Specification<Commande> hasMethodCommande(MethodCommande methodCommande) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("methodCommande"), methodCommande);
    }
}

