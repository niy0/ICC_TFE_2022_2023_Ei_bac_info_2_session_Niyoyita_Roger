package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PanierRepository panierRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PanierRepository panierRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.panierRepository = panierRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    //ajouter un nouvel utilisateur + role User
    public void addNewUser(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<Role> roleUSer = roleRepository.findById(1L);
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.getRoles().add(roleUSer.get());
        user.setDateCreation(LocalDateTime.now());
        this.save(user);

        //Création d'un panier pour un nouveau utilisateur
        Panier panier = new Panier();
        panier.setUtilisateur(user);
        panierRepository.save(panier);
    }

    //ajout d'un nouvel utilisateur par un admin
    public void adminNewUser(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setDateCreation(LocalDateTime.now());
        this.save(user);

        //Création d'un panier pour un nouveau utilisateur
        Panier panier = new Panier();
        panier.setUtilisateur(user);
        panierRepository.save(panier);
    }


    // Supprimer un utilisateur (avec gestion des relations)
    @Transactional
    public String deleteUser(Long userId) {
        // 1. Récupérer l'utilisateur par son ID
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 2. Anonymiser les informations personnelles de l'utilisateur
            user.setNom("ANONYMISE");
            user.setPrenom("ANONYMISE");
            user.setEmail("anonymise_" + user.getId() + "@example.com");
            user.setTelephone(null);
            user.setAdresse(null);  // Suppression de l'adresse
            user.setPassword(new BCryptPasswordEncoder().encode("anonymise_password"));// Suppression du mot de passe

            // 3. Conserver les commandes mais anonymiser les informations personnelles
            List<Commande> commandes = user.getCommandes();
            for (Commande commande : commandes) {
                commande.setPrenom("ANONYMISE");
                commande.setNom("ANONYMISE");
                commande.setEmail("anonymise_" + user.getId() + "@example.com");

                // Dé-lier l'utilisateur de la commande
                commande.setUtilisateur(null);
                commande.setUserId(null);  // Supprime aussi l'ID utilisateur dans la commande
            }

            // 4. Vider les produits favoris et les rôles de l'utilisateur (non nécessaires pour les obligations fiscales)
            user.getProduitsFavoris().clear();
            user.getRoles().clear();

            // 5. Sauvegarder les modifications d'anonymisation dans les commandes et l'utilisateur
            userRepository.save(user);

            // 6. Ne pas supprimer directement l'utilisateur, mais conserver l'entrée anonymisée
            return "Utilisateur anonymisé avec succès";
        } else {
            return "Utilisateur non trouvé";
        }
    }


    public String updateUser(Long userId, User updatedUser) {
        String errorMessage = "";
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser == null) {
            return errorMessage = "Error l'utilisateur n'existe pas"; // Gérer le cas où l'utilisateur n'existe pas
        }
        existingUser.setDateModification(LocalDateTime.now());
        userRepository.save(updatedUser);
        return errorMessage;
    }

    public void updateUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
            userRepository.save(user);
        }
    }


    public Page<User> getAllUsers(Pageable pageable, String sortBy, String searchId, String searchNom, String searchPrenom, String searchEmail, String sortRoles, String sortDate) {
        Specification<User> spec = Specification.where(null);

        if (searchId != null && !searchId.isEmpty()) {
            spec = spec.and(UserSpecifications.hasId(searchId));
        }
        if (searchNom != null && !searchNom.isEmpty()) {
            spec = spec.and(UserSpecifications.hasNom(searchNom));
        }
        if (searchPrenom != null && !searchPrenom.isEmpty()) {
            spec = spec.and(UserSpecifications.hasPrenom(searchPrenom));
        }
        if (searchEmail != null && !searchEmail.isEmpty()) {
            spec = spec.and(UserSpecifications.hasEmail(searchEmail));
        }
        if (sortRoles != null && !sortRoles.isEmpty()) {
            spec = spec.and(UserSpecifications.hasRole(sortRoles));
        }

        return userRepository.findAll(spec, pageable);
    }

    public List<Role> getAllRoles() {
        return userRepository.findAllRoles();
    }

    // Méthode pour rechercher un utilisateur par son e-mail
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Méthode pour créer un token de réinitialisation de mot de passe
    public PasswordResetToken createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(calculateExpiryDate(24 * 60)); // Expiration de 24 heures
        return passwordResetTokenRepository.save(resetToken);
    }

    // Méthode pour calculer la date d'expiration du token
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        return new Date(System.currentTimeMillis() + (expiryTimeInMinutes * 60 * 1000));
    }

    // Méthode pour mettre à jour le mot de passe de l'utilisateur après la réinitialisation
    public void updatePassword(String token, String newPassword) {
        User user = getUserByPasswordResetToken(token);  // Récupérer l'utilisateur via le token
        if (user != null && isTokenValid(token)) {
            user.setPassword(passwordEncoder.encode(newPassword));  // Hash du mot de passe avant de le sauvegarder
            userRepository.save(user);
        } else {
            throw new RuntimeException("Token invalide ou expiré");
        }
    }

    // Méthode pour retrouver l'utilisateur via le token de réinitialisation
    private User getUserByPasswordResetToken(String token) {
        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
        return resetTokenOptional.map(PasswordResetToken::getUser).orElse(null);
    }

    // Méthode pour valider si le token est toujours valide (non expiré)
    private boolean isTokenValid(String token) {
        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
        return resetTokenOptional.map(resetToken -> resetToken.getExpiryDate().after(new Date())).orElse(false);
    }
}
