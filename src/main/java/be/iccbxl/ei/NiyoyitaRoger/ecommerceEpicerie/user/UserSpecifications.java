package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    public static Specification<User> hasId(String id) {
        return (user, query, builder) -> builder.equal(user.get("id"), id);
    }

    public static Specification<User> hasNom(String nom) {
        return (user, query, builder) -> builder.like(builder.lower(user.get("nom")), "%" + nom.toLowerCase() + "%");
    }

    public static Specification<User> hasPrenom(String prenom) {
        return (user, query, builder) -> builder.like(builder.lower(user.get("prenom")), "%" + prenom.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (user, query, builder) -> builder.like(builder.lower(user.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(String roleName) {
        return (user, query, builder) -> {
            Join<User, Role> roles = user.join("roles");
            return builder.equal(roles.get("nom"), roleName);
        };
    }
}