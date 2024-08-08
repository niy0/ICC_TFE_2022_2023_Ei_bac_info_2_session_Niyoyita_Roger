package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PanierRepository panierRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PanierRepository panierRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.panierRepository = panierRepository;
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
    public void addNewUser(User user){
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
    public void adminNewUser(User user){
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

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
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
        // Implémentez cette méthode pour récupérer tous les rôles
        return userRepository.findAllRoles();
    }

}
