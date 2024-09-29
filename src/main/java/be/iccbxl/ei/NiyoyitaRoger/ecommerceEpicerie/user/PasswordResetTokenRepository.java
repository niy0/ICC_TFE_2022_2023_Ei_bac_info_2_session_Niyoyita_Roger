package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // Méthode pour supprimer un token
    @Transactional
    void deleteByToken(String token);
}
