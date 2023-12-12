package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
