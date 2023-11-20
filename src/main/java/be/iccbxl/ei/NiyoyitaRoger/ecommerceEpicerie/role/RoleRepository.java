package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository <Role, Long> {
    Role findByNom(String newRole);
}
